package com.decagosq022.qlockin.service;

import com.decagosq022.qlockin.payload.request.EmailDetails;
import jakarta.mail.MessagingException;

public interface EmailService {
    void sendEmailAlert(EmailDetails emailDetails);

    void sendSimpleMailMessage(EmailDetails message, String fullName, String link) throws MessagingException;

    void mimeMailMessage(EmailDetails emailDetails);
}
