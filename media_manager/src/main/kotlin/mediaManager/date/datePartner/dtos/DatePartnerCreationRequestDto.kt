package mediaManager.date.datePartner.dtos

import jakarta.validation.constraints.DecimalMax
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.Size

data class DatePartnerCreationRequestDto(
    @field:NotNull
    @field:Positive
    val userId: Long,
    @field:Size(min = 10, max = 500)
    val story: String? = null,
    @field:DecimalMin("1.0")
    @field:DecimalMax("10.0")
    val rate: Double? = null,
)
