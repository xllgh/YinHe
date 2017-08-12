package com.yinhe.iptvsetting;

import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.NetManager;
import com.yinhe.iptvsetting.object.NetAddress;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

public class NetInformationActivity extends Activity {

	private TextView tvIP;
	private TextView tvNetmask;
	private TextView tvGateway;
	private TextView tvDNS;
	private NetManager mNetManager;

	private static final int MSG_UPDATE = 0;
	private static final int MSG_NET_INFO_ERRO = 1;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_UPDATE:
				NetAddress netAddress = mNetManager.getNetAddress();

				tvIP.setText(netAddress.getIPaddress());
				tvNetmask.setText(netAddress.getNetMask());
				tvGateway.setText(netAddress.getGateWay());
				tvDNS.setText(netAddress.getDNS1());
				break;

			case MSG_NET_INFO_ERRO:
				tvIP.setText(R.string.obtain_Error);
				tvNetmask.setText(FuncUtil.STR_EMPTY);
				tvGateway.setText(FuncUtil.STR_EMPTY);
				tvDNS.setText(FuncUtil.STR_EMPTY);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_net_information);
		mNetManager = NetManager.createNetManagerInstance(this);

		findView();
		new Thread(mObatainIpRunnable).start();
	}

	private void findView() {
		tvIP = (TextView) findViewById(R.id.tvIP);
		tvNetmask = (TextView) findViewById(R.id.tvNetmask);
		tvGateway = (TextView) findViewById(R.id.tvGateway);
		tvDNS = (TextView) findViewById(R.id.tvDNS);
	}

	private Runnable mObatainIpRunnable = new Runnable() {
		@Override
		public void run() {
			NetAddress netAddress = mNetManager.obataintNetAddress();
			Log.e("xll", netAddress == null ? "netAddress is null "
					: netAddress.toString());
			if (netAddress != null
					&& !FuncUtil.isNullOrEmpty(netAddress.getIPaddress())) {
				mNetManager.setNetAddress(netAddress);
				mHandler.sendEmptyMessage(MSG_UPDATE);
			} else {
				mHandler.sendEmptyMessage(MSG_NET_INFO_ERRO);
			}
		}
	};

}
