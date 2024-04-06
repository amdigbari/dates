package mediaManager.auth

class TokenUtils {
    companion object {
        private const val TOKEN_START = "Bearer "

        /**
         * Extract the token value from the Auth header value.
         *
         * Example:
         * - extractTokenValue("Bearer foobar") = foobar
         * - extractTokenValue("JWT foobar") = JWT foobar
         *
         * @param token String. The Auth header value.
         *
         * @return String. The JWT token value.
         */
        fun extractTokenValue(token: String): String = token.substringAfter(this.TOKEN_START)

        /**
         * Checks the String value is starts with TokenType (in this case Bearer) or not
         *
         * @paras token String. Which can be null.
         *
         * @return Boolean.
         */
        fun isStartWithTokenType(token: String?): Boolean = token != null && token.startsWith(this.TOKEN_START)
    }
}
