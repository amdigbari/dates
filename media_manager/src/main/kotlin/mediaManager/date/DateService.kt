package mediaManager.date

import mediaManager.date.dateAsset.DateAssetService
import mediaManager.date.datePartner.DatePartnerService
import mediaManager.date.dtos.DateCreationRequestDto
import org.springframework.stereotype.Service

@Service
class DateService(
    private val dateRepository: DateRepository,
    private val dateAssetService: DateAssetService,
    private val datePartnerService: DatePartnerService,
) {
    // TODO: Catch and Convert Exceptions
    fun createDate(dto: DateCreationRequestDto): Date {
        val date = Date(shortStory = dto.shortStory, date = java.sql.Date.valueOf(dto.date))
        date.datesPartners.addAll(datePartnerService.saveDatePartners(date = date, partners = dto.partners))
        date.dateAssets.addAll(dateAssetService.saveMultipartFiles(date, dto.assets))

        return dateRepository.save(date)
    }

    // TODO: Implement Get Partners and Update
}
