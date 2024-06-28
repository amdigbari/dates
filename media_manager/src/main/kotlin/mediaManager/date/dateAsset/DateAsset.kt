package mediaManager.date.dateAsset

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import mediaManager.date.Date

@Entity
@Table(name = "dates_assets")
data class DateAsset(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0, // Added the 0, so I can create instance without error. It will be overwritten by DB.
    @ManyToOne
    val date: Date,
    @Column(name = "object_key", nullable = false)
    val objectKey: String, // The path of asset in the S3 Storage.
    @Column(nullable = false)
    val type: DateAssetType,
)
