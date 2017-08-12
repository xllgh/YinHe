package com.yinhe.bighomework.obj;

public class EITInfo {
	private String startTime;
	private String date;
	private String endTime;
	private String name;
	private int serverId;
	private int eitCategory;//3:pf,4:schedule
	public static final int EIT_PF=3;
	public static final int EIT_SCHEDULE=4;
	

	public int getEitCategory() {
		return eitCategory;
	}

	public void setEitCategory(int eitCategory) {
		this.eitCategory = eitCategory;
	}

	public int getServerId() {
		return serverId;
	}

	public void setServerId(int serverId) {
		this.serverId = serverId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public String getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}

	private int sectionNum;

	@Override
	public String toString() {
		return "EITInfo [startTime=" + startTime + ", duration=" + endTime
				+ ", name=" + name + ", sectionNum=" + sectionNum + "]";
	}

	public int getSectionNum() {
		return sectionNum;
	}

	public void setSectionNum(int sectionNum) {
		this.sectionNum = sectionNum;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
