package mediaManager.s3

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.S3Configuration
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import java.net.URI

@Configuration
@EnableConfigurationProperties(S3Properties::class)
class S3Config(val s3Properties: S3Properties) {
    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.AWS_GLOBAL)
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(s3Properties.accessKey, s3Properties.secretKey),
                ),
            )
            .apply {
                endpointOverride(URI.create(s3Properties.endpoint))
                serviceConfiguration(
                    S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build(),
                )
            }
            .build()
    }

    @Bean
    fun s3PreSigner(): S3Presigner {
        return S3Presigner.builder()
            .region(Region.AWS_GLOBAL)
            .credentialsProvider(
                StaticCredentialsProvider.create(AwsBasicCredentials.create(s3Properties.accessKey, s3Properties.secretKey)),
            )
            .apply {
                endpointOverride(URI.create(s3Properties.endpoint))
                serviceConfiguration(
                    S3Configuration.builder()
                        .pathStyleAccessEnabled(true)
                        .build(),
                )
            }
            .build()
    }
}
