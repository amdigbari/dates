package mediaManager.auth

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.DefaultSecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfiguration(private val authenticationProvider: AuthenticationProvider) {
    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        jwtAuthenticationFilter: JWTAuthenticationFilter,
    ): DefaultSecurityFilterChain =
        http
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/api/auth/login", "/api/auth/refresh-token", "/error", "/api/schema/**")
                    .permitAll()
                    .requestMatchers(HttpMethod.POST, "/api/auth/register")
                    .permitAll()
                    .requestMatchers("/api/users**")
                    .hasRole("ADMIN")
                    .anyRequest()
                    .fullyAuthenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .authenticationProvider(authenticationProvider)
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
}
