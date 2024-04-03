package mediaManager.auth

import mediaManager.auth.dtos.login.LoginRequestDto
import mediaManager.auth.dtos.login.LoginResponseDto
import mediaManager.expiredToken.ExpiredTokenService
import mediaManager.refreshToken.RefreshTokenService
import mediaManager.user.CustomUserDetailsService
import mediaManager.user.User
import mediaManager.user.UserService
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date

@Service
class AuthenticationService(
    private val authManager: AuthenticationManager,
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val userService: UserService,
    private val refreshTokenService: RefreshTokenService,
    private val expiredTokenService: ExpiredTokenService,
    private val jwtProperties: JWTProperties,
) {
    fun login(loginRequest: LoginRequestDto): LoginResponseDto {
        authManager.authenticate(UsernamePasswordAuthenticationToken(loginRequest.email, loginRequest.password))

        val user = userDetailsService.loadUserByUsername(loginRequest.email)

        val accessToken = generateAccessToken(user)
        val refreshToken = refreshTokenService.generate(user)

        return if (refreshToken != null) {
            LoginResponseDto(
                accessToken = accessToken,
                refreshToken = refreshToken,
            )
        } else {
            throw BadCredentialsException("Incorrect Email or Password!")
        }
    }

    fun register(user: User): User {
        return userService.createUser(user)
    }

    fun refreshAccessToken(token: String): String {
        val extractedEmail = tokenService.extractEmail(token)

        return extractedEmail.let { email ->
            val currentUserDetails = userDetailsService.loadUserByUsername(email)
            val refreshTokenUserDetails = refreshTokenService.findUserDetailsByToken(token)

            if (!tokenService.isExpired(token) && currentUserDetails.username == refreshTokenUserDetails?.username) {
                generateAccessToken(currentUserDetails)
            } else {
                throw AccessDeniedException("Invalid refresh token!")
            }
        }
    }

    fun logout(authHeader: String?) {
        if (!TokenUtils.isStartWithTokenType(authHeader)) {
            throw IllegalArgumentException("Invalid Token!")
        }

        val token = TokenUtils.extractTokenValue(authHeader!!)

        expiredTokenService.save(token)
    }

    private fun generateAccessToken(user: UserDetails) =
        tokenService.generate(
            userDetails = user,
            expirationDate = Date(System.currentTimeMillis() + jwtProperties.accessTokenExpiration),
        )
}
