package com.github.jknetl.ec.data.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum EnergyUnit {
	CUBIC_METER("m³"), KWH("kW/h");

	private final String symbol;
}
