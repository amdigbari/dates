package mediaManager.auth

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.UnsupportedJwtException
import mediaManager.auth.dtos.login.LoginRequestDto
import mediaManager.auth.dtos.login.LoginResponseDto
import mediaManager.auth.dtos.register.RegisterRequestDto
import mediaManager.exceptions.CustomIllegalArgumentException
import mediaManager.exceptions.TooManyAttemptsException
import mediaManager.expiredToken.ExpiredTokenService
import mediaManager.notification.mail.Mail
import mediaManager.notification.mail.MailService
import mediaManager.profile.ProfileService
import mediaManager.redis.RedisContext
import mediaManager.redis.RedisService
import mediaManager.refreshToken.RefreshTokenService
import mediaManager.user.CustomUserDetailsService
import mediaManager.user.UserService
import org.springframework.context.ApplicationContext
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
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
    private val profileService: ProfileService,
    private val refreshTokenService: RefreshTokenService,
    private val expiredTokenService: ExpiredTokenService,
    private val redisService: RedisService,
    private val mailService: MailService,
    private val jwtProperties: JWTProperties,
    private val authenticationProperties: AuthenticationProperties,
    private val applicationContext: ApplicationContext,
    private val messageSource: MessageSource,
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
            throw TooManyAttemptsException(
                messageSource.getMessage(
                    "auth.send-otp.try-again",
                    arrayOf(authenticationProperties.otpExpiration),
                    "Try again in ${authenticationProperties.otpExpiration} seconds!",
                    LocaleContextHolder.getLocale(),
                )
                    ?: "Try again in ${authenticationProperties.otpExpiration} seconds!",
            )
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

        return generateLoginResponseDto(loginRequest.email)
    }

    /**
     * Registers a new user and checks the otp of it
     *
     * @param dto RegisterRequestDto. A class including required data to create User and Profile.
     *
     * @throws CustomIllegalArgumentException with fieldName="otp"||"email", in case otp is not valid or email already exists.
     * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with a different value from that found in the persistence store. Also thrown if the entity is assumed to be present but does not exist in the database.
     */
    fun register(dto: RegisterRequestDto): LoginResponseDto {
        val invalidOTPException =
            CustomIllegalArgumentException(
                messageSource
                    .getMessage("auth.register.invalid-otp", null, LocaleContextHolder.getLocale())
                    ?: "OTP is not valid!.",
                "otp",
            )

        val userOTP =
            redisService.get(RedisContext.AUTH, getOTPCacheKey(dto.email)) ?: throw invalidOTPException
        if (userOTP != dto.otp) {
            throw invalidOTPException
        }
//         Deleting the OTP from Redis
        redisService.delete(RedisContext.AUTH, getOTPCacheKey(dto.email))

//        Creating User and Profile
        val user = userService.createUser(email = dto.email, password = dto.password)
        profileService.createProfile(user = user, fullName = dto.fullName, nickname = dto.nickname)

        return generateLoginResponseDto(user.email)
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
        val invalidRefreshTokenException =
            UnsupportedJwtException(
                messageSource
                    .getMessage("auth.refresh-token.invalid-refresh-token", null, LocaleContextHolder.getLocale())
                    ?: "Invalid refresh token!.",
            )

        try {
            val extractedEmail = tokenService.extractEmail(token)

            return extractedEmail.let { email ->
                val currentUserDetails = userDetailsService.loadUserByUsername(email)
                val refreshTokenUserDetails = refreshTokenService.findUserDetailsByToken(token)

                if (!tokenService.isExpired(token) && currentUserDetails.username == refreshTokenUserDetails.username) {
                    generateAccessToken(currentUserDetails)
                } else {
                    throw invalidRefreshTokenException
                }
            }
        } catch (exception: UnsupportedJwtException) {
            throw invalidRefreshTokenException
        } catch (exception: JwtException) {
            throw invalidRefreshTokenException
        } catch (exception: UsernameNotFoundException) {
            throw invalidRefreshTokenException
        } catch (exception: NoSuchElementException) {
            throw invalidRefreshTokenException
        } catch (exception: IllegalArgumentException) {
            throw invalidRefreshTokenException
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
        val invalidTokenException =
            UnsupportedJwtException(
                messageSource.getMessage(
                    "auth.invalid-token",
                    null,
                    "Invalid Token!",
                    LocaleContextHolder.getLocale(),
                ),
            )

        if (!TokenUtils.isStartWithTokenType(authHeader)) {
            throw invalidTokenException
        }

        val token = TokenUtils.extractTokenValue(authHeader!!)

        try {
            expiredTokenService.save(token)
        } catch (exception: IllegalArgumentException) {
            throw invalidTokenException
        }
    }

    private fun generateLoginResponseDto(email: String): LoginResponseDto {
        val invalidCredentialsMessage =
            messageSource.getMessage(
                "auth.login.incorrect-credentials",
                null,
                "Incorrect Email or Password!",
                LocaleContextHolder.getLocale(),
            )

        try {
            val user = userDetailsService.loadUserByUsername(email)

            val accessToken = generateAccessToken(user)
            val refreshToken = refreshTokenService.generate(user)

            return LoginResponseDto(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        } catch (error: UsernameNotFoundException) {
            throw BadCredentialsException(invalidCredentialsMessage)
        } catch (error: IllegalArgumentException) {
            throw BadCredentialsException(invalidCredentialsMessage)
        } catch (error: NoSuchElementException) {
            throw BadCredentialsException(invalidCredentialsMessage)
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
        subject =
            messageSource.getMessage(
                "auth.otp-email.subject",
                arrayOf(applicationContext.applicationName),
                "${applicationContext.applicationName} Email Verification",
                LocaleContextHolder.getLocale(),
            ) ?: "${applicationContext.applicationName} Email Verification",
        content = "<p>${messageSource.getMessage(
            "auth.otp-email.content",
            arrayOf(otp),
            "Verification Code: $otp",
            LocaleContextHolder.getLocale(),
        )}</p>",
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
