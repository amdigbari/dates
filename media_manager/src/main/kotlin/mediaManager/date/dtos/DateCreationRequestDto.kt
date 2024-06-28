package mediaManager.date.dtos

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import mediaManager.constants.DateTimeConstants
import mediaManager.date.datePartner.dtos.DatePartnerCreationRequestDto
import mediaManager.validations.File
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.multipart.MultipartFile

data class DateCreationRequestDto(
    @field:NotBlank
    @field:Size(min = 10, max = 500)
    val shortStory: String,
    @field:NotNull
    @field:JsonFormat(shape = JsonFormat.Shape.STRING, pattern = DateTimeConstants.DATE_FORMAT)
    @field:DateTimeFormat(pattern = DateTimeConstants.DATE_FORMAT)
    val date: String,
    @field:Size(min = 1)
    @field:File(
        allowedTypes = [
            // Images
            "image/jpeg",
            "image/png",
            "image/jpg",
            "image/avif",
            "image/webp",
            "image/heic",
            "image/heif",
            // Videos
            "video/mp4",
            "video/mpeg",
            "video/webm",
            // Audios
            "audio/mpeg",
            "audio/webm",
        ],
    )
    val assets: List<MultipartFile>,
    @field:Size(min = 1)
    val partners: List<DatePartnerCreationRequestDto>,
)
