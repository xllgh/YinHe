package com.yinhe.bighomework.obj;

public class ServerInfo {
    PMTInfo pmtInfo;
    SDTInfo sdtInfo;
	public PMTInfo getPmtInfo() {
		return pmtInfo;
	}
	public void setPmtInfo(PMTInfo pmtInfo) {
		this.pmtInfo = pmtInfo;
	}
	public SDTInfo getSdtInfo() {
		return sdtInfo;
	}
	public void setSdtInfo(SDTInfo sdtInfo) {
		this.sdtInfo = sdtInfo;
	}
	@Override
	public String toString() {
		return "ServerInfo [pmtInfo=" + pmtInfo + ", sdtInfo=" + sdtInfo + "]";
	}
    
    

}
