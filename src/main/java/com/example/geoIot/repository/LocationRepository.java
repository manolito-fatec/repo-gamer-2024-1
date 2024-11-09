package com.example.geoIot.repository;

import com.example.geoIot.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {

    List<Location> findAllByOrderByIdLocationDesc();
}
