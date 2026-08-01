package com.rabittel.lignesservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "vpn_lines")
@PrimaryKeyJoinColumn(name = "line_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class VPNLine extends Line {

    @NotNull
    @Positive
    @Column(nullable = false, length = 50)
    private Long bandwidth;

    @NotBlank
    @Column(nullable = false, unique = true, length = 45)
    private String ipAddress;
}