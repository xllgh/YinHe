package com.yinhe.bighomework.utils;

public class ServiceInfo {

	private int frequency;//Ƶ��
	private int symbolRate;//�����
	private String modulationMode; //���Ʒ�ʽ
	
	private String programName;//��Ŀ���
	private int Vpid; //��Ƶpid
	private int Apid; //��Ƶpid
	private int Ppid; //PCR pid
	private int VDecoder; //��Ƶ��������
	private int ADecoder; //��Ƶ��������
	
	private String eventName;//�¼����
	private String time; //ʱ��
	private String brief;//���
	
	private String classifyName;//�������
	private String includeProgram;//���Ŀ
	
	
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
	public String getModulationMode() {
		return modulationMode;
	}
	public void setModulationMode(String modulationMode) {
		this.modulationMode = modulationMode;
	}
	public String getProgramName() {
		return programName;
	}
	public void setProgramName(String programName) {
		this.programName = programName;
	}
	public int getVpid() {
		return Vpid;
	}
	public void setVpid(int vpid) {
		Vpid = vpid;
	}
	public int getApid() {
		return Apid;
	}
	public void setApid(int apid) {
		Apid = apid;
	}
	public int getPpid() {
		return Ppid;
	}
	public void setPpid(int ppid) {
		Ppid = ppid;
	}
	public int getVDecoder() {
		return VDecoder;
	}
	public void setVDecoder(int vDecoder) {
		VDecoder = vDecoder;
	}
	public int getADecoder() {
		return ADecoder;
	}
	public void setADecoder(int aDecoder) {
		ADecoder = aDecoder;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getBrief() {
		return brief;
	}
	public void setBrief(String brief) {
		this.brief = brief;
	}
	public String getClassifyName() {
		return classifyName;
	}
	public void setClassifyName(String classifyName) {
		this.classifyName = classifyName;
	}
	public String getIncludeProgram() {
		return includeProgram;
	}
	public void setIncludeProgram(String includeProgram) {
		this.includeProgram = includeProgram;
	}
	
	
}
