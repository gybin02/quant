package com.ricequant.strategy.simulator;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.Test;

import com.ricequant.strategy.def.IHStrategy;
import com.ricequant.strategy.sample.RSIStrategy;

public class RSIStrategySim extends BaseStrategySim {

	public RSIStrategySim() {
		excludes = new String[] { "core", "theInformer", "stockCode", "currentUnclosedProfitHeld",
				"highestUnclosedProfitHeld", "unclosedPositionInitValue" };
		includes = new String[] { "stockCode" };
	}

	protected IHStrategy createStrategy(String stockCode) {
		RSIStrategy strategy = new RSIStrategy();
		List<String> stockCodeList = new ArrayList<String>();
		stockCodeList.add(stockCode);
		strategy.setStockCode(stockCodeList);
		return strategy;
	}

	@Test
	public void testRun() {
		runAndReport();
	}

}
