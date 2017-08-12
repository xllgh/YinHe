package com.android.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.RecoverySystem;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.os.storage.ExtraInfo;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;
import android.os.PowerManager;
import android.provider.Settings.System;

public class ExternalStorageSettings extends SettingsPreferenceFragment {
  private static final String TAG = "ExternalStorageSettings";
    private int deviceCount = 0;
    private String currentSelectedPath;
    private String defaultPath;
    private String currentVolumeID;

    private String devPath[];
    private String devName[];

    List<ExtraInfo> ret;
	//kf47237
	List<ExtraInfo> rets = new ArrayList<ExtraInfo>();
    private String defauluuid;
    private static SharedPreferences m_sharedPreferences;
    private static final String pExStorage = java.lang.System.getenv("EXTERNAL_STORAGE_VOLD");
  @Override
  public void onCreate(Bundle savedInstanceState) {
    // TODO Auto-generated method stub
    m_sharedPreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
    super.onCreate(savedInstanceState);
    addPreferencesFromResource(R.xml.upgrade_settings);
    updatePreference();
  }
  private void updatePreference() {
    // TODO Auto-generated method stub
    try{
      ret = getMountService().getAllExtraInfos();
    } catch (RemoteException e ) {
      e.printStackTrace();
    }
	rets.clear();
    deviceCount = 0;
    defauluuid = m_sharedPreferences.getString("externalStorage", "null"+pExStorage);
    Log.d("xiangzhiwei", " " + defauluuid);
    int index = defauluuid.indexOf("/");
    defauluuid = defauluuid.substring(0,index);

		for (int i = 0; i < ret.size(); i++) {
			if (ret.get(i).mMountPoint == null
					|| ret.get(i).mMountPoint.equals("")) {
				break;
			} else {
				rets.add(ret.get(i));
			}
			deviceCount++;
		}
    devPath = new String[deviceCount];
    devName = new String[deviceCount];


		for (int i = 0; i < deviceCount; i++) {
			index = rets.get(i).mMountPoint.lastIndexOf("/");

			if (defauluuid.equals("null")) {
				String defautDevName = rets.get(i).mMountPoint
						.substring(index + 1);
				if (rets.get(i).mUUID == null
						|| pExStorage.contains(defautDevName)) {
					if(rets.get(i).mLabel != null){
						devName[i] = rets.get(i).mDiskLabel + ": " + rets.get(i).mLabel + "  (" + getActivity().getResources().getString(R.string.default_info) + ")";
					}else{
						devName[i] = rets.get(i).mDiskLabel + "  (" + getActivity().getResources().getString(R.string.default_info) + ")";
					}
				} else {
					if(rets.get(i).mLabel != null){
						devName[i] = rets.get(i).mDiskLabel+ ": " + rets.get(i).mLabel;
					}else{
						devName[i] = rets.get(i).mDiskLabel;
					}
				}
			} else if (defauluuid.equals(rets.get(i).mUUID)) {
				if(rets.get(i).mLabel != null){
					devName[i] = rets.get(i).mDiskLabel + ": " + rets.get(i).mLabel + "  (" + getActivity().getResources().getString(R.string.default_info) + ")";
				}else{
					devName[i] = rets.get(i).mDiskLabel + "  (" + getActivity().getResources().getString(R.string.default_info) + ")";
				}
			} else {
				if(rets.get(i).mLabel != null){
					devName[i] = rets.get(i).mDiskLabel+ ": " + rets.get(i).mLabel;
				}else{
					devName[i] = rets.get(i).mDiskLabel;
				}
			}
			devPath[i] = rets.get(i).mMountPoint;
		}

    PreferenceCategory preferenceCategory = new PreferenceCategory(getActivity());
    getPreferenceScreen().addPreference(preferenceCategory);
    preferenceCategory.setTitle(R.string.default_external_storage_title);

    for (int i = 0; i < deviceCount; i++) {
      Preference preference = new Preference(getActivity());
      preference.setTitle(devName[i]);
      preference.setKey(devPath[i]);
      preferenceCategory.addPreference(preference);
    }
  }

  BroadcastReceiver mReceiver = new BroadcastReceiver() {
    @Override
    public void onReceive(Context context, Intent intent) {
      Log.d(TAG, "------------------------------------------------------------------------------");
      if(Intent.ACTION_MEDIA_MOUNTED.equals(intent.getAction()) ||
          Intent.ACTION_MEDIA_UNMOUNTED.equals(intent.getAction())||
          Intent.ACTION_MEDIA_REMOVED.equals(intent.getAction()))
      {
        try
        {
          Thread.sleep(1000);
        }
        catch (InterruptedException e)
        {
          e.printStackTrace();
        }
        getPreferenceScreen().removeAll();
        updatePreference();
      }
    }
  };

  public static IMountService getMountService() {
    IBinder service = ServiceManager.getService("mount");
    if (service != null) {
               return IMountService.Stub.asInterface(service);
            } else {
               Log.e(TAG, "Can't get mount service");
           }
           return null;
    }
  @Override
  public void onResume() {
    // TODO Auto-generated method stub
    IntentFilter intentFilter_ScanFinish = new IntentFilter();
    intentFilter_ScanFinish.addAction(Intent.ACTION_MEDIA_MOUNTED);
    intentFilter_ScanFinish.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
    intentFilter_ScanFinish.addDataScheme("file");
    getActivity().registerReceiver(mReceiver, intentFilter_ScanFinish);
    super.onResume();
  }
  @Override
  public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
      Preference preference) {
    // TODO Auto-generated method stub
    String externPath = preference.getKey();
    setcurrentSelectedPath(externPath);
    String readPath = m_sharedPreferences.getString("externalStorage", "ffff"+pExStorage);
    int index = readPath.indexOf("/");
    setdefaultPath(readPath.substring(index));
    String defaultVolumeID = readPath.substring(0, index);

      IBinder service = ServiceManager.getService("mount");
      IMountService mountService = null;
      if(service != null){
        mountService = IMountService.Stub.asInterface(service);
          try {
            setcurrentVolumeID(getUUID(getcurrentSelectedPath()));
      } catch (Exception e) {
        e.printStackTrace();
      }
      }

      if(!defaultVolumeID.equals(getcurrentVolumeID())){
        if(defaultVolumeID.equals("null") && getcurrentVolumeID() == null){
          ;
        } else {
          AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
          builder.setMessage(getResources().getString(R.string.default_warning_title))
          .setCancelable(true)
          .setPositiveButton(getResources().getString(R.string.default_warning_enter), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              //only write xml and reboot
              {
                SharedPreferences.Editor editor= m_sharedPreferences.edit();
                editor.putString("externalStorage", getcurrentVolumeID() + getcurrentSelectedPath());
                editor.commit();
                // restart computer
                try {
//                  RecoverySystem.bootCom(getActivity());
                  PowerManager powerManager = (PowerManager)getActivity().getSystemService(Context.POWER_SERVICE);
                  powerManager.reboot("");
                } catch (Exception e) {
                  // TODO Auto-generated catch block
                  e.printStackTrace();
                }
              }
            }
          })
          .setNegativeButton(getResources().getString(R.string.unmount_cancle), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
              //TODO:
            }
          });

          AlertDialog dialog = builder.create();
          dialog.show();
        }
    }

    return super.onPreferenceTreeClick(preferenceScreen, preference);
  }

  private String getUUID(String path){
    for(int i = 0; i<rets.size(); i++){
      if(rets.get(i).mMountPoint.equals(path))
        return rets.get(i).mUUID;
    }
    return null;
  }

  private synchronized void setcurrentVolumeID(String currentVolumeID){
    this.currentVolumeID = currentVolumeID;
  }

  private synchronized String getcurrentVolumeID(){
    return this.currentVolumeID;
  }

  private synchronized void setcurrentSelectedPath(String currentSelectedPath){
    System.putString(getContentResolver(), "default_path", currentSelectedPath);
    this.currentSelectedPath = currentSelectedPath;
  }

  private synchronized String getcurrentSelectedPath(){
    return this.currentSelectedPath;
  }

  private synchronized void setdefaultPath(String defaultPath){
    this.defaultPath = defaultPath;
  }

  private synchronized String getdefaultPath(){
    return this.defaultPath;
  }

  @Override
  public void onDestroy() {
    // TODO Auto-generated method stub
    getActivity().unregisterReceiver(mReceiver);
    super.onDestroy();
  }


}
