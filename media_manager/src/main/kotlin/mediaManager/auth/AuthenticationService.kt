package mediaManager.auth

import mediaManager.auth.dtos.login.LoginRequestDto
import mediaManager.auth.dtos.login.LoginResponseDto
import mediaManager.auth.dtos.register.RegisterRequestDto
import mediaManager.exceptions.ExpiredException
import mediaManager.exceptions.TooManyAttemptsException
import mediaManager.expiredToken.ExpiredTokenService
import mediaManager.notification.mail.Mail
import mediaManager.notification.mail.MailService
import mediaManager.redis.RedisContext
import mediaManager.redis.RedisService
import mediaManager.refreshToken.RefreshTokenService
import mediaManager.user.CustomUserDetailsService
import mediaManager.user.Role
import mediaManager.user.User
import mediaManager.user.UserService
import org.springframework.context.ApplicationContext
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import kotlin.random.Random

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val userService: UserService,
    private val refreshTokenService: RefreshTokenService,
    private val expiredTokenService: ExpiredTokenService,
    private val redisService: RedisService,
    private val mailService: MailService,
    private val jwtProperties: JWTProperties,
    private val authenticationProperties: AuthenticationProperties,
    private val applicationContext: ApplicationContext,
) {
    fun login(loginRequest: LoginRequestDto): LoginResponseDto {
        authManager.authenticate(UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password))

        val user = userDetailsService.loadUserByUsername(loginRequest.email)

        val accessToken = generateAccessToken(user)
        val refreshToken = refreshTokenService.generate(user)

        try {
            return LoginResponseDto(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        } catch (error: IllegalArgumentException) {
            throw BadCredentialsException("Incorrect Email or Password!")
        }
    }

    fun register(request: RegisterRequestDto): User {
        val userOTP = redisService.get(RedisContext.AUTH, getOTPCacheKey(request.email)) ?: throw ExpiredException("OTP is Expired!")

        if (userOTP != request.otp) {
            throw IllegalArgumentException("OTP is not valid!")
        }
        // Deleting the OTP from Redis
        redisService.delete(RedisContext.AUTH, getOTPCacheKey(request.email))

        return userService.createUser(User(email = request.email, password = request.password, role = Role.USER))
    }

    fun refreshAccessToken(token: String): String {
        val extractedEmail = tokenService.extractEmail(token)

        return extractedEmail.let { email ->
            val currentUserDetails = userDetailsService.loadUserByUsername(email)
            val refreshTokenUserDetails = refreshTokenService.findUserDetailsByToken(token)

            if (!tokenService.isExpired(token) && currentUserDetails.username == refreshTokenUserDetails.username) {
                generateAccessToken(currentUserDetails)
            } else {
                throw AccessDeniedException("Invalid refresh token!")
            }
        }
    }

    fun logout(authHeader: String?) {
        if (!TokenUtils.isStartWithTokenType(authHeader)) {
            throw IllegalArgumentException("Invalid Token!")
        }

        val token = TokenUtils.extractTokenValue(authHeader!!)

        expiredTokenService.save(token)
    }

    fun sendOTP(email: String) {
        val oldOTP = redisService.get(RedisContext.AUTH, getOTPCacheKey(email))
        if (oldOTP != null) {
            throw TooManyAttemptsException("Try again in ${authenticationProperties.otpExpiration} seconds!")
        }

        val otp = generateOTP()

        redisService.set(RedisContext.AUTH, getOTPCacheKey(email), otp, authenticationProperties.otpExpiration)
        mailService.sendMail(getOTPMail(email, otp))
    }

    private fun getOTPCacheKey(email: String): String = "$email:otp"

    private fun generateOTP() =
        (1..authenticationProperties.otpLength)
            .map { Random.nextInt(0, 10) }
            .joinToString("")

    private fun getOTPMail(
        email: String,
        otp: String,
    ) = Mail(
        to = email,
        subject = "${applicationContext.applicationName} Email Verification",
        content = "<p>Verification Code: $otp</p>",
    )

    private fun generateAccessToken(user: UserDetails) =
        tokenService.generate(
            userDetails = user,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration),
        )
}
