package com.ricequant.strategy.basic.di.adx;

import static com.ricequant.strategy.basic.utils.FittingComputer.linearFitting;
import static com.ricequant.strategy.basic.utils.FittingComputer.normalize;
import static com.ricequant.strategy.basic.utils.FittingComputer.splineDerivatives;

import java.util.Arrays;

import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;

/**
 * At its most basic the Average Directional Index (ADX) can be used to
 * determine if a security is trending or not. This determination helps traders
 * choose between a trend following system or a non-trend following system.
 * Wilder suggests that a strong trend is present when ADX is above 25 and no
 * trend is present when below 20. There appears to be a gray zone between 20
 * and 25. As noted above, chartists may need to adjust the settings to increase
 * sensitivity and signals. ADX also has a fair amount of lag because of all the
 * smoothing techniques. Many technical analysts use 20 as the key level for
 * ADX.
 *
 */
public class ADXComputer {

	private Core core = new Core();

	/**
	 * 
	 * 计算adx
	 *
	 * @param close
	 *            至少150元素,150 periods are required to absorb the smoothing
	 *            techniques
	 * @param high
	 * @param low
	 * @param period
	 *            建议14
	 * @return
	 */
	public double[] computeADX(double[] close, double[] high, double[] low, int period) {
		MInteger begin = new MInteger();
		MInteger length = new MInteger();
		double[] out = new double[close.length - period * 2 + 1];

		core.adx(0, close.length - 1, high, low, close, period, begin, length, out);
		return out;
	}

	/**
	 * 根据输入的adx数组判断趋势是否在形成当中
	 * 
	 * @param adx
	 * @param trendingGrayTH
	 *            判断趋势正在形成 adx的下限值, 比如20-25一般是趋势模糊的值
	 * @Param trendingTH 强趋势下限值, 一般25
	 * @param lookbackPeriod
	 *            从最后adx元素起, 往前取多少个元素做趋势形成判断
	 * 
	 * @return
	 */
	public boolean adxTrendForming(double[] adx, double trendingGrayTH, double trendingTH,
			int lookbackPeriod) {
		double[] adxSubset = Arrays.copyOfRange(adx, adx.length - lookbackPeriod, adx.length);
		double adxSum = 0;
		double directionTH = 0.5;

		// adx均值>trendingGrayTH, 总体向上, 有adx>trendingTH, adx快速上升 4者满足2个当作形成中
		boolean hasAdxOverTH = false;
		double directionIndex = 0;
		for (int i = 0; i < adxSubset.length - 1; i++) {
			adxSum += adxSubset[i];

			if (adxSubset[i] > trendingTH) {
				hasAdxOverTH = true;
			}

			if (adxSubset[i + 1] > adxSubset[i]) {
				directionIndex++;
			}
		}
		boolean avgOverTH = adxSum / lookbackPeriod >= trendingGrayTH;
		boolean hasUpDirection = directionIndex / (lookbackPeriod - 1) >= directionTH;
		boolean adxTrendBoosting = adxTrendBoosting(adxSubset, 1);

		int satisfied = 0;
		if (hasAdxOverTH || avgOverTH) {
			satisfied++;
		}
		if (hasUpDirection) {
			satisfied++;
		}
		if (adxTrendBoosting) {
			satisfied++;
		}
		return satisfied >= 2;
	}

	/**
	 * 判断adx是否快速上升中
	 * 
	 * @param adx
	 * @param coeffTH
	 * @return
	 */
	public boolean adxTrendBoosting(double[] adx, double coeffTH) {
		double[] normalized = normalize(adx);

		double[] coeffs = linearFitting(normalized);
		boolean constantSpeed = coeffs[1] >= coeffTH;

		boolean upSpeed = coeffs[1] > coeffTH / 2
				&& linearFitting(splineDerivatives(normalized))[1] > 0;

		return constantSpeed || upSpeed;
	}

	/**
	 * 根据输入的adx数组判断趋势是否在衰减当中
	 * 
	 * @param adx
	 * @param trendingGrayTH
	 *            判断趋势正在消失 adx的下限值, 比如20-25一般是趋势模糊的值
	 * @Param trendingTH 强趋势下限值, 一般25
	 * @param lookbackPeriod
	 *            从最后adx元素起, 往前取多少个元素做趋势衰减判断
	 * 
	 * @return
	 */
	public boolean adxTrendWaning(double[] adx, double trendingGrayTH, double trendingTH,
			int lookbackPeriod) {
		double[] adxSubset = Arrays.copyOfRange(adx, adx.length - lookbackPeriod, adx.length);
		double adxSum = 0;
		double directionTH = 0.5;

		// adx均值<trendingTH, 总体向下, 有adx<trendingGrayTH, adx快速下降 4者满足2个当作衰减中
		boolean hasAdxBelowTH = false;
		double directionIndex = 0;
		for (int i = 0; i < adxSubset.length - 1; i++) {
			adxSum += adxSubset[i];

			if (adxSubset[i] < trendingGrayTH) {
				hasAdxBelowTH = true;
			}

			if (adxSubset[i + 1] < adxSubset[i]) {
				directionIndex++;
			}
		}
		boolean avgBelowTH = adxSum / lookbackPeriod <= trendingTH;
		boolean hasDownDirection = directionIndex / (lookbackPeriod - 1) >= directionTH;
		boolean adxTrendFalling = adxTrendFalling(adxSubset, -1);

		int satisfied = 0;
		if (hasAdxBelowTH || avgBelowTH) {
			satisfied++;
		}
		if (hasDownDirection) {
			satisfied++;
		}
		if (adxTrendFalling) {
			satisfied++;
		}
		return satisfied >= 2;
	}

	/**
	 * 判断adx是否快速下降中
	 * 
	 * @param adx
	 * @param coeffTH
	 * @return
	 */
	public boolean adxTrendFalling(double[] adx, double coeffTH) {
		double[] normalized = normalize(adx);

		double[] coeffs = linearFitting(normalized);
		boolean constantSpeed = coeffs[1] <= coeffTH;

		boolean upSpeed = coeffs[1] < coeffTH / 2
				&& linearFitting(splineDerivatives(normalized))[1] < 0;

		return constantSpeed || upSpeed;
	}
}
