package mediaManager.refreshToken

import jakarta.transaction.Transactional
import mediaManager.user.User
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.sql.Timestamp
import java.util.Optional

interface RefreshTokenRepository : CrudRepository<RefreshToken, Long> {
    @Modifying
    @Transactional
    @Query("Update RefreshToken SET deletedAt = CURRENT_TIMESTAMP WHERE user = ?1 AND deletedAt IS NULL")
    fun deleteAllByUserId(user: User)

    @Query("SELECT r FROM RefreshToken r WHERE r.token = ?1 AND r.deletedAt IS NULL")
    fun findByToken(token: String): Optional<RefreshToken>

    @Query("SELECT r FROM RefreshToken r WHERE r.user = ?1 AND r.deletedAt IS NULL ORDER BY r.createdAt DESC LIMIT 1")
    fun findByUser(user: User): Optional<RefreshToken>

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken SET deletedAt = CURRENT_TIMESTAMP WHERE token = ?1 AND deletedAt IS NULL")
    fun deleteByToken(token: String)

    @Modifying
    @Transactional
    @Query("UPDATE RefreshToken SET deletedAt = CURRENT_TIMESTAMP WHERE createdAt < ?1 AND deletedAt IS NULL")
    fun deleteByCleanup(cleanupThreshold: Timestamp)
}
