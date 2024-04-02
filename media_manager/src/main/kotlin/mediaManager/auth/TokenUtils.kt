package mediaManager.auth

class TokenUtils {
    companion object {
        private const val TOKEN_START = "Bearer "

        fun extractTokenValue(token: String): String = token.substringAfter(this.TOKEN_START)

        fun isStartWithTokenType(token: String?): Boolean = token != null && token.startsWith(this.TOKEN_START)
    }
}
