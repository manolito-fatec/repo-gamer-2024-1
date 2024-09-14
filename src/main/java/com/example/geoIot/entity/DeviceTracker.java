package com.example.geoIot.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="device_tracker")
public class DeviceTracker {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id_device_tracker")
    private Long idDeviceTracker;
    @Column(name = "id_text_device_tracker")
    private String idTextDeviceTracker;
    @Column(name = "created_at_device_tracker")
    private LocalDateTime createdDeviceTracker;
    @Column(name="latitude_device_tracker")
    private BigDecimal latitude;
    @Column(name="longitude_device_tracker")
    private BigDecimal longitude;

    @ManyToOne
    @JoinColumn(name="id_person")
    private Person personDeviceTracker;
}
