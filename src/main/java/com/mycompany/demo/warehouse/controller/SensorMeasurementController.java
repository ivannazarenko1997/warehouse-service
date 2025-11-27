package com.mycompany.demo.warehouse.controller;

import com.mycompany.demo.warehouse.dto.AlarmResponseDto;
import com.mycompany.demo.warehouse.service.SensorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/api/alarms")
@RequiredArgsConstructor
public class SensorMeasurementController {

    private final SensorService sensorService;

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    public Page<AlarmResponseDto> getAlarms(
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable,
            @RequestParam(required = false) String sensorType
    ) {
         return sensorService.getAlarms(sensorType,pageable);
    }

}
