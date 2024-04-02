package mediaManager.expiredToken

import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.sql.Timestamp
import java.util.Optional

interface ExpiredTokenRepository : CrudRepository<ExpiredToken, Long> {
    @Query("SELECT e from ExpiredToken e WHERE e.token = ?1")
    fun findByToken(token: String): Optional<ExpiredToken>

    @Modifying
    @Transactional
    @Query("DELETE FROM ExpiredToken e WHERE e.createdAt < ?1")
    fun deleteByCleanup(cleanupThreshold: Timestamp)
}
