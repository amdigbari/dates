package mediaManager.validations

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.springframework.web.multipart.MultipartFile
import kotlin.reflect.KClass

class FileValidator : ConstraintValidator<File, Any> {
    private lateinit var allowedTypes: Array<String>

    override fun initialize(annotation: File) {
        allowedTypes = annotation.allowedTypes
    }

    override fun isValid(
        value: Any?,
        context: ConstraintValidatorContext?,
    ): Boolean {
        return when (value) {
            is MultipartFile -> isValidFile(value)
            is List<*> -> value.all { it is MultipartFile && isValidFile(it) }
            else -> false
        }
    }

    private fun isValidFile(file: MultipartFile?): Boolean {
        if (file == null || file.isEmpty) {
            return false
        }
        val fileType = file.contentType ?: return false
        return allowedTypes.contains(fileType)
    }
}

@Target(AnnotationTarget.VALUE_PARAMETER, AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [FileValidator::class])
annotation class File(
    val message: String = "Invalid file type",
    val allowedTypes: Array<String> = ["application/pdf"],
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
)
