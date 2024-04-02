package mediaManager.user

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    @GetMapping
    fun list(): MutableList<User> = userService.findAll().toMutableList()

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
    ): User {
        try {
            return userService.findById(id).get()
        } catch (error: NoSuchElementException) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User does not found!")
        }
    }
}
