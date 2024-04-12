package mediaManager.expiredToken

import mediaManager.auth.JWTProperties
import mediaManager.exceptions.CustomIllegalArgumentException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.sql.Timestamp
import java.time.DateTimeException
import java.time.Instant

@Service
class ExpiredTokenService(
    private val expiredTokenRepository: ExpiredTokenRepository,
    private val jwtProperties: JWTProperties,
) {
    /**
     * Creates a new row in the database for the token.
     *
     * @param token String.
     *
     * @return ExpiredToken. The saved Entity which is ExpiredToken.
     *
     * @throws CustomIllegalArgumentException with fieldName="email" in case email exists.
     * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with a different value from that found in the persistence store. Also thrown if the entity is assumed to be present but does not exist in the database.
     */
    fun save(token: String): ExpiredToken = expiredTokenRepository.save(ExpiredToken(token = token))

    /**
     * Fetch Entity based on the JWT token value.
     *
     * @param token String. JWT token value.
     *
     * @return ExpiredToken.
     *
     * @throws NoSuchElementException in case token not found in the database.
     */
    fun findByToken(token: String) = expiredTokenRepository.findByToken(token).get()

    /**
     * Clean-ups the expired tokens daily.
     *
     * @throws DateTimeException – if the result exceeds the maximum or minimum instant
     * @throws ArithmeticException – if numeric overflow occurs
     */
    @Scheduled(fixedRateString = "86400000") // Daily
    fun cleanupTokens() {
        val expirationThreshold = Instant.now().minusMillis(jwtProperties.accessTokenExpiration)
        expiredTokenRepository.deleteByCleanup(Timestamp.from(expirationThreshold))
    }
}
