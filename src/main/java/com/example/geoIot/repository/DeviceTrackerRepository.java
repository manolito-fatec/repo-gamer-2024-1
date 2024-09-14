package com.example.geoIot.repository;

import com.example.geoIot.entity.DeviceTracker;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeviceTrackerRepository extends JpaRepository<DeviceTracker, Long>{
}
