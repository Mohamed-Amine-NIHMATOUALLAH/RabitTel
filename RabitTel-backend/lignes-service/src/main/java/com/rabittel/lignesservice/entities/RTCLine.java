package com.rabittel.lignesservice.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "rtc_lines")
@PrimaryKeyJoinColumn(name = "line_id")
@Getter
@Setter
@SuperBuilder
public class RTCLine extends Line {

    // JPA requires a no-arg constructor with at least protected visibility
    protected RTCLine() {
        super();
    }
}