package com.mycompany.demo.warehouse.repository;

import com.mycompany.demo.warehouse.domain.Sensor;

import java.time.LocalDate;
import java.time.ZoneOffset;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration(classes = {SensorRepository.class})
@EnableAutoConfiguration
@EntityScan(basePackages = {"com.mycompany.demo.warehouse.domain"})
@DataJpaTest
class SensorRepositoryDiffblueTest {
    @Autowired
    private SensorRepository sensorRepository;


    @Test
    @DisplayName("Test findBySensorTypeContainingIgnoreCase(String, Pageable)")
    @Disabled("TODO: Complete this test")
    void testFindBySensorTypeContainingIgnoreCase() {

        Sensor sensor = new Sensor();
        sensor.setAlarm(true);
        sensor.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sensor.setDescription("The characteristics of someone or something");
        sensor.setSensorId("42");
        sensor.setSensorType("Sensor Type");
        sensor.setValue(10.0d);

        Sensor sensor2 = new Sensor();
        sensor2.setAlarm(false);
        sensor2.setCreatedAt(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant());
        sensor2.setDescription("Description");
        sensor2.setSensorId("Sensor Id");
        sensor2.setSensorType("42");
        sensor2.setValue(0.5d);
        sensorRepository.save(sensor);
        sensorRepository.save(sensor2);

        // Act
        sensorRepository.findBySensorTypeContainingIgnoreCase("Sensor Type", Pageable.unpaged());
    }
}
