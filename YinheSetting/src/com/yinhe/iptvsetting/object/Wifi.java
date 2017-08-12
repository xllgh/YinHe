package com.yinhe.iptvsetting.object;

public class Wifi {
	private String name = null;
	private int signalLever = 0;
	private Boolean isConnect = false;

	public Wifi() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Wifi(String name, int signalLever, Boolean isConnect) {
		super();
		this.name = name;
		this.signalLever = signalLever;
		this.isConnect = isConnect;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getSignalLever() {
		return signalLever;
	}

	public void setSignalLever(int signalLever) {
		this.signalLever = signalLever;
	}

	public Boolean getIsConnect() {
		return isConnect;
	}

	public void setIsConnect(Boolean isConnect) {
		this.isConnect = isConnect;
	}

}
