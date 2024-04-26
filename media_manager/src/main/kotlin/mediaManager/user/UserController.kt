package mediaManager.user

import mediaManager.profile.Profile
import mediaManager.profile.ProfileResponseDto
import mediaManager.profile.ProfileService
import mediaManager.restAPI.HttpResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val profileService: ProfileService,
) {
    @GetMapping("/info")
    fun get(
        @RequestHeader("Authorization") authHeader: String?,
    ): ResponseEntity<HttpResponse<ProfileResponseDto>> =
        HttpResponse.ok(
            profileService
                .findByAuthHeader(authHeader)
                .mapToProfileResponseDto(),
        )

    private fun Profile.mapToProfileResponseDto(): ProfileResponseDto =
        ProfileResponseDto(
            id = this.id,
            email = this.user.email,
            fullName = this.fullName,
            nickname = this.nickname,
        )
}
