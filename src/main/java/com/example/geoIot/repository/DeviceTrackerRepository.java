package com.example.geoIot.repository;

import com.example.geoIot.entity.DeviceTracker;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DeviceTrackerRepository extends JpaRepository<DeviceTracker, Long>{

    public Optional<List<DeviceTracker>> findByPersonDeviceTrackerIdAndCreatedAtDeviceTrackerBetween(Long personId, LocalDateTime init, LocalDateTime end);
}
