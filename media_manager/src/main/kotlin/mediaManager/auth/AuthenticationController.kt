package mediaManager.auth

import mediaManager.auth.dtos.login.LoginRequestDto
import mediaManager.auth.dtos.login.LoginResponseDto
import mediaManager.auth.dtos.refreshToken.RefreshTokenRequestDto
import mediaManager.auth.dtos.refreshToken.RefreshTokenResponseDto
import mediaManager.auth.dtos.register.RegisterRequestDto
import mediaManager.auth.dtos.sendOtp.SendOTPRequestDto
import mediaManager.exceptions.ExpiredException
import mediaManager.exceptions.TooManyAttemptsException
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
    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequestDto,
    ): LoginResponseDto =
        authenticationService.login(
            loginRequest,
        )

    @PostMapping("/register")
    fun register(
        @RequestBody request: RegisterRequestDto,
//        TODO: Add validation for fields
    ): User {
        try {
            return authenticationService.register(request)
        } catch (error: ExpiredException) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "OTP is incorrect or expired!")
        } catch (error: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "OTP is incorrect or expired!")
        }
    }

    @PostMapping("/refresh-token")
    fun refreshToken(
        @RequestBody request: RefreshTokenRequestDto,
    ): RefreshTokenResponseDto = authenticationService.refreshAccessToken(request.token).mapToTokenResponse(request.token)

    @DeleteMapping("/logout")
    fun logout(
        @RequestHeader("Authorization") authHeader: String,
    ): ResponseEntity<Boolean> {
        authenticationService.logout(authHeader)
        return ResponseEntity.ok().build()
    }

    @PostMapping("/send-otp")
    fun sendOtp(
        @RequestBody request: SendOTPRequestDto,
    ): ResponseEntity<Boolean> {
        try {
            authenticationService.sendOTP(request.email)
        } catch (error: TooManyAttemptsException) {
            throw ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, error.message)
        }

        return ResponseEntity.ok().build()
    }

    private fun String.mapToTokenResponse(refreshToken: String): RefreshTokenResponseDto =
        RefreshTokenResponseDto(
            accessToken = this,
            refreshToken = refreshToken,
        )
}
