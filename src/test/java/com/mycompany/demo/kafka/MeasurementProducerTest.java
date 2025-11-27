package com.mycompany.demo.kafka;

import com.mycompany.demo.warehouse.exception.WarehouseServiceException;
import com.mycompany.demo.warehouse.kafka.producer.MeasurementProducer;
import com.mycompany.demo.warehouse.model.SensorMeasurementEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MeasurementProducerTest {

    @Mock
    KafkaTemplate<String, SensorMeasurementEvent> kafkaTemplate;

    MeasurementProducer producer;

    @BeforeEach
    void init() {
        producer = new MeasurementProducer(kafkaTemplate);
        ReflectionTestUtils.setField(producer, "topicName", "topic-test");
    }

    @Test
    void positive_sendTemperature() {
        SensorMeasurementEvent e = SensorMeasurementEvent.builder().sensorId("t1").value(10.0).build();
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(new CompletableFuture<>());
        producer.sendMessage(e);
        verify(kafkaTemplate).send("topic-test", "t1", e);
    }

    @Test
    void positive_sendHumidity() {
        SensorMeasurementEvent e = SensorMeasurementEvent.builder().sensorId("h1").value(55.0).build();
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(new CompletableFuture<>());
        producer.sendMessage(e);
        verify(kafkaTemplate).send("topic-test", "h1", e);
    }

    @Test
    void positive_sendZeroValue() {
        SensorMeasurementEvent e = SensorMeasurementEvent.builder().sensorId("z1").value(0.0).build();
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(new CompletableFuture<>());
        producer.sendMessage(e);
        verify(kafkaTemplate).send("topic-test", "z1", e);
    }

    @Test
    void positive_sendLargeValue() {
        SensorMeasurementEvent e = SensorMeasurementEvent.builder().sensorId("big").value(99999.99).build();
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(new CompletableFuture<>());
        producer.sendMessage(e);
        verify(kafkaTemplate).send("topic-test", "big", e);
    }

    @Test
    void positive_sendWithNullType() {
        SensorMeasurementEvent e = SensorMeasurementEvent.builder().sensorId("x1").value(12.3).build();
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(new CompletableFuture<>());
        producer.sendMessage(e);
        verify(kafkaTemplate).send("topic-test", "x1", e);
    }


    @Test
    void negative_kafkaThrowsRuntimeException() {
        SensorMeasurementEvent e = SensorMeasurementEvent.builder().sensorId("err1").value(10.0).build();
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenThrow(new RuntimeException("fail"));
        assertThrows(WarehouseServiceException.class, () -> producer.sendMessage(e));
    }

    @Test
    void negative_nullSensorId() {
        SensorMeasurementEvent e = SensorMeasurementEvent.builder().sensorId(null).value(10.0).build();
        when(kafkaTemplate.send(anyString(), any(), any())).thenThrow(new RuntimeException("null key"));
        assertThrows(WarehouseServiceException.class, () -> producer.sendMessage(e));
    }


    @Test
    void negative_futureCompletionExceptionInsideCallback() {
        SensorMeasurementEvent e = SensorMeasurementEvent.builder().sensorId("fail2").value(88.0).build();
        CompletableFuture<SendResult<String, SensorMeasurementEvent>> future = new CompletableFuture<>();
        when(kafkaTemplate.send(anyString(), anyString(), any())).thenReturn(future);
        future.completeExceptionally(new RuntimeException("async fail"));
        producer.sendMessage(e);
        verify(kafkaTemplate).send("topic-test", "fail2", e);
    }

    @Test
    void negative_sendWithoutTopicSet() {
        MeasurementProducer p2 = new MeasurementProducer(kafkaTemplate);
        SensorMeasurementEvent e = SensorMeasurementEvent.builder().sensorId("nt").value(5).build();
        when(kafkaTemplate.send(null, "nt", e)).thenThrow(new RuntimeException());
        assertThrows(WarehouseServiceException.class, () -> p2.sendMessage(e));
    }
}
