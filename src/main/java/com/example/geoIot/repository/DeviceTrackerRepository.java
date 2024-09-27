package com.example.geoIot.repository;

import com.example.geoIot.entity.DeviceTracker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface DeviceTrackerRepository extends JpaRepository<DeviceTracker, Long>{

    public Page<DeviceTracker> findByPersonDeviceTrackerIdPersonAndCreatedAtDeviceTrackerBetweenOrderByCreatedAtDeviceTracker(Long personId, LocalDateTime init, LocalDateTime end, Pageable pageable);
}
