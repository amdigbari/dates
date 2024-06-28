package mediaManager.user

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import mediaManager.date.Date
import mediaManager.date.datePartner.DatePartner

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0, // Added the 0, so I can create instance without error. It will be overwritten by DB.
    @Column(nullable = false, unique = true)
    val email: String,
    @Column(nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    val password: String,
    @OneToMany(mappedBy = "user", cascade = [CascadeType.ALL])
    val datesPartners: MutableSet<DatePartner> = mutableSetOf(),
) {
    val dates: Set<Date>
        get() = datesPartners.map { it.date }.toSet()
}
