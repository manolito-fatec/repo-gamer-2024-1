package com.example.geoIot.repository;

import com.example.geoIot.entity.DeviceTrackerRedis;
import org.springframework.data.repository.CrudRepository;

public interface DeviceTrackerRedisRepository  extends CrudRepository<DeviceTrackerRedis, String> {
}
