package mediaManager.expiredToken

import mediaManager.auth.JWTProperties
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.Instant

@Service
class ExpiredTokenService(
    private val expiredTokenRepository: ExpiredTokenRepository,
    private val jwtProperties: JWTProperties,
) {
    @Scheduled(fixedRateString = "86400000") // Daily
    fun cleanupTokens() {
        val expirationThreshold = Instant.now().minusMillis(jwtProperties.accessTokenExpiration)
        expiredTokenRepository.deleteByCleanup(Timestamp.from(expirationThreshold))
    }

    fun save(token: String): ExpiredToken = expiredTokenRepository.save(ExpiredToken(token = token))

    fun findByToken(token: String) = expiredTokenRepository.findByToken(token)
}
