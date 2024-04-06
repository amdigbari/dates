package mediaManager.restAPI

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class HttpException(
    val message: String,
) {
    val status = false

    companion object {
        /**
         * @return ResponseEntity of HttpException with message and given status code
         */
        private fun error(
            exception: Exception,
            status: HttpStatus,
        ): ResponseEntity<HttpException> =
            ResponseEntity.status(status).body(HttpException(message = exception.message ?: status.reasonPhrase))

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
         * @return ResponseEntity of HttpException with message and status code 422
         */
        fun unprocessableEntity(exception: Exception): ResponseEntity<HttpException> = error(exception, HttpStatus.UNPROCESSABLE_ENTITY)

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
