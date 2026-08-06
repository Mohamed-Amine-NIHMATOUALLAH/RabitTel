package com.rabittel.notificationservice.mappers;

import com.rabittel.notificationservice.dtos.request.NotificationRequestDTO;
import com.rabittel.notificationservice.dtos.response.NotificationDeliveryResponseDTO;
import com.rabittel.notificationservice.dtos.response.NotificationResponseDTO;
import com.rabittel.notificationservice.entities.Notification;
import com.rabittel.notificationservice.entities.NotificationDelivery;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

/**
 * MapStruct mapper for {@link Notification} and {@link NotificationDelivery}.
 *
 * <p>{@code deliveryCount} is computed from the size of the deliveries collection
 * via a custom expression to avoid an unmapped-property warning.</p>
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper {

    /*
     * Request → Notification entity
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "deliveries", ignore = true)
    Notification toEntity(NotificationRequestDTO dto);

    /*
     * Notification → Response DTO
     * deliveryCount is derived from the size of the deliveries list.
     */
    @Mapping(target = "deliveryCount", expression = "java(notification.getDeliveries() != null ? notification.getDeliveries().size() : 0)")
    NotificationResponseDTO toResponseDTO(Notification notification);

    /*
     * Delivery → Response DTO
     */
    NotificationDeliveryResponseDTO toDeliveryResponseDTO(NotificationDelivery delivery);
}
