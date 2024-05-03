package mediaManager.restAPI

import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.NotNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class HttpResponse<T : Any>(
    @field:NotNull
    val data: T,
) {
    @field:NotNull
    @field:AssertTrue
    val status = true

    companion object {
        /**
         * @return ResponseEntity of HttpResponse of body
         */
        fun <T : Any> ok(body: T): ResponseEntity<HttpResponse<T>> = ResponseEntity.ok(HttpResponse(data = body))

        /**
         * @return ResponseEntity of HttpResponse of body with status code 201
         */
        fun <T : Any> created(body: T): ResponseEntity<HttpResponse<T>> =
            ResponseEntity
                .status(HttpStatus.CREATED)
                .body(HttpResponse(data = body))
    }
}
