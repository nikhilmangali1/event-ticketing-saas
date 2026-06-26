package com.nikhil.ticketflow.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.from}")
    private String fromMail;

    @Async("emailExecutor")
    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, String> variables) {
        try {
            log.info("sending email to {} from {}", to, fromMail);
            String htmlContent = loadAndPopulateTemplate(templateName, variables);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromMail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            log.info("mail sent successfully to {}", to);
        } catch (MessagingException | IOException e) {
            log.error("Failed to send HTML email to {} : {}", to, e.getMessage());
        }
    }

    @Async("emailExecutor")
    public void sendTicketBookingEmail(String to, String subject, String templateName, Map<String, String> variables,
                                        byte[] qrImageBytes, String ticketId) {
        try {
            log.info("sending ticket booking email to {} from {}", to, fromMail);
            String htmlContent = loadAndPopulateTemplate(templateName, variables);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
            helper.setFrom(fromMail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            ByteArrayResource qrResource = new ByteArrayResource(qrImageBytes);
            helper.addInline("qrImage", qrResource, "image/png");
            helper.addAttachment("ticket-" + ticketId + ".png", qrResource, "image/png");

            mailSender.send(message);
            log.info("ticket booking email sent successfully to {}", to);
        } catch (MessagingException | IOException e) {
            log.error("Failed to send ticket booking email to {} : {}", to, e.getMessage());
        }
    }


    private String loadAndPopulateTemplate(String templateName, Map<String, String> variables) throws IOException {
        String filePath = "email/" + templateName + ".html";
        ClassPathResource resource = new ClassPathResource(filePath);
        String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            content = content.replace("{{" + entry.getKey() + "}}", entry.getValue());
        }
        return content;
    }
}
