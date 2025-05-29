package com.example.templategenerator.service.document;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailSenderUsername;

    public void sendAssignment(String to, byte[] file, String filename) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        System.out.println("FROM: " + mailSenderUsername + " TO: " + to);
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setFrom(mailSenderUsername);
        helper.setTo(to);
        helper.setSubject("Ваши задания");
        helper.setText("Во вложении — сгенерированный документ.");
        helper.addAttachment(filename, new ByteArrayResource(file),
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        mailSender.send(message);
    }
}
