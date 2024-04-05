package mediaManager.refreshToken

import mediaManager.auth.JWTProperties
import mediaManager.auth.TokenService
import mediaManager.user.CustomUserDetailsService
import mediaManager.user.UserService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant
import java.util.Date

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userService: UserService,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val jwtProperties: JWTProperties,
) {
    fun findUserDetailsByToken(token: String): UserDetails {
        val refreshToken = refreshTokenRepository.findByToken(token).get()

        return userDetailsService.loadUserByUsername(refreshToken.user.email)
    }

    fun generate(userDetails: UserDetails): String {
        val user = userService.findByEmail(userDetails.username).get()

        val currentRefreshToken = refreshTokenRepository.findByUser(user)

        return if (currentRefreshToken.isPresent) {
            currentRefreshToken.get().token
        } else {
            refreshTokenRepository.deleteAllByUserId(user)
            val token =
                tokenService.generate(
                    userDetails = userDetails,
                    expirationDate = Date(System.currentTimeMillis() + jwtProperties.refreshTokenExpiration),
                )
            refreshTokenRepository.save(RefreshToken(token = token, user = user))
            token
        }
    }

    @Scheduled(fixedRateString = "604800000") // Weekly
    fun cleanupTokens() {
        val expirationThreshold = Instant.now().minusMillis(jwtProperties.accessTokenExpiration)
        refreshTokenRepository.deleteByCleanup(Timestamp.from(expirationThreshold))
    }
}
