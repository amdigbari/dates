package mediaManager.i18n

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("i18n")
data class I18nProperties(val defaultLanguage: String, val defaultCountry: String)
