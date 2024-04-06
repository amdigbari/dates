package mediaManager.expiredToken

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import java.sql.Timestamp
import java.time.Instant

/**
 * Holds the logged out access tokens that still are not expired.
 */
@Entity
@Table(name = "expired_tokens")
data class ExpiredToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0, // Added the 0, so I can create instance without error. It will be overwritten by DB.
    @Column(unique = true)
    val token: String,
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val createdAt: Timestamp = Timestamp.from(Instant.now()),
)
