package mediaManager.user

import mediaManager.exceptions.CustomIllegalArgumentException
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val messageSource: MessageSource,
) {
    /**
     * Creates User and save it to database if the email is not exists yet.
     *
     * @param email String.
     * @param password String. Be aware that the return user will have encoded password stored in the database.
     *
     * @throws CustomIllegalArgumentException with fieldName="email" in case email exists.
     * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with a different value from that found in the persistence store. Also thrown if the entity is assumed to be present but does not exist in the database.
     */
    fun createUser(
        email: String,
        password: String,
    ): User {
        try {
            this.findByEmail(email)
            throw CustomIllegalArgumentException(
                messageSource
                    .getMessage("user.email-already-exists", null, LocaleContextHolder.getLocale())
                    ?: "An account exists with this email!.",
                "email",
            )
        } catch (exception: NoSuchElementException) {
            val user = User(email = email, password = passwordEncoder.encode(password))
            userRepository.save(user)
            return user
        }
    }

    /**
     * Fetch User from database by ID.
     *
     * @param id Long.
     *
     * @throws NoSuchElementException in case id not found in the database.
     */
    fun findById(id: Long): User {
        return userRepository.findById(id).get()
    }

    /**
     * Fetch User from database by email.
     *
     * @param email String.
     *
     * @throws NoSuchElementException in case email not found in the database.
     */
    fun findByEmail(email: String): User = userRepository.findByEmail(email).get()
}
