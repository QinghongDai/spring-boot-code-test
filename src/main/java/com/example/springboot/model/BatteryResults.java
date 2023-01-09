package com.example.springboot.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class BatteryResults implements Serializable {
    private Long total;
    private Double avgCapacity;
    private List<Battery> batteries;
}
