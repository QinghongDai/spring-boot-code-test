package com.example.springboot;

import com.example.springboot.model.Battery;
import com.example.springboot.model.BatteryQuery;
import com.example.springboot.model.BatteryResults;
import com.example.springboot.service.BatteryService;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@AutoConfigureMockMvc
public class BatteryControllerTest {

	@Autowired
	private BatteryService batteryService;

	@Test
	public void saveAndQueryTest() throws Exception {
		Battery battery = new Battery();

		for (int i = 0; i < 6; i++) {
			battery.setName("battery" + i);
			battery.setPostcode(Long.valueOf(i));
			battery.setWattCapacity((double) (i + 100));
			batteryService.insertData(battery);
		}

		System.out.println("----------complete sava-------------");

		BatteryQuery query = new BatteryQuery();
		query.setPostcodeFrom(2L);
		query.setPostcodeTo(4L);
		BatteryResults res = batteryService.queryData(query);

		System.out.println("------------complete query-----------");
	}
}
