package com.mycompany.demo.controller;


import com.mycompany.demo.warehouse.controller.SensorMeasurementController;
import com.mycompany.demo.warehouse.dto.AlarmResponseDto;
import com.mycompany.demo.warehouse.service.SensorService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SensorMeasurementControllerTest {

    @Mock
    private SensorService sensorService;

    @InjectMocks
    private SensorMeasurementController controller;


    @Test
    void getAlarms_defaultPageableAndNoSensorType_shouldReturnPageFromService() {
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());

        AlarmResponseDto dto = AlarmResponseDto.builder()
                .id(1L)
                .sensorId("t1")
                .sensorType("TEMPERATURE")
                .value(30.0)
                .threshold(35.0)
                .alarm(false)
                .createdAt(Instant.now())
                .build();

        Page<AlarmResponseDto> expectedPage = new PageImpl<>(List.of(dto), pageable, 1);

        when(sensorService.getAlarms(isNull(), eq(pageable))).thenReturn(expectedPage);

        Page<AlarmResponseDto> actual = controller.getAlarms(pageable, null);

        assertThat(actual).isSameAs(expectedPage);
        verify(sensorService, times(1)).getAlarms(null, pageable);
    }

    @Test
    void getAlarms_withSensorTypeTemperature_shouldDelegateWithSensorType() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<AlarmResponseDto> expectedPage = new PageImpl<>(List.of());
        when(sensorService.getAlarms(eq("TEMPERATURE"), eq(pageable))).thenReturn(expectedPage);

        Page<AlarmResponseDto> result = controller.getAlarms(pageable, "TEMPERATURE");

        assertThat(result).isSameAs(expectedPage);
        verify(sensorService).getAlarms("TEMPERATURE", pageable);
    }

    @Test
    void getAlarms_withSensorTypeHumidity_shouldDelegateWithSensorType() {
        Pageable pageable = PageRequest.of(1, 5, Sort.by("createdAt").ascending());

        AlarmResponseDto dto1 = AlarmResponseDto.builder()
                .id(2L)
                .sensorId("h1")
                .sensorType("HUMIDITY")
                .value(60.0)
                .threshold(50.0)
                .alarm(true)
                .createdAt(Instant.now())
                .build();

        AlarmResponseDto dto2 = AlarmResponseDto.builder()
                .id(3L)
                .sensorId("h2")
                .sensorType("HUMIDITY")
                .value(55.5)
                .threshold(50.0)
                .alarm(true)
                .createdAt(Instant.now())
                .build();

        Page<AlarmResponseDto> expectedPage = new PageImpl<>(List.of(dto1, dto2), pageable, 2);

        when(sensorService.getAlarms(eq("HUMIDITY"), eq(pageable))).thenReturn(expectedPage);

        Page<AlarmResponseDto> result = controller.getAlarms(pageable, "HUMIDITY");

        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getSensorId()).isEqualTo("h1");
        assertThat(result.getContent().get(1).getSensorId()).isEqualTo("h2");
        verify(sensorService).getAlarms("HUMIDITY", pageable);
    }

    @Test
    void getAlarms_shouldPassPageableWithDefaultSortCreatedAtDesc() {
        Pageable inputPageable = PageRequest.of(0, 20, Sort.by("createdAt").descending());

        when(sensorService.getAlarms(isNull(), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(), inputPageable, 0));

        controller.getAlarms(inputPageable, null);

        ArgumentCaptor<Pageable> captor = ArgumentCaptor.forClass(Pageable.class);
        verify(sensorService).getAlarms(isNull(), captor.capture());

        Pageable captured = captor.getValue();
        assertThat(captured.getPageNumber()).isZero();
        assertThat(captured.getPageSize()).isEqualTo(20);
        Sort.Order order = captured.getSort().getOrderFor("createdAt");
        assertThat(order).isNotNull();
        assertThat(order.getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void getAlarms_serviceReturnsEmptyPage_shouldReturnEmptyPage() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<AlarmResponseDto> emptyPage = Page.empty(pageable);

        when(sensorService.getAlarms(isNull(), eq(pageable))).thenReturn(emptyPage);

        Page<AlarmResponseDto> result = controller.getAlarms(pageable, null);

        assertThat(result.getTotalElements()).isZero();
        assertThat(result.getContent()).isEmpty();
        verify(sensorService).getAlarms(null, pageable);
    }

    @Test
    void getAlarms_whenServiceThrowsRuntimeException_shouldPropagate() {
        Pageable pageable = PageRequest.of(0, 10);

        when(sensorService.getAlarms(any(), eq(pageable)))
                .thenThrow(new RuntimeException("DB not available"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> controller.getAlarms(pageable, "TEMPERATURE")
        );

        assertThat(ex.getMessage()).isEqualTo("DB not available");
        verify(sensorService).getAlarms("TEMPERATURE", pageable);
    }

    @Test
    void getAlarms_whenPageableIsNull_shouldCallServiceWithNullPageable() {
        when(sensorService.getAlarms(eq("TEMPERATURE"), isNull()))
                .thenReturn(null);

        Page<AlarmResponseDto> result = controller.getAlarms(null, "TEMPERATURE");

        assertThat(result).isNull();
        verify(sensorService).getAlarms("TEMPERATURE", null);
    }

    @Test
    void getAlarms_whenServiceReturnsNull_shouldReturnNull() {
        Pageable pageable = PageRequest.of(0, 10);

        when(sensorService.getAlarms(eq("UNKNOWN"), eq(pageable)))
                .thenReturn(null);

        Page<AlarmResponseDto> result = controller.getAlarms(pageable, "UNKNOWN");

        assertThat(result).isNull();
        verify(sensorService).getAlarms("UNKNOWN", pageable);
    }

    @Test
    void getAlarms_whenSensorTypeBlank_shouldStillDelegateToService() {
        Pageable pageable = PageRequest.of(0, 10);

        Page<AlarmResponseDto> page = new PageImpl<>(List.of());
        when(sensorService.getAlarms(eq(""), eq(pageable))).thenReturn(page);

        Page<AlarmResponseDto> result = controller.getAlarms(pageable, "");

        assertThat(result).isSameAs(page);
        verify(sensorService).getAlarms("", pageable);
    }

    @Test
    void getAlarms_whenServiceThrowsCustomException_shouldPropagate() {
        Pageable pageable = PageRequest.of(0, 10);

        RuntimeException ex0 = new RuntimeException("Internal service error");
        when(sensorService.getAlarms(isNull(), eq(pageable))).thenThrow(ex0);

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> controller.getAlarms(pageable, null)
        );

        assertThat(ex.getMessage()).isEqualTo("Internal service error");
        verify(sensorService).getAlarms(null, pageable);
    }
}
