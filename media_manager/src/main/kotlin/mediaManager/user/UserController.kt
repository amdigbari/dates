package mediaManager.user

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
    fun list(): MutableList<User> = userService.findAll().toMutableList()

    @GetMapping("/{id}")
    fun get(
        @PathVariable id: Long,
    ): User = userService.findById(id).get()
}
