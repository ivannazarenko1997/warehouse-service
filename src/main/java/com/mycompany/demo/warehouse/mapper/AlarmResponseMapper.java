package com.mycompany.demo.warehouse.mapper;


import com.mycompany.demo.warehouse.domain.Sensor;
import com.mycompany.demo.warehouse.dto.AlarmResponseDto;

public final class AlarmResponseMapper {
    private AlarmResponseMapper() {
    }
    public static AlarmResponseDto fromEntity(Sensor sensor) {
        if (sensor == null) {
            return null;
        }

        return AlarmResponseDto.builder()
                .id(sensor.getId())
                .sensorId(sensor.getSensorId())
                .sensorType(sensor.getSensorType())
                .value(sensor.getValue())
                .alarm(sensor.isAlarm())
                .threshold(sensor.getValue())
                .message(sensor.getDescription())
                .createdAt(sensor.getCreatedAt())
                .build();
    }


}