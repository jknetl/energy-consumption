package com.github.jknetl.ec.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
public class MeasurementsController {

	@GetMapping("/measurements")
	public String getMeasurements() {
		return "Hello World!";
	}
}
