package com.example.geoIot.service.device;

import com.example.geoIot.entity.DeviceTracker;
import com.example.geoIot.entity.dto.DeviceTrackerDto;
import com.example.geoIot.entity.dto.DeviceTrackerPeriodRequestDto;
import com.example.geoIot.entity.dto.history.HistoryDto;
import com.example.geoIot.entity.dto.history.StopDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface DeviceTrackerService {
    void saveDeviceTracker(List<DeviceTracker> pDeviceTracker);

    Page<DeviceTrackerDto> getDeviceTrackerByDateInterval(DeviceTrackerPeriodRequestDto requestDto, Pageable pageable);

    Page<StopDto> getStopList(DeviceTrackerPeriodRequestDto requestDto, Pageable pageable);

    Page<HistoryDto> searchHistoryByDateInterval(DeviceTrackerPeriodRequestDto requestDto, Pageable pageable);
}
