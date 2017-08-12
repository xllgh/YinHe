package com.yinhe.iptvsetting.object;

/**
 * 网络地址。
 * 
 * @author zhbn
 * 
 */
public class NetAddress {

	/**
	 * IP地址
	 */
	private String mIPaddress;
	/**
	 * 子网掩码
	 */
	private String mNetMask;
	/**
	 * 默认网关
	 */
	private String mGateWay;
	/**
	 * DNS服务器1
	 */
	private String mDNS1;
	/**
	 * DNS服务器2
	 */
	private String mDNS2;

	public String getIPaddress() {
		return mIPaddress;
	}

	public void setIPaddress(String mIPaddress) {
		this.mIPaddress = mIPaddress;
	}

	public String getNetMask() {
		return mNetMask;
	}

	public void setNetMask(String mNetMask) {
		this.mNetMask = mNetMask;
	}

	public String getGateWay() {
		return mGateWay;
	}

	public void setGateWay(String mGateWay) {
		this.mGateWay = mGateWay;
	}

	public String getDNS1() {
		return mDNS1;
	}

	public void setmDNS1(String mDNS1) {
		this.mDNS1 = mDNS1;
	}

	public String getmDNS2() {
		return mDNS2;
	}

	public void setmDNS2(String mDNS2) {
		this.mDNS2 = mDNS2;
	}

}
