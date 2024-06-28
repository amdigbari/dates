package mediaManager.date.dateAsset

import mediaManager.date.Date
import mediaManager.s3.S3ObjectMode
import mediaManager.s3.S3Service
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile

@Service
class DateAssetService(private val dateAssetRepository: DateAssetRepository, private val s3Service: S3Service) {
    fun saveMultipartFiles(
        date: Date,
        files: List<MultipartFile>,
    ): List<DateAsset> {
        return files.map { saveMultipartFile(date = date, file = it) }
    }

    fun saveMultipartFile(
        date: Date,
        file: MultipartFile,
    ): DateAsset {
        val assetType = getMultipartFileType(file)
        val s3ObjectKey = s3Service.uploadObject(file = file, directoryPath = "dates", mode = S3ObjectMode.PRIVATE)
        return dateAssetRepository.save(DateAsset(date = date, objectKey = s3ObjectKey, type = assetType))
    }

    private fun getMultipartFileType(file: MultipartFile): DateAssetType {
        val contentType = file.contentType ?: throw IllegalArgumentException("Content type is null")

        return when {
            contentType.startsWith("image") -> DateAssetType.IMAGE
            contentType.startsWith("video") -> DateAssetType.VIDEO
            contentType.startsWith("audio") -> DateAssetType.AUDIO

            else -> throw IllegalArgumentException("Invalid content type")
        }
    }
}
