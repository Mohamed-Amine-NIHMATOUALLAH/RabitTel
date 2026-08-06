package com.rabittel.notificationservice.controller;

import com.rabittel.notificationservice.dtos.request.NotificationRequestDTO;
import com.rabittel.notificationservice.dtos.response.ApiErrorResponse;
import com.rabittel.notificationservice.dtos.response.NotificationResponseDTO;
import com.rabittel.notificationservice.services.interfaces.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(
        value = "/api/v1/notifications",
        produces = MediaType.APPLICATION_JSON_VALUE
)
@Tag(
        name = "Notification API",
        description = "Manage notification creation, dispatching and consultation."
)
public class NotificationController {

    private final NotificationService notificationService;

    // ==========================================================
    // SEND NOTIFICATION
    // ==========================================================

    @Operation(
            summary = "Send notification",
            description = """
                    Creates a new notification and dispatches it
                    through every requested channel.
                    
                    Supported channels:
                    - EMAIL
                    - IN_APP
                    - SMS
                    - WHATSAPP
                    - MICROSOFT_TEAMS
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Notification successfully created"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Validation error",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponse.class
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponse.class
                            )
                    )
            )
    })
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NotificationResponseDTO> send(

            @Valid
            @RequestBody
            NotificationRequestDTO request
    ) {

        log.info(
                "Sending notification | type={} | recipient={} | channels={}",
                request.getType(),
                request.getRecipient(),
                request.getChannels()
        );

        NotificationResponseDTO response =
                notificationService.send(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    // ==========================================================
    // GET ALL
    // ==========================================================

    @Operation(
            summary = "Get all notifications",
            description = "Returns every notification ordered by creation date."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Notifications retrieved successfully"
    )
    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getAll() {

        log.debug("Retrieving all notifications.");

        return ResponseEntity.ok(
                notificationService.getAll()
        );
    }

    // ==========================================================
    // GET BY ID
    // ==========================================================

    @Operation(
            summary = "Get notification by ID",
            description = "Returns a notification with all its deliveries."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Notification found"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notification not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponse.class
                            )
                    )
            )
    })
    @GetMapping("/{id}")
    public ResponseEntity<NotificationResponseDTO> getById(

            @Parameter(description = "Notification UUID")
            @PathVariable
            UUID id
    ) {

        log.debug("Retrieving notification {}", id);

        return ResponseEntity.ok(
                notificationService.getById(id)
        );
    }

    // ==========================================================
    // GET BY RECIPIENT
    // ==========================================================

    @Operation(
            summary = "Get recipient notifications",
            description = "Returns every notification associated with a recipient."
    )
    @ApiResponse(
            responseCode = "200",
            description = "Notifications retrieved successfully"
    )
    @GetMapping("/recipient/{recipient}")
    public ResponseEntity<List<NotificationResponseDTO>> getRecipientNotifications(

            @Parameter(description = "Recipient email or username")
            @PathVariable
            String recipient
    ) {

        log.debug("Retrieving notifications for {}", recipient);

        return ResponseEntity.ok(
                notificationService.getRecipientNotifications(recipient)
        );
    }

    // ==========================================================
    // MARK AS READ
    // ==========================================================

    @Operation(
            summary = "Mark notification as read",
            description = "Marks a notification delivery as read."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "204",
                    description = "Notification marked as read"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Notification delivery not found",
                    content = @Content(
                            schema = @Schema(
                                    implementation = ApiErrorResponse.class
                            )
                    )
            )
    })
    @PatchMapping("/{deliveryId}/read")
    public ResponseEntity<Void> markAsRead(

            @Parameter(description = "Notification Delivery UUID")
            @PathVariable
            UUID deliveryId
    ) {

        log.debug(
                "Marking notification delivery {} as read.",
                deliveryId
        );

        notificationService.markAsRead(deliveryId);

        return ResponseEntity.noContent().build();
    }

}