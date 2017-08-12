package com.android.settings.g3;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.ContentResolver;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Looper;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Log;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.Toast;
import android.net.g3.G3Manager;
import android.net.G3StateTracker;

public class G3Enabler implements OnCheckedChangeListener {

    private Context mContext;
    private Switch mSwitch;
    private boolean DEBUG = true;
    private static final String TAG = "G3Enabler";
    private G3Manager g3Manager;
    private static final String SWITCH_ON  =  "com.android.setttings.3g.switch_on";
    private static final String SWITCH_OFF = "com.android.setttings.3g.switch_off";
    private static INetworkManagementService mNetworkManagementService;

    public G3Enabler() {
        super();
    }

    public G3Enabler(Context mContext, Switch mSwitch) {
        super();
        this.mContext = mContext;
        this.mSwitch = mSwitch;
        g3Manager = (G3Manager) mContext.getSystemService(Context.G3_SERVICE);

        IBinder networkManagementServiceBinder;
        networkManagementServiceBinder = ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE);
        mNetworkManagementService = INetworkManagementService.Stub.asInterface(networkManagementServiceBinder);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        // TODO Auto-generated method stub
        if(DEBUG) Log.i(TAG, "onCheckedChanged(" + isChecked + "): Switch to " + (isChecked?"ON":"OFF"));
        int status = g3Manager.getG3ConnectStatus();
        if(isChecked){
            if((status == G3Manager.G3_CONNECT_STATE_DISCONNECTING)
             ||(status == G3Manager.G3_CONNECT_STATE_CONNECTING)){
                mSwitch.setChecked(false);
                 return;
             }
            Intent intent = new Intent(SWITCH_ON);
            mContext.sendBroadcast(intent);

            if((status == G3Manager.G3_CONNECT_STATE_DISCONNECTED)
            || (status == G3Manager.G3_CONNECT_STATE_UNKNOWN)){
                new ConnTask().execute();
            }
        }else{
            if((status == G3Manager.G3_CONNECT_STATE_DISCONNECTING)
             ||(status == G3Manager.G3_CONNECT_STATE_CONNECTING)){
                 mSwitch.setChecked(true);
                 return;
             }
            Intent intent = new Intent(SWITCH_OFF);
            mContext.sendBroadcast(intent);
            if((status == G3Manager.G3_CONNECT_STATE_CONNECTED)
            || (status == G3Manager.G3_CONNECT_STATE_DISCONNECTED)
            || (status == G3Manager.G3_CONNECT_STATE_UNKNOWN)){
                new disconnTask().execute();
            }
        }
    }

    private synchronized void setG3persistState(boolean enabled) {
        if(DEBUG) Log.i(TAG, "setG3persistState(" + enabled + ")");
        final ContentResolver cr = mContext.getContentResolver();
        Settings.Secure.putInt(cr, Settings.Secure.G3_ON,
                enabled ? G3Manager.G3_STATE_ENABLED : G3Manager.G3_STATE_DISABLED);
        try{
            mNetworkManagementService.enableG3StateCheckThread(enabled);
        }catch (RemoteException e){
            Log.e(TAG, "enableG3StateCheckThread() error!");
        }
    }

    private class ConnTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if(DEBUG) Log.i(TAG, "ConnTask-->doInBackground:" + (g3Manager.isCardadded()?"added":"removed"));
            //if(g3Manager.isCardadded()){
                g3Manager.enableG3(true);
            //}else{
            //    setG3persistState(true);
            //}
            return null;
        }
    }

    private class disconnTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            if(DEBUG) Log.i(TAG, "disconnTask-->doInBackground:" + (g3Manager.isCardadded()?"added":"removed"));
            //if(g3Manager.isCardadded()){
                g3Manager.enableG3(false);
            //}else{
            //    setG3persistState(false);
            //}
            return null;
        }
    }

    public Switch getSwitch() {
        return mSwitch;
    }

    public void setSwitch(Switch switch_, G3Enabler mG3Enabler) {
        int g3EnableState = 0;
        if(DEBUG) Log.i(TAG, "3g setSwitch()");
        if (mSwitch == switch_)
            return;
        mSwitch.setOnCheckedChangeListener(null);
        mSwitch = switch_;
        mSwitch.setOnCheckedChangeListener(this);

        try {
            g3EnableState = Settings.Secure.getInt(mContext.getContentResolver(), Settings.Secure.G3_ON);
        } catch (SettingNotFoundException e) {
            Log.e(TAG, "setSwitch(): read system setting G3_ON failed");
            e.printStackTrace();
        }
        if (DEBUG)
            Log.i(TAG, "setSwitch  -- getG3State: " + g3EnableState);
        boolean isEnabled = (g3EnableState == g3Manager.G3_STATE_ENABLED);
        mSwitch.setChecked(isEnabled);
    }

    public void resume() {
        if(DEBUG) Log.i(TAG, "resume()-->setOnCheckedChangeListener()");
        mSwitch.setOnCheckedChangeListener(this);
    }

    public void pasue() {
        mSwitch.setOnCheckedChangeListener(null);
    }

    public G3Manager getManager()
    {
        return g3Manager;
    }

}
