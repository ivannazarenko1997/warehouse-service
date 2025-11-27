package com.mycompany.demo.warehouse.mapper;


import com.mycompany.demo.warehouse.domain.Sensor;
import com.mycompany.demo.warehouse.kafka.event.SensorMeasurementDto;
import com.mycompany.demo.warehouse.model.SensorMeasurementEvent;

public final class EventListenerMapper {
    private EventListenerMapper() {
    }

    public static SensorMeasurementEvent toListenerEvent(     SensorMeasurementEvent eventFromKafka) {
        return SensorMeasurementEvent.builder()
                .sensorId(eventFromKafka.getSensorId())
                .type(eventFromKafka.getType() )
                .value(eventFromKafka.getValue())
                .build();

    }
    public static SensorMeasurementDto toListenerEventDto(  SensorMeasurementEvent eventFromKafka) {
        return SensorMeasurementDto.builder()
                .sensorId(eventFromKafka.getSensorId())
                .sensorType(eventFromKafka.getType() )
                .value(eventFromKafka.getValue())
                .build();
    }
    public static Sensor toSensor(SensorMeasurementEvent eventFromKafka) {

        Sensor sensor = new  Sensor();
        sensor.setSensorId(eventFromKafka.getSensorId());
        sensor.setSensorType(eventFromKafka.getType().characteristic);
        sensor.setValue(eventFromKafka.getValue());

        return  sensor;
    }


}