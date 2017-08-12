package com.android.settings;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

import com.hisilicon.android.HiAoService;

import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;
import android.os.SystemProperties;

public class SoundSettings extends SettingsPreferenceFragment implements OnPreferenceChangeListener {
	private static final String TAG = "SoundSettings";
	private static final String HDMI_MODE = "hdmimode";
	private static final String SPDIF_MODE = "spdifmode";
	private static final String BLUELIGHT_MODE="bluelight";
	private static final String DOBLY_MODE="dobly";
	SharedPreferences sharedPreferences;
	private ListPreference mHDMI;
	private ListPreference mSPDIF;
	private ListPreference mBLUELIGHT;
    private ListPreference mDobly;
	private AudioManager mAudioManager;
	private HiAoService mAOService;

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);

		mAudioManager = (AudioManager) getActivity().getSystemService(
				Context.AUDIO_SERVICE);
		mAOService = new HiAoService();
             if(SystemProperties.get("ro.dolby.dmacert.enable").equals("true") || SystemProperties.get("ro.dolby.iptvcert.enable").equals("true") || SystemProperties.get("ro.dolby.dvbcert.enable").equals("true"))
                 addPreferencesFromResource(R.xml.volume_preference_dobly);
             else
                 addPreferencesFromResource(R.xml.volume_preference);
		sharedPreferences = PreferenceManager
				.getDefaultSharedPreferences(getActivity());

		mHDMI = (ListPreference) findPreference(HDMI_MODE);
		mHDMI.setOnPreferenceChangeListener(this);
		mSPDIF = (ListPreference) findPreference(SPDIF_MODE);
		mSPDIF.setOnPreferenceChangeListener(this);
		mBLUELIGHT = (ListPreference) findPreference(BLUELIGHT_MODE);
		mBLUELIGHT.setOnPreferenceChangeListener(this);
        if(SystemProperties.get("ro.dolby.dmacert.enable").equals("true") || SystemProperties.get("ro.dolby.iptvcert.enable").equals("true") || SystemProperties.get("ro.dolby.dvbcert.enable").equals("true"))
        {
            mDobly = (ListPreference) findPreference(DOBLY_MODE);
			mDobly.setOnPreferenceChangeListener(this);
        }
		int srentry = mAOService.getAudioOutput(1);
        mHDMI.setValue(String.valueOf(srentry));
        mHDMI.setSummary(mHDMI.getEntry());

        srentry = mAOService.getAudioOutput(2);
        if(srentry>=2){
            mSPDIF.setValue(String.valueOf(srentry-1));//mode-2,keep the value with spdif mode
        }else{
            mSPDIF.setValue(String.valueOf(srentry));
        }
        mSPDIF.setSummary(mSPDIF.getEntry());

        srentry = mAOService.getAudioOutput(3);
        mBLUELIGHT.setValue(String.valueOf(srentry));
        mBLUELIGHT.setSummary(mBLUELIGHT.getEntry());

        if(mHDMI.getValue().equals("2")){
            mBLUELIGHT.setEnabled(false);
        }else{
            mBLUELIGHT.setEnabled(true);
        }
        if(SystemProperties.get("ro.dolby.dmacert.enable").equals("true") || SystemProperties.get("ro.dolby.iptvcert.enable").equals("true") || SystemProperties.get("ro.dolby.dvbcert.enable").equals("true"))
        {
            if(SystemProperties.get("persist.sys.audio.dobly.output").equals("true"))
            {
                mDobly.setValue(String.valueOf(0));
                mDobly.setSummary(mDobly.getEntry());
            }
            else
            {
                mDobly.setValue(String.valueOf(1));
                mDobly.setSummary(mDobly.getEntry());
            }
	    }
       }
	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == mHDMI) {
			String srentry = (String) newValue;
			mHDMI.setValue(srentry);
			mHDMI.setSummary(mHDMI.getEntry());

			int mode = Integer.parseInt(srentry);
			mAOService.setAudioOutput(1, mode);

			SharedPreferences p = getPreferenceScreen().getSharedPreferences();
			SharedPreferences.Editor editor = p.edit();
			editor.putString(HDMI_MODE, srentry);
			editor.commit();

			if(mHDMI.getValue().equals("2")){
			    mBLUELIGHT.setEnabled(false);
			}else{
			    mBLUELIGHT.setEnabled(true);
			}
		} else if (preference == mSPDIF) {
			String srentry = (String) newValue;
			mSPDIF.setValue(srentry);
			mSPDIF.setSummary(mSPDIF.getEntry());

			int mode = Integer.parseInt(srentry);
			//John edit begin
			int unf_mode = -1;
			if(mode == 0){//John:close
				unf_mode = 0;
			}
			else{//John:LPCM or RAW
				unf_mode = mode + 1;
			}
			mAOService.setAudioOutput(2, unf_mode);//unf_mode,keep the value with spdif mode
			//John edit end

			SharedPreferences p = getPreferenceScreen().getSharedPreferences();
			SharedPreferences.Editor editor = p.edit();
			editor.putString(SPDIF_MODE, srentry);
			editor.commit();
		} else if (preference == mBLUELIGHT){
		    String srentry = (String) newValue;
            mBLUELIGHT.setValue(srentry);
            mBLUELIGHT.setSummary(mBLUELIGHT.getEntry());
		    int mode = Integer.parseInt(srentry);
            mAOService.setBluerayHbr(mode);

            SharedPreferences p = getPreferenceScreen().getSharedPreferences();
            SharedPreferences.Editor editor = p.edit();
            editor.putString(BLUELIGHT_MODE, srentry);
            editor.commit();
		} else if (preference == mDobly){
		    String srentry = (String) newValue;
            mDobly.setValue(srentry);
            mDobly.setSummary(mDobly.getEntry());
            if(srentry.equals("0"))
		SystemProperties.set("persist.sys.audio.dobly.output","true");
            else if(srentry.equals("1"))
		SystemProperties.set("persist.sys.audio.dobly.output","false");
		}

		return true;
	}

	private String getSoundSettingValue(int type) {
		SharedPreferences p = getPreferenceScreen().getSharedPreferences();
		String soundState ="";
		if(type == 1) {
			soundState = p.getString(HDMI_MODE,"1");
		} else if (type == 2) {
			soundState = p.getString(SPDIF_MODE,"1");
		} else if (type == 3){
		    soundState = p.getString(BLUELIGHT_MODE,"0");
		}
		return soundState;
	}
}
