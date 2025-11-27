package com.mycompany.demo.service;


import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.mycompany.demo.warehouse.enums.SensorType;
import com.mycompany.demo.warehouse.kafka.event.SensorMeasurementDto;
import com.mycompany.demo.warehouse.service.impl.LoggerServiseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoggerServiseImplTest {

    private LoggerServiseImpl service;
    private ListAppender<ILoggingEvent> appender;

    @BeforeEach
    void setup() {
        service = new LoggerServiseImpl();
        Logger logger = (Logger) LoggerFactory.getLogger(LoggerServiseImpl.class);
        appender = new ListAppender<>();
        appender.start();
        logger.detachAndStopAllAppenders();
        logger.addAppender(appender);
    }

    @Test
    void printLog_temperature_positive() {
        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorType(SensorType.TEMPERATURE)
                .value(30.5)
                .build();
        service.printLog(dto);
        List<ILoggingEvent> logs = appender.list;
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getFormattedMessage()).contains("No ALARM.Standart log [TEMPERATURE]");
    }

    @Test
    void printLog_humidity_positive() {
        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorType(SensorType.HUMIDITY)
                .value(45.0)
                .build();
        service.printLog(dto);
        List<ILoggingEvent> logs = appender.list;
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getFormattedMessage()).contains("No ALARM.Standart log [HUMIDITY]");
    }

    @Test
    void printAlarmLog_temperature_positive() {
        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorType(SensorType.TEMPERATURE)
                .value(50.0)
                .build();
        service.printAlarmLog(dto);
        List<ILoggingEvent> logs = appender.list;
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getFormattedMessage()).contains("🚨 ALARM! [TEMPERATURE]");
    }

    @Test
    void printAlarmLog_humidity_positive() {
        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorType(SensorType.HUMIDITY)
                .value(80.0)
                .build();
        service.printAlarmLog(dto);
        List<ILoggingEvent> logs = appender.list;
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getFormattedMessage()).contains("🚨 ALARM! [HUMIDITY]");
    }

    @Test
    void printAlarmLog_extremeValue_positive() {
        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorType(SensorType.TEMPERATURE)
                .value(9999.99)
                .build();
        service.printAlarmLog(dto);
        List<ILoggingEvent> logs = appender.list;
        assertThat(logs).hasSize(1);
        assertThat(logs.get(0).getFormattedMessage()).contains("9999.99");
    }

    @Test
    void printLog_nullSensorType_negative() {
        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorType(null)
                .value(20)
                .build();
        assertThrows(NullPointerException.class, () -> service.printLog(dto));
    }

    @Test
    void printAlarmLog_nullSensorType_negative() {
        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorType(null)
                .value(20)
                .build();
        assertThrows(NullPointerException.class, () -> service.printAlarmLog(dto));
    }

    @Test
    void printLog_valueNaN_negative() {
        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorType(SensorType.HUMIDITY)
                .value(Double.NaN)
                .build();
        service.printLog(dto);
        List<ILoggingEvent> logs = appender.list;
        assertThat(logs.get(0).getFormattedMessage()).contains("value=NaN");
    }

    @Test
    void printLog_valueInfinity_negative() {
        SensorMeasurementDto dto = SensorMeasurementDto.builder()
                .sensorType(SensorType.TEMPERATURE)
                .value(Double.POSITIVE_INFINITY)
                .build();
        service.printLog(dto);
        List<ILoggingEvent> logs = appender.list;
        assertThat(logs.get(0).getFormattedMessage()).contains("value=Infinity");
    }

    @Test
    void printAlarmLog_nullDto_negative() {
        assertThrows(NullPointerException.class, () -> service.printAlarmLog(null));
    }
}
