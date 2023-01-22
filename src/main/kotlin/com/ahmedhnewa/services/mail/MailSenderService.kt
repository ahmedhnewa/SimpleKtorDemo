package com.ahmedhnewa.services.mail

interface MailSenderService {
    suspend fun sendEmail(
        emailMessage: EmailMessage
    ): Boolean
}