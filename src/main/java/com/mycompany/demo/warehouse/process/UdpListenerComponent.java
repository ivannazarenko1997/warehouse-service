package com.mycompany.demo.warehouse.process;

import com.mycompany.demo.warehouse.config.UdpListenerProperties;
import com.mycompany.demo.warehouse.enums.SensorType;
import com.mycompany.demo.warehouse.exception.WarehouseServiceException;
import com.mycompany.demo.warehouse.mapper.EventListenerMapper;
import com.mycompany.demo.warehouse.kafka.producer.MeasurementProducer;
import com.mycompany.demo.warehouse.model.SensorMeasurementEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.netty.udp.UdpServer;

import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class UdpListenerComponent {

    private final MeasurementProducer producer;
    private final UdpListenerProperties properties;

    private static final Pattern PATTERN =
            Pattern.compile("sensor_id=(?<id>[^;]+);\\s*value=(?<val>-?\\d+(?:\\.\\d+)?)");
    public UdpListenerComponent(MeasurementProducer producer,
                                UdpListenerProperties properties) {
        this.producer = producer;
        this.properties = properties;
    }

    @PostConstruct
    public void startUdpListeners() {
        try {
            log.info("=== Starting Warehouse Service UDP Listeners ===");
            log.info("Configured UDP listeners: {}", properties.getListeners());

            for (UdpListenerProperties.ListenerConfig config : properties.getListeners()) {
                startUdpListener(config);
            }
        } catch(Exception e) {
            log.error("Cannot start Warehouse Service",e);
        }
    }

    private void startUdpListener(UdpListenerProperties.ListenerConfig config) {
        UdpServer.create()
                .host(config.getBindHost())
                .port(config.getPort())
                .handle((inbound, outbound) ->
                        inbound.receive()
                                .asString(StandardCharsets.UTF_8)
                                .flatMap(msg -> processMessage(msg, config.getType()))
                                .then()
                )
                .bind()
                .doOnSuccess(conn ->
                        log.info("UDP listener ready for [{}] on {}:{}",
                                config.getType(), config.getBindHost(), config.getPort()))
                .doOnError(ex ->
                        log.error("Failed to bind UDP listener for [{}] on {}:{}",
                                config.getType(), config.getBindHost(), config.getPort(), ex))
                .subscribe();
    }

    private Mono<Void> processMessage(String rawMessage, SensorType type) {

            String message = rawMessage.trim();
            log.info("UDP RECEIVE [{}]: '{}'", type, message);

        SensorMeasurementEvent datagram = parseMeasurement(message, type);
            if (datagram == null) {
                log.warn("Could not parse UDP message for type [{}]: '{}'", type, message);
                return Mono.empty();
            }

            SensorMeasurementEvent event = EventListenerMapper.toListenerEvent(datagram);
            log.debug("Parsed SensorMeasurementEvent: {}", event);

            return Mono.fromRunnable(() -> producer.sendMessage(event));

    }

    private SensorMeasurementEvent parseMeasurement(String message, SensorType type) {
        Matcher matcher = PATTERN.matcher(message);

        if (!matcher.find()) {
            log.warn("UDP parse failed: message does not match pattern. " +
                    "message='{}', pattern='{}'", message, PATTERN.pattern());
            throw new WarehouseServiceException("UDP parse failed");
        }

        try {
            String sensorId = matcher.group("id").trim();
            String rawVal = matcher.group("val").trim();

            rawVal = rawVal.replace(',', '.');

            double value = Double.parseDouble(rawVal);

            return new SensorMeasurementEvent(sensorId, type, value);
        } catch (NumberFormatException e) {
            log.error("Failed to parse numeric value from UDP message. message='{}'", message, e);
            throw new WarehouseServiceException("Failed to parse numeric value from UDP message");
        } catch (IllegalArgumentException e) {
            // In case named groups are missing / mis-typed
            log.error("Failed to extract groups from UDP message. message='{}'", message, e);
            throw new WarehouseServiceException("Failed to extract groups from UDP message. message");
        }
    }
}
