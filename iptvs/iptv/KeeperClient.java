package com.yinhe.android.iptv;

import android.content.ServiceConnection;
import android.content.ComponentName;
import com.android.settings.keeper.KeeperAidl;
import android.os.IBinder;
import android.content.Context;
import android.util.Log;
import android.content.Intent;

public class KeeperClient {
    private static final String TAG = "KeeperClient";

    private static Context applicationContext = null;
    private static ServiceConnection keeperServiceConnection = null;
    private static KeeperAidl keeperInterface = null;

    public static void init(Context context) {
        if (context == null) {
            return;
        }
        if (applicationContext != null) {
            return;
        }

        applicationContext = context.getApplicationContext();

        keeperServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                Log.d(TAG, "service disconnected, try rebind");
                keeperInterface = null;
                bindKeeperService();
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Log.d(TAG, "service connected");
                keeperInterface = KeeperAidl.Stub.asInterface(service);
            }
        };

        bindKeeperService();
    }

    public static boolean getConnectedState() {
        if (keeperInterface != null) {
            return true;
        }

        return false;
    }

    private static boolean bindKeeperService() {
        try {
            applicationContext.unbindService(keeperServiceConnection);
        }
        catch (Exception e) {
        }

        try {
            applicationContext.bindService(new Intent("com.android.settings.keeper.KEEPER_SERVICE"),
                                                keeperServiceConnection, Context.BIND_AUTO_CREATE);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }

}

