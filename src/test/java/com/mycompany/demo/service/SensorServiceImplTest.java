package com.mycompany.demo.service;

import com.mycompany.demo.warehouse.domain.Sensor;
import com.mycompany.demo.warehouse.dto.AlarmResponseDto;
import com.mycompany.demo.warehouse.mapper.AlarmResponseMapper;
import com.mycompany.demo.warehouse.repository.SensorRepository;
import com.mycompany.demo.warehouse.service.impl.SensorServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mockStatic;

@ExtendWith(MockitoExtension.class)
class SensorServiceImplTest {

    @Mock
    private SensorRepository sensorRepository;

    @InjectMocks
    private SensorServiceImpl sensorService;

    @Test
    void getAlarms_withSensorType_positive() {
        String sensorType = "temp";
        Pageable pageable = PageRequest.of(0, 10);
        Sensor sensor = new Sensor();
        Page<Sensor> sensorPage = new PageImpl<>(List.of(sensor), pageable, 1);
        when(sensorRepository.findBySensorTypeContainingIgnoreCase(sensorType, pageable))
                .thenReturn(sensorPage);

        AlarmResponseDto dto = AlarmResponseDto.builder().sensorId("s1").build();

        try (MockedStatic<AlarmResponseMapper> mapperMock = mockStatic(AlarmResponseMapper.class)) {
            mapperMock.when(() -> AlarmResponseMapper.fromEntity(sensor)).thenReturn(dto);

            Page<AlarmResponseDto> result = sensorService.getAlarms(sensorType, pageable);

            assertEquals(1, result.getTotalElements());
            assertEquals(dto, result.getContent().get(0));
            verify(sensorRepository).findBySensorTypeContainingIgnoreCase(sensorType, pageable);
            verify(sensorRepository, never()).findAll(any(Pageable.class));
        }
    }

    @Test
    void getAlarms_withoutSensorType_positive() {
        String sensorType = null;
        Pageable pageable = PageRequest.of(0, 20);
        Sensor sensor1 = new Sensor();
        Sensor sensor2 = new Sensor();
        Page<Sensor> sensorPage = new PageImpl<>(List.of(sensor1, sensor2), pageable, 2);
        when(sensorRepository.findAll(pageable)).thenReturn(sensorPage);

        AlarmResponseDto dto1 = AlarmResponseDto.builder().sensorId("s1").build();
        AlarmResponseDto dto2 = AlarmResponseDto.builder().sensorId("s2").build();

        try (MockedStatic<AlarmResponseMapper> mapperMock = mockStatic(AlarmResponseMapper.class)) {
            mapperMock.when(() -> AlarmResponseMapper.fromEntity(sensor1)).thenReturn(dto1);
            mapperMock.when(() -> AlarmResponseMapper.fromEntity(sensor2)).thenReturn(dto2);

            Page<AlarmResponseDto> result = sensorService.getAlarms(sensorType, pageable);

            assertEquals(2, result.getTotalElements());
            assertTrue(result.getContent().containsAll(List.of(dto1, dto2)));
            verify(sensorRepository).findAll(pageable);
            verify(sensorRepository, never()).findBySensorTypeContainingIgnoreCase(anyString(), any());
        }
    }

    @Test
    void getAlarms_blankSensorType_positive() {
        String sensorType = "   ";
        Pageable pageable = PageRequest.of(1, 5);
        Sensor sensor = new Sensor();
        Page<Sensor> sensorPage = new PageImpl<>(List.of(sensor), pageable, 1);
        when(sensorRepository.findAll(pageable)).thenReturn(sensorPage);

        AlarmResponseDto dto = AlarmResponseDto.builder().sensorId("s1").build();

        try (MockedStatic<AlarmResponseMapper> mapperMock = mockStatic(AlarmResponseMapper.class)) {
            mapperMock.when(() -> AlarmResponseMapper.fromEntity(sensor)).thenReturn(dto);

            Page<AlarmResponseDto> result = sensorService.getAlarms(sensorType, pageable);

            assertEquals(6, result.getTotalElements());
            assertEquals(dto, result.getContent().get(0));
            verify(sensorRepository).findAll(pageable);
        }
    }

    @Test
    void getAlarms_emptyResult_positive() {
        String sensorType = "temp";
        Pageable pageable = PageRequest.of(0, 10);
        Page<Sensor> sensorPage = new PageImpl<>(List.of(), pageable, 0);
        when(sensorRepository.findBySensorTypeContainingIgnoreCase(sensorType, pageable))
                .thenReturn(sensorPage);

        try (MockedStatic<AlarmResponseMapper> mapperMock = mockStatic(AlarmResponseMapper.class)) {
            Page<AlarmResponseDto> result = sensorService.getAlarms(sensorType, pageable);

            assertEquals(0, result.getTotalElements());
            assertTrue(result.getContent().isEmpty());
            verify(sensorRepository).findBySensorTypeContainingIgnoreCase(sensorType, pageable);
        }
    }

    @Test
    void saveAndFlush_positive() {
        Sensor sensor = new Sensor();
        when(sensorRepository.saveAndFlush(sensor)).thenReturn(sensor);

        Sensor result = sensorService.saveAndFlush(sensor);

        assertSame(sensor, result);
        verify(sensorRepository).saveAndFlush(sensor);
    }

    @Test
    void getAlarms_withSensorType_repositoryThrows_negative() {
        String sensorType = "temp";
        Pageable pageable = PageRequest.of(0, 10);
        when(sensorRepository.findBySensorTypeContainingIgnoreCase(sensorType, pageable))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> sensorService.getAlarms(sensorType, pageable)
        );

        assertTrue(ex.getMessage().contains("Cannot execute getAlarms"));
        verify(sensorRepository).findBySensorTypeContainingIgnoreCase(sensorType, pageable);
    }

    @Test
    void getAlarms_withoutSensorType_repositoryThrows_negative() {
        String sensorType = null;
        Pageable pageable = PageRequest.of(0, 10);
        when(sensorRepository.findAll(pageable))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> sensorService.getAlarms(sensorType, pageable)
        );

        assertTrue(ex.getMessage().contains("Cannot execute getAlarms"));
        verify(sensorRepository).findAll(pageable);
    }

    @Test
    void getAlarms_blankSensorType_repositoryThrows_negative() {
        String sensorType = " ";
        Pageable pageable = PageRequest.of(0, 10);
        when(sensorRepository.findAll(pageable))
                .thenThrow(new RuntimeException("DB error"));

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> sensorService.getAlarms(sensorType, pageable)
        );

        assertTrue(ex.getMessage().contains("Cannot execute getAlarms"));
        verify(sensorRepository).findAll(pageable);
    }

    @Test
    void getAlarms_mapperThrows_negative() {
        String sensorType = "temp";
        Pageable pageable = PageRequest.of(0, 10);
        Sensor sensor = new Sensor();
        Page<Sensor> sensorPage = new PageImpl<>(List.of(sensor), pageable, 1);
        when(sensorRepository.findBySensorTypeContainingIgnoreCase(sensorType, pageable))
                .thenReturn(sensorPage);

        try (MockedStatic<AlarmResponseMapper> mapperMock = mockStatic(AlarmResponseMapper.class)) {
            mapperMock.when(() -> AlarmResponseMapper.fromEntity(sensor))
                    .thenThrow(new RuntimeException("Mapping error"));

            RuntimeException ex = assertThrows(
                    RuntimeException.class,
                    () -> sensorService.getAlarms(sensorType, pageable)
            );

            assertTrue(ex.getMessage().contains("Cannot execute getAlarms"));
        }
    }

    @Test
    void getAlarms_nullPageable_negative() {
        String sensorType = "temp";
        Pageable pageable = null;

        RuntimeException ex = assertThrows(
                RuntimeException.class,
                () -> sensorService.getAlarms(sensorType, pageable)
        );

        assertTrue(ex.getMessage().contains("Cannot execute getAlarms"));
    }
}
