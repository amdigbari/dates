package mediaManager.auth

import mediaManager.auth.dtos.login.LoginRequestDto
import mediaManager.auth.dtos.login.LoginResponseDto
import mediaManager.auth.dtos.refreshToken.RefreshTokenRequestDto
import mediaManager.auth.dtos.refreshToken.RefreshTokenResponseDto
import mediaManager.auth.dtos.register.RegisterRequestDto
import mediaManager.auth.dtos.sendOtp.SendOTPRequestDto
import mediaManager.exceptions.ExpiredException
import mediaManager.exceptions.TooManyAttemptsException
import mediaManager.restAPI.HttpResponse
import mediaManager.user.User
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
class AuthenticationController(val authenticationService: AuthenticationService) {
    @PostMapping("/send-otp")
    fun sendOtp(
        @RequestBody request: SendOTPRequestDto,
    ): ResponseEntity<HttpResponse<Boolean>> {
        try {
            authenticationService.sendOTP(request.email)
        } catch (error: TooManyAttemptsException) {
            throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, error.message)
        }

        return HttpResponse.ok(true)
    }

    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequestDto,
    ): ResponseEntity<HttpResponse<LoginResponseDto>> = HttpResponse.ok(authenticationService.login(loginRequest))

    @PostMapping("/register")
    fun register(
        @RequestBody request: RegisterRequestDto,
//        TODO: Add validation for fields
    ): ResponseEntity<HttpResponse<User>> {
        try {
            val user = authenticationService.register(email = request.email, otp = request.otp, password = request.password)
            return HttpResponse.created(user)
        } catch (error: ExpiredException) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "OTP is incorrect or expired!")
        } catch (error: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "OTP is incorrect or expired!")
        }
    }

    @PostMapping("/refresh-token")
    fun refreshToken(
        @RequestBody request: RefreshTokenRequestDto,
    ): ResponseEntity<HttpResponse<RefreshTokenResponseDto>> =
        HttpResponse.ok(authenticationService.refreshAccessToken(request.token).mapToTokenResponse(request.token))

    @DeleteMapping("/logout")
    fun logout(
        @RequestHeader("Authorization") authHeader: String,
    ): ResponseEntity<HttpResponse<Boolean>> {
        authenticationService.logout(authHeader)
        return HttpResponse.ok(true)
    }

    /**
     * Extension function to Generate RefreshTokenDto. Should call on the access token
     *
     * @param refreshToken String.
     */
    private fun String.mapToTokenResponse(refreshToken: String): RefreshTokenResponseDto =
        RefreshTokenResponseDto(
            accessToken = this,
            refreshToken = refreshToken,
        )
}
