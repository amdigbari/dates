package mediaManager.auth

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.UnsupportedJwtException
import mediaManager.auth.dtos.login.LoginRequestDto
import mediaManager.auth.dtos.login.LoginResponseDto
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
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.mail.MailException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
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
    /**
     * Sending Otp via the mail service and cache in the redis
     *
     * @param email String.
     *
     * @throws TooManyAttemptsException in case there is an otp available for the user in the redis.
     * @throws MailException
     */
    fun sendOTP(email: String) {
        val oldOTP = redisService.get(RedisContext.AUTH, getOTPCacheKey(email))
        if (oldOTP != null) {
            throw TooManyAttemptsException("Try again in ${authenticationProperties.otpExpiration} seconds!")
        }

        val otp = generateOTP()

        redisService.set(RedisContext.AUTH, getOTPCacheKey(email), otp, authenticationProperties.otpExpiration)
        mailService.sendMail(getOTPMail(email, otp))
    }

    /**
     * @param loginRequest required data like username and password to login. See LoginRequestDto.
     *
     * @return The access and refresh token for the user. See LoginResponseDto.
     *
     * @throws DisabledException in case user is disabled.
     * @throws LockedException in case user is locked.
     * @throws BadCredentialsException in case username or password are incorrect.
     * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with a different value from that found in the persistence store. Also thrown if the entity is assumed to be present but does not exist in the database.
     */
    fun login(loginRequest: LoginRequestDto): LoginResponseDto {
        authManager.authenticate(UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password))

        try {
            val user = userDetailsService.loadUserByUsername(loginRequest.email)

            val accessToken = generateAccessToken(user)
            val refreshToken = refreshTokenService.generate(user)

            return LoginResponseDto(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        } catch (error: UsernameNotFoundException) {
            throw BadCredentialsException("Incorrect Email or Password!")
        } catch (error: IllegalArgumentException) {
            throw BadCredentialsException("Incorrect Email or Password!")
        } catch (error: NoSuchElementException) {
            throw BadCredentialsException("Incorrect Email or Password!")
        }
    }

    /**
     * Registers a new user and checks the otp of it
     *
     * @param email String.
     * @param otp String.
     * @param password String
     *
     * @throws IllegalArgumentException in case otp is not valid or email already exists.
     * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with a different value from that found in the persistence store. Also thrown if the entity is assumed to be present but does not exist in the database.
     */
    fun register(
        email: String,
        otp: String,
        password: String,
    ): User {
        val userOTP = redisService.get(RedisContext.AUTH, getOTPCacheKey(email)) ?: throw IllegalArgumentException("OTP is not valid!")

        if (userOTP != otp) {
            throw IllegalArgumentException("OTP is not valid!")
        }
        // Deleting the OTP from Redis
        redisService.delete(RedisContext.AUTH, getOTPCacheKey(email))

        return userService.createUser(email = email, password = password, role = Role.USER)
    }

    /**
     * Generates new access token via the refresh token
     *
     * @param token String. The refresh token value.
     *
     * @return String. The new access token value.
     *
     * @throws UnsupportedJwtException If the JWT string is not valid.
     */
    fun refreshAccessToken(token: String): String {
        try {
            val extractedEmail = tokenService.extractEmail(token)

            return extractedEmail.let { email ->
                val currentUserDetails = userDetailsService.loadUserByUsername(email)
                val refreshTokenUserDetails = refreshTokenService.findUserDetailsByToken(token)

                if (!tokenService.isExpired(token) && currentUserDetails.username == refreshTokenUserDetails.username) {
                    generateAccessToken(currentUserDetails)
                } else {
                    throw UnsupportedJwtException("Invalid refresh token!")
                }
            }
        } catch (exception: UnsupportedJwtException) {
            throw UnsupportedJwtException("Invalid refresh token!")
        } catch (exception: JwtException) {
            throw UnsupportedJwtException("Invalid refresh token!")
        } catch (exception: UsernameNotFoundException) {
            throw UnsupportedJwtException("Invalid refresh token!")
        } catch (exception: NoSuchElementException) {
            throw UnsupportedJwtException("Invalid refresh token!")
        } catch (exception: IllegalArgumentException) {
            throw UnsupportedJwtException("Invalid refresh token!")
        }
    }

    /**
     * Logging out the user.
     *
     * @param authHeader The Auth header String. The token with token type.
     *
     * @throws UnsupportedJwtException in case the token is invalid.
     * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with a different value from that found in the persistence store. Also thrown if the entity is assumed to be present but does not exist in the database.
     */
    fun logout(authHeader: String?) {
        if (!TokenUtils.isStartWithTokenType(authHeader)) {
            throw UnsupportedJwtException("Invalid Token!")
        }

        val token = TokenUtils.extractTokenValue(authHeader!!)

        try {
            expiredTokenService.save(token)
        } catch (exception: IllegalArgumentException) {
            throw UnsupportedJwtException("Invalid Token!")
        }
    }

    /**
     * Generates the redis key for saving the otp
     *
     * @param email String
     *
     * @return String
     */
    private fun getOTPCacheKey(email: String): String = "$email:otp"

    /**
     * Generates a random otp
     *
     * @return String.
     */
    private fun generateOTP() =
        (1..authenticationProperties.otpLength)
            .map { Random.nextInt(0, 10) }
            .joinToString("")

    /**
     * Gets the mail content for the otp
     *
     * @param email String. The email address.
     * @param otp String. The otp value.
     *
     * @return Mail.
     */
    private fun getOTPMail(
        email: String,
        otp: String,
    ) = Mail(
        to = email,
        subject = "${applicationContext.applicationName} Email Verification",
        content = "<p>Verification Code: $otp</p>",
    )

    /**
     * Generates a JWT token for the user.
     *
     * @param user UserDetails.
     *
     * @return String.
     */
    private fun generateAccessToken(user: UserDetails) =
        tokenService.generate(
            userDetails = user,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration),
        )
}
