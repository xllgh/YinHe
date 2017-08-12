package com.android.settings.ethernet;

import com.android.settings.R;

import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.net.DhcpInfo;
import android.os.AsyncTask;
import android.preference.DialogPreference;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.net.pppoe.PppoeManager;
import android.net.ethernet.*;
import android.app.Dialog;
import android.provider.Settings.System;

public class pppoeLoginDialog extends DialogPreference implements TextWatcher {
	private static final String TAG = pppoeLoginDialog.class.getSimpleName();
	private boolean DEBUG = false;
	private EditText mUsername;
	private EditText mPassword;
	private TextView mStatus;
	private TextView mIP_address;
	private Context mContext;
	private String user_name;
	private String password;
	private boolean isConnected = true;
	private PppoeManager pppoeManager;
	private EthernetManager mEthManager;
	private EthernetEnabler mEthEnabler;

	public pppoeLoginDialog(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		pppoeManager = (PppoeManager) context
				.getSystemService(Context.PPPOE_SERVICE);
		mEthEnabler = new EthernetEnabler(context, null);
		mEthManager = mEthEnabler.getManager();
		mContext = context;
	}

	@Override
	protected View onCreateDialogView() {
		// TODO Auto-generated method stub
		super.onCreateDialogView();
		isConnected = getPppoeState();
		isConnected = getPppoeState();
		if (isConnected) {
			setPositiveButtonText(mContext.getResources().getString(
					R.string.wifi_forget));
			setDialogLayoutResource(R.layout.pppoe_diconneted_dialog);
		} else {
			setNegativeButtonText(mContext.getResources().getString(
					R.string.wifi_forget));
			setPositiveButtonText(mContext.getResources().getString(
					R.string.wifi_connect));
			setDialogLayoutResource(R.layout.pppoe_login_layout);
		}
		return super.onCreateDialogView();
	}

	public boolean getPppoeState() {
		return PppoeManager.PPPOE_STATE_CONNECT == pppoeManager.getPppoeState();
	}

	@Override
	protected void onBindDialogView(View view) {
		// TODO Auto-generated method stub
		super.onBindDialogView(view);
		isConnected = getPppoeState();
		if (isConnected) {
			setPositiveButtonText(mContext.getResources().getString(
					R.string.wifi_forget));
			mStatus = (TextView) view.findViewById(R.id.pppoe_status);
			mIP_address = (TextView) view.findViewById(R.id.pppoe_ip);
			String ip_s = pppoeManager.getIpaddr(mEthManager
					.getInterfaceName().toString());
			String ip = pppoeManager.getIpaddr("ppp0");
			if (null != ip) {
				mIP_address.setText(ip);
			}
		} else {
			setPositiveButtonText(mContext.getResources().getString(
					R.string.wifi_connect));
			setNegativeButtonText(mContext.getResources().getString(
					R.string.wifi_forget));
			user_name = pppoeManager.getPppoeUsername();
			password = pppoeManager.getPppoePassword();
			mUsername = (EditText) view.findViewById(R.id.login_username);
			if (null != user_name) {
				mUsername.setText(user_name);
			}
			mPassword = (EditText) view.findViewById(R.id.login_password);
			if (null != password) {
				mPassword.setText(password);
			}
			mUsername.addTextChangedListener(this);
			mPassword.addTextChangedListener(this);

		}
	}

	@Override
	public void onDismiss(DialogInterface dialog) {
		// TODO Auto-generated method stub
		super.onDismiss(dialog);
	}

	@Override
	protected void onClick() {
		// TODO Auto-generated method stub
		isConnected = getPppoeState();
		if (isConnected) {
			setPositiveButtonText(mContext.getResources().getString(
					R.string.wifi_forget));
		} else {
			setNegativeButtonText(mContext.getResources().getString(
					R.string.wifi_forget));
			setPositiveButtonText(mContext.getResources().getString(
					R.string.wifi_connect));
		}
		super.onClick();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			if (isConnected) {
				new ForgetPppoe().execute();
				if (null != this.getDialog()) {
					onDismiss(this.getDialog());
				}
			} else {
				if (null == mUsername || null == mUsername.getText()) {
					return;
				} else {
                    pppoeManager.setPppoeUsername(mUsername.getText().toString());
				}
				if (null == mPassword || null == mPassword.getText()) {
					return;
				} else {
                    pppoeManager.setPppoePassword(mPassword.getText().toString());
				}
				new LoginPppoe().execute();
				if (null != this.getDialog()) {
					onDismiss(this.getDialog());
				}
			}
			break;
		case DialogInterface.BUTTON_NEGATIVE:
			if (!isConnected) {
                if(EthernetManager.ETHERNET_CONNECT_MODE_PPPOE.equals(mEthManager.getEthernetMode())
                       && mEthManager.getEthernetState() == EthernetManager.ETHERNET_STATE_ENABLED) {
					new ForgetPppoe().execute();
					if (null != this.getDialog()) {
						onDismiss(this.getDialog());
					}
				}
			}
			break;
		}
		super.onClick(dialog, which);
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		// TODO Auto-generated method stub

	}

	class LoginPppoe extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			// TODO Auto-generated method stub
			if (PppoeManager.PPPOE_STATE_CONNECT == pppoeManager.getPppoeState()) {
                String username = pppoeManager.getPppoeUsername();
                String password = pppoeManager.getPppoePassword();
                String ifname = null;
                if(EthernetManager.ETHERNET_CONNECT_MODE_PPPOE.equals(mEthManager.getEthernetMode())
                       && mEthManager.getEthernetState() == EthernetManager.ETHERNET_STATE_ENABLED) {
                    ifname = mEthManager.getInterfaceName();
                }
                pppoeManager.connect(username,password,ifname); // return
				System.putInt(mContext.getContentResolver(), "pppoe_enable", 1);
			}// return boolean
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
            pppoeManager.disconnect(ifname);
			System.putInt(mContext.getContentResolver(), "pppoe_enable", 0);
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
}
