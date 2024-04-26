package mediaManager.validations

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import mediaManager.auth.AuthenticationProperties
import java.util.regex.Pattern
import kotlin.reflect.KClass

class OTPValidator(authenticationProperties: AuthenticationProperties) : ConstraintValidator<OTP, String> {
    private val otpPattern = "^\\d{${authenticationProperties.otpLength}}$"

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext,
    ): Boolean {
        if (value.isNullOrEmpty()) {
            return false
        }

        return Pattern.matches(otpPattern, value)
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
@Constraint(validatedBy = [OTPValidator::class])
annotation class OTP(
    val message: String = "Invalid OTP Format!.",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
