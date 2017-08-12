package com.android.settings.keeper;

interface KeeperAidl
{
	String getPPPOEUserName();
	void setPPPOEUserName(String value);
	
	String getPPPOEPasswd();
	void setPPPOEPasswd(String value);
	
	String getDHCPUserName();
	void setDHCPUserName(String value);
	
	String getDHCPPasswd();
	void setDHCPPasswd(String value);
	
	String getITVAuthUrl();
	void setITVAuthUrl(String value);
	
	String getITVReserveAuthUrl();
	void setITVReserveAuthUrl(String value);
	
	String getITVLogUrl();
	void setITVLogUrl(String value);
	
	String getITVUserName();
	void setITVUserName(String value);
	
	String getITVPasswd();
	void setITVPasswd(String value);
	
	String getAccessType();
	void setAccessType(String value);
	
	String getAccessMethod();
	void setAccessMethod(String value);
	
	String getITVWGUrl();
	void setITVWGUrl(String value);
	
	String getITVWGUserName();
	void setITVWGUserName(String value);
	
	String getITVWGPasswd();
	void setITVWGPasswd (String value);
	
	String getITVUpgradeUrl();
	void setITVUpgradeUrl(String value);
	
	int getAudioIOType();
	void setAudioIOType(int value);
	
	String getParam(String key);
	void setParam(String key, String value);

	String getITVLogInterval();
	void setITVLogInterval(String value);
}
