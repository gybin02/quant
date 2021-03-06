package com.ricequant.strategy.support.mock;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import com.ricequant.strategy.def.HOrderRejectReasonEnum;
import com.ricequant.strategy.def.IHOrder;
import com.ricequant.strategy.def.IHOrderBuilderBase;

public class DummyOrderBuilderBase implements IHOrderBuilderBase {

	private RunnerContext runnerContext;

	private int day;

	private String stockCode;

	private double percent;

	private double shares;

	private int tradeDirection;

	public DummyOrderBuilderBase(RunnerContext runnerContext, int day, String stockCode,
			int tradeDirection, double percent, double shares) {
		super();
		this.runnerContext = runnerContext;
		this.day = day;
		this.stockCode = stockCode;
		this.percent = percent;
		this.shares = shares;
		this.tradeDirection = tradeDirection;
	}

	@Override
	public IHOrder commit() {
		if (shares != 0) {
			runnerContext.getPortfolioHolder().tradeShares(day, stockCode, tradeDirection, shares);
		} else {
			runnerContext.getPortfolioHolder()
					.tradePercent(day, stockCode, tradeDirection, percent);
		}
		return null;
	}

	@Override
	public IHOrder commit(Consumer<IHOrder> onSuccess,
			BiConsumer<HOrderRejectReasonEnum, IHOrder> onRejected) {
		throw new IllegalArgumentException("unimplemented");
	}

}
