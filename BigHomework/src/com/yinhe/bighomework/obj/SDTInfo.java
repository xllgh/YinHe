package com.yinhe.bighomework.obj;

public class SDTInfo {
	private String serverName;
	private int serverId;
	private String category;
	private int frequency;
	private int symbolRate;
	
	private int schedulFlag;
	private int followingFlag;
	private int CA_MODE;
	
	private String index;
	

	public String getIndex() {
		return index;
	}
	public void setIndex(String index) {
		this.index = index;
	}
	public int getCA_MODE() {
		return CA_MODE;
	}
	public void setCA_MODE(int cA_MODE) {
		CA_MODE = cA_MODE;
	}
	public int getSchedulFlag() {
		return schedulFlag;
	}
	public void setSchedulFlag(int schedulFlag) {
		this.schedulFlag = schedulFlag;
	}
	public int getFollowingFlag() {
		return followingFlag;
	}
	public void setFollowingFlag(int followingFlag) {
		this.followingFlag = followingFlag;
	}
	public int getSymbolRate() {
		return symbolRate;
	}
	public void setSymbolRate(int symbolRate) {
		this.symbolRate = symbolRate;
	}
	public int getFrequency() {
		return frequency;
	}
	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}
	public String getServerName() {
		return serverName;
	}
	public void setServerName(String serverName) {
		this.serverName = serverName;
	}
	public int getServerId() {
		return serverId;
	}
	public void setServerId(int serverId) {
		this.serverId = serverId;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	@Override
	public String toString() {
		return "SDTInfo [serverName=" + serverName + ", serverId=" + serverId
				+ ", category=" + category + ", frequency=" + frequency
				+ ", symbolRate=" + symbolRate + ", schedulFlag=" + schedulFlag
				+ ", followingFlag=" + followingFlag + ", CA_MODE=" + CA_MODE
				+ "]";
	}
	
	

}
