package mediaManager.auth

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import mediaManager.expiredToken.ExpiredTokenService
import mediaManager.user.CustomUserDetailsService
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JWTAuthenticationFilter(
    private val userDetailsService: CustomUserDetailsService,
    private val tokenService: TokenService,
    private val expiredTokenService: ExpiredTokenService,
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val authHeader: String? = request.getHeader("Authorization")

        if (!TokenUtils.isStartWithTokenType(authHeader)) {
            filterChain.doFilter(request, response)
            return
        }

        val jwtToken = TokenUtils.extractTokenValue(authHeader!!)
        val email = tokenService.extractEmail(jwtToken)
        val isTokenExpired = expiredTokenService.findByToken(jwtToken).isPresent

        if (!isTokenExpired && SecurityContextHolder.getContext().authentication == null) {
            val foundUser = userDetailsService.loadUserByUsername(email)

            if (tokenService.isValid(jwtToken, foundUser)) {
                this.updateContext(foundUser, request)
            }

            filterChain.doFilter(request, response)
        }

        filterChain.doFilter(request, response)
    }

    private fun updateContext(
        foundUser: UserDetails,
        request: HttpServletRequest,
    ) {
        val authToken = UsernamePasswordAuthenticationToken(foundUser, null, foundUser.authorities)
        authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = authToken
    }
}
