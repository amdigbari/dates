package mediaManager.user

import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service

typealias ApplicationUser = mediaManager.user.User

@Service
class CustomUserDetailsService(private val userRepository: UserRepository, private val messageSource: MessageSource) : UserDetailsService {
    /**
     * Locates the user based on the username which is email.
     * @param username the email value of the user.
     *
     * @return a fully populated user record (never null)
     * @throws UsernameNotFoundException in case email address is not found
     */
    override fun loadUserByUsername(username: String): UserDetails {
        try {
            return userRepository.findByEmail(username).get().mapToUserDetails()
        } catch (error: NoSuchElementException) {
            throw UsernameNotFoundException(
                messageSource.getMessage("user.email-not-fount", null, LocaleContextHolder.getLocale()) ?: "Email not Found!",
            )
        }
    }

    /**
     * ApplicationUser extension function to convert ApplicationUser to UserDetails
     *
     * @return UserDetails
     */
    private fun ApplicationUser.mapToUserDetails(): UserDetails =
        User.builder()
            .username(this.email)
            .password(this.password)
            .build()
}
