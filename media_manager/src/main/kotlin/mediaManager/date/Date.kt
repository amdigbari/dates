package mediaManager.date

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import jakarta.persistence.Temporal
import jakarta.persistence.TemporalType
import mediaManager.date.dateAsset.DateAsset
import mediaManager.date.datePartner.DatePartner
import mediaManager.user.User

@Entity
@Table(name = "dates")
data class Date(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0, // Added the 0, so I can create instance without error. It will be overwritten by DB.
    @OneToMany(mappedBy = "date", cascade = [CascadeType.ALL])
    val datesPartners: MutableSet<DatePartner> = mutableSetOf(),
    @OneToMany(mappedBy = "date", cascade = [CascadeType.ALL])
    val dateAssets: MutableSet<DateAsset> = mutableSetOf(),
    @Column(name = "short_story", nullable = false)
    val shortStory: String,
    @Column(nullable = false)
    @Temporal(TemporalType.DATE)
    val date: java.sql.Date,
) {
    val partners: Set<User>
        get() = datesPartners.map { it.user }.toSet()
}
