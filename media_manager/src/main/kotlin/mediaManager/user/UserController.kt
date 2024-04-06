package mediaManager.user

import mediaManager.restAPI.HttpResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping
    fun list(): ResponseEntity<HttpResponse<MutableList<User>>> = HttpResponse.ok(userService.findAll().toMutableList())

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
    ): ResponseEntity<HttpResponse<User>> = HttpResponse.ok(userService.findById(id))
}
