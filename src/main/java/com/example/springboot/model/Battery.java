package com.example.springboot.model;

import lombok.*;
import java.io.Serializable;

@Data
public class Battery implements Serializable {

    private String name;
    private Long postcode;
    private Double wattCapacity;
}
