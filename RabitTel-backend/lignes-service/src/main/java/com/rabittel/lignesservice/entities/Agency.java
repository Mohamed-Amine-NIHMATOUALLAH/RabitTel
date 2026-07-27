package com.rabittel.lignesservice.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "agencies")
@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class Agency {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(nullable = false, updatable = false)
    private UUID id;

    @NotBlank
    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String directorateCode;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String region;

    @Builder.Default
    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime lastModificationDate;

    @OneToMany(mappedBy = "agency", fetch = FetchType.LAZY)
    private List<Line> lines;

    // si je veux ajouter une map
    /*
    @Column(length = 255)
    private String address;

    @Column
    private Double latitude;

    @Column
    private Double longitude;
    */
}