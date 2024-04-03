package mediaManager.auth

import mediaManager.auth.dtos.login.LoginRequestDto
import mediaManager.auth.dtos.login.LoginResponseDto
import mediaManager.auth.dtos.refreshToken.RefreshTokenRequestDto
import mediaManager.auth.dtos.refreshToken.RefreshTokenResponseDto
import mediaManager.user.User
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class AuthController(val authenticationService: AuthenticationService) {
    @PostMapping("/login")
    fun login(
        @RequestBody loginRequest: LoginRequestDto,
    ): LoginResponseDto =
        authenticationService.login(
            loginRequest,
        )

    @PostMapping("/register")
    fun register(
        @RequestBody user: User,
//        TODO: Add serializer to avoid passing the user Role
    ): User = authenticationService.register(user)

    @PostMapping("/refresh-token")
    fun refreshToken(
        @RequestBody request: RefreshTokenRequestDto,
    ): RefreshTokenResponseDto = authenticationService.refreshAccessToken(request.token).mapToTokenResponse(request.token)

    @DeleteMapping("/logout")
    fun logout(
        @RequestHeader("Authorization") authHeader: String,
    ): ResponseEntity<Boolean> {
        authenticationService.logout(authHeader)
        return ResponseEntity.noContent().build()
    }

    private fun String.mapToTokenResponse(refreshToken: String): RefreshTokenResponseDto =
        RefreshTokenResponseDto(
            accessToken = this,
            refreshToken = refreshToken,
        )
}
