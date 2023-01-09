package com.example.springboot.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class BatteryQuery implements Serializable {
    private Long postcodeFrom;
    private Long postcodeTo;

    private Integer pageNo;
    private Integer pageSize;
}
