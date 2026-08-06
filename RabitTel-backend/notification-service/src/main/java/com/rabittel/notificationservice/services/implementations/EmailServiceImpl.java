package com.rabittel.notificationservice.services.implementations;

import com.rabittel.notificationservice.dtos.request.EmailNotificationRequestDTO;
import com.rabittel.notificationservice.services.interfaces.EmailService;
import com.rabittel.notificationservice.services.interfaces.TemplateService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;

/**
 * SMTP email implementation using Brevo (smtp-relay.brevo.com).
 *
 * <p>Two addresses are involved:
 * <ul>
 *   <li>{@code spring.mail.username} — SMTP authentication credential (Brevo login)</li>
 *   <li>{@code app.mail.from} — the visible "From:" address in the email,
 *       which MUST be verified in Brevo under Senders → Domains & Dedicated IPs</li>
 * </ul>
 * Confusing them is the most common reason emails are blocked by Brevo or
 * land in spam with Gmail.</p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final TemplateService templateService;

    /**
     * Visible sender address — must be verified in Brevo.
     * Set via {@code app.mail.from} in application.properties.
     */
    @Value("${app.mail.from}")
    private String fromAddress;

    /**
     * Visible sender display name (e.g. "RabitTel").
     */
    @Value("${app.mail.from-name:RabitTel}")
    private String fromName;

    @Override
    public void sendEmail(EmailNotificationRequestDTO request) {

        if (request == null) {
            throw new IllegalArgumentException("Email request cannot be null.");
        }

        log.info("Preparing email — to={}, type={}", request.getRecipient(), request.getType());

        // Generate subject and body from Thymeleaf template
        String subject  = templateService.generateSubject(request.getType());
        String htmlBody = templateService.generateBody(request.getType(), request.getVariables());

        // Expose subject back to the caller (dispatcher uses it for the delivery record)
        request.setSubject(subject);

        try {
            MimeMessage message = mailSender.createMimeMessage();

            // multipart=true allows HTML content
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // From: "RabitTel" <noreply@rabittel.com>
            helper.setFrom(new InternetAddress(fromAddress, fromName, "UTF-8"));

            helper.setTo(request.getRecipient());
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // true = HTML

            log.debug("Sending via Brevo SMTP — from={}, to={}, subject={}",
                    fromAddress, request.getRecipient(), subject);

            mailSender.send(message);

            log.info("Email sent — to={}, type={}, subject={}",
                    request.getRecipient(), request.getType(), subject);

        } catch (UnsupportedEncodingException ex) {
            log.error("Invalid sender address encoding — from={}: {}", fromAddress, ex.getMessage(), ex);
            throw new RuntimeException("Invalid sender address: " + fromAddress, ex);

        } catch (MessagingException ex) {
            log.error("SMTP error sending to={}: {}", request.getRecipient(), ex.getMessage(), ex);
            throw new RuntimeException(
                    "SMTP failure sending to " + request.getRecipient() + ": " + ex.getMessage(), ex);

        } catch (MailException ex) {
            log.error("Mail transport error sending to={}: {}", request.getRecipient(), ex.getMessage(), ex);
            throw new RuntimeException(
                    "Mail transport failure for " + request.getRecipient() + ": " + ex.getMessage(), ex);
        }
    }
}
