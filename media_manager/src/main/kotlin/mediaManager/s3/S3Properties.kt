package mediaManager.s3

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("s3")
data class S3Properties(
    val endpoint: String,
    val accessKey: String,
    val secretKey: String,
    val bucketName: String,
    val publicDir: String,
    val privateDir: String,
)
