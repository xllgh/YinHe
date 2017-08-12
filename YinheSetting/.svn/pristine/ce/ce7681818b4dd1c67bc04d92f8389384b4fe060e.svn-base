package com.yinhe.iptvsetting;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.hisilicon.android.hisysmanager.HiSysManager;

import com.android.settings.yinhe.Native;
import com.yinhe.iptvsetting.common.EditTextBgToStar;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.NetManager;

/**
 * 系统版本信息。
 * 
 * @author zhbn
 */
public class SystemInforActivity extends Activity {

	private static final String TAG = "SystemInfo";

	private static final int POSITION_AUTH_RESET = 0;
	private static final int POSITION_FACTORY_RESET = 2;

	private String resStrStorageInfo;

	private Activity mActivity;
	private LayoutInflater mInflater;
	private NetManager mNetManager;
	private SystemInfoAdapter mSystemInfoAdapter;
	private String[] mArrInfoName;
	private String[] mArrInfoValue;

	private static final String PROPERTY_KEY_USER_NAME = "prop.unicom.account";
	private static final String PROPERTY_KEY_PASSWORD = "prop.unicom.password";
	private static final String PORPERTY_KEY_AUTH_STATE = "prop.auth.state";
	private static final String INTENT_KEY_AUTH_ACCOUNT = "intent_key_auth_account";
	private static final String INTENT_KEY_AUTH_PASSWORD = "intent_key_auth_password";
	private String mAccount;
	private String mPassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.system_infor);
		mActivity = this;

		resStrStorageInfo = mActivity.getResources().getString(
				R.string.storage_info);
		mInflater = LayoutInflater.from(mActivity);
		mNetManager = NetManager.createNetManagerInstance(this);

		initData();
	}

	private void initData() {
		mArrInfoName = getResources().getStringArray(R.array.system_info);
		mArrInfoValue = new String[mArrInfoName.length];
		mAccount = FuncUtil.getSystemProp(PROPERTY_KEY_USER_NAME);
		mPassword = FuncUtil.getSystemProp(PROPERTY_KEY_PASSWORD);
		mArrInfoValue[0] = mAccount;
		mArrInfoValue[1] = Build.BRAND;
		mArrInfoValue[POSITION_FACTORY_RESET] = "";
		mArrInfoValue[3] = Build.MANUFACTURER;
		mArrInfoValue[4] = Build.MODEL;
		mArrInfoValue[5] = Native.getsn();
		mArrInfoValue[6] = mNetManager.getEthernetMacAddress();
		mArrInfoValue[7] = Build.VERSION.RELEASE;
		mArrInfoValue[8] = Build.VERSION.INCREMENTAL;
		mArrInfoValue[9] = FuncUtil.getSystemProp("ro.yinhe.hardware.version");
		mArrInfoValue[10] = FuncUtil.getCpuInfo();
		mArrInfoValue[11] = formatStorageInfo(FuncUtil.getMemoryInfo(mActivity));
		mArrInfoValue[12] = formatStorageInfo(FuncUtil
				.getExternalStorageInfo(mActivity));

		ListView listView = (ListView) findViewById(R.id.lv_system_info);
		mSystemInfoAdapter = new SystemInfoAdapter();
		listView.setAdapter(mSystemInfoAdapter);

		final OnFocusChangeListener focusChangeListener = new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (v instanceof Button) {
					Button btn = (Button) v;
					btn.setTextColor(hasFocus ? Color.YELLOW : Color.WHITE);
				}
			}
		};

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (position == POSITION_AUTH_RESET) {
					createAuthResetDialog();
				}

				if (position == POSITION_FACTORY_RESET) {
					AlertDialog.Builder builder = new AlertDialog.Builder(
							mActivity, R.style.style_dialog);

					LayoutInflater inflater = getLayoutInflater();
					View viewDialog = inflater.inflate(R.layout.dialog, null);
					builder.setView(viewDialog);
					final AlertDialog myDialog = builder.show();

					Button btCancle = (Button) viewDialog
							.findViewById(R.id.bt_cancle);
					Button btSure = (Button) viewDialog
							.findViewById(R.id.bt_sure);
					btSure.setOnFocusChangeListener(focusChangeListener);
					btCancle.setOnFocusChangeListener(focusChangeListener);
					btCancle.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							myDialog.dismiss();
						}
					});

					btSure.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (ActivityManager.isUserAMonkey()) {
								Log.e(TAG, "Reset action by monkey!");
								return;
							}
							HiSysManager hisys = new HiSysManager();
							hisys.reset();
							sendBroadcast(new Intent(
									"android.intent.action.MASTER_CLEAR"));
						}
					});
				}
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		NetManager.destroyNetManagerInstance();
	}

	private String formatStorageInfo(String info) {
		String strFromat = "";
		if (info != null) {
			String[] arryInfo = info.split(FuncUtil.STR_SLASH);
			if (arryInfo.length == 2) {
				strFromat = String.format(resStrStorageInfo, arryInfo[0],
						arryInfo[1]);
			}
		}
		return strFromat;
	}

	private void createAuthResetDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this,
				R.style.style_dialog);
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_auth_config, null);
		final EditText etAccount = (EditText) view
				.findViewById(R.id.et_auth_account);
		etAccount.setText(mAccount);
		final EditText etPassword = (EditText) view
				.findViewById(R.id.et_auth_password);
		final EditTextBgToStar toStar = new EditTextBgToStar();
		etPassword.setTransformationMethod(toStar);
		etPassword.setText(mPassword);

		builder.setView(view);
		builder.setTitle(R.string.title_hint_auth);
		builder.setPositiveButton(R.string.btn_auth_reset,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						FuncUtil.setSystemProp(PORPERTY_KEY_AUTH_STATE, "not good");
						Intent intent = new Intent();
						intent.setClassName("com.yinhe.unicom", "com.yinhe.unicom.MainActivity");
						intent.putExtra(INTENT_KEY_AUTH_ACCOUNT, etAccount.getText().toString().trim());
						intent.putExtra(INTENT_KEY_AUTH_PASSWORD, etPassword.getText().toString().trim());
						startActivity(intent);
					}
				});
		builder.setNegativeButton(R.string.cancle,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}
				});
		AlertDialog alertDialog = builder.show();
		final Button btnOK = alertDialog
				.getButton(DialogInterface.BUTTON_POSITIVE);
		btnOK.setEnabled(isUserInfoCorrect(mAccount, mPassword));

		TextWatcher textWatcher = new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				boolean enable = false;
				if (etAccount.getText() != null && etPassword.getText() != null) {
					String account = etAccount.getText().toString().trim();
					String password = etPassword.getText().toString().trim();
					enable = isUserInfoCorrect(account, password);
				}
				btnOK.setEnabled(enable);
				btnOK.setTextColor(enable ? Color.WHITE : Color.GRAY);
			}
		};
		etAccount.addTextChangedListener(textWatcher);
		etPassword.addTextChangedListener(textWatcher);
	}

	private boolean isUserInfoCorrect(String account, String password) {
		if (account == null || password == null) {
			return false;
		}
		return (account.length() >= 1) && (password.length() >= 4);
	}

	class SystemInfoAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mArrInfoName.length;
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			Holder holder;
			if (convertView == null) {
				convertView = mInflater
						.inflate(R.layout.system_info_item, null);
				holder = new Holder();
				holder.tvInfoName = (TextView) convertView
						.findViewById(R.id.tv_item_name);
				holder.tvInfoValue = (TextView) convertView
						.findViewById(R.id.tv_item_value);
				holder.ivFactoryReset = (ImageView) convertView
						.findViewById(R.id.iv_factory_reset);
				convertView.setTag(holder);
			} else {
				holder = (Holder) convertView.getTag();
			}
			holder.tvInfoName.setText(mArrInfoName[position]);
			if (position == POSITION_FACTORY_RESET) {
				holder.tvInfoValue.setVisibility(View.GONE);
				holder.ivFactoryReset.setVisibility(View.VISIBLE);
			} else {
				holder.ivFactoryReset.setVisibility(View.GONE);
				holder.tvInfoValue.setVisibility(View.VISIBLE);
				holder.tvInfoValue.setText(mArrInfoValue[position]);
			}
			return convertView;
		}

		class Holder {
			TextView tvInfoName;
			TextView tvInfoValue;
			ImageView ivFactoryReset;
		}
	}

}
