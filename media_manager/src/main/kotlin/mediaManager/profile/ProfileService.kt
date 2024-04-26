package mediaManager.profile

import io.jsonwebtoken.UnsupportedJwtException
import mediaManager.auth.TokenService
import mediaManager.auth.TokenUtils
import mediaManager.exceptions.CustomIllegalArgumentException
import mediaManager.user.User
import mediaManager.user.UserService
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.stereotype.Service

@Service
class ProfileService(
    private val profileRepository: ProfileRepository,
    private val userService: UserService,
    private val tokenService: TokenService,
    private val messageSource: MessageSource,
) {
    /**
     * Creates Profile and save it to database if the user is not exists yet.
     *
     * @param user User. The user that will be associated with this profile.
     * @param fullName String. The fullName of the user. This will be used to search users.
     * @param nickname String. The nickname that the user prefer others to see him/her with.
     *
     * @throws CustomIllegalArgumentException with fieldName="email" in case email exists.
     * @throws OptimisticLockingFailureException when the entity uses optimistic locking and has a version attribute with a different value from that found in the persistence store. Also thrown if the entity is assumed to be present but does not exist in the database.
     */
    fun createProfile(
        user: User,
        fullName: String,
        nickname: String?,
    ): Profile {
        try {
            return profileRepository.save(Profile(user = user, fullName = fullName, nickname = nickname))
        } catch (exception: IllegalArgumentException) {
//            We can safely assume that the IllegalArgumentException only occurs in case we have duplicate user,
//            because the language is type-safe and the db has no length constraint
            throw CustomIllegalArgumentException(
                messageSource.getMessage(
                    "profile.duplicate-user",
                    null,
                    "Profile with this user already exists!.",
                    LocaleContextHolder.getLocale(),
                )
                    ?: "Profile with this user already exists!.",
                "user",
            )
        }
    }

    /**
     * Fetch Profile from database by ID.
     *
     * @param user User.
     *
     * @throws NoSuchElementException in case id not found in the database.
     */
    fun findByUser(user: User): Profile {
        return profileRepository.findByUser(user).get()
    }

    /**
     * Fetch Profile from database by authHeader.
     *
     * @param authHeader String. JWT Token Header with token type.
     *
     * @throws UnsupportedJwtException in case token is invalid or no user with provided token found!.
     */
    fun findByAuthHeader(authHeader: String?): Profile {
        if (!TokenUtils.isStartWithTokenType(authHeader)) {
            throw UnsupportedJwtException(
                messageSource.getMessage(
                    "auth.invalid-token",
                    null,
                    "Invalid Token!",
                    LocaleContextHolder.getLocale(),
                ),
            )
        }

        return findByUser(
            userService.findByEmail(
                tokenService.extractEmail(
                    TokenUtils.extractTokenValue(authHeader!!),
                ),
            ),
        )
    }
}
