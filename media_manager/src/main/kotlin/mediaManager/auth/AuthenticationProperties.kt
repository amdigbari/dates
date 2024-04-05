package mediaManager.auth

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("auth")
data class AuthenticationProperties(
    val otpLength: Int,
    val otpExpiration: Long,
)
