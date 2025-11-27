package com.mycompany.demo.warehouse.kafka.consumer;



import com.mycompany.demo.warehouse.domain.Sensor;
import com.mycompany.demo.warehouse.enums.SensorType;
import com.mycompany.demo.warehouse.exception.WarehouseServiceException;
import com.mycompany.demo.warehouse.kafka.event.SensorMeasurementDto;
import com.mycompany.demo.warehouse.mapper.EventListenerMapper;

import com.mycompany.demo.warehouse.model.SensorMeasurementEvent;
import com.mycompany.demo.warehouse.service.LoggerServise;
import com.mycompany.demo.warehouse.service.SensorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class MeasurementConsumer {


    private final LoggerServise loggerServise;
    private final SensorService sensorService;
    @KafkaListener(topics = "${app.kafka.topics.measurements.events}", groupId = "${spring.kafka.consumer.group-id}")
    public void listen(SensorMeasurementEvent event) {
        if (event == null || event.getType() ==null) {
            log.error("Income event to kafka consumer is empty");
            return;
        }
        final String type  = safeLower(event.getType().name() );
        if (isEmpty(type) ) {
            log.error("Income event type is empty for event:{}",event);
            throw new WarehouseServiceException("Income event type is empty for event");
        }
        switch (type) {
            case "temperature","humidity" -> handleSensor(event);
            default -> {
               log.error("Unsupported event type for Event={}",  event );
            }
        }
    }
    private boolean isEmpty(String check) {
        return StringUtils.isEmpty(check);
    }

    private void handleSensor(SensorMeasurementEvent event) {
        try {
            SensorMeasurementDto sensorMeasurementDto = EventListenerMapper.toListenerEventDto(event);

            if (isAlarm(event)) {
                loggerServise.printAlarmLog(sensorMeasurementDto);
                Sensor sensor = EventListenerMapper.toSensor(event);
                sensorService.saveAndFlush(sensor);
            } else {
                // just alarms    sensorService.saveAndFlush(sensor);
                loggerServise.printLog(sensorMeasurementDto);
            }

        } catch (Exception e) {
            log.error("Failed to upsert document for event id={}. Event={}", event.getValue(), event, e);
        }
    }

    private boolean isAlarm( SensorMeasurementEvent event) {
        SensorType type = event.getType();
        double value = event.getValue();
        double threshold = type.threshold;
        return (value > threshold);
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.toLowerCase();
    }
}