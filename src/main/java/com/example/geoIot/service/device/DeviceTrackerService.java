package com.example.geoIot.service.device;

import com.example.geoIot.entity.DeviceTracker;
import com.example.geoIot.entity.dto.DeviceTrackerDto;
import com.example.geoIot.entity.dto.DeviceTrackerPeriodRequestDto;

import java.util.List;


public interface DeviceTrackerService {
    void saveDeviceTracker(List<DeviceTracker> pDeviceTracker);

    List<DeviceTrackerDto> getDeviceTrackerByDateInterval(DeviceTrackerPeriodRequestDto requestDto);
}
