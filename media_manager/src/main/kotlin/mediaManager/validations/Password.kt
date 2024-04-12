package mediaManager.validations

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import java.util.regex.Pattern
import kotlin.reflect.KClass

class PasswordValidator : ConstraintValidator<Password, String> {
    private val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).{8,}\$"

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value.isNullOrEmpty()) {
            return false
        }

        return Pattern.matches(passwordPattern, value)
    }
}

/**
 * Custom password validator that match the input with the pattern
 *
 * Pattern specifies
 * - Password must have at least one lower and upper case characters
 * - Password must have at least one digit
 * - Password length must be between 8-20
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [PasswordValidator::class])
annotation class Password(
    val message: String =
        "Password must have at least one lower and one upper characters, " +
            "one digit, and must be between 8-20 length.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
