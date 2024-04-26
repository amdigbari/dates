package mediaManager.profile

import mediaManager.user.User
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository
import java.util.Optional

interface ProfileRepository : CrudRepository<Profile, Long> {
    @Query("SELECT p FROM Profile p WHERE p.user = ?1")
    fun findByUser(user: User): Optional<Profile>
}
