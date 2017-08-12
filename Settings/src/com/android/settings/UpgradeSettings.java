package com.android.settings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.android.settings.R;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.storage.IMountService;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.widget.Toast;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.RemoteException;
import android.os.Handler;
import android.os.Message;
import com.hisilicon.android.hisysmanager.HiSysManager;

public class UpgradeSettings extends SettingsPreferenceFragment {
    private static final String TAG = "UpgradeSettings";
    private StorageManager mStorageManager = null;
    private int deviceCount;
    private static ProgressDialog progressBar = null;
    private static Context mContext = null;
    private static checkUpdateThread mThread = null;
    final static Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case 0:
                {
                    String path = (String)msg.obj;
                    progressBar = new ProgressDialog(mContext);
                    progressBar.setTitle(mContext.getString(R.string.file_check));
                    progressBar.setMessage(mContext.getString(R.string.file_checking));
                    progressBar.setCancelable(false);
                    progressBar.show();
                    mThread = new checkUpdateThread(path);
                    mThread.start();
                    break;
                }
            case 1:
                {
                    int result = msg.arg1;
                    String path = (String)msg.obj;
                    progressBar.dismiss();
                    if(result == 0)
                    {
                        HiSysManager hisys = new HiSysManager();
                        hisys.upgrade(path);
                        Intent intent = new Intent("android.intent.action.MASTER_CLEAR");
                        intent.putExtra("mount_point", path);
                        mContext.sendBroadcast(intent);
                    }
                    else if(result == -1)
                    {
                        Toast tmpToast = Toast.makeText(mContext, R.string.file_check_failed,Toast.LENGTH_SHORT);
                        tmpToast.show();
                    }
                    break;
                }
            }
        }
    };

    @Override
        public void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            if(mStorageManager == null){
                mStorageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
            }
            addPreferencesFromResource(R.xml.upgrade_settings);
            updatePreference();
            mContext = getActivity();
        }
    @Override
        public void onResume() {
            IntentFilter intentFilter_ScanFinish = new IntentFilter();
            intentFilter_ScanFinish.addAction(Intent.ACTION_MEDIA_MOUNTED);
            intentFilter_ScanFinish.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
            intentFilter_ScanFinish.addDataScheme("file");
            getActivity().registerReceiver(mReceiver, intentFilter_ScanFinish);
            super.onResume();
        }

    BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
            public void onReceive(Context context, Intent intent) {
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

    @Override
        public void onDestroy() {
            if (mReceiver != null) {
                getActivity().unregisterReceiver(mReceiver);
                mReceiver = null;
            }
            super.onDestroy();
        }
    StorageVolume[] storageVolumes  ;
    private void updatePreference(){
        try{
            IBinder service = ServiceManager.getService("mount");
            if (service != null) {
                IMountService mountService = IMountService.Stub.asInterface(service);
                List<android.os.storage.ExtraInfo> mountList = mountService.getAllExtraInfos();
                storageVolumes = mStorageManager.getVolumeList();
                String[] devicePath = getDevicePath(storageVolumes);
                deviceCount = mountList.size();
                PreferenceCategory preferenceCategory = new PreferenceCategory(getActivity());
                getPreferenceScreen().addPreference(preferenceCategory);
                preferenceCategory.setTitle(R.string.upgrade_settings_title);

                for (int i = 0; i < deviceCount; i++) {
                    Preference preference = new Preference(getActivity());
                    if(mountList.get(i).mLabel != null){
                        preference.setTitle(mountList.get(i).mDiskLabel + ": " + mountList.get(i).mLabel);
                    }else{
                        preference.setTitle(mountList.get(i).mDiskLabel);
                    }
                    preference.setKey(mountList.get(i).mMountPoint);
                    preferenceCategory.addPreference(preference);
                }
            }
        }catch (RemoteException e ) {
            e.printStackTrace();
        }
    }

    public String getUpdateFilePath(String fileSuffix){
        if(storageVolumes != null && storageVolumes.length > 0){
            for (int i = 0; i < storageVolumes.length; i++) {
                if(storageVolumes[i].getPath().contains(fileSuffix)){
                    return storageVolumes[i].getPath();
                }
            }
        }
        return "/mnt/nand";

    }

    public static void checkUpdateFile(String updatePath){

        File file = new File(updatePath+"/update.zip");

        Message msg = mHandler.obtainMessage(1);
        msg.arg1= 0;
        msg.obj = updatePath;
        mHandler.sendMessage(msg);
    }

    @Override
        public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
                Preference preference) {
            String updatePath = preference.getKey();
            if (CheckUpdateFile(updatePath)) {
                ConfirmUpgradeDialog.show(UpgradeSettings.this, updatePath);
            }
            else
            {
                Toast.makeText(getActivity(), R.string.n_update_f, Toast.LENGTH_SHORT).show();
            }

            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

    private boolean CheckUpdateFile(String path)
    {
        File file = new File(path+"/update.zip");

        try{
            if(file.exists())
            {
                return true;
            }
        }catch (Exception e) {
            Log.d(TAG, e.toString());
            return false;
        }
        return false;

    }

    private String[] getDevicePath(StorageVolume[] storageVolumes) {
        String[] tmpPath = new String[storageVolumes.length];
        for (int i = 0; i < storageVolumes.length; i++) {
            tmpPath[i] = getMountDevices(storageVolumes[i].getPath());
        }
        int count = storageVolumes.length;
        //delete repeat
        for (int i = 0; i < storageVolumes.length; i++) {
            for (int j = i + 1; j < storageVolumes.length; j++) {
                try {
                    if (tmpPath[i] != null) {
                        if (tmpPath[j].equals(tmpPath[i]) && tmpPath[j] != null) {
                            tmpPath[j] = null;
                            count--;
                        }
                    }
                } catch (Exception e) {

                }
            }
        }
        String[] path = new String[count];
        int j = 0;
        for (int i = 0; i < storageVolumes.length; i++) {
            if (tmpPath[i] != null) {
                path[j] = tmpPath[i];
                j++;
            }
        }
        //sort
        for (int i = 0; i < count; i++) {
            for (int k = i + 1; k < count; k++) {
                if (path[i].compareTo(path[k]) > 0) {
                    String tmp = path[k];
                    path[k] = path[i];
                    path[i] = tmp;
                }
            }
        }
        return path;
    }

    public String getMountDevices(String path){
        int start=0;
        start = path.lastIndexOf("/");
        String mountPath = path.substring(start+1);
        return mountPath;
    }

    public static class ConfirmUpgradeDialog extends DialogFragment {
        private static String mountPoint = null;
        public static void show(UpgradeSettings parent, String mntPoint) {
            mountPoint = mntPoint;
            if (!parent.isAdded()) return;

            final ConfirmUpgradeDialog dialog = new ConfirmUpgradeDialog();
            dialog.setTargetFragment(parent, 0);
            dialog.show(parent.getFragmentManager(), "System Upgrade");
        }

        @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                final Context context = getActivity();
                final AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage(R.string.upgrade_dialog_summary);

                builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        final UpgradeSettings target = (UpgradeSettings) getTargetFragment();
                        if (target != null) {
                        Message msg = mHandler.obtainMessage(0);
                        msg.obj = mountPoint;
                        mHandler.sendMessage(msg);
                        }
                        }
                        });
                builder.setNegativeButton(android.R.string.cancel, null);
                return builder.create();
            }
    }
    private static class checkUpdateThread extends Thread
    {
        public String updatePath = null;

        public checkUpdateThread(String path)
        {
            this.updatePath = path;
        }
        public void run()
        {
            checkUpdateFile(updatePath);
        }
    }
}

