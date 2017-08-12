package com.android.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.SystemProperties;
import com.android.settings.R;
import com.hisilicon.android.netshare.NativeSamba;

public class NetSambaSettings extends SettingsPreferenceFragment implements
		OnSharedPreferenceChangeListener,
		SambaEditPinPreference.OnPinEnteredListener,
		EditPinPreference.OnPinEnteredListener,
		Preference.OnPreferenceClickListener,
		Preference.OnPreferenceChangeListener {
	static final String TAG = "SambaSetting";

	static final String BLANK = "";

	static final String SPACE = " ";

	static final String ENTER = "\n";

	static final String SERVER_SHARE_MODE = "share";

	static final String SERVER_USER_MODE = "user";

	static final String SHARED_DIRECTORY = "/mnt";

	static final String SHARED_FILE_NAME = "share";

	static final String NET_BIOS_NAME = BLANK;

	static final String CAN_WRITE = "yes";

	static final String CAN_NOT_WRITE = "no";
	/*server user*/
	static final String SERVER_USER = "administrator";
	/*Defined key code*/
	public static final String BUTTON_CHANGE_PASS_KEY = "button_change_pass_key";

	public static final String ADD_NEW_SHARE = "button_file_key";

	public static final String CONTROL_CHECKBOX = "control_checkbox";

	public static final String BUTTON_CHANGE_ACCOUNT_KEY = "account_edittext";

	public static final String BUTTON_GROUP_KEY = "group_edittext";

	public static final String BUTTON_SERVER_INFO_KEY = "server_edittext";

	public static final String BATCH_DELETE_SHARE = "batch_del_shares";

	public static final String KEY_SERVER_USER_MODE = "server_user_mode";

	/*Enable password protection*/
	public static final String KEY_ENABLE_PASSWORD = "password_enabled";

	/*whether write able*/
	public static final String KEY_WRITEABLE = "chkbox_writeable";

	// preference "edit share"
	public static final String EDIT_SHARE_PREFS = "edit_share_prefs";

	// net-bios name
	public static final String EDIT_PREFS_NET_BIOS_NAME = "edt_prefs_net_bios";

	private static final String DEFAULT_WORK = "WORKGROUP";

    private static final String SAMBA_STATUS = "samba_status";

	/*default log row*/
	public static final int LOG_NUM = 225;

	/*CNcomment:Initialize the object
	The password settings*/
	private PreferenceScreen attrPrefScreenChangePwd;

	private CheckBoxPreference control_checkbox;

	//private EditTextPreference group_edittext;

	/*Enable password protection*/
	private CheckBoxPreference nChkboxPrefsEnablePwd;

	/*whether write able*/
	private CheckBoxPreference nChkboxPrefsWriteable;

	private NativeSamba samba;

	SharedPreferences spre;

	/*To store the Working Group input by user*/
	private String group = BLANK;

	/*To store the server info input by user*/
	private String server_info = SERVER_SHARE_MODE;

	/*hostname*/
	private String netBiosName = BLANK;

	/*shared file permissions*/
	private static final String FILE_PERMISSION = "0777";

	/*shared directory permissions*/
	private static final String DIRECTORY_PERMISSION = "0777";

	/*Available*/
	private static final String AVAIL = "yes";

	/*to browse*/
	private static final String BROWSE = "yes";

	/*can write*/
	private String nCanWrite = CAN_NOT_WRITE;

	/*password*/
	private String password = BLANK;

	private int count = 0;

	private Context mContext = null;
	static {
		System.loadLibrary("android_runtime");
	}

	@Override
	public void onResume() {
		super.onResume();

		// new an instance for NativeSamba to call native methods
		samba = new NativeSamba();

		PreferenceScreen ps = getPreferenceScreen();
        if (ps != null) {
            ps.removeAll();
        }
		/*Load the configuration page*/
		addPreferencesFromResource(R.xml.netservice_sambasettings_prefs);

		ps = getPreferenceScreen();
		mContext = ps.getContext();
		spre = ps.getSharedPreferences();

		/*At this point the process is turned on, 0 off, 1 on*/
		int initFlag = samba.getSambaProcessState();
		boolean startFlag = setInitCheckboxc(initFlag, CONTROL_CHECKBOX,
				control_checkbox, spre);
		writeToShare(spre, CONTROL_CHECKBOX, startFlag);

		/*Judgment of the Working Group is empty*/
		String workgroupValue = spre.getString(BUTTON_GROUP_KEY, BLANK);
		if (BLANK.equals(workgroupValue)) {
			writeStringToShare(spre, BUTTON_GROUP_KEY,
					getString(R.string.default_workgroup));
		}

		spre.registerOnSharedPreferenceChangeListener(this);
		PreferenceScreen prefSet = getPreferenceScreen();

		/*on/off samba*/
		control_checkbox = (CheckBoxPreference) prefSet
				.findPreference(CONTROL_CHECKBOX);
		control_checkbox.setChecked(startFlag);

		control_checkbox.setOnPreferenceChangeListener(this);

		/*enable password protection*/
		nChkboxPrefsEnablePwd = (CheckBoxPreference) findPreference(KEY_ENABLE_PASSWORD);
		nChkboxPrefsEnablePwd.setOnPreferenceChangeListener(this);

		/*workgroup*/
		group = spre.getString(BUTTON_GROUP_KEY,
				getString(R.string.default_workgroup));

		/*password*/
		/*Register button for click response*/
		attrPrefScreenChangePwd = (PreferenceScreen) prefSet
				.findPreference(BUTTON_CHANGE_PASS_KEY);

		attrPrefScreenChangePwd.setOnPreferenceClickListener(this);
		/*Set a password is available*/
		if (nChkboxPrefsEnablePwd.isChecked()) {
			attrPrefScreenChangePwd.setEnabled(true);
		} else {
			attrPrefScreenChangePwd.setEnabled(false);
		}

		server_info = spre.getString(KEY_SERVER_USER_MODE, BLANK);
		if (BLANK.equals(server_info)) {
			server_info = SERVER_SHARE_MODE;
		}

	}

	InputFilter nInputFilter = new InputFilter() {

		public CharSequence filter(CharSequence source, int start, int end,
				Spanned dest, int dstart, int dend) {
			String groupInfo  = spre.getString(BUTTON_GROUP_KEY,
					getString(R.string.default_workgroup));
			Context context = mContext;
			boolean isContainIn = context.getString(
					R.string.registered_share_dir_chars).contains(source);
			if (isContainIn || groupInfo.equals(source)) {
				return source;
			}
			showToast(context, context.getString(R.string.input_char_unvalidate));
			return "";
		}

	};


	/*set select state when initialization*/
	private boolean setInitCheckboxc(int flag, String key,
			CheckBoxPreference chkbox, SharedPreferences prefs) {
		boolean isStarted = false;
		if (flag == 1) {
			isStarted = true;
		}
		return isStarted;
	}

	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	/**
	 * Override onSharedPreferenceChanged method when values stored in xml file
	 * have been changed
	 */
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {
	}

	// override onPinEntered method
	// add new samba server share
	public void onPinEntered(SambaEditPinPreference preference,
			boolean positiveResult) {
	}// end of onPinEntered method

	/**
	 * Delegate to the respective handlers.
	 */
	public void onPinEntered(EditPinPreference preference,
			boolean positiveResult) {

	}

	/**
	 * override onPreferenceClick method to implement onPreferenceClickListener
	 * tell if we click the deleting shares preference to delete several shares
	 */
	public boolean onPreferenceClick(Preference preference) {

		/*Change Password ?*/
		if (BUTTON_CHANGE_PASS_KEY.equals(preference.getKey())) {
			showChangePwdDlg();
			return true;
		}

		return false;
	}

//	boolean shouldShowHint;

	/*Display error message flag, 0 normal*/
	int attrIsMatched = 0;

	/*Recorded in the Enter password box content*/
	String attrTmpInputPwd;

	/*Show Change Password dialog box.*/

	private void showChangePwdDlg() {

		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.pwd_modified_layout, null);
		attrDlgChangePwd = new AlertDialog.Builder(mContext).setView(view)
				.setTitle(R.string.pass_dialog).create();
		attrDlgChangePwd.setButton(DialogInterface.BUTTON_POSITIVE,
				getString(R.string.okBut), new DialogClickListener());
		attrDlgChangePwd.setButton(DialogInterface.BUTTON_NEGATIVE,
				getString(R.string.cancelBut), new DialogClickListener());
		attrDlgChangePwd.show();
		attrDlgChangePwd.getButton(DialogInterface.BUTTON_POSITIVE)
				.setTextAppearance(mContext,
						android.R.style.TextAppearance_Large_Inverse);
		attrDlgChangePwd.getButton(DialogInterface.BUTTON_NEGATIVE)
				.setTextAppearance(mContext,
						android.R.style.TextAppearance_Large_Inverse);

		TextView tvPwdErrHint = (TextView) attrDlgChangePwd
				.findViewById(R.id.tvPwdErrHint);
		final EditText edtInputPwd = (EditText) attrDlgChangePwd
				.findViewById(R.id.edtInputPwd);
		edtInputPwd.setFilters(new InputFilter[] { nInputFilter });
		final EditText edtConfirmPwd = (EditText) attrDlgChangePwd
				.findViewById(R.id.edtConfirmPwd);
		edtConfirmPwd.setFilters(new InputFilter[] { nInputFilter });

		EditText oldInputPwd = (EditText) attrDlgChangePwd
				.findViewById(R.id.edtInputOldPwd);
		oldInputPwd.setFilters(new InputFilter[] { nInputFilter });
		TextView text = (TextView) attrDlgChangePwd.findViewById(R.id.oldTitle);
		final String oldPwd = spre.getString(BUTTON_CHANGE_PASS_KEY, BLANK);

		if (oldPwd.equals(BLANK)) {
			edtInputPwd.setEnabled(true);
			edtInputPwd.setFocusable(true);
			edtConfirmPwd.setEnabled(true);
			edtConfirmPwd.setFocusable(true);
		} else {
			text.setVisibility(View.VISIBLE);
			oldInputPwd.setVisibility(View.VISIBLE);
			edtInputPwd.setEnabled(false);
			edtInputPwd.setFocusable(false);
			edtConfirmPwd.setEnabled(false);
			edtConfirmPwd.setFocusable(false);
		}

		oldInputPwd.addTextChangedListener(new TextWatcher() {

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				if (s.toString().equals(oldPwd)) {
					edtInputPwd.setEnabled(true);
					edtInputPwd.setFocusable(true);
					edtInputPwd.setFocusableInTouchMode(true);
					edtConfirmPwd.setEnabled(true);
					edtConfirmPwd.setFocusable(true);
					edtConfirmPwd.setFocusableInTouchMode(true);
				} else {
					edtInputPwd.setEnabled(false);
					edtInputPwd.setFocusable(false);
					edtConfirmPwd.setEnabled(false);
					edtConfirmPwd.setFocusable(false);
				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {

			}

			public void afterTextChanged(Editable s) {

			}
		});
		/*Password input is empty*/
		if (attrIsMatched == -1 || attrIsMatched == -2) {
			tvPwdErrHint.setText(R.string.pwd_empty_hint);
			tvPwdErrHint.setVisibility(View.VISIBLE);

			edtInputPwd.setText(attrTmpInputPwd);
			edtInputPwd.requestFocus();
			attrIsMatched = 0;
		} else if (attrIsMatched == 1) {
			tvPwdErrHint.setText(R.string.mismatchPin2);
			tvPwdErrHint.setVisibility(View.VISIBLE);

			edtInputPwd.setText(attrTmpInputPwd);
			edtInputPwd.requestFocus();
			attrIsMatched = 0;
		}

	}

	AlertDialog attrDlgChangePwd;
	/**
	 * DialogInterface.OnMultiChoiceClickListener AlertDialog that provide
	 * multiSelection
	 */
	static class DialogMultiChoiceSelectListener implements
			DialogInterface.OnMultiChoiceClickListener {
		// override onClick method
		public void onClick(DialogInterface dialog, int which, boolean isChecked) {

		}
	}

	/**
	 * DialogInterface.OnClickListener listener of positive and cancel button or
	 * clicked-item on UI
	 */
	class DialogClickListener implements DialogInterface.OnClickListener {
		// override onClick method
		public void onClick(DialogInterface dialog, int which) {

			// change password dialog
			if (attrDlgChangePwd == dialog) {
				handlePwdChange(dialog, which);
			}
		} // end of onClick method
	} // class DialogClickListener

	// key fields
	static final String WRITABLE = "writable";

	static final String AVAILABLE = "available";

	static final String BROWSEABLE = "browseable";

	static final String CREATE_MASK = "create mask";

	static final String DIRECTORY_MASK = "directory mask";

	static final String PATH = "path";

	static final String VALID_USERS = "valid users";

	static final String GUEST_OK = "guest ok";

	/*
	Password change function
	*/
	private void handlePwdChange(DialogInterface dialog, int which) {
		if (DialogInterface.BUTTON_POSITIVE == which) {
			/*Password prompt*/
			EditText edtInputPwd = (EditText) attrDlgChangePwd
					.findViewById(R.id.edtInputPwd);
			EditText edtConfirmPwd = (EditText) attrDlgChangePwd
					.findViewById(R.id.edtConfirmPwd);

			String inputPwd = edtInputPwd.getText().toString().trim();
			String comfirmPwd = edtConfirmPwd.getText().toString().trim();
			/*To judge whether the input is empty*/
			if (null == inputPwd || BLANK.equals(inputPwd)) {
				attrTmpInputPwd = BLANK;
				attrIsMatched = -1;
				onPreferenceClick(attrPrefScreenChangePwd);
				return;
			}
			if (null == comfirmPwd || BLANK.equals(comfirmPwd)) {
				attrTmpInputPwd = inputPwd;
				attrIsMatched = -2;
				onPreferenceClick(attrPrefScreenChangePwd);
				return;
			}
			/*Whether two same input */
			if (inputPwd.equals(comfirmPwd)) {
				int globalFlat = samba.setGlobal(group, BLANK, server_info,
						LOG_NUM, netBiosName);
				writeStringToShare(spre, KEY_SERVER_USER_MODE, SERVER_USER_MODE);
				int flag = samba.setPasswd(comfirmPwd);
				/*Settings are correct*/
				if (flag == 0) {
					showToast(
							mContext,
							getString(R.string.preference_set_success,
									getString(R.string.pass)));
					/*display the Password after settings*/
					writeStringToShare(spre, BUTTON_CHANGE_PASS_KEY, comfirmPwd);
					dialog.dismiss();
					restartServer();
					attrIsMatched = 0;
					return;
				} else {
					showToast(
							mContext,
							getString(R.string.preference_set_failed,
									getString(R.string.pass)));
					return;
				}
			} else {
				attrIsMatched = 1;
				onPreferenceClick(attrPrefScreenChangePwd);
				edtInputPwd.requestFocus();
			}
			return;
		}
		if (DialogInterface.BUTTON_NEGATIVE == which) {
			dialog.dismiss();
			return;
		}
	}

	/**
	 * Override onPreferenceChange method
	 */
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		/*The current state of on/off*/
		if (preference.getKey().equals(CONTROL_CHECKBOX)) {
			Boolean isStarted = (Boolean) newValue;
			/*samba start*/
			if (isStarted) {
				/*start samba server*/
				int flag = startServer(spre);
				if (flag == 0) {
					showToast(mContext,
							getString(R.string.start_service_successfully));
                    SharedPreferences p = getPreferenceScreen().getSharedPreferences();
			        SharedPreferences.Editor editor = p.edit();
                    editor.putString(SAMBA_STATUS, "true");
			        editor.commit();
                    SystemProperties.set("persist.sys.samba.status","true");
				} else if (flag < 0) {
					showToast(mContext,
							getString(R.string.start_service_fail));
					return false;
				}
			} else {
				int flag = samba.stopSambaServer();
				if (flag == 0) {
					showToast(mContext,
							getString(R.string.stop_service_successfully));
                    SharedPreferences p = getPreferenceScreen().getSharedPreferences();
			        SharedPreferences.Editor editor = p.edit();
                    editor.putString(SAMBA_STATUS, "false");
			        editor.commit();
                    SystemProperties.get("persist.sys.samba.status","false");
				} else if (flag < 0) {
					showToast(mContext,
							getString(R.string.stop_service_fail));
					return false;
				}
			}
			return true;
		}

		/*Enable password protection*/
		if (preference.getKey().equals(KEY_ENABLE_PASSWORD)) {
			Boolean isEnabled = (Boolean) newValue;
			attrPrefScreenChangePwd.setEnabled(isEnabled);
			if (!isEnabled) {
				server_info = SERVER_SHARE_MODE;
			} else {
				String passwordValue = spre.getString(BUTTON_CHANGE_PASS_KEY,
						BLANK);
				if (null == passwordValue || BLANK.equals(passwordValue)) {
					server_info = SERVER_SHARE_MODE;
				} else {
					server_info = SERVER_USER_MODE;
				}
			}
			showToast(mContext, getString(R.string.xxx_set_success));
			/*Write server mode to share*/
			writeStringToShare(spre, KEY_SERVER_USER_MODE, server_info);
			spre.edit().putBoolean(KEY_ENABLE_PASSWORD, isEnabled).commit();
			restartServer();
			return true;
		}

		return false;
	}

	/*
	change int layout file to view
	*/
	public View inflateLayout(int layoutId) {
		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		return inflater.inflate(layoutId, null);
	}

	/*
	 *save the string content to share file
	 * @param prefs
	 * @param key
	 * @param value
	*/
	private void writeStringToShare(SharedPreferences prefs, String key,
			String value) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(key, value);
		editor.commit();
	}

	private void writeToShare(SharedPreferences prefs, String key, Object value) {
	// value) {
		SharedPreferences.Editor editor = prefs.edit();
		if (value instanceof Boolean) {
			editor.putBoolean(key, (Boolean) value);
		}
		editor.commit();
	}

	/*
	 *Custom Toast
	 * @param mContext
	 *            Context
	 * @param res
	 *            Strings resources file
	*/
	public static void showToast(Context mContext, String res) {
		LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext)
				.inflate(R.layout.toast_text, null);
		TextView toast_text = (TextView) layout.findViewById(R.id.no_file_id);
		toast_text.setText(res);
		Toast toast = new Toast(mContext);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
		toast.setView(layout);
		toast.show();
	}

	public int startServer(SharedPreferences sp) {

		count++;
		spre = sp;
		/*Before turn on the server, the associated parameter settings*/
		password = spre.getString(BUTTON_CHANGE_PASS_KEY, BLANK);
		boolean check = spre.getBoolean(KEY_ENABLE_PASSWORD, false);

		if (count == 1) {
			samba = new NativeSamba();
			samba.setUser(SERVER_USER);
		}
		if (check) {
			samba.setPasswd(password);
		}
		/*Set the global parameters*/
		group = spre.getString(BUTTON_GROUP_KEY, DEFAULT_WORK);
		server_info = spre.getString(KEY_SERVER_USER_MODE, SERVER_SHARE_MODE);
		samba.setGlobal(group, "", server_info, LOG_NUM, netBiosName);

		/*After open the process to succeed, open sharing*/
		boolean isWriteable = spre.getBoolean(KEY_WRITEABLE, false);
		if (isWriteable) {
			nCanWrite = CAN_NOT_WRITE;
		} else {
			nCanWrite = CAN_WRITE;
		}
		/*Get all the configuration information*/
		String heads = samba.getProperty();
		/*Not share information,Increase,Have share information,read and write edit*/
		if (null == heads) {
			int fg1 = samba.addProperty(SHARED_FILE_NAME, SHARED_DIRECTORY,
					FILE_PERMISSION, DIRECTORY_PERMISSION, AVAIL, BROWSE,
					nCanWrite, SERVER_USER);
		} else {
			int fg2 = samba.editShare(SHARED_FILE_NAME, SHARED_DIRECTORY,
					FILE_PERMISSION, DIRECTORY_PERMISSION, AVAIL, BROWSE,
					nCanWrite, SERVER_USER);
		}
		/*samba start*/
		int flag = samba.startSambaServer();
		return flag;
	}

	private void restartServer() {
		if (control_checkbox.isChecked()) {
			int flag = samba.stopSambaServer();
			if (flag == 0) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				startServer(spre);
			}
		}
	}
}

                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                          