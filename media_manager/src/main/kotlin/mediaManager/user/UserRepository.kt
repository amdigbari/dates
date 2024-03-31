package mediaManager.user

import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface UserRepository : CrudRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.email = ?1")
    fun findByEmail(email: String): Optional<User>
}
