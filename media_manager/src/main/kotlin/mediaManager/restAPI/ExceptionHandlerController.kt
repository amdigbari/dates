package mediaManager.restAPI

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.UnsupportedJwtException
import mediaManager.exceptions.CustomIllegalArgumentException
import mediaManager.exceptions.CustomMethodArgumentNotValidException
import mediaManager.exceptions.TooManyAttemptsException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.ResponseEntity
import org.springframework.mail.MailException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.validation.FieldError
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.DateTimeException

@ControllerAdvice
class ExceptionHandlerController {
    @ExceptionHandler(
        UnsupportedJwtException::class,
        JwtException::class,
    )
    fun handleUnauthorizedException(exception: DisabledException): ResponseEntity<HttpException> = HttpException.unauthorized(exception)

    @ExceptionHandler(
        DisabledException::class,
        LockedException::class,
        AccessDeniedException::class,
    )
    fun handleForbiddenException(exception: DisabledException): ResponseEntity<HttpException> = HttpException.forbidden(exception)

    @ExceptionHandler(UsernameNotFoundException::class, NoSuchElementException::class)
    fun handleNotFoundException(exception: Exception): ResponseEntity<HttpException> = HttpException.notFound(exception)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleInvalidArgumentException(exception: MethodArgumentNotValidException): ResponseEntity<HttpException> {
        val groupedErrors = HashMap<String, List<String>>()

        exception.bindingResult.allErrors.forEach { error ->
            val fieldName = (error as FieldError).field
            val errorMessage = error.getDefaultMessage()
            if (errorMessage != null) {
                val fieldErrors = groupedErrors[fieldName]?.toMutableList() ?: mutableListOf()
                fieldErrors.add(errorMessage)
                groupedErrors[fieldName] = fieldErrors
            }
        }

        val errors: List<UnprocessableEntityFieldError> =
            groupedErrors.map { error ->
                UnprocessableEntityFieldError(
                    error.key,
                    error.value,
                )
            }

        return HttpException.unprocessableEntity(
            CustomMethodArgumentNotValidException(exception.parameter, exception.bindingResult, "Form parse error!"),
            errors,
        )
    }

    @ExceptionHandler(CustomIllegalArgumentException::class)
    fun handleCustomIllegalArgumentException(exception: CustomIllegalArgumentException): ResponseEntity<HttpException> =
        HttpException.unprocessableEntity(
            exception,
            listOf(
                UnprocessableEntityFieldError(
                    fieldName = exception.fieldName,
                    errors = listOf(exception.message ?: "Field is not valid!"),
                ),
            ),
        )

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(exception: BadCredentialsException): ResponseEntity<HttpException> =
        HttpException.unprocessableEntity(
            BadCredentialsException("Invalid Credentials"),
            errors = emptyList(),
        )

    @ExceptionHandler(TooManyAttemptsException::class)
    fun handleTooManyRequestsException(exception: Exception): ResponseEntity<HttpException> = HttpException.tooManyRequests(exception)

    @ExceptionHandler(
        OptimisticLockingFailureException::class,
        MailException::class,
        DateTimeException::class,
        ArithmeticException::class,
        Exception::class,
    )
    fun handleServerException(exception: Exception): ResponseEntity<HttpException> = HttpException.internalServerError(exception)
}
