package com.mycompany.demo.warehouse.service;


import com.mycompany.demo.warehouse.domain.Sensor;
import com.mycompany.demo.warehouse.dto.AlarmResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SensorService {
    Page<AlarmResponseDto> getAlarms(String sensorType, Pageable pageable);
    Sensor saveAndFlush(Sensor sensor);

}