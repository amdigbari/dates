package mediaManager.auth

import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.Keys
import mediaManager.exceptions.CustomIllegalArgumentException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date

@Service
class TokenService(jwtProperties: JWTProperties) {
    private val secretKey =
        Keys.hmacShaKeyFor(
            jwtProperties.key.toByteArray(),
        )

    /**
     * Generates a JWT token for the User with the given expirationDate and additional claims.
     *
     * @param userDetails UserDetails.
     * @param expirationDate Date. The Date value of the token expiration.
     * @param additionalClaims Map. Can be anything.
     *
     * @return String. The token value.
     */
    fun generate(
        userDetails: UserDetails,
        expirationDate: Date,
        additionalClaims: Map<String, Any> = emptyMap(),
    ): String =
        Jwts.builder()
            .claims()
            .subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(expirationDate)
            .add(additionalClaims)
            .and()
            .signWith(secretKey)
            .compact()

    /**
     * Extracts the email from the token
     *
     * @param token String.
     *
     * @return String. The email.
     *
     * @throws UnsupportedJwtException If the JWT argument does not represent a signed Claims JWT.
     * @throws JwtException If the JWT string cannot be parsed or validated as required.
     * @throws CustomIllegalArgumentException with fieldName="token", If the JWT string is null or empty or only whitespace.
     */
    fun extractEmail(token: String): String = getAllClaims(token).subject

    /**
     * Checks whether the token is expired or not.
     *
     * @param token String.
     *
     * @return Boolean.
     *
     * @throws UnsupportedJwtException If the JWT argument does not represent a signed Claims JWT.
     * @throws JwtException If the JWT string cannot be parsed or validated as required.
     * @throws CustomIllegalArgumentException with fieldName="token", If the JWT string is null or empty or only whitespace.
     */
    fun isExpired(token: String): Boolean =
        getAllClaims(token)
            .expiration
            .before(Date(System.currentTimeMillis()))

    /**
     * Checks whether the token is valid or not, and also checks it via the user email.
     *
     * @param token String.
     * @param userDetails UserDetails
     *
     * @return Boolean.
     */
    fun isValid(
        token: String,
        userDetails: UserDetails,
    ): Boolean {
        return try {
            val email = extractEmail(token)

            userDetails.username == email && !isExpired(token)
        } catch (exception: UnsupportedJwtException) {
            false
        } catch (exception: JwtException) {
            false
        } catch (exception: IllegalArgumentException) {
            false
        }
    }

    /**
     * Extract all the claims from a given token.
     *
     * @param token String.
     *
     * @return Claims. E.g. email, expirationDate, etc.
     *
     * @throws UnsupportedJwtException If the JWT argument does not represent a signed Claims JWT.
     * @throws JwtException If the JWT string cannot be parsed or validated as required.
     * @throws CustomIllegalArgumentException with fieldName="token", If the JWT string is null or empty or only whitespace.
     */
    private fun getAllClaims(token: String): Claims {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (exception: IllegalArgumentException) {
            throw CustomIllegalArgumentException("JWT is invalid", "token")
        }
    }
}
