package com.mycompany.demo.warehouse.service;

import com.mycompany.demo.warehouse.kafka.event.SensorMeasurementDto;


public interface LoggerServise {
    void printLog(SensorMeasurementDto event);
    void printAlarmLog(  SensorMeasurementDto measurementDto  );
}
