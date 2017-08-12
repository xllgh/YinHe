package com.yinhe.iptvsetting;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnKeyListener;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings.Secure;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yinhe.iptvsetting.common.EditTextBgToStar;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.LogUtil;
import com.yinhe.iptvsetting.common.PreferencesUtils;
import com.yinhe.iptvsetting.view.ImageReflect;
import com.yinhe.iptvsetting.view.NetWorkSpeedTestDialog;
import com.yinhe.iptvsetting.view.ScaleAnimEffect;
import com.yinhe.securityguard.SecurityCat;

public class MainActivity extends Activity implements OnClickListener,
		OnFocusChangeListener {
	private LogUtil mLogUtil = new LogUtil(MainActivity.class);

	private static final String SETTINGS_PASSWORD = "10010";

	private static final int[] KEY_CODE_FACTORY_PSW = { KeyEvent.KEYCODE_2,
			KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_2,
			KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_3,
			KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_1, KeyEvent.KEYCODE_3 };
	private ArrayList<Integer> mEditorRecorder = new ArrayList<Integer>();

	private SecurityCat mSecurityCat = null;
	private ServiceConnection mSecurityConnection = null;

	private ImageView[] ivReflect = new ImageView[3];
	/* 白框 */
	private ImageView whiteBorder = null;

	private FrameLayout WireAccess = null;
	private FrameLayout WirelessAccess = null;
	private FrameLayout DisplayAndSound = null;
	private FrameLayout SeniorSet = null;
	private FrameLayout VersionInfor = null;
	private FrameLayout ApacheInfor = null;
	private FrameLayout RouterSetting = null;
	private FrameLayout netInformation=null;

	private ScaleAnimEffect animEffect;

	private static final int MSG_SERVICE_CONNECTED = 0;
	private static final int MSG_SERVICE_CHECK = 1;
	private static final int MSG_SERVICE_RECONNECT = 2;

	private int mServiceConnectCount = 0;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case MSG_SERVICE_CONNECTED:
				if (mSecurityCat != null) {
					try {
						mSecurityCat.startLocalCheck();
					} catch (RemoteException e) {
						e.printStackTrace();
					}
				}
				break;
			case MSG_SERVICE_CHECK:
				mLogUtil.d("MSG_SERVICE_CHECK");
				if (mSecurityCat != null) {
					return;
				}
				ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
				am.forceStopPackage(MainActivity.this.getPackageName());
				break;
			case MSG_SERVICE_RECONNECT:
				if (mServiceConnectCount == 2) {
					return;
				}
				mServiceConnectCount++;
				bindService();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		super.setContentView(R.layout.activity_main);
		initView();
		bindService();
		createPasswordDialog();
	}

	private void initView() {
		this.WireAccess = (FrameLayout) findViewById(R.id.fl_wire_connect);
		this.WireAccess.setOnFocusChangeListener(this);
		this.WireAccess.setOnClickListener(this);

		this.WirelessAccess = (FrameLayout) findViewById(R.id.fl_wireless_access);
		this.WirelessAccess.setOnFocusChangeListener(this);
		this.WirelessAccess.setOnClickListener(this);

		this.DisplayAndSound = (FrameLayout) findViewById(R.id.fl_display_and_sound);
		this.DisplayAndSound.setOnFocusChangeListener(this);
		this.DisplayAndSound.setOnClickListener(this);

		this.SeniorSet = (FrameLayout) findViewById(R.id.fl_senior_set);
		this.SeniorSet.setOnFocusChangeListener(this);
		this.SeniorSet.setOnClickListener(this);

		this.VersionInfor = (FrameLayout) findViewById(R.id.fl_version_infor);
		this.VersionInfor.setOnFocusChangeListener(this);
		this.VersionInfor.setOnClickListener(this);

		this.ApacheInfor = (FrameLayout) findViewById(R.id.fl_apache_infor);
		this.ApacheInfor.setOnFocusChangeListener(this);
		this.ApacheInfor.setOnClickListener(this);

		this.RouterSetting = (FrameLayout) findViewById(R.id.fl_router_setting);
		this.RouterSetting.setOnFocusChangeListener(this);
		this.RouterSetting.setOnClickListener(this);
		
		this.netInformation=(FrameLayout) findViewById(R.id.fl_net_information);
		this.netInformation.setOnFocusChangeListener(this);
		this.netInformation.setOnClickListener(this);

		whiteBorder = (ImageView) findViewById(R.id.white_boder);

		this.ivReflect[0] = (ImageView) findViewById(R.id.iv_reflect_senior_set);
		this.ivReflect[1] = (ImageView) findViewById(R.id.iv_reflect_version_infor);
		this.ivReflect[2] = (ImageView) findViewById(R.id.iv_reflect_apache_infor);

		Bitmap bitmap0 = ImageReflect.convertViewToBitmap(SeniorSet);
		Bitmap bitmap1 = ImageReflect.convertViewToBitmap(VersionInfor);
		Bitmap bitmap2 = ImageReflect.convertViewToBitmap(ApacheInfor);

		this.ivReflect[0].setImageBitmap(ImageReflect.createCutReflectedImage(
				this, bitmap0, 0));
		this.ivReflect[1].setImageBitmap(ImageReflect.createCutReflectedImage(
				this, bitmap1, 0));
		this.ivReflect[2].setImageBitmap(ImageReflect.createCutReflectedImage(
				this, bitmap2, 0));

		if (!bitmap0.isRecycled()) {
			bitmap0.recycle();
		}

		if (!bitmap1.isRecycled()) {
			bitmap1.recycle();
		}

		if (!bitmap2.isRecycled()) {
			bitmap2.recycle();
		}

		this.animEffect = new ScaleAnimEffect();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mHandler.removeCallbacksAndMessages(null);
		unBindService();
	}

	@Override
	public void onClick(View arg0) {
		Intent intent = null;
		switch (arg0.getId()) {
		case R.id.fl_net_information:
			intent=new Intent(this,NetInformationActivity.class);

			break;
		
		case R.id.fl_wire_connect:
			intent = new Intent(this, WireConnectFragmentActivity.class);
			break;
		case R.id.fl_wireless_access:
			intent = new Intent(this, WirelessConnectActivity.class);
			break;
		case R.id.fl_display_and_sound:
			intent = new Intent(this, SoundAndDisplayFragmentActivity.class);
			break;
		case R.id.fl_senior_set:
			intent = new Intent();
			intent.setClassName("com.android.settings",
					"com.android.settings.Settings");
			intent.setAction("com.yinhe.iptvsetting");
			intent.putExtra("YinheSettings", "YinheSettings");
			break;
		case R.id.fl_version_infor:
			intent = new Intent(this, SystemInforActivity.class);
			break;
		case R.id.fl_apache_infor:
			// intent = new Intent(this, NetworkInforActivity.class);
			intent = new Intent(this, NetworkDetectionActivity.class);
			break;

		case R.id.fl_router_setting:
			intent = new Intent();
			intent.setClassName("com.guoantvbox.cs.systemsetting",
					"com.yinhe.csga_net_setting.Router_Setting");
			break;
		}
		if (intent != null) {
			startActivityForResult(intent, 0);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		startYinheConfig(keyCode);
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == SoundAndDisplayFragmentActivity.RESULT_VALUE) {
			Intent intent = new Intent(this,
					SoundAndDisplayFragmentActivity.class);
			intent.putExtra(SoundAndDisplayFragmentActivity.SHOW_FONT, true);
			startActivityForResult(intent, 0);
		}
	}

	@Override
	public void onFocusChange(final View v, boolean hasFocus) {
		if (hasFocus) {
			this.animEffect.setAttributs(1.0F, 1.125F, 1.0F, 1.125F, 100L);
			Animation localAnimation = this.animEffect.createAnimation();
			localAnimation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
				}

				@Override
				public void onAnimationRepeat(Animation animation) {
				}

				@Override
				public void onAnimationEnd(Animation animation) {
					moveWhiteBorder(v);
				}
			});
			v.startAnimation(localAnimation);
			v.bringToFront();
			whiteBorder.bringToFront();
		} else {
			whiteBorder.setVisibility(View.GONE);
			v.setBackground(null);
			this.animEffect.setAttributs(1.125F, 1.0F, 1.125F, 1.0F, 100L);
			v.startAnimation(animEffect.createAnimation());
		}

	}

	/**
	 * 移动背景图片至焦点项目。
	 */
	private void moveWhiteBorder(View v) {
		if ((this.whiteBorder != null)) {
			this.whiteBorder.setVisibility(View.VISIBLE);
			whiteBorder.setX(v.getX() - (whiteBorder.getWidth() - v.getWidth())
					/ 2);
			whiteBorder.setY(v.getY()
					- (whiteBorder.getHeight() - v.getHeight()) / 2);
		}
	}

	private void createPasswordDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(
				MainActivity.this, R.style.style_dialog);
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_password_input, null);
		final EditText psEditText = (EditText) view
				.findViewById(R.id.et_password);
		psEditText.setHint(R.string.hint_input_admin_password);
		final EditTextBgToStar toStar = new EditTextBgToStar();
		psEditText.setTransformationMethod(toStar);
		CheckBox chbShowPwd = (CheckBox) view.findViewById(R.id.show_password);
		chbShowPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				psEditText.setTransformationMethod(isChecked ? null : toStar);
			}
		});
		builder.setView(view);
		builder.setTitle(R.string.title_hint);
		builder.setPositiveButton(R.string.sure,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						try {
							Field field = dialog.getClass().getSuperclass()
									.getDeclaredField("mShowing");
							field.setAccessible(true);
							if (SETTINGS_PASSWORD.equals(psEditText.getText()
									.toString())) {
								field.set(dialog, true);
								dialog.dismiss();
							} else {
								FuncUtil.showToast(MainActivity.this,
										R.string.str_error_password);
								field.set(dialog, false);
								psEditText.requestFocus();
							}
						} catch (Exception e) {
							e.printStackTrace();
							MainActivity.this.finish();
						}
					}
				});
		builder.setNegativeButton(R.string.cancle,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MainActivity.this.finish();
					}
				});
		builder.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				MainActivity.this.finish();
			}
		});
		builder.create();
		AlertDialog al = builder.show();
		al.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		final Button btnOK = al.getButton(DialogInterface.BUTTON_POSITIVE);
		btnOK.setEnabled(false);
		psEditText.addTextChangedListener(new TextWatcher() {
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
				btnOK.setEnabled(s.length() >= 5);
			}
		});
	}

	private void bindService() {
		if (mSecurityConnection != null) {
			unbindService(mSecurityConnection);
		}
		mSecurityConnection = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
				mSecurityCat = null;
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				mLogUtil.d("onServiceConnected");
				mSecurityCat = SecurityCat.Stub.asInterface(service);
				mHandler.sendEmptyMessage(MSG_SERVICE_CONNECTED);
			}
		};
		Intent intent = new Intent("com.yinhe.mysecurityguard");
		boolean isSuccess = bindService(intent, mSecurityConnection,
				Context.BIND_AUTO_CREATE);
		if (!isSuccess) {
			mHandler.sendEmptyMessageDelayed(MSG_SERVICE_RECONNECT, 2000);
		}

		mHandler.sendEmptyMessageDelayed(MSG_SERVICE_CHECK,
				Double.valueOf(Math.random() * 10000).intValue() + 10000);
	}

	private void unBindService() {
		if (mSecurityConnection != null) {
			unbindService(mSecurityConnection);
		}
	}

	private void startYinheConfig(int keyCode) {
		mEditorRecorder.add(keyCode);
		int recordSize = mEditorRecorder.size();
		if ((mEditorRecorder.get(recordSize - 1)) != KEY_CODE_FACTORY_PSW[recordSize - 1]) {
			mEditorRecorder.clear();
		} else if (KEY_CODE_FACTORY_PSW.length == recordSize) {
			mEditorRecorder.clear();
			createConfigrationDialog();
		}
	}

	private void createConfigrationDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this,
				R.style.style_dialog);
		LayoutInflater inflater = getLayoutInflater();
		View view = inflater.inflate(R.layout.dialog_yinhe_config, null);

		final EditText etDownloadUrl = (EditText) view
				.findViewById(R.id.et_download_url);
		final EditText etDownloadFileName = (EditText) view
				.findViewById(R.id.et_download_file_name);
		final EditText etUpdateUrl = (EditText) view
				.findViewById(R.id.et_update_url);

		etDownloadUrl.setText(PreferencesUtils.getString(this,
				NetWorkSpeedTestDialog.SP_KEY_DOWNLOAD_URL,
				NetWorkSpeedTestDialog.DEFAULT_DOWNLOAD_URL));
		etDownloadFileName.setText(PreferencesUtils.getString(this,
				NetWorkSpeedTestDialog.SP_KEY_DOWNLOAD_FILE_NAME,
				NetWorkSpeedTestDialog.DEFAULT_FILE_NAME));
		etUpdateUrl.setText(Secure.getString(getContentResolver(),
				FuncUtil.KEY_UPDATE_URL));

		builder.setView(view);
		builder.setTitle(R.string.network_info_config);
		builder.setPositiveButton(R.string.btn_save,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String downloadUrl = etDownloadUrl.getText().toString();
						String downloadFileName = etDownloadFileName.getText()
								.toString();
						String updateUrl = etUpdateUrl.getText().toString();

						PreferencesUtils.putString(MainActivity.this,
								NetWorkSpeedTestDialog.SP_KEY_DOWNLOAD_URL,
								downloadUrl);
						PreferencesUtils
								.putString(
										MainActivity.this,
										NetWorkSpeedTestDialog.SP_KEY_DOWNLOAD_FILE_NAME,
										downloadFileName);
						Secure.putString(getContentResolver(),
								FuncUtil.KEY_UPDATE_URL, updateUrl);
					}
				});
		builder.setNegativeButton(R.string.btn_cancel, null);
		final AlertDialog dialog = builder.create();
		dialog.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(DialogInterface dialogInterface, int keyCode,
					KeyEvent event) {
				if (dialog.getButton(DialogInterface.BUTTON_POSITIVE)
						.hasFocus() && keyCode == KeyEvent.KEYCODE_5) {
					etDownloadUrl.requestFocus();
					return true;
				}
				return false;
			}
		});
		dialog.getWindow().setSoftInputMode(
				WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
		dialog.show();
		etUpdateUrl.requestFocus();
	}
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      