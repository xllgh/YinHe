package com.android.settings.g3;

import java.util.Timer;
import java.util.TimerTask;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.ContentObserver;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.RemoteException;
import android.os.INetworkManagementService;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.widget.Toast;
import android.os.SystemProperties;
import android.view.Gravity;
import android.widget.Switch;
import android.preference.PreferenceActivity;
import android.app.Activity;
import android.app.ActionBar;
import com.android.settings.R;
import android.net.g3.G3Manager;
import com.android.settings.SettingsPreferenceFragment;
import android.net.G3StateTracker;

public class G3Settings extends SettingsPreferenceFragment implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "G3Settings";
    private CheckBoxPreference mAuto;
    private CheckBoxPreference mCustom;
    private G3Dialog G3Dialog;

    private Preference modeType;
    private Preference ipAddress;
    private ListPreference accessPoint;
    private Preference apnName;
    private Preference dialNumber;
    private Preference pinCode;
    private Preference userName;
    private Preference userPassword;

    private Preference modeTypeCategory;
    private Preference accessPointCategory;
    private Preference apnNameCategory;
    private Preference dialNumberCategory;
    private Preference pinCodeCategory;
    private Preference userNameCategory;
    private Preference userPasswordCategory;

    private static final String KEY_MODULE_TYPE   = "module_type";
    private static final String key_ip_address    = "ip_address";
    private static final String KEY_CONNECTION_MODE_AUTO   = "connection_mode_auto";
    private static final String KEY_CONNECTION_MODE_CUSTOM = "connection_mode_custom";
    private static final String KEY_ACCESS_POINT  = "access_point";
    private static final String KEY_APN_NAME      = "apn_name";
    private static final String KEY_DIAL_NUMBER   = "dial_number";
    private static final String KEY_PIN_CODE      = "pin_code";
    private static final String KEY_USER_NAME     = "user_name";
    private static final String KEY_USER_PASSWORD = "user_password";
    private static final String KEY_MODULE_TYPE_CATEGORY  = "module_type_category";
    private static final String KEY_ACCESS_POINT_CATEGORY = "access_point_category";
    private static final String KEY_APN_NAME_TITLE        = "apn_name_title";
    private static final String KEY_DIAL_NUMBER_TITLE     = "dial_number_title";
    private static final String KEY_USER_NAME_TITLE       = "user_name_title";
    private static final String KEY_USER_PASSWORD_TITLE   = "user_password_title";

    private static final String KEY_PIN_CODE_TITLE  = "pin_code_title";
    private static final String SWITCH_ON           =  "com.android.setttings.3g.switch_on";
    private static final String SWITCH_OFF          = "com.android.setttings.3g.switch_off";
    private static final String SET_MODEL_TYPE      = "com.android.setttings.3g.set_model_type";
    private static final String SET_IP              = "com.android.setttings.3g.set_ip";

    private static final String CHINA_MOBILE        = "China Mobile";
    private static final String CHINA_TELECOM       = "China Telecom";
    private static final String CHINA_UNICOM        = "China Unicom";
    private static final String USER_DEFINED        = "User Defined";

    private static final String MOBILE_APN          = "cmnet";
    private static final String UNICOM_APN          = "3gnet";
    private static final String TELECOM_APN         = "";
    private static final String MOBILE_DIAL_NUMBER  = "*98*1#";
    private static final String UNICOM_DIAL_NUMBER  = "*99#";
    private static final String TELECOM_DIAL_NUMBER = "#777";
    private static final String MOBILE_PINCODE      = "";
    private static final String UNICOM_PINCODE      = "";
    private static final String TELECOM_PINCODE     = "";
    private static final String MOBILE_USERNAME     = "";
    private static final String UNICOM_USERNAME     = "";
    private static final String TELECOM_USERNAME    = "card";
    private static final String MOBILE_PWD          = "";
    private static final String UNICOM_PWD          = "";
    private static final String TELECOM_PWD         = "card";

    private static final String BOGUS_DNS1 = "10.11.12.13";
    private static final String BOGUS_DNS2 = "10.11.12.14";
    private static String validDns1 = "8.8.8.8";
    private static String validDns2 = "8.8.4.4";

    private final IntentFilter mIntentFilter;
    private boolean DEBUG = true;
    private boolean isAutoCheck;
    private boolean isCustomCheck;
    private String  access_point;
    private String  mode = null;
    private String  state;
    private Preference clieckedPreference;
    private G3Enabler mG3Enabler;
    private G3Manager g3Manager;
    private INetworkManagementService mNwService;

    public G3Settings() {
        super();
        mIntentFilter = new IntentFilter(G3Manager.G3_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(SWITCH_ON);
        mIntentFilter.addAction(SWITCH_OFF);
        mIntentFilter.addAction(SET_MODEL_TYPE);
        mIntentFilter.addAction(SET_IP);
    }

    private void setAutoAndCustomSummary(String summary){
        if(mAuto.isChecked()){
            mCustom.setChecked(false);
            mAuto.setSummary(summary);
            mCustom.setSummary("");
        }
        else if(mCustom.isChecked()){
            mAuto.setChecked(false);
            mAuto.setSummary("");
            mCustom.setSummary(summary);
        }else{
            mAuto.setSummary("");
            mCustom.setSummary("");
        }
    }

    private void showManualModeInfo(){

        if(DEBUG) Log.d(TAG,"showManualModeInfo()");
        try{
            int g3Mode = g3Manager.getConnectMode();
            boolean isMaunalMode = (g3Mode == g3Manager.G3_CONNECT_MODE_MANUAL);
            if(!isMaunalMode || !g3Manager.isCardadded()) return;

            SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
            SharedPreferences.Editor editor = sp.edit();
            String apValue    = sp.getString(KEY_ACCESS_POINT, "");
            ContentResolver resolver = getActivity().getContentResolver();
            String apnNameValue    = Settings.Secure.getString(resolver,Settings.Secure.G3_APN_NAME);
            String userNameValue   = Settings.Secure.getString(resolver,Settings.Secure.G3_USER_NAME);
            String dialNumberValue = Settings.Secure.getString(resolver,Settings.Secure.G3_DIAL_NUMBER);
            String passwdValue     = Settings.Secure.getString(resolver,Settings.Secure.G3_USER_PASS);
            String pinCodeValue    = Settings.Secure.getString(resolver,Settings.Secure.G3_PIN_CODE);
            if(apValue.equals("")){
                apValue = USER_DEFINED;
                editor.putString(KEY_ACCESS_POINT, USER_DEFINED);
                editor.commit();
            }
            accessPoint.setEnabled(true);
            accessPoint.setTitle(apValue);
            if (USER_DEFINED.equals(apValue)) {
                apnName.setEnabled(true);
                apnName.setTitle(apnNameValue);
                dialNumber.setEnabled(true);
                dialNumber.setTitle(dialNumberValue);
                pinCode.setEnabled(true);
                pinCode.setTitle(pinCodeValue);
                userName.setEnabled(true);
                userName.setTitle(userNameValue);
                userPassword.setEnabled(true);
                userPassword.setTitle(passwdValue);
            }
        }catch(NullPointerException e){
            Log.d(TAG,e+"");
        }

    }

    private void verifyBogusDns() {
        String dns1 = "";
        String dns2 = "";
        if(g3Manager.getG3ConnectStatus() == G3Manager.G3_CONNECT_STATE_CONNECTED) {
            dns1 = g3Manager.getDns1("ppp0");
            dns2 = g3Manager.getDns2("ppp0");
        } else
            return;

        /* update latest valid dns */
        if(dns1 != null && !("".equals(dns1)) && !(BOGUS_DNS1.equals(dns1))
            && !(BOGUS_DNS2.equals(dns1))) {
                Log.i(TAG, "update dns1:" + dns1);
                validDns1 = dns1;
        }

        if(dns2 != null && !("".equals(dns2)) && !(BOGUS_DNS1.equals(dns2))
            && !(BOGUS_DNS2.equals(dns2))) {
                Log.i(TAG, "update dns2:" + dns2);
                validDns2 = dns2;
        }

        /* verify dns if get bogus dns */
        if((BOGUS_DNS1.equals(dns1) && BOGUS_DNS2.equals(dns2))
         ||(BOGUS_DNS1.equals(dns2) && BOGUS_DNS2.equals(dns1))) {
            Log.i(TAG, "3G get bogus dns1:" + dns1 + " dns2:" + dns2);
            Log.i(TAG, "Valid dns1:" + validDns1 + " dns2:" + validDns1);

            String[] dns = new String[2];
            if(validDns1 != null && !("".equals(validDns1)) && !(BOGUS_DNS1.equals(validDns1))
            && !(BOGUS_DNS2.equals(validDns1))) {
                Log.i(TAG, "Change bogus dns:" + dns1 + "/" + dns2 +
                    " to " + validDns1 + "/" + validDns2);
                dns[0] = validDns1;

                if(validDns2 != null && !("".equals(validDns2)) && !(BOGUS_DNS1.equals(validDns2))
                && !(BOGUS_DNS2.equals(validDns2)))
                    dns[1] = validDns2;

                try {
                    mNwService.setDnsServersForInterface("ppp0", dns, "");
                    Log.i(TAG, "setDnsServersForInterface(ppp0" + dns +")");
                } catch (RemoteException e) {
                    Log.e(TAG, "3G connect success, setIpForwardingEnabled failed");
                }
            }
        }
    }

    private void showIpAddress(boolean isShow){
        Thread getIpThread = new Thread(new Runnable() {
            @Override
            public void run() {
            String ip = "";
            final int MAX_RETRY_TIMES = 10;
            int ix = 0;
            if(DEBUG) Log.i(TAG, "get ip thread start ");
            do{
                ip = "";
                if(g3Manager.getG3ConnectStatus() == G3Manager.G3_CONNECT_STATE_CONNECTED)
                    ip = g3Manager.getIpaddr("ppp0");

                verifyBogusDns();
                if((ip != null) && !("".equals(ip)))
                    break;
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    Log.d(TAG, e.toString());
                }
            }while(++ix < MAX_RETRY_TIMES);
            if(DEBUG) Log.i(TAG, "showIpAddress(): " + ip);
            if((ip != null) && !("".equals(ip))){
                Intent intent = new Intent(SET_IP);
                intent.putExtra("IP", ip);
                try{
                    getActivity().sendBroadcast(intent);
                }catch(NullPointerException e){
                    e.printStackTrace();
                }
            }
        }
        });

        if(isShow){
            getIpThread.start();
        }else{
            ipAddress.setEnabled(false);
            ((TextPrefreence)ipAddress).setText("");
        }
    }

    private void updateStateByConnectEvent(int event){
        int g3SwitchState = g3Manager.getG3PersistedState();
        boolean isG3On = (g3SwitchState == G3Manager.G3_STATE_ENABLED);
        if(!isG3On){
            Log.e(TAG,"updateStateByConnectEvent()  ---> SWITCH_OFF");
            enableScreen(false);
            return;
        }
        /*
        boolean isCardAddedState = g3Manager.isCardadded();
        if(!isCardAddedState){
            setAutoAndCustomSummary(getString(R.string.third_gen_insert_card));
            return;
        }
        */

        showIpAddress(false);
        addMaunalPreference();
        showManualModeInfo();
        switch(event){
        case G3StateTracker.EVENT_G3_CONNECT_SUCCESSED:
            if (DEBUG) Log.i(TAG, "onReceive() <== EVENT_G3_CONNECT_SUCCESSED");
            setAutoAndCustomSummary(getString(R.string.third_gen_connected));
            mAuto.setEnabled(true);
            mCustom.setEnabled(true);
            accessPointCategory.setEnabled(true);
            accessPoint.setEnabled(true);
            showIpAddress(true);
            try {
                mNwService.setIpForwardingEnabled(true);
            } catch (RemoteException e) {
                Log.e(TAG, "3G connect success, setIpForwardingEnabled failed");
            }
            break;
        case G3StateTracker.EVENT_G3_CONNECT_FAILED:
            if (DEBUG) Log.i(TAG, "onReceive() <== EVENT_G3_CONNECT_FAILED");
            setAutoAndCustomSummary(getString(R.string.third_gen_connect_failed));
            mAuto.setEnabled(true);
            mCustom.setEnabled(true);
            accessPointCategory.setEnabled(true);
            accessPoint.setEnabled(true);
            enableScreen(true);
            break;
        case G3StateTracker.EVENT_G3_DISCONNECT_SUCCESSED:
            if (DEBUG) Log.i(TAG, "onReceive() <== EVENT_G3_DISCONNECT_SUCCESSED");
            setAutoAndCustomSummary(getString(R.string.third_gen_disconnect));
            mAuto.setEnabled(true);
            mCustom.setEnabled(true);
            showModelType(true);
            break;
        case G3StateTracker.EVENT_G3_DISCONNECT_FAILED:
            if (DEBUG) Log.i(TAG, "onReceive() <== EVENT_G3_DISCONNECT_FAILED");
            setAutoAndCustomSummary(getString(R.string.third_gen_disconnet_failed));
            mAuto.setEnabled(true);
            mCustom.setEnabled(true);
            showModelType(true);
            break;
        case G3StateTracker.EVENT_G3_CONNECTING:
            if (DEBUG) Log.i(TAG, "onReceive() <== EVENT_G3_CONNECTING");
            setAutoAndCustomSummary(getString(R.string.third_gen_connecting));
            mAuto.setEnabled(false);
            mCustom.setEnabled(false);
            accessPoint.setEnabled(false);
            showModelType(true);
            break;
        case G3StateTracker.ENEVT_G3_DISCONNECTING:
            if (DEBUG) Log.i(TAG, "onReceive() <== ENEVT_G3_DISCONNECTING");
            mAuto.setEnabled(false);
            mCustom.setEnabled(false);
            accessPoint.setEnabled(false);
            break;
        case G3StateTracker.EVENT_G3_CARD_ADD:
            if (DEBUG) Log.i(TAG, "onReceive() <== EVENT_G3_CARD_ADD");
            mAuto.setEnabled(true);
            mCustom.setEnabled(true);
            accessPoint.setEnabled(true);
            showModelType(true);
            break;
        case G3StateTracker.EVENT_G3_CARD_REMOVE:
            if (DEBUG) Log.i(TAG, "onReceive() <== EVENT_G3_CARD_REMOVE");
            setAutoAndCustomSummary(getString(R.string.third_gen_insert_card));
            mAuto.setEnabled(false);
            mCustom.setEnabled(false);
            showModelType(false);
            removeManualPreference();
            accessPoint.setEnabled(false);
            break;
        case G3StateTracker.EVENT_G3_CONNECT_FAILED_WRONG_PASSWORD:
            if (DEBUG) Log.i(TAG, "onReceive() <== EVENT_G3_CONNECT_FAILED_WRONG_PASSWORD");
            setAutoAndCustomSummary(getString(R.string.third_gen_wrong_passwd));
            mAuto.setEnabled(true);
            mCustom.setEnabled(true);
            break;
        }
    }

    private final BroadcastReceiver mG3BroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            int defaultValue = -1;
            int event = -1;

            if (SWITCH_ON.equals(intent.getAction())) {
                if(DEBUG) Log.i(TAG, "RCV <=== SWITCH_ON broadcast");
                addMaunalPreference();
                showManualModeInfo();
                showModelType(true);
                if(g3Manager.isCardadded()){
                    if(DEBUG) Log.i(TAG, "SWITCH_ON & card added");
                    mAuto.setEnabled(true);
                    mCustom.setEnabled(true);
                    accessPoint.setEnabled(true);
                }else{
                    if(DEBUG) Log.i(TAG, "SWITCH_ON & card removed");
                    mAuto.setEnabled(false);
                    mCustom.setEnabled(false);
                    accessPoint.setEnabled(false);
                }

            }
            if (SWITCH_OFF.equals(intent.getAction())) {
                if(DEBUG) Log.i(TAG, "RCV <=== SWITCH_OFF broadcast");
                removeManualPreference();
                showModelType(false);
                enableScreen(false);
                if(g3Manager.isCardadded())
                    setAutoAndCustomSummary("");
            }
            if(SET_MODEL_TYPE.equals(intent.getAction())){
                String modeltype = intent.getStringExtra("MODEL_TYPE");
                if(DEBUG) Log.i(TAG, "RCV <=== SET_MODEL_TYPE broadcast:" + modeltype);
                if((modeltype != null) && !("".equals(modeltype)))
                    ((TextPrefreence) modeType).setText(modeltype);
            }

            if(SET_IP.equals(intent.getAction())){
                String ip = intent.getStringExtra("IP");
                if(DEBUG) Log.i(TAG, "RCV <=== SET_IP broadcast:" + ip);
                if((ip != null) && !("".equals(ip)))
                    ((TextPrefreence) ipAddress).setText(ip);
            }

            if (intent.getAction().equals(G3Manager.G3_STATE_CHANGED_ACTION)) {
                event = intent.getIntExtra(G3Manager.G3_STATE_EXTRA_KEY, defaultValue);
                updateStateByConnectEvent(event);
                getActivity().removeStickyBroadcast(intent);
            }
            if(DEBUG) Log.i(TAG, "BroadcastReceiver ---> onReceive() return");
        }

    };

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String value = newValue.toString();
        if(KEY_ACCESS_POINT.equals(preference.getKey())){
            accessPoint.setTitle(value);
            g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_MANUAL);

            AsyncTask<Void, Void, Void> task = new AsyncTask<Void, Void, Void>(){

                @Override
                protected Void doInBackground(Void... arg0) {
                    // TODO Auto-generated method stub
                    if(g3Manager.getG3ConnectStatus() == G3Manager.G3_CONNECT_STATE_CONNECTED)
                        g3Manager.setG3State(G3Manager.G3_STATE_DISABLED);
                    g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_MANUAL);
                    g3Manager.setG3State(G3Manager.G3_STATE_ENABLED);
                    return null;
                }

                @Override
                protected void onPostExecute(Void result) {
                    Log.i(TAG, "onPreferenceChange() ---> onPostExecute ---> updateUI()");
                    updateUI();
                }
            };

             chooseAccessPoint(value);
            if(USER_DEFINED.equals(value)){
                Log.i(TAG, "onPreferenceChange() ---> " + USER_DEFINED);
                enableScreen(true);
            }else if(CHINA_MOBILE.equals(value)){
                Log.i(TAG, "onPreferenceChange() ---> " + CHINA_MOBILE);
                enableScreen(true);
                removeManualPreference();
                showIpAddress(false);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }else if(CHINA_UNICOM.equals(value)){
                Log.i(TAG, "onPreferenceChange() ---> " + CHINA_UNICOM);
                enableScreen(true);
                removeManualPreference();
                showIpAddress(false);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
            else if(CHINA_TELECOM.equals(value)){
                Log.i(TAG, "onPreferenceChange() ---> " + CHINA_TELECOM);
                enableScreen(true);
                removeManualPreference();
                showIpAddress(false);
                task.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }
        return true;
    }

    private void chooseAccessPoint(String accessPoint){
        SharedPreferences sp = getPreferenceScreen().getSharedPreferences();
        SharedPreferences.Editor editor = sp.edit();

        if(accessPoint.equals(CHINA_MOBILE)){
            writeToDB(MOBILE_APN, MOBILE_DIAL_NUMBER, MOBILE_PINCODE, MOBILE_USERNAME, MOBILE_PWD);
            editor.putString(KEY_ACCESS_POINT, CHINA_MOBILE);
        }else if(accessPoint.equals(CHINA_UNICOM)){
            writeToDB(UNICOM_APN, UNICOM_DIAL_NUMBER, UNICOM_PINCODE, UNICOM_USERNAME, UNICOM_PWD);
            editor.putString(KEY_ACCESS_POINT, CHINA_UNICOM);
        }
        else if(accessPoint.equals(CHINA_TELECOM)){
            writeToDB(TELECOM_APN, TELECOM_DIAL_NUMBER, TELECOM_PINCODE, TELECOM_USERNAME, TELECOM_PWD);
            editor.putString(KEY_ACCESS_POINT, CHINA_TELECOM);
        }else if(accessPoint.equals(USER_DEFINED)){
            editor.putString(KEY_ACCESS_POINT, USER_DEFINED);
        }
        editor.commit();
    }

    private void writeToDB(String apnValue, String dialNumberValue, String pincodeValue, String userNameValue, String pwdValue) {
        ContentResolver resolver = getActivity().getContentResolver();
        Log.d(TAG,"apn = " + apnValue+ " " + "dialNumber = " +dialNumberValue + " username= " +
            userNameValue + " pincode = " + pincodeValue + " pwd = " + pwdValue);

        Settings.Secure.putString(resolver,Settings.Secure.G3_USER_NAME,userNameValue);
        Settings.Secure.putString(resolver,Settings.Secure.G3_APN_NAME,apnValue);
        Settings.Secure.putString(resolver,Settings.Secure.G3_DIAL_NUMBER,dialNumberValue);
        Settings.Secure.putString(resolver,Settings.Secure.G3_USER_PASS,pwdValue);
        Settings.Secure.putString(resolver,Settings.Secure.G3_PIN_CODE,pincodeValue);
    }

    private void initG3UI(){
        mAuto = (CheckBoxPreference) findPreference(KEY_CONNECTION_MODE_AUTO);
        mCustom = (CheckBoxPreference) findPreference(KEY_CONNECTION_MODE_CUSTOM);
        modeType = findPreference(KEY_MODULE_TYPE);
        ipAddress = findPreference(key_ip_address);
        accessPoint = (ListPreference)findPreference(KEY_ACCESS_POINT);
        apnName = findPreference(KEY_APN_NAME);
        dialNumber = findPreference(KEY_DIAL_NUMBER);
        pinCode = findPreference(KEY_PIN_CODE);
        userName = findPreference(KEY_USER_NAME);
        userPassword = findPreference(KEY_USER_PASSWORD);

        modeTypeCategory = findPreference(KEY_MODULE_TYPE_CATEGORY);
        accessPointCategory = findPreference(KEY_ACCESS_POINT_CATEGORY);
        apnNameCategory = findPreference(KEY_APN_NAME_TITLE);
        dialNumberCategory = findPreference(KEY_DIAL_NUMBER_TITLE);
        pinCodeCategory = findPreference(KEY_PIN_CODE_TITLE);
        userNameCategory = findPreference(KEY_USER_NAME_TITLE);
        userPasswordCategory = findPreference(KEY_USER_PASSWORD_TITLE);
        if(DEBUG) Log.i(TAG, "initG3UI() success");
    }

    private int getPersistG3EnableState(){
        int g3EnableState = G3Manager.G3_STATE_UNKNOWN;
        ContentResolver resolver = getActivity().getContentResolver();
        try {
            g3EnableState = Settings.Secure.getInt(resolver, Settings.Secure.G3_ON);
        } catch (SettingNotFoundException e) {
            Log.e(TAG, "getPersistG3EnableState(): read system setting G3_ON failed");
            e.printStackTrace();
            g3EnableState = G3Manager.G3_STATE_UNKNOWN;
        }
        if(DEBUG){
            String state;
            switch(g3EnableState){
            case G3Manager.G3_STATE_ENABLED:
                state = "G3_STATE_ENABLED";
                break;
            case G3Manager.G3_STATE_DISABLED:
                state = "G3_STATE_DISABLED";
                break;
            default:
                state = "G3_STATE_UNKNOWN";
                break;

            }
            Log.i(TAG, "getPersistG3EnableState() system setting G3_ON: " + state);
        }
        return g3EnableState;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        final Activity activity = getActivity();
        Switch actionBarSwitch = new Switch(activity);
        if (activity instanceof PreferenceActivity) {
            PreferenceActivity preferenceActivity = (PreferenceActivity) activity;
            if (preferenceActivity.onIsHidingHeaders() || !preferenceActivity.onIsMultiPane()) {
                final int padding = activity.getResources().getDimensionPixelSize(
                        R.dimen.action_bar_switch_padding);
                actionBarSwitch.setPaddingRelative(0, 0, padding, 0);
                activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                        ActionBar.DISPLAY_SHOW_CUSTOM);
                activity.getActionBar().setCustomView(actionBarSwitch, new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER_VERTICAL | Gravity.END));
            }
        }
        mG3Enabler = new G3Enabler(activity, actionBarSwitch);
        mG3Enabler.setSwitch(actionBarSwitch, mG3Enabler);
        g3Manager = (G3Manager)getActivity().getSystemService(Context.G3_SERVICE);
        if(DEBUG) Log.i(TAG, "Init G3Enabler and G3Manager success");
        actionBarSwitch.setChecked((getPersistG3EnableState() == G3Manager.G3_STATE_ENABLED));

        int g3Mode = g3Manager.getG3PersistedConnectMode();
        //g3Manager.setConnectMode(g3Mode);
        isAutoCheck = (g3Mode == G3Manager.G3_CONNECT_MODE_AUTO);
        isCustomCheck = (g3Mode == G3Manager.G3_CONNECT_MODE_MANUAL);
        if(DEBUG) Log.i(TAG, "onCreate() -->connect mode: " +
            (isAutoCheck?"G3_CONNECT_MODE_AUTO":"G3_CONNECT_MODE_MANUAL"));

        this.addPreferencesFromResource(R.xml.third_gen_settings);
        initG3UI();

        mAuto.setChecked(isAutoCheck);
        mCustom.setChecked(isCustomCheck);

        if(getPersistG3EnableState() != G3Manager.G3_STATE_ENABLED){
            enableScreen(false);
        }else{
            //show or hide some preference
            PreferenceScreen screen = getPreferenceScreen();
            if (isAutoCheck || (!isAutoCheck && !isCustomCheck)) {
                removeManualPreference();
            }else
                addMaunalPreference();
        }
        IBinder b = ServiceManager
				.getService(Context.NETWORKMANAGEMENT_SERVICE);
		mNwService = INetworkManagementService.Stub.asInterface(b);
    }

    /* getModuleType() will cose about 6s, so run it in a thread */
    private void showModelType(boolean isShow){
        Thread getModelTypeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String modelType;
                final int MAX_RETRY_TIMES = 10;
                if(DEBUG) Log.i(TAG, "showModelType() thread start ");
                int ix = 0;
                do{
                    modelType = g3Manager.getModuleType();
                    if((modelType != null) && !("".equals(modelType)))
                        break;
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        Log.d(TAG, e.toString());
                    }
                }while(++ix < MAX_RETRY_TIMES);
                if(DEBUG) Log.i(TAG, "showModelType(): " + modelType);
                if((modelType != null) && !("".equals(modelType))){
                    Intent intent = new Intent(SET_MODEL_TYPE);
                    intent.putExtra("MODEL_TYPE", modelType);
                    try{
                        getActivity().sendBroadcast(intent);
                    }catch(NullPointerException e){
                        e.printStackTrace();
                    }
                }
            }
        });
        if(isShow){
            if(DEBUG) Log.i(TAG, "showModelType() start ");
            getModelTypeThread.start();
        }else
            ((TextPrefreence) modeType).setText("");
        if(DEBUG)Log.i(TAG, "showModelType(" + isShow + ") success");
    }

    public void updateUI(){
        if(g3Manager.isCardadded()){
            if(DEBUG) Log.i(TAG, "onResume() -->3g card is added");
            showModelType(true);
            showIpAddress(false);
            switch(g3Manager.getG3ConnectStatus()){
                case G3Manager.G3_CONNECT_STATE_CONNECTED:
                    mAuto.setEnabled(true);
                    mCustom.setEnabled(true);
                    setAutoAndCustomSummary(getString(R.string.third_gen_connected));
                    showIpAddress(true);
                    break;
                case G3Manager.G3_CONNECT_STATE_DISCONNECTED:
                case G3Manager.G3_CONNECT_STATE_UNKNOWN:
                    mAuto.setEnabled(true);
                    mCustom.setEnabled(true);
                    setAutoAndCustomSummary(getString(R.string.third_gen_disconnect));
                    break;
                case G3Manager.G3_CONNECT_STATE_CONNECTING:
                    mAuto.setEnabled(false);
                    mCustom.setEnabled(false);
                    setAutoAndCustomSummary(getString(R.string.third_gen_connecting));
                    break;
                case G3Manager.G3_CONNECT_STATE_DISCONNECTING:
                    mAuto.setEnabled(false);
                    mCustom.setEnabled(false);
                    break;
            }

            if (mCustom.isChecked()) {
                addMaunalPreference();
                showManualModeInfo();
                g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_MANUAL);
            }else if(mAuto.isChecked()){
                g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_AUTO);
            }else{
                g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_NONE);
            }
        }else{
            if(DEBUG) Log.i(TAG, "updateUI() -->3g card is removed");
            mAuto.setEnabled(false);
            mCustom.setEnabled(false);
            setAutoAndCustomSummary(getString(R.string.third_gen_insert_card));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
        mG3Enabler.resume();
        getActivity().registerReceiver(mG3BroadcastReceiver, mIntentFilter);
        registerObserver();
        removeManualPreference();
        modeType.setEnabled(false);
        ipAddress.setEnabled(false);
        accessPoint.setOnPreferenceChangeListener(this);
        updateUI();
    }

    private void registerObserver() {
        ContentResolver resolver = this.getContentResolver();
        resolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.G3_USER_NAME), true, dbObserver);
        resolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.G3_APN_NAME), true, dbObserver);
        resolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.G3_DIAL_NUMBER), true, dbObserver);
        resolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.G3_USER_PASS), true, dbObserver);
        resolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.G3_PIN_CODE), true, dbObserver);
        resolver.registerContentObserver(Settings.Secure.getUriFor(Settings.Secure.G3_ON), true, switchObserver);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
    clieckedPreference = preference;
        AsyncTask<Void, Void, Void> asyncTask = new AsyncTask<Void, Void, Void>() {
            PreferenceScreen screen = getPreferenceScreen();

            @Override
            protected void onPreExecute() {
            if(DEBUG) Log.i(TAG, "onPreferenceTreeClick() --> onPreExecute()");
                if (clieckedPreference == mCustom) {
                    if(DEBUG) Log.i(TAG, "onPreferenceTreeClick() --> custon is clicked");
                    if(mCustom.isChecked()){
                        if(DEBUG) Log.i(TAG, "onPreferenceTreeClick() --> custon is checked");
                        g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_MANUAL);
                        addMaunalPreference();
                        showManualModeInfo();
                        mAuto.setChecked(false); //exclusive checkbox
                        mAuto.setSummary("");

                    }else{
                        if(DEBUG) Log.i(TAG, "onPreferenceTreeClick() --> custon is not checked");
                        g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_NONE);
                        mCustom.setSummary("");
                    }
                    mAuto.setEnabled(true);
                    mCustom.setEnabled(true);
                } else if (clieckedPreference == mAuto) {
                    if(DEBUG) Log.i(TAG, "onPreferenceTreeClick() --> auto is clicked");
                    if(mAuto.isChecked()){
                        if(DEBUG) Log.i(TAG, "onPreferenceTreeClick() --> auto is checked");
                        g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_AUTO);
                        removeManualPreference();
                        mCustom.setChecked(false);
                        mCustom.setSummary("");
                        mAuto.setEnabled(false);
                        mCustom.setEnabled(false);
                    }else{
                        if(DEBUG) Log.i(TAG, "onPreferenceTreeClick() --> auto is not checked");
                        g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_NONE);
                        mAuto.setSummary("");
                        mAuto.setEnabled(true);
                        mCustom.setEnabled(true);
                    }
                }
                super.onPreExecute();
            }

            @Override
            protected Void doInBackground(Void... params) {
            if(DEBUG) Log.i(TAG, "onPreferenceTreeClick() --> doInBackground()");
                if (clieckedPreference == mAuto || clieckedPreference == mCustom) {
                    if(mAuto.isChecked()) {
                        if(g3Manager.getG3ConnectStatus() == G3Manager.G3_CONNECT_STATE_CONNECTED)
                            g3Manager.setG3State(G3Manager.G3_STATE_DISABLED);
                        g3Manager.setConnectMode(g3Manager.G3_CONNECT_MODE_AUTO);
                        g3Manager.setG3State(G3Manager.G3_STATE_ENABLED);
                    }else if (mCustom.isChecked()) {
                        if(g3Manager.getG3ConnectStatus() == G3Manager.G3_CONNECT_STATE_CONNECTED)
                            g3Manager.setG3State(G3Manager.G3_STATE_DISABLED);
                        g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_MANUAL);
                        if(!((getPreferenceScreen().getSharedPreferences()
                            .getString(KEY_ACCESS_POINT, USER_DEFINED).equals(USER_DEFINED))))
                            g3Manager.setG3State(G3Manager.G3_STATE_ENABLED);
                    }else{
                        g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_NONE);
                        g3Manager.setG3State(G3Manager.G3_STATE_DISABLED);
                    }
                }
                return null;
            }
        };

        asyncTask.execute();
        return super.onPreferenceTreeClick(preferenceScreen, clieckedPreference);
    }

    //accesspoint is china unicom or china mobile or china telecom will invoke this
    private void removeManualPreference(){
        PreferenceScreen screen = getPreferenceScreen();
        screen.removePreference(dialNumberCategory);
        screen.removePreference(pinCodeCategory);
        screen.removePreference(userNameCategory);
        screen.removePreference(userPasswordCategory);
        screen.removePreference(apnNameCategory);

        if(g3Manager.getConnectMode() != G3Manager.G3_CONNECT_MODE_MANUAL){
            screen.removePreference(accessPointCategory);
        }
    }

    private void addMaunalPreference(){
        PreferenceScreen screen = getPreferenceScreen();
        String accessPointValue = screen.getSharedPreferences().getString(KEY_ACCESS_POINT, "");
        SharedPreferences.Editor editor = screen.getSharedPreferences().edit();
        if((g3Manager.getConnectMode() == G3Manager.G3_CONNECT_MODE_MANUAL)){
            if(DEBUG) Log.i(TAG, "addMaunalPreference() in G3_CONNECT_MODE_MANUAL");
            screen.addPreference(accessPointCategory);
            accessPoint.setEnabled(true);
            if(accessPointValue.equals("")){
                accessPointValue = "User Defined";
                editor.putString(KEY_ACCESS_POINT, USER_DEFINED);
                editor.commit();
                accessPoint.setTitle(accessPointValue);
            }
            if((g3Manager.isCardadded()) && (USER_DEFINED.equals(accessPointValue))){
                if(DEBUG) Log.i(TAG, "addMaunalPreference() in USER_DEFINED");
                screen.addPreference(apnNameCategory);
                screen.addPreference(userPasswordCategory);
                screen.addPreference(userNameCategory);
                screen.addPreference(pinCodeCategory);
                screen.addPreference(dialNumberCategory);
            }
          }
    }

    private ContentObserver dbObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (DEBUG)Log.e(TAG, "[G3Setting] dbObserver()->datebase changed: " + selfChange);
            showManualModeInfo();
            checkSwitchDB();
        }
    };

    private ContentObserver switchObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (DEBUG) Log.i(TAG, "switchObserver.onChange() : " + selfChange);
            try{
                checkSwitchDB();
            }catch(Exception e){
                Log.d(TAG,e+"");
            }
        }
    };

    private void enableScreen(boolean isEnable){
        if(DEBUG) Log.i(TAG, "enableScreen(" + isEnable + ")");
        if(isEnable){
            addMaunalPreference();
            showManualModeInfo();
            showIpAddress(true);
        }else{
            removeManualPreference();
            showIpAddress(false);
        }
        mAuto.setEnabled(isEnable);
        mCustom.setEnabled(isEnable);
        modeType.setEnabled(isEnable);
        ipAddress.setEnabled(isEnable);
        accessPoint.setEnabled(isEnable);
        apnName.setEnabled(isEnable);
        dialNumber.setEnabled(isEnable);
        pinCode.setEnabled(isEnable);
        userName.setEnabled(isEnable);
        userPassword.setEnabled(isEnable);

        if(mAuto.isChecked()) {
            g3Manager.setConnectMode(g3Manager.G3_CONNECT_MODE_AUTO);
        }else if (mCustom.isChecked()) {
            g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_MANUAL);
        }else{
            g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_NONE);
        }
    }

    protected void checkSwitchDB() {
        ContentResolver resolver = getActivity().getContentResolver();
            int SwitchEnable = getPersistG3EnableState();
            if (SwitchEnable == G3Manager.G3_STATE_DISABLED) {
                if(DEBUG) Log.i(TAG, "checkSwitchDB(): G3_STATE_DISABLED");
                enableScreen(false);
            } else if ((SwitchEnable == G3Manager.G3_STATE_ENABLED)) {
                if(DEBUG) Log.i(TAG, "checkSwitchDB(): G3_STATE_ENABLED");
                if(g3Manager.isCardadded())
                    enableScreen(true);
            }
            modeType.setEnabled(false);
            ipAddress.setEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mG3Enabler.pasue();
        g3Manager.setG3PersistedConnectMode(g3Manager.getConnectMode());
    }

    @Override
    public void onDestroy() {
        if(DEBUG) Log.i(TAG, "onDestroy()");
        g3Manager.setG3PersistedConnectMode(g3Manager.getConnectMode());
        this.getContentResolver().unregisterContentObserver(switchObserver);
        this.getContentResolver().unregisterContentObserver(dbObserver);
        getActivity().unregisterReceiver(mG3BroadcastReceiver);
        super.onDestroy();
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                              