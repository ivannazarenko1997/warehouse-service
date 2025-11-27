package com.mycompany.demo.warehouse.kafka.event;


import com.mycompany.demo.warehouse.enums.SensorType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;

@Builder
@Getter
@Setter
@ToString
public class SensorMeasurementDto {

    private Long id;

    private String sensorId;

    private SensorType sensorType;

    private double value;

    private String description;

    private Instant createdAt;
}
