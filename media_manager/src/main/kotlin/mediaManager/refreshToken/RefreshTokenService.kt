package mediaManager.refreshToken

import mediaManager.auth.JWTProperties
import mediaManager.auth.TokenService
import mediaManager.user.CustomUserDetailsService
import mediaManager.user.UserService
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.DateTimeException
import java.time.Instant
import java.util.Date

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userService: UserService,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JWTProperties,
    private val messageSource: MessageSource,
) {
    /**
     * Fetch UserDetails based on the JWT token value.
     *
     * @param token String. JWT token value.
     *
     * @return UserDetails.
     *
     * @throws NoSuchElementException in case token not found in the database.
     */
    fun findUserDetailsByToken(token: String): UserDetails {
        try {
            val refreshToken = refreshTokenRepository.findByToken(token).get()

            return userDetailsService.loadUserByUsername(refreshToken.user.email)
        } catch (exception: UsernameNotFoundException) {
            refreshTokenRepository.deleteByToken(token)

            throw NoSuchElementException(
                messageSource
                    .getMessage("refresh-token.not-exists", null, LocaleContextHolder.getLocale())
                    ?: "refresh token not exists.",
            )
        }
    }

    /**
     * Generates a refresh token for user and save it to database.
     *
     * @param userDetails The UserDetails instance of the user. See UserDetails.
     * @return The refresh token value generated via TokenService.
     * @throws NoSuchElementException in case user email not found
     * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with a different value from that found in the persistence store. Also thrown if the entity is assumed to be present but does not exist in the database.
     */
    fun generate(userDetails: UserDetails): String {
        val user = userService.findByEmail(userDetails.username)

        val currentRefreshToken = refreshTokenRepository.findByUser(user)

        if (currentRefreshToken.isPresent) {
            val token = currentRefreshToken.get().token
            val isCurrentTokenExpired = tokenService.isExpired(token)

            if (!isCurrentTokenExpired) {
                return token
            }
        }

        refreshTokenRepository.deleteAllByUserId(user)
        val token =
            tokenService.generate(
                userDetails = userDetails,
                expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration),
            )
        refreshTokenRepository.save(RefreshToken(token = token, user = user))

        return token
    }

    /**
     * Clean-ups the expired refresh tokens weekly.
     *
     * @throws DateTimeException – if the result exceeds the maximum or minimum instant
     * @throws ArithmeticException – if numeric overflow occurs
     */
    @Scheduled(fixedRateString = "604800000") // Weekly
    fun cleanupTokens() {
        val expirationThreshold = Instant.now().minusMillis(jwtProperties.accessTokenExpiration)
        refreshTokenRepository.deleteByCleanup(Timestamp.from(expirationThreshold))
    }
}
