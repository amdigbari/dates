package mediaManager.date

import jakarta.validation.Valid
import mediaManager.date.datePartner.dtos.DatePartnerCreationRequestDto
import mediaManager.date.dtos.DateCreationRequestDto
import mediaManager.restAPI.HttpResponse
import mediaManager.user.User
import mediaManager.user.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/dates")
class DateController(private val dateService: DateService, private val userService: UserService) {
    @PostMapping("/")
    fun createDate(
        @Valid @RequestBody dto: DateCreationRequestDto,
        @RequestHeader("Authorization") authHeader: String,
    ): ResponseEntity<HttpResponse<Date>> {
        val user = userService.findByAuthHeader(authHeader)
        // TODO: Check All the partners are in following list
        return HttpResponse.created(dateService.createDate(dto.addUser(user)))
    }

    private fun DateCreationRequestDto.addUser(user: User): DateCreationRequestDto {
        return DateCreationRequestDto(
            shortStory = this.shortStory,
            assets = this.assets,
            partners = listOf(DatePartnerCreationRequestDto(userId = user.id), *this.partners.toTypedArray()),
        )
    }
}
