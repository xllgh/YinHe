/*
 * Copyright (C) 2010 The Android-x86 Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Yi Sun <beyounn@gmail.com>
 */

package com.android.settings.ethernet;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.*;
import android.os.Handler;
import android.os.IBinder;
import android.os.AsyncTask;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.ServiceManager;
import android.preference.Preference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.provider.Settings.System;

public class EthernetEnabler implements CompoundButton.OnCheckedChangeListener {
	private static final String TAG = "SettingsEthEnabler";
	private Switch mSwitch;
	private EthernetManager mEthManager;
	private ConnectivityManager mConnectivityManager;
	private INetworkManagementService mNwService;
	private boolean DEBUG = false;
	PppoeManager pppoeManager;
	private Context mcontext;

	public EthernetEnabler(Context context, Switch switch_) {
		mcontext = context;
		mSwitch = switch_;
		pppoeManager = (PppoeManager) context
				.getSystemService(Context.PPPOE_SERVICE);
		mEthManager = (EthernetManager) context
				.getSystemService(Context.ETHERNET_SERVICE);
		mConnectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		IBinder b = ServiceManager
				.getService(Context.NETWORKMANAGEMENT_SERVICE);
		mNwService = INetworkManagementService.Stub.asInterface(b);
	}

	public INetworkManagementService getNetworkManagerment() {
		return mNwService;
	}

	public EthernetManager getManager() {
		return mEthManager;
	}

	public ConnectivityManager getConnectivityManager() {
		return mConnectivityManager;
	}

	public void resume() {
		mSwitch.setOnCheckedChangeListener(this);
	}

	public void pause() {
		mSwitch.setOnCheckedChangeListener(null);
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (DEBUG)
			Log.i(TAG, "PreferenceChange do  some thing change Ethernet State");
		setEthEnabled((Boolean) newValue);
		return false;
	}

	private void setEthEnabled(final boolean enable) {
		int state = mEthManager.getEthernetState();
		if (DEBUG)
			Log.i(TAG, "try get Ethernet State : " + state);

		if (state != EthernetManager.ETHERNET_STATE_ENABLED && enable) {
			if (mEthManager.isEthernetConfigured() != true) {
				if (DEBUG)
					Log.i(TAG,
							"mEthManager.isEthConfigured:"
									+ mEthManager.isEthernetConfigured());
			} else {
				mEthManager.setEthernetEnabled(enable);
				if (DEBUG)
					Log.i(TAG, "try set EthernetEnabled ");
			}
		} else {
			if (DEBUG)
				Log.i(TAG, "Ethernet is Configured show this ");
			mEthManager.setEthernetEnabled(enable);
		}
		if (DEBUG)
			Log.i(TAG, "begin to check net info ");
	}

	public void setSwitch(Switch switch_, EthernetEnabler Enabler) {
		if (mSwitch == switch_)
			return;
		mSwitch.setOnCheckedChangeListener(null);
		mSwitch = switch_;
		mSwitch.setOnCheckedChangeListener(this);

		int ethernetState = 0;
		try {
			ethernetState = Settings.System.getInt(
					mcontext.getContentResolver(), Settings.Secure.ETHERNET_ON);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
		}
		if (DEBUG)
			Log.i(TAG, "setSwitch  -- getEthernetState: " + ethernetState);
		boolean isEnabled = ethernetState == EthernetManager.ETHERNET_STATE_ENABLED;
		boolean isDisabled = ethernetState == EthernetManager.ETHERNET_STATE_DISABLED;
		mSwitch.setChecked(isEnabled);
		if (DEBUG)
			Log.i(TAG, "switch isEnabled: " + isEnabled);
		if (DEBUG)
			Log.i(TAG, "switch isDisabled: " + isDisabled);
		if (DEBUG)
			Log.i(TAG, "switch ethernetState: " + ethernetState);
		if (DEBUG)
			Log.i(TAG, "switch ETHERNET_STATE_ENABLED: "
					+ EthernetManager.ETHERNET_STATE_ENABLED);
		mSwitch.setEnabled(true);
		if (DEBUG)
			Log.i(TAG, "setSwitch setEnabled= true");
		mSwitch.setEnabled(isEnabled || isDisabled);
		if (DEBUG)
			Log.i(TAG, "mSwitch.isChecked :" + mSwitch.isChecked());
	}

	public Switch getSwitch() {
		return mSwitch;
	}

	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean Enable) {
		boolean mEthernetState = (mEthManager.getEthernetState() == EthernetManager.ETHERNET_STATE_ENABLED ? true
				: false);
		if (Enable == false) {
            if(EthernetManager.ETHERNET_CONNECT_MODE_PPPOE.equals(mEthManager.getEthernetMode())
                   && mEthManager.getEthernetState() == EthernetManager.ETHERNET_STATE_ENABLED) {
			System.putInt(mcontext.getContentResolver(), "pppoe_enable", 1);
				new ForgetPppoe().execute();
			}
		} else if (Enable == true) {
            if(EthernetManager.ETHERNET_CONNECT_MODE_PPPOE.equals(mEthManager.getEthernetMode())
                   && mEthManager.getEthernetState() == EthernetManager.ETHERNET_STATE_ENABLED
                   && pppoeManager.getPppoeState() == PppoeManager.PPPOE_STATE_CONNECT) {
				new LoginPppoe().execute();
			}
		}
		Message msg = new Message();
		msg.what = 1;
		if (Enable) {
			msg.arg1 = 1;
		} else {
			msg.arg1 = 0;
		}
		mHandler.sendMessageDelayed(msg, 500);
	}

	class LoginPppoe extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
            String username = pppoeManager.getPppoeUsername();
            String password = pppoeManager.getPppoePassword();
            String ifname = null;
            if(EthernetManager.ETHERNET_CONNECT_MODE_PPPOE.equals(mEthManager.getEthernetMode())
                   && mEthManager.getEthernetState() == EthernetManager.ETHERNET_STATE_ENABLED) {
                ifname = mEthManager.getInterfaceName();
            }
			pppoeManager.connect(username,password,ifname); // return
																			// boolean
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}

	class ForgetPppoe extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
            String ifname = null;
            if(EthernetManager.ETHERNET_CONNECT_MODE_PPPOE.equals(mEthManager.getEthernetMode())
                   && mEthManager.getEthernetState() == EthernetManager.ETHERNET_STATE_ENABLED) {
                ifname = mEthManager.getInterfaceName();
            }
			pppoeManager.disconnect(ifname); // return
																			// boolean
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}

		@Override
		protected void onProgressUpdate(Void... values) {
			// TODO Auto-generated method stub
			super.onProgressUpdate(values);
		}

	}

/*
	public int ipCheckInuse(String address) {
		int result=0;
		try {
			result = mNwService.checkArpIpcheck(address);
		} catch (Exception e) {
			Log.e(TAG, "ipCheckInuse error:" + e);
			return -1;
		}

		return result;
	}
*/

	Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int what = msg.what;
			boolean enable = true;
			if (EthernetManager.ETHERNET_STATE_ENABLED == msg.arg1) {
				enable = true;
			} else if (EthernetManager.ETHERNET_STATE_DISABLED == msg.arg1) {
				enable = false;
			}
			switch (what) {
			case 1:
				mEthManager.enableEthernet(enable);
			}
			super.handleMessage(msg);
		}
	};
}
