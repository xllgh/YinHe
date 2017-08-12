package com.android.settings.ethernet;


import android.app.Activity;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import android.net.ethernet.EthernetManager;
import com.android.settings.R;
public class Option61Dialog extends DialogFragment {
	private EthernetManager mEthManager;
	private EthernetEnabler mEthEnabler;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		final Activity activity = getActivity();
		Switch actionBarSwitch = new Switch(activity);
		mEthEnabler = new EthernetEnabler(activity, actionBarSwitch);
		mEthManager = mEthEnabler.getManager();
		View view = inflater.inflate(R.layout.dhcp_option_61, container, false);
		final EditText username = (EditText)view.findViewById(R.id.et_option_60_username);
		final EditText pwd = (EditText)view.findViewById(R.id.et_option_60_pwd);

		Button save = (Button)view.findViewById(R.id.btn_option_60_ok);
		Button cancle = (Button)view.findViewById(R.id.btn_option_60_cancle);
		save.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String login = username.getText().toString();
				String password = pwd.getText().toString();
				mEthManager.setDhcpOption60(true, login, password);
				dismiss();
			}
		});
		cancle.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});
		return view;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		getDialog().getWindow().setTitle(getResources().getString(R.string.option_tips));
	}
}
