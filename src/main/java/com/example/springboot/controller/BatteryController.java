package com.example.springboot.controller;

import com.example.springboot.model.Battery;
import com.example.springboot.model.BatteryQuery;
import com.example.springboot.model.BatteryResults;
import com.example.springboot.service.BatteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BatteryController {
	@Autowired
	BatteryService batteryService;

	@PostMapping("/saveBattery")
	public void saveBattery(@Validated @RequestBody Battery battery) {
		batteryService.insertData(battery);
	}

	@GetMapping("/searchBattery")
	public BatteryResults searchBattery(@Validated @RequestBody BatteryQuery batteryQuery) {
		return batteryService.queryData(batteryQuery);
	}
}
