package com.mycompany.demo.warehouse.enums;

public enum SensorType {
    TEMPERATURE("temperature",35.0, "°C"),
    HUMIDITY("humidity",50.0, "%");
    public final String characteristic;
    public final double threshold;
    public final String unit;

    SensorType(String characteristic, double threshold, String unit) {
        this.characteristic =characteristic;
        this.threshold = threshold;
        this.unit = unit;
    }
}