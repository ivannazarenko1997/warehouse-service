package com.mycompany.demo.warehouse.kafka.producer;



import com.mycompany.demo.warehouse.exception.WarehouseServiceException;
import com.mycompany.demo.warehouse.model.SensorMeasurementEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MeasurementProducer {

    private final KafkaTemplate<String, SensorMeasurementEvent> kafkaTemplate;

    @Value("${app.kafka.topics.measurements.events}")
    private String topicName;

    public MeasurementProducer(KafkaTemplate<String, SensorMeasurementEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMessage(SensorMeasurementEvent measurement) {
        try {
            kafkaTemplate.send(topicName, measurement.getSensorId(), measurement)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.error("KAFKA PUBLISH SUCCESS: Sent to %s offset %d -> %s%n",
                                    result.getRecordMetadata().topic(), result.getRecordMetadata().offset(), measurement);
                        } else {
                            log.error("KAFKA PUBLISH FAILED for " + measurement.getSensorId() + ": " + ex.getMessage());
                        }
                    });
        } catch(Exception e) {
            log.error("Cannot send data to kafka",e);
            throw new WarehouseServiceException("Cannot send data to kafka",e);
        }
    }
}