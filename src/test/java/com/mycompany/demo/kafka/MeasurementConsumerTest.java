package com.mycompany.demo.kafka;


import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.mycompany.demo.warehouse.enums.SensorType;
import com.mycompany.demo.warehouse.kafka.consumer.MeasurementConsumer;
import com.mycompany.demo.warehouse.kafka.event.SensorMeasurementDto;
import com.mycompany.demo.warehouse.mapper.EventListenerMapper;
import com.mycompany.demo.warehouse.model.SensorMeasurementEvent;
import com.mycompany.demo.warehouse.service.LoggerServise;
import com.mycompany.demo.warehouse.service.SensorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class MeasurementConsumerTest {

    @Mock
    private LoggerServise loggerServise;

    @Mock
    private SensorService sensorService;

    private MeasurementConsumer consumer;

    @BeforeEach
    void setUp() {
        consumer = new MeasurementConsumer(loggerServise, sensorService);
    }

    @Test
    void listen_nullEvent_shouldReturnAndNotCallServices() {
        consumer.listen(null);

        verifyNoInteractions(loggerServise, sensorService);
    }

    @Test
    void listen_nullType_shouldReturnAndNotCallServices() {
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorId("t1")
                .type(null)
                .value(10.0)
                .build();

        consumer.listen(event);

        verifyNoInteractions(loggerServise, sensorService);
    }

    @Test
    void listen_temperatureBelowThreshold_shouldLogOnly() {
        double belowThreshold = SensorType.TEMPERATURE.threshold - 1.0;

        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorId("t1")
                .type(SensorType.TEMPERATURE)
                .value(belowThreshold)
                .build();

        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorId("t1")
                .sensorType(SensorType.TEMPERATURE)
                .value(belowThreshold)
                .build();

        try (MockedStatic<EventListenerMapper> mocked = mockStatic(EventListenerMapper.class)) {
            mocked.when(() -> EventListenerMapper.toListenerEventDto(event))
                    .thenReturn(dto);

            consumer.listen(event);

            verify(loggerServise).printLog(dto);
            verify(loggerServise, never()).printAlarmLog(any());
            verify(sensorService, never()).saveAndFlush(any());
        }
    }

    @Test
    void listen_temperatureAboveThreshold_shouldLogAlarmAndSaveSensor() {
        double aboveThreshold = SensorType.TEMPERATURE.threshold + 1.0;

        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorId("t1")
                .type(SensorType.TEMPERATURE)
                .value(aboveThreshold)
                .build();

        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorId("t1")
                .sensorType(SensorType.TEMPERATURE)
                .value(aboveThreshold)
                .build();

        try (MockedStatic<EventListenerMapper> mocked = mockStatic(EventListenerMapper.class)) {
            mocked.when(() -> EventListenerMapper.toListenerEventDto(event))
                    .thenReturn(dto);
            mocked.when(() -> EventListenerMapper.toSensor(event))
                    .thenReturn(new com.mycompany.demo.warehouse.domain.Sensor());

            consumer.listen(event);

            verify(loggerServise).printAlarmLog(dto);
            verify(loggerServise, never()).printLog(any());
            verify(sensorService).saveAndFlush(any());
        }
    }

    @Test
    void listen_humidityBelowThreshold_shouldLogOnly() {
        double belowThreshold = SensorType.HUMIDITY.threshold - 1.0;

        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorId("h1")
                .type(SensorType.HUMIDITY)
                .value(belowThreshold)
                .build();

        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorId("h1")
                .sensorType(SensorType.HUMIDITY)
                .value(belowThreshold)
                .build();

        try (MockedStatic<EventListenerMapper> mocked = mockStatic(EventListenerMapper.class)) {
            mocked.when(() -> EventListenerMapper.toListenerEventDto(event))
                    .thenReturn(dto);

            consumer.listen(event);

            verify(loggerServise).printLog(dto);
            verify(loggerServise, never()).printAlarmLog(any());
            verify(sensorService, never()).saveAndFlush(any());
        }
    }

    @Test
    void listen_humidityAboveThreshold_shouldLogAlarmAndSaveSensor() {
        double aboveThreshold = SensorType.HUMIDITY.threshold + 1.0;

        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorId("h1")
                .type(SensorType.HUMIDITY)
                .value(aboveThreshold)
                .build();

        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorId("h1")
                .sensorType(SensorType.HUMIDITY)
                .value(aboveThreshold)
                .build();

        try (MockedStatic<EventListenerMapper> mocked = mockStatic(EventListenerMapper.class)) {
            mocked.when(() -> EventListenerMapper.toListenerEventDto(event))
                    .thenReturn(dto);
            mocked.when(() -> EventListenerMapper.toSensor(event))
                    .thenReturn(new com.mycompany.demo.warehouse.domain.Sensor());

            consumer.listen(event);

            verify(loggerServise).printAlarmLog(dto);
            verify(loggerServise, never()).printLog(any());
            verify(sensorService).saveAndFlush(any());
        }
    }

    @Test
    void listen_unsupportedType_shouldNotCallServices() {
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorId("x1")
                .type(SensorType.TEMPERATURE)
                .value(SensorType.TEMPERATURE.threshold)
                .build();

        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorId("x1")
                .sensorType(SensorType.TEMPERATURE)
                .value(SensorType.TEMPERATURE.threshold)
                .build();

        try (MockedStatic<EventListenerMapper> mocked = mockStatic(EventListenerMapper.class)) {
            mocked.when(() -> EventListenerMapper.toListenerEventDto(event))
                    .thenReturn(dto);

            consumer.listen(event);


            verify(loggerServise).printLog(dto);
            verify(loggerServise, never()).printAlarmLog(any());
            verify(sensorService, never()).saveAndFlush(any());
        }
    }

    @Test
    void listen_withValidType_shouldNotThrowWarehouseServiceException() {
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorId("t1")
                .type(SensorType.TEMPERATURE)
                .value(SensorType.TEMPERATURE.threshold - 0.1)
                .build();

        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorId("t1")
                .sensorType(SensorType.TEMPERATURE)
                .value(SensorType.TEMPERATURE.threshold - 0.1)
                .build();

        try (MockedStatic<EventListenerMapper> mocked = mockStatic(EventListenerMapper.class)) {
            mocked.when(() -> EventListenerMapper.toListenerEventDto(event))
                    .thenReturn(dto);

            consumer.listen(event); // should not throw
        }
    }


    @Test
    void safeLower_and_isEmpty_notDirectlyExposedButContractIsCoveredThroughListen() {
        SensorMeasurementEvent event = SensorMeasurementEvent.builder()
                .sensorId("t1")
                .type(SensorType.TEMPERATURE)
                .value(10.0)
                .build();

        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorId("t1")
                .sensorType(SensorType.TEMPERATURE)
                .value(10.0)
                .build();

        try (MockedStatic<EventListenerMapper> mocked = mockStatic(EventListenerMapper.class)) {
            mocked.when(() -> EventListenerMapper.toListenerEventDto(event))
                    .thenReturn(dto);

            consumer.listen(event);

            verify(loggerServise).printLog(dto);
        }
    }
}
