package com.rabittel.lignesservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "gsm_lines")
@PrimaryKeyJoinColumn(name = "line_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class GSMLine extends Line {

    @NotBlank
    @Column(nullable = false, length = 100)
    private String serviceFunction;

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String chipSerialNumber;

    @Column(nullable = false)
    private LocalDate chipDeliveryDate;

    @NotBlank
    @Column(nullable = false, length = 4)
    private String pinCode;

    @NotBlank
    @Column(nullable = false, length = 8)
    private String pukCode;
}