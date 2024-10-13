package com.example.geoIot.repository;

import com.example.geoIot.entity.DeviceTracker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface DeviceTrackerRepository extends JpaRepository<DeviceTracker, Long> {

    public Page<DeviceTracker> findByPersonDeviceTrackerIdPersonAndCreatedAtDeviceTrackerBetweenOrderByCreatedAtDeviceTracker(Long personId, LocalDateTime init, LocalDateTime end, Pageable pageable);

    @Query("""
    select dt 
    from DeviceTracker dt
    where dt.personDeviceTracker.idPerson = ?1 and 
    dt.createdAtDeviceTracker BETWEEN ?2 AND ?3
    order by dt.createdAtDeviceTracker asc
    """)
    public Page<DeviceTracker> filterToHistoric(Long personId, LocalDateTime init, LocalDateTime end, Pageable pageable);

}