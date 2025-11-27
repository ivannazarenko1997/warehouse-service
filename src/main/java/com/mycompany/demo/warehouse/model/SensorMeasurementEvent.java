package com.mycompany.demo.warehouse.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.mycompany.demo.warehouse.enums.SensorType;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;


@Data
@Builder
@ToString
public class SensorMeasurementEvent {
    private final String sensorId;
    private final SensorType type;
    private final double value;


    @JsonCreator
    public SensorMeasurementEvent(
            @JsonProperty("sensorId") String sensorId,
            @JsonProperty("type") SensorType type,
            @JsonProperty("value") double value) {
        this.sensorId = sensorId;
        this.type = type;
        this.value = value;
    }


    @Override
    public String toString() {
        return String.format("%s [ID=%s, Value=%.1f%s, Threshold=%.1f%s]",
                type.name(), sensorId, value, type.unit, type.threshold, type.unit);
    }
}