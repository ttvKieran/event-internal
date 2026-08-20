package com.example.notification_service.infrastructure.email;

import com.example.notification_service.application.port.out.NotificationSenderPort;
import com.example.notification_service.domain.model.aggregate.Notification;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JavaMailSenderAdapter implements NotificationSenderPort {

    private final JavaMailSender mailSender;

    @Override
    public void send(Notification notification) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(notification.getRecipient().getEmail());
            helper.setSubject(notification.getSubject());
            helper.setText(notification.getBody(), true); // true = HTML

            mailSender.send(message);

            log.info("[Email] Đã gửi email tới {} | subject: {}",
                notification.getRecipient().getEmail(), notification.getSubject());

        } catch (MessagingException e) {
            throw new RuntimeException("[Email] Gửi email thất bại: " + e.getMessage(), e);
        }
    }
}
