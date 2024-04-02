package mediaManager.auth

import mediaManager.auth.dtos.login.LoginRequestDto
import mediaManager.auth.dtos.login.LoginResponseDto
import mediaManager.auth.dtos.refreshToken.RefreshTokenRequestDto
import mediaManager.auth.dtos.refreshToken.RefreshTokenResponseDto
import mediaManager.user.User
import org.springframework.dao.OptimisticLockingFailureException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/auth")
class AuthController(val authenticationService: AuthenticationService) {
    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequestDto,
    ): LoginResponseDto =
        authenticationService.login(
            loginRequest,
        ) ?: throw BadCredentialsException("Incorrect Email or Password!")

    @PostMapping("/register")
    fun register(
        @RequestBody user: User,
    ): User? {
//        TODO: Add serializer to avoid passing the user Role
        return authenticationService.register(
            user,
        ) ?: throw ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "The user already exists")
    }

    @PostMapping("/refresh-token")
    fun refreshToken(
        @RequestBody request: RefreshTokenRequestDto,
    ): RefreshTokenResponseDto =
        authenticationService.refreshAccessToken(request.token)
            ?.mapToTokenResponse(request.token)
            ?: throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Refresh Token!")

    @DeleteMapping("/logout")
    fun logout(
        @RequestHeader("Authorization") authHeader: String,
    ): ResponseEntity<Boolean> {
        try {
            authenticationService.logout(authHeader)
            return ResponseEntity.noContent().build()
        } catch (error: IllegalArgumentException) {
            throw ResponseStatusException(HttpStatus.FORBIDDEN, "Invalid Token!")
        } catch (error: OptimisticLockingFailureException) {
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Server Error!")
        }
    }

    private fun String.mapToTokenResponse(refreshToken: String): RefreshTokenResponseDto =
        RefreshTokenResponseDto(
            accessToken = this,
            refreshToken = refreshToken,
        )
}
