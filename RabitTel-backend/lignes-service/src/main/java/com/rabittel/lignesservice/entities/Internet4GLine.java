package com.rabittel.lignesservice.entities;

import com.rabittel.lignesservice.enums.Internet4GBandwidth;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "internet_4g_lines")
@PrimaryKeyJoinColumn(name = "line_id")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Internet4GLine extends Line {

    @NotBlank
    @Column(nullable = false, length = 100)
    private String serviceFunction;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String simSerialNumber;

    @NotBlank
    @Column(nullable = false, length = 4)
    private String pinCode;

    @NotBlank
    @Column(nullable = false, length = 8)
    private String pukCode;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String equipment;

    @NotBlank
    @Column(nullable = false, unique = true, length = 100)
    private String equipmentSerialNumber;

    @NotNull
    @Convert(converter = Internet4GBandwidth.Internet4GBandwidthConverter.class)
    @Column(nullable = false, length = 50)
    private Internet4GBandwidth bandwidth;
}
