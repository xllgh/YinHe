package com.yinhe.bighomework.obj;

public class ChannelInfo {

	private int frequency;
	private int symbolRate;

	private int videoPid;
	private int videoDecoder;
	private String strVideoDecoder;

	private int audioPid;
	private int audioDecoder;
	private String strAudioDecoder; 

	private int prcPid;
	private String channelName;
	private String channelCategory;

	public int getSymbolRate() {
		return symbolRate;
	}

	public void setSymbolRate(int symbolRate) {
		this.symbolRate = symbolRate;
	}

	public String getStrVideoDecoder() {
		return strVideoDecoder;
	}

	public void setStrVideoDecoder(String strVideoDecoder) {
		this.strVideoDecoder = strVideoDecoder;
	}

	public String getStrAudioDecoder() {
		return strAudioDecoder;
	}

	public void setStrAudioDecoder(String strAudioDecoder) {
		this.strAudioDecoder = strAudioDecoder;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getChannelCategory() {
		return channelCategory;
	}

	public void setChannelCategory(String channelCategory) {
		this.channelCategory = channelCategory;
	}

	private int serverId;

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getVideoPid() {
		return videoPid;
	}

	public void setVideoPid(int videoPid) {
		this.videoPid = videoPid;
	}

	public int getVideoDecoder() {
		return videoDecoder;
	}

	public void setVideoDecoder(int videoDecoder) {
		this.videoDecoder = videoDecoder;
	}

	public int getAudioPid() {
		return audioPid;
	}

	public void setAudioPid(int audioPid) {
		this.audioPid = audioPid;
	}

	public int getAudioDecoder() {
		return audioDecoder;
	}

	public void setAudioDecoder(int audioDecoder) {
		this.audioDecoder = audioDecoder;
	}

	public int getPrcPid() {
		return prcPid;
	}

	public void setPrcPid(int prcPid) {
		this.prcPid = prcPid;
	}

	public String getChannelName() {
		return channelName;
	}

	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

}
