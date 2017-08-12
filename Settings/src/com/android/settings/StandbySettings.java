/*
 * Copyright (C) 2008 The Android Open Source Project
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

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.util.Log;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import com.hisilicon.android.HiDisplayManager;


public class StandbySettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener{
    // have we been launched from the setup wizard?
    protected static final String EXTRA_IS_FIRST_RUN = "firstRun";
    private static final String KEY_HDMI_SUSPEND_ENABLE = "hdmi_standby_enable";
    private static final String KEY_HDMI_SUSPEND_TIME = "set_hdmi_standby_time";

    private CheckBoxPreference mHdmiStandbyEnable;
    private ListPreference mHdmiStandbyTimeList;

    private HiDisplayManager display_manager;

    private int oldTime;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        addPreferencesFromResource(R.xml.standby_prefs);
        display_manager = new HiDisplayManager();
        initUI();
    }

    private void initUI() {
        display_manager.getHDMISuspendTime();
        int hdmiStandbyEnabled = display_manager.getHDMISuspendEnable();

        mHdmiStandbyEnable = (CheckBoxPreference) findPreference(KEY_HDMI_SUSPEND_ENABLE);
        mHdmiStandbyEnable.setChecked(hdmiStandbyEnabled==1?true:false);

        mHdmiStandbyTimeList = (ListPreference) findPreference(KEY_HDMI_SUSPEND_TIME);
        mHdmiStandbyTimeList.setOnPreferenceChangeListener(this);
        Log.e("Standby","--->getHDMISuspendTime is:"+display_manager.getHDMISuspendTime());
        mHdmiStandbyTimeList.setValue(String.valueOf(display_manager.getHDMISuspendTime()));
    }

    @Override
    public void onResume() {
        super.onResume();
        mHdmiStandbyEnable.setChecked(display_manager.getHDMISuspendEnable()==1?true:false);
        mHdmiStandbyTimeList.setValue(String.valueOf(display_manager.getHDMISuspendTime()));
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if(preference == mHdmiStandbyEnable)
        {
            boolean value = mHdmiStandbyEnable.isChecked();
            display_manager.setHDMISuspendEnable(value ? 1 : 0);
        }
        if(preference == mHdmiStandbyTimeList){
            mHdmiStandbyTimeList.setValue(String.valueOf(display_manager.getHDMISuspendTime()));
            return true;
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
    public boolean onPreferenceChange(Preference preference, Object objValue) {
        final String key = preference.getKey();
        try
        {
            if (KEY_HDMI_SUSPEND_TIME.equals(key))
            {
                int newTime = Integer.parseInt((String) objValue);
                display_manager.setHDMISuspendTime(newTime);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return true;
    }
}
