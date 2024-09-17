package com.example.geoIot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name="tracker")
public class DeviceTracker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tracker")
    private Long idDeviceTracker;
    @Column(name = "ito_tracker_code")
    private String idTextDeviceTracker;
    @Column(name = "created_at")
    private LocalDateTime createdDeviceTracker;
    @Column(name="latitude")
    private BigDecimal latitude;
    @Column(name="longitude")
    private BigDecimal longitude;

    @ManyToOne
    @JoinColumn(name="id_person")
    private Person personDeviceTracker;
}
