package mediaManager.restAPI

import io.jsonwebtoken.JwtException
import io.jsonwebtoken.UnsupportedJwtException
import mediaManager.exceptions.TooManyAttemptsException
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.mail.MailException
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.userdetails.UsernameNotFoundException
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

    @ExceptionHandler(IllegalArgumentException::class, BadCredentialsException::class, HttpMessageNotReadableException::class)
    fun handleUnprocessableEntityException(exception: Exception): ResponseEntity<HttpException> =
        HttpException.unprocessableEntity(exception)

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
