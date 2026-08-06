package com.rabittel.notificationservice.services.interfaces;

import com.rabittel.notificationservice.enums.NotificationType;
import org.springframework.stereotype.Service;

import java.util.Map;
@Service
public interface TemplateService {

    String generateSubject(NotificationType type);

    String generateBody(
            NotificationType type,
            Map<String, Object> variables
    );

}