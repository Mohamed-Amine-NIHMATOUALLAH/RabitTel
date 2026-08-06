package com.rabittel.notificationservice.services.implementations;

import com.rabittel.notificationservice.enums.NotificationTemplate;
import com.rabittel.notificationservice.enums.NotificationType;
import com.rabittel.notificationservice.services.interfaces.TemplateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Arrays;
import java.util.Locale;
import java.util.Map;

/**
 * Renders Thymeleaf HTML email templates by {@link NotificationType}.
 *
 * <p>Injects the dedicated {@code emailTemplateEngine} ({@link SpringTemplateEngine})
 * which has OGNL, Spring EL, and all standard dialects properly wired.
 * This avoids {@code NoClassDefFoundError: ognl/PropertyAccessor} that occurs
 * when using a bare {@code TemplateEngine} created outside Spring's autoconfigure.</p>
 */
@Slf4j
@Service
public class TemplateServiceImpl implements TemplateService {

    private final SpringTemplateEngine templateEngine;

    public TemplateServiceImpl(
            @Qualifier("emailTemplateEngine") SpringTemplateEngine templateEngine
    ) {
        this.templateEngine = templateEngine;
    }

    @Override
    public String generateSubject(NotificationType type) {
        String subject = resolveTemplate(type).getSubject();
        log.debug("Subject for type={}: {}", type, subject);
        return subject;
    }

    @Override
    public String generateBody(NotificationType type, Map<String, Object> variables) {

        NotificationTemplate template = resolveTemplate(type);

        log.debug("Rendering template '{}' for type={}", template.getTemplate(), type);

        Context context = new Context(Locale.ENGLISH);
        if (variables != null && !variables.isEmpty()) {
            context.setVariables(variables);
        }

        try {
            String html = templateEngine.process(template.getTemplate(), context);
            log.debug("Template '{}' rendered ({} chars)", template.getTemplate(), html.length());
            return html;
        } catch (Exception ex) {
            log.error("Failed to render template '{}' for type={}: {}",
                    template.getTemplate(), type, ex.getMessage(), ex);
            throw new RuntimeException(
                    "Template rendering failed for type " + type + ": " + ex.getMessage(), ex);
        }
    }

    private NotificationTemplate resolveTemplate(NotificationType type) {
        return Arrays.stream(NotificationTemplate.values())
                .filter(t -> t.getType() == type)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "No template configured for notification type: " + type));
    }
}
