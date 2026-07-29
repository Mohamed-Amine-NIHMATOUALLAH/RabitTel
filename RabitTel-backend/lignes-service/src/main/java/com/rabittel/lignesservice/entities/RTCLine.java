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
@NoArgsConstructor
public class RTCLine extends Line { }