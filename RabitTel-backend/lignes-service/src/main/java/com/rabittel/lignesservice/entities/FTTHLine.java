package com.rabittel.lignesservice.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ftth_lines")
@PrimaryKeyJoinColumn(name = "line_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class FTTHLine extends Line {

    @NotBlank
    @Column(nullable = false, unique = true, length = 50)
    private String fixedLineNumber;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String routerBrand;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String bandwidth;
}