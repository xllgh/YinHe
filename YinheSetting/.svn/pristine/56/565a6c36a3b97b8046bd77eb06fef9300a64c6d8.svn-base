package com.yinhe.iptvsetting.view;

import android.app.Activity;
import android.support.v4.app.Fragment;

public class BaseFragment extends Fragment {
    
    public Activity mActivity;

	private OnNetTypeChangeListener mOnNetTypeChangeListener;
	
	public void setOnNetTypeChangeListener(OnNetTypeChangeListener listener) {
		mOnNetTypeChangeListener = listener;
	}
	/**
	 * 有线接入模块用，重新获取IP地址。
	 */
	protected void reObtainNetAddress(int type) {
		if (mOnNetTypeChangeListener != null) {
			mOnNetTypeChangeListener.execute(type);
		}
	}

	/**
	 * 监听器，PPPoE、DHCP+、LAN、LanDHCP模块下的事件。
	 * 
	 * @author zhbn
	 * 
	 */
	public interface OnNetTypeChangeListener {
		public static final int LAN_DHCP_ON = 0;
		public static final int LAN_DHCP_OFF = 1;
		public static final int LAN_ON = 2;
		public static final int DHCP_PLUS_ON = 3;
		public static final int DHCP_PLUS_OFF = 4;
		public static final int PPPOE_ON = 5;
		public static final int PPPOE_OFF = 6;

		void execute(int type);
	}

}
