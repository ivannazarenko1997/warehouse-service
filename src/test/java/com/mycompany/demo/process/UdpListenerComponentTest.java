package com.mycompany.demo.process;

import com.mycompany.demo.warehouse.config.UdpListenerProperties;
import com.mycompany.demo.warehouse.enums.SensorType;
import com.mycompany.demo.warehouse.exception.WarehouseServiceException;
import com.mycompany.demo.warehouse.mapper.EventListenerMapper;
import com.mycompany.demo.warehouse.kafka.producer.MeasurementProducer;
import com.mycompany.demo.warehouse.model.SensorMeasurementEvent;
import com.mycompany.demo.warehouse.process.UdpListenerComponent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class UdpListenerComponentTest {

    @Mock
    private MeasurementProducer producer;

    @Mock
    private UdpListenerProperties properties;

    private UdpListenerComponent component;

    @BeforeEach
    void setUp() {
        component = new UdpListenerComponent(producer, properties);
    }


    @Test
    void parseMeasurement_validTemperature_shouldParseCorrectly() {
        String message = "sensor_id=t1; value=35.3";

        SensorMeasurementEvent datagram =
                invokeParseMeasurement(message, SensorType.TEMPERATURE);

        assertThat(datagram).isNotNull();
        assertThat(datagram.getSensorId()).isEqualTo("t1");
        assertThat(datagram.getType()).isEqualTo(SensorType.TEMPERATURE);
        assertThat(datagram.getValue()).isEqualTo(35.3);
    }


    @Test
    void parseMeasurement_validTemperature_shouldParseCorrectly1() {
        String message = "sensor_id=h1; value=35.3";

        SensorMeasurementEvent datagram =
                invokeParseMeasurement(message, SensorType.HUMIDITY);

        assertThat(datagram).isNotNull();
        assertThat(datagram.getSensorId()).isEqualTo("h1");
        assertThat(datagram.getType()).isEqualTo(SensorType.HUMIDITY);
        assertThat(datagram.getValue()).isEqualTo(35.3);
    }

    @Test
    void parseMeasurement_invalidPattern_shouldThrowWarehouseServiceException() {
        String message = "bad message without expected pattern";

         assertThrows(
                WarehouseServiceException.class,
                () -> invokeParseMeasurement(message, SensorType.TEMPERATURE)
        );
    }

    @Test
    void parseMeasurement_invalidNumber_shouldThrowWarehouseServiceException() {
        String message = "sensor_id=t1; value=abc";

         assertThrows(
                WarehouseServiceException.class,
                () -> invokeParseMeasurement(message, SensorType.TEMPERATURE)
        );
    }

    @Test
    void processMessage_validMessage_shouldSendToProducer() {
        String message = "sensor_id=t1; value=30.5";
        SensorMeasurementEvent fakeEvent =
                new SensorMeasurementEvent("t1", SensorType.TEMPERATURE, 30.5);

        try (MockedStatic<EventListenerMapper> mocked =
                     mockStatic(EventListenerMapper.class)) {

            mocked.when(() -> EventListenerMapper.toListenerEvent(any(SensorMeasurementEvent.class)))
                    .thenReturn(fakeEvent);

            Mono<Void> mono = invokeProcessMessage(message, SensorType.TEMPERATURE);

            StepVerifier.create(mono).verifyComplete();

            verify(producer).sendMessage(fakeEvent);
        }
    }

    @Test
    void processMessage_invalidMessage_shouldThrowAndNotCallProducer() {
        String badMessage = "invalid message";

        assertThrows(
                WarehouseServiceException.class,
                () -> invokeProcessMessage(badMessage, SensorType.HUMIDITY)
        );

        verify(producer, never()).sendMessage(any());
    }

    private SensorMeasurementEvent invokeParseMeasurement(String message, SensorType type) {
        try {
            Method method = UdpListenerComponent.class
                    .getDeclaredMethod("parseMeasurement", String.class, SensorType.class);
            method.setAccessible(true);
            return (SensorMeasurementEvent) method.invoke(component, message, type);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtime) {
                throw runtime; // let JUnit see WarehouseServiceException directly
            }
            throw new RuntimeException(cause);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Mono<Void> invokeProcessMessage(String message, SensorType type) {
        try {
            Method method = UdpListenerComponent.class
                    .getDeclaredMethod("processMessage", String.class, SensorType.class);
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            Mono<Void> result = (Mono<Void>) method.invoke(component, message, type);
            return result;
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException runtime) {
                throw runtime;
            }
            throw new RuntimeException(cause);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
