package com.mycompany.demo.mapper;

import com.mycompany.demo.warehouse.domain.Sensor;
import com.mycompany.demo.warehouse.dto.AlarmResponseDto;
import com.mycompany.demo.warehouse.mapper.AlarmResponseMapper;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class AlarmResponseMapperTest {

    // --------------------------
    // 1) NORMAL MAPPING
    // --------------------------
    @Test
    void fromEntity_shouldMapAllFieldsCorrectly() {
        Sensor sensor = new Sensor();
        sensor.setId(1L);
        sensor.setSensorId("t1");
        sensor.setSensorType("TEMPERATURE");
        sensor.setValue(35.7);
        sensor.setAlarm(true);
        sensor.setDescription("High temp");
        Instant created = Instant.now();
        sensor.setCreatedAt(created);

        AlarmResponseDto dto = AlarmResponseMapper.fromEntity(sensor);

        assertThat(dto).isNotNull();
        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getSensorId()).isEqualTo("t1");
        assertThat(dto.getSensorType()).isEqualTo("TEMPERATURE");
        assertThat(dto.getValue()).isEqualTo(35.7);
        assertThat(dto.getAlarm()).isTrue();
        assertThat(dto.getThreshold()).isEqualTo(35.7);
        assertThat(dto.getMessage()).isEqualTo("High temp");
        assertThat(dto.getCreatedAt()).isEqualTo(created);
    }

    // --------------------------
    // 2) NULL ENTITY
    // --------------------------
    @Test
    void fromEntity_nullInput_shouldReturnNull() {
        AlarmResponseDto dto = AlarmResponseMapper.fromEntity(null);
        assertThat(dto).isNull();
    }

    // --------------------------
    // 3) ALARM = FALSE
    // --------------------------
    @Test
    void fromEntity_alarmFalse_shouldMapCorrectly() {
        Sensor sensor = new Sensor();
        sensor.setId(2L);
        sensor.setSensorId("h1");
        sensor.setSensorType("HUMIDITY");
        sensor.setValue(48.0);
        sensor.setAlarm(false);
        sensor.setDescription("Normal humidity");
        Instant created = Instant.now();
        sensor.setCreatedAt(created);

        AlarmResponseDto dto = AlarmResponseMapper.fromEntity(sensor);

        assertThat(dto).isNotNull();
        assertThat(dto.getAlarm()).isFalse();
        assertThat(dto.getThreshold()).isEqualTo(48.0);
        assertThat(dto.getMessage()).isEqualTo("Normal humidity");
    }

    // --------------------------
    // 4) NO DESCRIPTION & NO SENSOR TYPE
    // --------------------------
    @Test
    void fromEntity_missingOptionalFields_shouldMapNullsCorrectly() {
        Sensor sensor = new Sensor();
        sensor.setId(3L);
        sensor.setSensorId("x9");
        sensor.setSensorType(null);       // test nullable type
        sensor.setValue(12.5);
        sensor.setAlarm(true);
        sensor.setDescription(null);      // test nullable description
        Instant created = Instant.now();
        sensor.setCreatedAt(created);

        AlarmResponseDto dto = AlarmResponseMapper.fromEntity(sensor);

        assertThat(dto).isNotNull();
        assertThat(dto.getSensorType()).isNull();
        assertThat(dto.getMessage()).isNull();
        assertThat(dto.getValue()).isEqualTo(12.5);
        assertThat(dto.getThreshold()).isEqualTo(12.5);
    }

    // --------------------------
    // 5) BOUNDARY VALUES (NEGATIVE, ZERO, VERY LARGE)
    // --------------------------
    @Test
    void fromEntity_boundaryValues_shouldMapCorrectly() {
        Sensor sensor = new Sensor();
        sensor.setId(4L);
        sensor.setSensorId("b1");
        sensor.setSensorType("TEST");
        sensor.setValue(-99.999);     // negative
        sensor.setAlarm(true);
        sensor.setDescription("Negative value test");
        Instant created = Instant.now();
        sensor.setCreatedAt(created);

        AlarmResponseDto dto = AlarmResponseMapper.fromEntity(sensor);

        assertThat(dto).isNotNull();
        assertThat(dto.getValue()).isEqualTo(-99.999);
        assertThat(dto.getThreshold()).isEqualTo(-99.999);
        assertThat(dto.getMessage()).isEqualTo("Negative value test");
    }
}
