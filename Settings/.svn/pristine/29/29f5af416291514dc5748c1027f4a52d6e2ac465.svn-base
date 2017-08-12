/*
 * Copyright (C) 2010 The Android Open Source Project
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
 */

package com.android.settings.wifi;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.os.SystemProperties;
import com.android.settings.R;

/**
 * Dialog to configure the SSID and security settings
 * for Access Point operation
 */
public class WifiApDialog extends AlertDialog implements View.OnClickListener, TextWatcher, AdapterView.OnItemSelectedListener, OnCheckedChangeListener {

    static final int BUTTON_SUBMIT = DialogInterface.BUTTON_POSITIVE;

    private final DialogInterface.OnClickListener mListener;

    public static final int OPEN_INDEX = 0;
    public static final int WPA_INDEX = 1;
    public static final int WPA2_INDEX = 2;
    public static String  HiddenSSID_ON_OFF;
    public String  mWiFi_Channels;

    private View mView;
    private TextView mSsid;
    private int mSecurityTypeIndex = OPEN_INDEX;
    private String mChannelsIndex[];
    private EditText mPassword;
    private Spinner mSecurity;
    private Spinner mChannel;

    WifiConfiguration mWifiConfig;

    public WifiApDialog(Context context, DialogInterface.OnClickListener listener,
            WifiConfiguration wifiConfig) {
        super(context);
        mListener = listener;
        mWifiConfig = wifiConfig;
        if (wifiConfig != null) {
            mSecurityTypeIndex = getSecurityTypeIndex(wifiConfig);
        }
    }

    public static int getSecurityTypeIndex(WifiConfiguration wifiConfig) {
        if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return WPA_INDEX;
        } else if (wifiConfig.allowedKeyManagement.get(KeyMgmt.WPA2_PSK)) {
            return WPA2_INDEX;
        }
        return OPEN_INDEX;
    }

    public WifiConfiguration getConfig() {

        WifiConfiguration config = new WifiConfiguration();

        /**
         * TODO: SSID in WifiConfiguration for soft ap
         * is being stored as a raw string without quotes.
         * This is not the case on the client side. We need to
         * make things consistent and clean it up
         */
        config.SSID = mSsid.getText().toString();
        config.channel=Integer.parseInt(mWiFi_Channels);
    SystemProperties.set("persist.sys.settings.channels", mWiFi_Channels);

    if(HiddenSSID_ON_OFF.equals("true")){
        config.hiddenSSID = true;
            SystemProperties.set("persist.sys.settings.ssidonoff", "true");
    }else if(HiddenSSID_ON_OFF.equals("false")){
        config.hiddenSSID = false;
            SystemProperties.set("persist.sys.settings.ssidonoff", "false");
    }


        switch (mSecurityTypeIndex) {
            case OPEN_INDEX:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                return config;

            case WPA_INDEX:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                if (mPassword.length() != 0) {
                    String password = mPassword.getText().toString();
                    config.preSharedKey = password;
                }
                return config;

            case WPA2_INDEX:
                config.allowedKeyManagement.set(KeyMgmt.WPA2_PSK);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                if (mPassword.length() != 0) {
                    String password = mPassword.getText().toString();
                    config.preSharedKey = password;
                }
                return config;
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        mView = getLayoutInflater().inflate(R.layout.wifi_ap_dialog, null);
        mSecurity = ((Spinner) mView.findViewById(R.id.security));
        mChannel = ((Spinner) mView.findViewById(R.id.channels));

        setView(mView);
        setInverseBackgroundForced(true);

        Context context = getContext();

        setTitle(R.string.wifi_tether_configure_ap_text);
        mView.findViewById(R.id.type).setVisibility(View.VISIBLE);
        mSsid = (TextView) mView.findViewById(R.id.ssid);
        mPassword = (EditText) mView.findViewById(R.id.password);
        mChannelsIndex= context.getResources().getStringArray(R.array.wifi_channels);
        setButton(BUTTON_SUBMIT, context.getString(R.string.wifi_save), mListener);
        setButton(DialogInterface.BUTTON_NEGATIVE,
        context.getString(R.string.wifi_cancel), mListener);

        if (mWifiConfig != null) {
            mSsid.setText(mWifiConfig.SSID);
            mSecurity.setSelection(mSecurityTypeIndex);

      if (mSecurityTypeIndex == WPA_INDEX ||
                  mSecurityTypeIndex == WPA2_INDEX) {
                  mPassword.setText(mWifiConfig.preSharedKey);
            }

        mWiFi_Channels =SystemProperties.get("persist.sys.settings.channels");
        if(mWiFi_Channels==null || mWiFi_Channels.equals("")){
        mChannel.setSelection(0);
                mWifiConfig.channel = Integer.parseInt(mChannelsIndex[0]);
        SystemProperties.set("persist.sys.settings.channels", mChannelsIndex[0]);

        }else{
        int chPosition=0;
                for (int i = 0; i < mChannelsIndex.length; i++) {
                    if (mChannelsIndex[i].equals(mWiFi_Channels)){
                        chPosition=i;
            mWifiConfig.channel = Integer.parseInt(mChannelsIndex[i]);
                    }
                }
                mChannel.setSelection(chPosition);
      }

    HiddenSSID_ON_OFF = SystemProperties.get("persist.sys.settings.ssidonoff");
      if(HiddenSSID_ON_OFF==null || HiddenSSID_ON_OFF.equals(""))
        {
            HiddenSSID_ON_OFF = "false";
            SystemProperties.set("persist.sys.settings.ssidonoff", HiddenSSID_ON_OFF);
      mWifiConfig.hiddenSSID=false;
        }
    if(HiddenSSID_ON_OFF.equals("true")){
      mWifiConfig.hiddenSSID=true;
    }else{
      mWifiConfig.hiddenSSID=false;
    }

        }

        mSsid.addTextChangedListener(this);
        mPassword.addTextChangedListener(this);
        ((CheckBox) mView.findViewById(R.id.show_password)).setOnCheckedChangeListener(this);
        CheckBox checkHiddenSSID=(CheckBox) mView.findViewById(R.id.show_ssid);
        checkHiddenSSID.setOnCheckedChangeListener(this);
        checkHiddenSSID.setChecked(mWifiConfig.hiddenSSID);

        mSecurity.setOnItemSelectedListener(this);
        mChannel.setOnItemSelectedListener(this);

        super.onCreate(savedInstanceState);

        showSecurityFields();
        validate();
    }

    private void validate() {
        if ((mSsid != null && mSsid.length() == 0) ||
                   (((mSecurityTypeIndex == WPA_INDEX) || (mSecurityTypeIndex == WPA2_INDEX))&&
                        mPassword.length() < 8)) {
            getButton(BUTTON_SUBMIT).setEnabled(false);
        } else {
            getButton(BUTTON_SUBMIT).setEnabled(true);
        }
    }

    public void onClick(View view) {
        /*
         * mPassword.setInputType( InputType.TYPE_CLASS_TEXT | (((CheckBox)
         * view).isChecked() ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD :
         * InputType.TYPE_TEXT_VARIATION_PASSWORD));
         */
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void afterTextChanged(Editable editable) {
        validate();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent==mSecurity) {
            mSecurityTypeIndex = position;
            showSecurityFields();
            validate();
        }
        if (parent==mChannel) {
            mWiFi_Channels= mChannelsIndex[position];
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }

    private void showSecurityFields() {
        if (mSecurityTypeIndex == OPEN_INDEX) {
            mView.findViewById(R.id.fields).setVisibility(View.GONE);
            return;
        }
        mView.findViewById(R.id.fields).setVisibility(View.VISIBLE);
    }

    @Override
    public void onCheckedChanged(CompoundButton view, boolean isChecked) {
        // TODO Auto-generated method stub
        if (view.getId() == R.id.show_password) {
            mPassword.setInputType(InputType.TYPE_CLASS_TEXT
                    | (((CheckBox) view).isChecked() ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD : InputType.TYPE_TEXT_VARIATION_PASSWORD));
        } else if (view.getId() == R.id.show_ssid) {
            if (isChecked) {
        HiddenSSID_ON_OFF="true";
            } else {
        HiddenSSID_ON_OFF="false";
            }
        }
    }
}
