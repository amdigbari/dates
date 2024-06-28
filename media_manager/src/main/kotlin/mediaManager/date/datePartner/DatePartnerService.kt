package mediaManager.date.datePartner

import mediaManager.date.Date
import mediaManager.date.datePartner.dtos.DatePartnerCreationRequestDto
import mediaManager.user.UserRepository
import org.springframework.stereotype.Service

@Service
class DatePartnerService(private val datePartnerRepository: DatePartnerRepository, private val userRepository: UserRepository) {
    fun saveDatePartners(
        date: Date,
        partners: List<DatePartnerCreationRequestDto>,
    ): List<DatePartner> {
        return datePartnerRepository.saveAll(
            userRepository.findAllById(partners.map { it.userId }).withIndex().map {
                DatePartner(
                    date = date,
                    user = it.value,
                    story = partners[it.index].story,
                    rate = partners[it.index].rate,
                )
            },
        ).toList()
    }
}
