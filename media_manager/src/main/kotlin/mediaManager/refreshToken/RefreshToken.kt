package mediaManager.refreshToken

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import mediaManager.user.User
import org.hibernate.annotations.SQLDelete
import java.sql.Timestamp
import java.time.Instant

/**
 * Entity that holds the value of refresh tokens for each user.
 * The delete methodology of this table is soft.
 */
@Entity
@Table(name = "refresh_tokens")
@SQLDelete(sql = "Update refresh_tokens SET deleted_at = CURRENT_TIMESTAMP WHERE id=? AND deleted_at IS NULL")
data class RefreshToken(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0, // Added the 0, so I can create instance without error. It will be overwritten by DB.
    @Column(nullable = false, unique = true)
    val token: String,
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,
    @Column(name = "created_at", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    val createdAt: Timestamp = Timestamp.from(Instant.now()),
    @Column(name = "deleted_at", nullable = true)
    val deletedAt: Timestamp? = null,
)
