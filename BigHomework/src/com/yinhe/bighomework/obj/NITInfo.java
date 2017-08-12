package com.yinhe.bighomework.obj;

public class NITInfo {
	private int frequency;
	private int symbolRate;

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getSymbolRate() {
		return symbolRate;
	}

	public void setSymbolRate(int symbolRate) {
		this.symbolRate = symbolRate;
	}

	@Override
	public String toString() {
		return "NITInfo [frequency=" + frequency + ", symbolRate=" + symbolRate
				+ "]";
	}

}
