package mediaManager.notification.mail

/**
 * @property to String. The email address of the mail receiver.
 * @property subject String. The subject of the mail.
 * @property content String. The content of the mail. It can be HTML string too.
 */
data class Mail(
    val to: String,
    val subject: String,
    val content: String,
)
