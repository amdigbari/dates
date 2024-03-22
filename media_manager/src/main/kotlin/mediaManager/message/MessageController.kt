package mediaManager.message

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class MessageController(private val messageRepository: MessageRepository) {
    @GetMapping
    fun getMessages(): MutableIterable<Message> = messageRepository.findAll()

    @PostMapping
    fun createMessage(
        @RequestBody message: Message,
    ): Message = messageRepository.save(message)
}
