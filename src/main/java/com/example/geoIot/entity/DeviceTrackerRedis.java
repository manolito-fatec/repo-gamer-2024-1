package com.example.geoIot.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("device")
public class DeviceTrackerRedis implements Serializable{

    @Id
    @Indexed
    private String idTextDeviceTracker;
    private LocalDateTime createdDeviceTracker;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Person fullName;
}
