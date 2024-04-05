package mediaManager.notification.mail

import org.springframework.boot.autoconfigure.mail.MailProperties
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Service

@Service
class MailService(val mailSender: JavaMailSender, val mailProperties: MailProperties) {
    fun sendMail(mail: Mail) {
        val message = mailSender.createMimeMessage()
        val helper = MimeMessageHelper(message)
        helper.setFrom(mailProperties.username)
        helper.setTo(mail.to)
        helper.setSubject(mail.subject)
        helper.setText(mail.content, true)

        mailSender.send(message)
    }
}
