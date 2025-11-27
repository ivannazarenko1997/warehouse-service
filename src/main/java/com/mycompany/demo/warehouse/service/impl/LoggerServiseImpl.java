package com.mycompany.demo.warehouse.service.impl;

import com.mycompany.demo.warehouse.enums.SensorType;
import com.mycompany.demo.warehouse.kafka.event.SensorMeasurementDto;
import com.mycompany.demo.warehouse.service.LoggerServise;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class LoggerServiseImpl implements LoggerServise {

    public void printLog(SensorMeasurementDto event) {
        SensorType type = event.getSensorType();
        double value = event.getValue();
        double threshold = type.threshold;


        log.error(
                "No ALARM.Standart log [{}] value={}{} EXCEEDED threshold={}{}. event={}",
                type.name(),
                value, type.unit,
                threshold, type.unit,
                event
        );

    }

    @Override
    public void printAlarmLog(SensorMeasurementDto measurementDto) {
        SensorType type = measurementDto.getSensorType();
        double value = measurementDto.getValue();
        double threshold = type.threshold;


        log.error(
                "🚨 ALARM! [{}] value={}{} EXCEEDED threshold={}{}. event={}",
                type.name(),
                value, type.unit,
                threshold, type.unit,
                measurementDto
        );

    }


}
