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

package com.android.settings;

import static android.provider.Settings.System.SCREEN_OFF_TIMEOUT;
import android.app.ActivityManagerNative;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.database.ContentObserver;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.IWindowManager;
import android.view.Surface;

import com.android.settings.R;
import com.android.settings.video.ScopePreference;
import com.hisilicon.android.HiDisplayManager;

import java.util.*;
import android.app.AlertDialog;

import java.lang.reflect.Method;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Rect;
import android.os.*;

import android.widget.Toast;
import java.util.Calendar;
import java.util.Date;
import android.text.format.DateFormat;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.widget.TimePicker;
import android.app.Activity;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;

public class MediaSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private static final String TAG = "MediaSettings";

    public static final String MEDIA_HIPLAYER_ENABLE = "service.media.hiplayer";
    private static final String FORMAT_ADAPTION_ENABLE = "persist.sys.video.adaptformat";

    private String RATIO_KEY = "ratio";
    private String CVRS_KEY = "cvrs";
    public static final String PLAYER_KEY = "player";
    private static final String FORMAT_ADAPTION_KEY = "output_format_adaption";

    private HiDisplayManager display_manager;

    private ListPreference ratioPref; // ratio Pref
    private ListPreference cvrsPref; // cvrs Pref
    private ListPreference playerPref; // player Pref
    private CheckBoxPreference mOutputFormatAdaption;
    private static final int DEFAULT_VALUE = 0;
    private static final int FAILED_VALUE = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MediaSettings onCreate...");
        display_manager = new HiDisplayManager();

        addPreferencesFromResource(R.xml.media_settings);
        SharedPreferences p = getPreferenceScreen().getSharedPreferences();
        String v = p.getString(PLAYER_KEY, "1");
        int player = (v != null) && v.equals("0") ? 0 : 1;

        ratioPref = (ListPreference) findPreference(RATIO_KEY);
        cvrsPref = (ListPreference) findPreference(CVRS_KEY);
        playerPref = (ListPreference) findPreference(PLAYER_KEY);
        mOutputFormatAdaption = (CheckBoxPreference) findPreference(FORMAT_ADAPTION_KEY);

        int ratio = display_manager.getAspectRatio();
        int cvrs = display_manager.getAspectCvrs();

        if (ratio == FAILED_VALUE) {
            ratio = DEFAULT_VALUE;
        }
        if (cvrs == FAILED_VALUE) {
            cvrs = DEFAULT_VALUE;
        }

        ratioPref.setValueIndex(ratio);
        cvrsPref.setValueIndex(cvrs);
        playerPref.setValueIndex(player);
        String device_name = SystemProperties.get("ro.product.device");
        if(device_name.equals("Hi3798MV100")){
           removePreference(PLAYER_KEY);//hide videoplay select
        }
        mOutputFormatAdaption.setChecked("true".equals(
                SystemProperties.get(FORMAT_ADAPTION_ENABLE)));
    }

    @Override
    public void onResume() {
        super.onResume();
        ratioPref.setSummary(ratioPref.getEntry());
        cvrsPref.setSummary(cvrsPref.getEntry());
        playerPref.setSummary(playerPref.getEntry());

        ratioPref.setOnPreferenceChangeListener(this);
        cvrsPref.setOnPreferenceChangeListener(this);
        playerPref.setOnPreferenceChangeListener(this);
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mOutputFormatAdaption) {
            boolean value = mOutputFormatAdaption.isChecked();
            if (value) {
                SystemProperties.set(FORMAT_ADAPTION_ENABLE, "true");
            } else {
                SystemProperties.set(FORMAT_ADAPTION_ENABLE, "false");
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        if (RATIO_KEY.equals(key)) {
            updateRatio((Integer.parseInt((String) objValue)));
        }
        if (CVRS_KEY.equals(key)) {
            updateCvrs(Integer.parseInt((String) objValue));
        }
        if (PLAYER_KEY.equals(key)) {
            updatePlayer(Integer.parseInt((String) objValue));
        }
        return true;
    }

    /* update ration value */
    private void updateRatio(int ratioValue) {
        ratioPref.setSummary(ratioPref.getEntries()[ratioValue]);
        display_manager.setAspectRatio(ratioValue);
        display_manager.SaveParam();
    }

    /* update player value */
    private void updatePlayer(int value) {
        playerPref.setSummary(playerPref.getEntries()[value]);
        SharedPreferences p = getPreferenceScreen().getSharedPreferences();
        SharedPreferences.Editor editor = p.edit();
        editor.putString(PLAYER_KEY, value == 1 ? "true" : "false");
        editor.commit();
        editor.apply();
        Log.d(TAG, "updatePlayer option -- value:" + value);
        SystemProperties.set(MEDIA_HIPLAYER_ENABLE, value == 1 ? "true" : "false");
    }

    /* update cvrs value */
    private void updateCvrs(int cvrsValue) {
        cvrsPref.setSummary(cvrsPref.getEntries()[cvrsValue]);
        display_manager.setAspectCvrs(cvrsValue);
        display_manager.SaveParam();
    }
}
