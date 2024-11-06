package com.example.geoIot.service.device;

import com.example.geoIot.entity.DeviceTracker;
import com.example.geoIot.entity.dto.DeviceTrackerDto;
import com.example.geoIot.entity.dto.DeviceTrackerPeriodRequestDto;
import com.example.geoIot.entity.dto.history.HistoryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;


public interface DeviceTrackerService {
    void saveDeviceTracker(List<DeviceTracker> pDeviceTracker);

    Page<DeviceTrackerDto> getDeviceTrackerByDateInterval(DeviceTrackerPeriodRequestDto requestDto, Pageable pageable);

    Page<HistoryDto> searchHistoryByDateInterval(DeviceTrackerPeriodRequestDto requestDto, Pageable pageable);

    List<DeviceTrackerDto> getTrackersInsideLocation(Long locationId, LocalDateTime init, LocalDateTime end, Long userId);
}
