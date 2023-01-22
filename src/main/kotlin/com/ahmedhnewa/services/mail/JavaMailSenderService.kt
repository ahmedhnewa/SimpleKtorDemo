package com.ahmedhnewa.services.mail

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import javax.mail.*
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

class JavaMailSenderService: MailSenderService {
    private val props = Properties().apply {
        this["mail.smtp.host"] = "smtp.gmail.com"
        this["mail.smtp.port"] = "587"
        this["mail.smtp.auth"] = "true"
        this["mail.smtp.starttls.enable"] = "true"
    }
    private val session: Session = Session.getInstance(props, object : Authenticator() {
        override fun getPasswordAuthentication(): PasswordAuthentication {
            val username = System.getenv("EMAIL_USERNAME").ifEmpty { throw IllegalStateException("EMAIL_USERNAME env should not be null.") }
            val password = System.getenv("EMAIL_PASSWORD").ifEmpty { throw IllegalStateException("EMAIL_PASSWORD env should not be null.") }
            return PasswordAuthentication(username, password)
        }
    })
    override suspend fun sendEmail(emailMessage: EmailMessage): Boolean = withContext(Dispatchers.IO) {
        return@withContext try {
            val message = MimeMessage(session)
            val from = System.getenv("FROM_EMAIL").ifEmpty { throw IllegalStateException("FROM_EMAIL env should not be null.") }
            message.setFrom(InternetAddress(from))
            message.setRecipients(
                Message.RecipientType.TO,
                emailMessage.to.lowercase().trim()
            )
            message.subject = emailMessage.subject
            message.sentDate = Date()
            message.setText(emailMessage.body)
            Transport.send(message)
            true
        } catch (mex: MessagingException) {
            println("send failed, exception: $mex")
            false
        } catch (e: Exception) {
            e.printStackTrace()
            println("Unhandled exception ${e.javaClass.name} from ${e.javaClass.packageName}")
            false
        }
    }
}