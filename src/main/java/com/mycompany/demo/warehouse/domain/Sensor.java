package com.mycompany.demo.warehouse.domain;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "sensor")
@Getter
@Setter
public class Sensor {

    public Sensor() {}

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = false, length = 100)
    private String sensorId;

    @Column(nullable = false, length = 100)
    private String sensorType;

    @Column(nullable = false)
    private double value ;

    @Column(length = 255)
    private String description;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean alarm = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;


}