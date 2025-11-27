package com.mycompany.demo.warehouse.repository;

import com.mycompany.demo.warehouse.domain.Sensor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SensorRepository extends JpaRepository<Sensor, Long>, JpaSpecificationExecutor<Sensor> {
    Page<Sensor> findBySensorTypeContainingIgnoreCase(String sensorType, Pageable pageable);

}