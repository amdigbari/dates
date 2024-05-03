package mediaManager.profile

import jakarta.validation.constraints.NotNull

data class ProfileResponseDto(
    @field:NotNull
    val id: Long,
    @field:NotNull
    val email: String,
    @field:NotNull
    val fullName: String,
    @field:NotNull
    val nickname: String?,
)
