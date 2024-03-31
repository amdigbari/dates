package mediaManager.user

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.Optional

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    fun createUser(user: User): User? {
        val founded = this.findByEmail(user.email)

        return if (!founded.isPresent) {
            userRepository.save(
                user.copy(password = passwordEncoder.encode(user.password)),
            )
            user
        } else {
            null
        }
    }

    fun findById(id: Long): Optional<User> {
        return userRepository.findById(id)
    }

    fun findAll(): MutableList<User> = userRepository.findAll().toMutableList()

    fun findByEmail(email: String): Optional<User> = userRepository.findByEmail(email)

    fun deleteById(id: Long) {
        userRepository.deleteById(id)
    }
}
