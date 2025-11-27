package com.mycompany.demo.warehouse.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AlarmResponseDto {

    private Long id;
    private String sensorId;
    private String sensorType;
    private Double value;
    private Double threshold;
    private Boolean alarm;
    private String message;
    private Instant createdAt;
    private String description;

}