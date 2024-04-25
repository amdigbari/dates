package mediaManager.profile

data class ProfileResponseDto(
    val id: Long,
    val email: String,
    val fullName: String,
    val nickname: String?,
)
