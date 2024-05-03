package mediaManager.restAPI

import jakarta.validation.constraints.AssertFalse
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class UnprocessableEntityFieldError(val fieldName: String, val errors: List<String>)

data class HttpException(
    @field:NotNull
    val message: String,
    @field:NotNull
    val errors: List<UnprocessableEntityFieldError>? = null,
) {
    // This value should be present on response.
    @field:NotNull
    @field:AssertFalse
    val status = false

    companion object {
        /**
         * @return ResponseEntity of HttpException with message and given status code
         */
        private fun error(
            exception: Exception,
            status: HttpStatus,
            errors: List<UnprocessableEntityFieldError>? = null,
        ): ResponseEntity<HttpException> {
            if (status == HttpStatus.UNPROCESSABLE_ENTITY && errors == null) {
                throw IllegalArgumentException("The field errors is required when the status is UNPROCESSABLE_ENTITY(422)")
            }

            return ResponseEntity.status(status).body(HttpException(message = exception.message ?: status.reasonPhrase, errors = errors))
        }

        /**
         * @return ResponseEntity of HttpException with message and status code 401
         */
        fun unauthorized(exception: Exception): ResponseEntity<HttpException> = error(exception, HttpStatus.UNAUTHORIZED)

        /**
         * @return ResponseEntity of HttpException with message and status code 403
         */
        fun forbidden(exception: Exception): ResponseEntity<HttpException> = error(exception, HttpStatus.FORBIDDEN)

        /**
         * @return ResponseEntity of HttpException with message and status code 404
         */
        fun notFound(exception: Exception): ResponseEntity<HttpException> = error(exception, HttpStatus.NOT_FOUND)

        /**
         * @param exception Any Exception
         * @param errors This param is required
         * and must an object with keys as field names with error
         * and the value of them is the message of the field error
         *
         * @return ResponseEntity of HttpException with message and status code 422
         */
        fun unprocessableEntity(
            exception: Exception,
            errors: List<UnprocessableEntityFieldError>,
        ): ResponseEntity<HttpException> = error(exception = exception, status = HttpStatus.UNPROCESSABLE_ENTITY, errors = errors)

        /**
         * @return ResponseEntity of HttpException with message and status code 429
         */
        fun tooManyRequests(exception: Exception): ResponseEntity<HttpException> = error(exception, HttpStatus.TOO_MANY_REQUESTS)

        /**
         * @return ResponseEntity of HttpException with message and status code 500
         */
        fun internalServerError(exception: Exception): ResponseEntity<HttpException> = error(exception, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
