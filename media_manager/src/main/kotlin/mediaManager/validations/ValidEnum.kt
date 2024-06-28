package mediaManager.validations

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import kotlin.reflect.KClass

class EnumValidator : ConstraintValidator<ValidEnum, String> {
    private lateinit var enumClass: Class<out Enum<*>>

    override fun initialize(annotation: ValidEnum) {
        enumClass = annotation.enumClass.java
    }

    override fun isValid(
        value: String?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        return value != null && enumClass.enumConstants.any { it.name == value }
    }
}

@Target(AnnotationTarget.FIELD, AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [EnumValidator::class])
annotation class ValidEnum(
    val enumClass: KClass<out Enum<*>>,
    val message: String = "Invalid value for enum",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
