package com.decagosq022.qlockin.service.Impl;

import com.decagosq022.qlockin.payload.request.EmailDetails;
import com.decagosq022.qlockin.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine tEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;
    @Override
    public void sendEmailAlert(EmailDetails emailDetails) {
        try {
            SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
            simpleMailMessage.setFrom(senderEmail);
            simpleMailMessage.setTo(emailDetails.getRecipient());
            simpleMailMessage.setText(emailDetails.getMessageBody());
            simpleMailMessage.setSubject(emailDetails.getSubject());

            javaMailSender.send(simpleMailMessage);
            System.out.println("Mail sent successfully!!");

        } catch (MailException exception) {
            throw new RuntimeException(exception);
        }

    }

    @Override
    public void sendSimpleMailMessage(EmailDetails message, String templateName) throws MessagingException {
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        Map<String, Object> variables = Map.of(
                "name", message.getFullName(),
                "id", message.getEmployeeId(),
                "link", message.getLink()
        );
        context.setVariables(variables);
        helper.setFrom(senderEmail);
        helper.setTo(message.getRecipient());
        helper.setSubject(message.getSubject());
        String html = tEngine.process("email", context);
        helper.setText(html, true);

        javaMailSender.send(msg);
        log.info("Sending email: to {}",message.getRecipient());

    }

    @Override
    public void mimeMailMessage(EmailDetails emailDetails) {

        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());

            helper.setText(emailDetails.getMessageBody(), true); // Set true if the body is HTML, false for plain text
            helper.setFrom(senderEmail);
            helper.setTo(emailDetails.getRecipient());
            helper.setSubject(emailDetails.getSubject());

            javaMailSender.send(mimeMessage);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
