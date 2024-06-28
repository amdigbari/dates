package mediaManager.date.datePartner

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import mediaManager.date.Date
import mediaManager.user.User

@Entity
@Table(name = "dates_partners")
data class DatePartner(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0, // Added the 0, so I can create instance without error. It will be overwritten by DB.
    @ManyToOne
    @JoinColumn(name = "user_id")
    val user: User,
    @ManyToOne
    @JoinColumn(name = "date_id")
    val date: Date,
    @Column
    val rate: Double = 0.0,
    @Column
    val story: String = "",
)
