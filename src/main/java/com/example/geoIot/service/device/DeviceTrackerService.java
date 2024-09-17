package com.example.geoIot.service.device;

import com.example.geoIot.entity.DeviceTracker;

import java.util.List;


public interface DeviceTrackerService {
    public void saveDeviceTracker(List<DeviceTracker> pDeviceTracker);
}
