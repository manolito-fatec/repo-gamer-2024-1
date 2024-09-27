package com.example.geoIot.service.device;

import com.example.geoIot.entity.dto.DeviceTrackerRedisDto;

import java.util.List;

public interface DeviceTrackerRedisService{

    void saveDataInCache(List<DeviceTrackerRedisDto> deviceTrackerRedis);

    void synchronizeDataBase();
}
