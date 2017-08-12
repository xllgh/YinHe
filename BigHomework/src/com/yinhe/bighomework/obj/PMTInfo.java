package com.yinhe.bighomework.obj;

public class PMTInfo {

	private int videoPid;
	private int audioPid;
	private int videoType;
	private int audioType;
	private int frequency;
	private int serverId;
	private int symbolRate;
	private int ecmPid;
	private int pcrPid;
	private String strVideoType;
	private String strAudeoType;
	private String pid;

	public int getEcmPid() {
		return ecmPid;
	}

	public void setEcmPid(int ecmPid) {
		this.ecmPid = ecmPid;
	}

	public int getPcrPid() {
		return pcrPid;
	}

	public void setPcrPid(int pcrPid) {
		this.pcrPid = pcrPid;
	}

	

	public String getPid() {
		return pid;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public String getStrVideoType() {
		return strVideoType;
	}

	public void setStrVideoType(String strVideoType) {
		this.strVideoType = strVideoType;
	}

	public String getStrAudeoType() {
		return strAudeoType;
	}

	public void setStrAudeoType(String strAudeoType) {
		this.strAudeoType = strAudeoType;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public int getVideoPid() {
		return videoPid;
	}

	public void setVideoPid(int videoPid) {
		this.videoPid = videoPid;
	}

	public int getAudioPid() {
		return audioPid;
	}

	public void setAudioPid(int audioPid) {
		this.audioPid = audioPid;
	}

	public int getVideoType() {
		return videoType;
	}

	public void setVideoType(int videoType) {
		this.videoType = videoType;
	}

	public int getAudioType() {
		return audioType;
	}

	public void setAudioType(int audioType) {
		this.audioType = audioType;
	}

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
		return "PMTInfo [videoPid=" + videoPid + ", audioPid=" + audioPid
				+ ", videoType=" + videoType + ", audioType=" + audioType
				+ ", frequency=" + frequency + ", serverId=" + serverId
				+ ", symbolRate=" + symbolRate + ", ecmPid=" + String.format("%02x", ecmPid)
				+ ", pcrPid=" + pcrPid + ", strVideoType=" + strVideoType
				+ ", strAudeoType=" + strAudeoType + ", pid=" + pid + "]";
	}

	

}
