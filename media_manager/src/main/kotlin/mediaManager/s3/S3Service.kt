package mediaManager.s3

import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import software.amazon.awssdk.awscore.exception.AwsServiceException
import software.amazon.awssdk.core.exception.SdkClientException
import software.amazon.awssdk.core.sync.RequestBody
import software.amazon.awssdk.services.s3.S3Client
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.model.S3Exception
import java.util.UUID

@Service
class S3Service(private val s3Client: S3Client, private val s3Properties: S3Properties) {
    /**
     * Uploading a file into S3 bucket
     *
     * (file, "root/sub-path") ---> "root/sub-path/xxxx-xxxx-xxxx-xxxx_filename"
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
    fun uploadFile(
        file: MultipartFile,
        directoryPath: String?,
    ): String {
        val fileName = file.originalFilename ?: throw IllegalArgumentException("Filename is missing!.")
        val fileKey = "${directoryPath?.trim('/') ?: ""}/${UUID.randomUUID()}_$fileName"

        s3Client.putObject(
            PutObjectRequest.builder()
                .bucket(s3Properties.bucketName)
                .key(fileKey)
                .contentType(file.contentType)
                .build(),
            RequestBody.fromInputStream(file.inputStream, file.size),
        )

        return fileKey
    }
}
