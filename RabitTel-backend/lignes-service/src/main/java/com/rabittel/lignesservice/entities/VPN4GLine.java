package com.rabittel.lignesservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "vpn_4g_lines")
@PrimaryKeyJoinColumn(name = "line_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VPN4GLine extends Line {

    @NotBlank
    @Column(nullable = false, length = 100)
    private String equipment;

    @NotBlank
    @Column(nullable = false, unique = true, length = 45)
    private String ipAddress;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String serialNumber;

    @NotNull
    @Column(nullable = false)
    private LocalDate deliveryDate;
}