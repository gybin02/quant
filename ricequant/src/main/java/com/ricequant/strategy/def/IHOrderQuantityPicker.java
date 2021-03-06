package com.ricequant.strategy.def;

public interface IHOrderQuantityPicker {

	/**
	 * 该方法决定多少股的数量。
	 * 
	 * @param numShares
	 * @return
	 */
	IHOrderBuilderBase shares(double numShares);

	/**
	 * 该方法决定多少手的数量
	 * 
	 * @param numLots
	 * @return
	 */
	IHOrderBuilderBase lots(double numLots);

	/**
	 * 决定持仓目标占总资金百分比
	 *
	 * @return
	 */
	IHOrderBuilderBase percent(double percent);

}
