package mediaManager.s3

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.awscore.exception.AwsServiceException
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.GetObjectRequest
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest
import java.net.URL
import java.time.Duration
import java.util.UUID

@Service
class S3Service(private val s3Client: S3Client, private val s3PreSigner: S3Presigner, private val s3Properties: S3Properties) {
    /**
     * Uploading an object into S3 bucket
     *
     * (object, "root/sub-path", PUBLIC) ---> "public/root/sub-path/xxxx-xxxx-xxxx-xxxx_filename"
     *
     * @param file MultipartFile
     * @param directoryPath String. The path of directory you want the file save into.
     *
     * @return String. The saved file path.
     *
     * @throws IllegalArgumentException in case originalFilename of the file be null.
     * @throws AwsServiceException
     * @throws SdkClientException
     * @throws S3Exception
     */
    fun uploadObject(
        file: MultipartFile,
        directoryPath: String = "",
        mode: S3ObjectMode = S3ObjectMode.PUBLIC,
    ): String {
        val fileName = file.originalFilename ?: throw IllegalArgumentException("Filename is missing!.")

        val modePath = if (mode == S3ObjectMode.PUBLIC) s3Properties.publicDir else s3Properties.privateDir
        val basePath = "$modePath/${directoryPath.trim()}".trim('/')
        val objectKey = "$basePath/${UUID.randomUUID()}_$fileName"

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(s3Properties.bucketName)
                .key(objectKey)
                .contentType(file.contentType)
                .build(),
            RequestBody.fromInputStream(file.inputStream, file.size),
        )

        return objectKey
    }

    /**
     * Signing an object with a duration to generate a public access url
     *
     * @param objectKey String. The path of the object in the bucket.
     * @param expiresIn Long. The expiration duration in minutes. Default to 10.
     *
     * @return URL. The signed URL.
     *
     * @throws AwsServiceException
     * @throws SdkClientException
     * @throws S3Exception
     */
    fun signObject(
        objectKey: String,
        expiresIn: Long = 10,
    ): URL {
        val getObjectRequest =
            GetObjectRequest.builder()
                .bucket(s3Properties.bucketName)
                .key(objectKey)
                .build()
        val getObjectPreSignRequest =
            GetObjectPresignRequest.builder()
                .getObjectRequest(getObjectRequest)
                .signatureDuration(Duration.ofMinutes(expiresIn))
                .build()

        val preSignRequest = s3PreSigner.presignGetObject(getObjectPreSignRequest)
        return preSignRequest.url()
    }
}
