package com.mycompany.demo.warehouse.service.impl;


import com.mycompany.demo.warehouse.domain.Sensor;
import com.mycompany.demo.warehouse.dto.AlarmResponseDto;
import com.mycompany.demo.warehouse.exception.WarehouseServiceException;
import com.mycompany.demo.warehouse.mapper.AlarmResponseMapper;
import com.mycompany.demo.warehouse.repository.SensorRepository;
import com.mycompany.demo.warehouse.service.SensorService;
import io.micrometer.core.annotation.Timed;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class SensorServiceImpl implements SensorService {

    private final SensorRepository sensorRepository;

    @Timed(
            value = "sensor.alarms.timer",
            description = "Time to execute sensors alarms search",
            extraTags = {"component", "warehouse-service"}
    )

    @Override
    public Page<AlarmResponseDto> getAlarms(String sensorType, Pageable pageable) {
        try {
            if (StringUtils.hasText(sensorType)) {
                return sensorRepository
                        .findBySensorTypeContainingIgnoreCase(sensorType, pageable).map(AlarmResponseMapper::fromEntity);
            } else {
                return sensorRepository.findAll(pageable).map(AlarmResponseMapper::fromEntity);
            }
        } catch (Exception e) {
            log.error("Cannot execute getAlarms", e);
            throw new WarehouseServiceException("Cannot execute getAlarms", e);
        }
    }

    @Override
    public Sensor saveAndFlush(Sensor sensor) {
        return sensorRepository.saveAndFlush(sensor);
    }



}
