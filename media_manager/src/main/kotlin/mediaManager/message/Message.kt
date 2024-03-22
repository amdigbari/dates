@file:Suppress("ktlint:standard:no-wildcard-imports")

package mediaManager.message

import jakarta.persistence.*

@Entity
@Table(name = "message")
data class Message(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    @Column(nullable = false)
    val text: String,
)
