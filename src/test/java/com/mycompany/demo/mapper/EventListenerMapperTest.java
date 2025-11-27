package com.mycompany.demo.mapper;


import com.mycompany.demo.warehouse.domain.Sensor;
import com.mycompany.demo.warehouse.enums.SensorType;
import com.mycompany.demo.warehouse.kafka.event.SensorMeasurementDto;
import com.mycompany.demo.warehouse.mapper.EventListenerMapper;
import com.mycompany.demo.warehouse.model.SensorMeasurementEvent;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EventListenerMapperTest {

    @Test
    void toListenerEvent_shouldMapDatagramToEvent_forTemperature() {
        SensorMeasurementEvent datagram = new SensorMeasurementEvent(
                "t1",
                SensorType.TEMPERATURE,
                35.3
        );

        SensorMeasurementEvent event = EventListenerMapper.toListenerEvent(datagram);

        assertThat(event).isNotNull();
        assertThat(event.getSensorId()).isEqualTo("t1");
        assertThat(event.getType()).isEqualTo(SensorType.TEMPERATURE);
        assertThat(event.getValue()).isEqualTo(35.3);
    }


    @Test
    void toListenerEvent_shouldMapDatagramToEvent_forHumidity() {
        SensorMeasurementEvent datagram = new SensorMeasurementEvent(
                "h1",
                SensorType.HUMIDITY,
                60.2
        );

        SensorMeasurementEvent event = EventListenerMapper.toListenerEvent(datagram);

        assertThat(event).isNotNull();
        assertThat(event.getSensorId()).isEqualTo("h1");
        assertThat(event.getType()).isEqualTo(SensorType.HUMIDITY);
        assertThat(event.getValue()).isEqualTo(60.2);
    }

    @Test
    void toListenerEvent_shouldMapNegativeValue() {
        SensorMeasurementEvent datagram = new SensorMeasurementEvent(
                "t2",
                SensorType.TEMPERATURE,
                -5.0
        );

        SensorMeasurementEvent event = EventListenerMapper.toListenerEvent(datagram);

        assertThat(event).isNotNull();
        assertThat(event.getValue()).isEqualTo(-5.0);
        assertThat(event.getSensorId()).isEqualTo("t2");
    }

    @Test
    void toListenerEventDto_shouldMapEventToDto() {
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorId("t1")
                .type(SensorType.TEMPERATURE)
                .value(36.6)
                .build();

        SensorMeasurementDto dto = EventListenerMapper.toListenerEventDto(event);

        assertThat(dto).isNotNull();
        assertThat(dto.getSensorId()).isEqualTo("t1");
        assertThat(dto.getSensorType()).isEqualTo(SensorType.TEMPERATURE);
        assertThat(dto.getValue()).isEqualTo(36.6);
    }

    @Test
    void toListenerEventDto_shouldMapHumidityCorrectly() {
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorId("h1")
                .type(SensorType.HUMIDITY)
                .value(49.9)
                .build();

        SensorMeasurementDto dto = EventListenerMapper.toListenerEventDto(event);

        assertThat(dto).isNotNull();
        assertThat(dto.getSensorId()).isEqualTo("h1");
        assertThat(dto.getSensorType()).isEqualTo(SensorType.HUMIDITY);
        assertThat(dto.getValue()).isEqualTo(49.9);
    }

    @Test
    void toSensor_shouldMapEventToSensor() {
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorId("t1")
                .type(SensorType.TEMPERATURE)
                .value(35.0)
                .build();

        Sensor sensor = EventListenerMapper.toSensor(event);

        assertThat(sensor).isNotNull();
        assertThat(sensor.getSensorId()).isEqualTo("t1");
        assertThat(sensor.getValue()).isEqualTo(35.0);
    }


    @Test
    void toSensor_shouldUseSensorTypeCharacteristic() {
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorId("h1")
                .type(SensorType.HUMIDITY)
                .value(55.5)
                .build();

        Sensor sensor = EventListenerMapper.toSensor(event);

        assertThat(sensor).isNotNull();
        assertThat(sensor.getSensorType())
                .isEqualTo(SensorType.HUMIDITY.characteristic);
    }

    @Test
    void roundTrip_datagramToEventToDto_shouldKeepCoreData() {
        SensorMeasurementEvent datagram = new SensorMeasurementEvent(
                "t9",
                SensorType.TEMPERATURE,
                42.42
        );

        SensorMeasurementEvent event = EventListenerMapper.toListenerEvent(datagram);
        SensorMeasurementDto dto = EventListenerMapper.toListenerEventDto(event);

        assertThat(dto.getSensorId()).isEqualTo("t9");
        assertThat(dto.getSensorType()).isEqualTo(SensorType.TEMPERATURE);
        assertThat(dto.getValue()).isEqualTo(42.42);
    }

    @Test
    void roundTrip_eventToSensor_shouldPreserveIdTypeAndValue() {
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorId("x1")
                .type(SensorType.TEMPERATURE)
                .value(0.0)
                .build();

        Sensor sensor = EventListenerMapper.toSensor(event);

        assertThat(sensor.getSensorId()).isEqualTo("x1");
        assertThat(sensor.getSensorType()).isEqualTo(SensorType.TEMPERATURE.characteristic);
        assertThat(sensor.getValue()).isEqualTo(0.0);
    }

    @Test
    void toListenerEvent_nullDatagram_shouldThrowNullPointerException() {
        assertThrows(
                NullPointerException.class,
                () -> EventListenerMapper.toListenerEvent(null)
        );
    }

}
