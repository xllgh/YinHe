package com.yinhe.android.iptv;

import android.content.Context;
import android.util.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import android.content.Intent;

@JNINamespace("content::iptv")
public class AuthenticationCTC {
    private static final String TAG = "AuthenticationCTC";
    private static Context applicationContext = null;

    public static void init(Context context) {
        if (context == null) {
            return;
        }
        if (applicationContext != null) {
            return;
        }

        applicationContext = context.getApplicationContext();
    }

    public static void destory() {
        applicationContext = null;
    }

    @CalledByNative
    private static boolean CTCStartUpdate() {

        Log.d(TAG, "CTCStartUpdate");

        Intent intent = new Intent();
        intent.setAction("com.network.apkupgrade.action.UPDATE_START");
        applicationContext.sendBroadcast(intent);
        return true;
    }
}

