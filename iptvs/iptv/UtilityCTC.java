package com.yinhe.android.iptv;

import android.content.Context;
import android.util.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import android.content.Intent;

@JNINamespace("content::iptv")
public class UtilityCTC {
    private static final String TAG = "UtilityCTC";
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
    private static boolean startLocalCfg() {
        Log.d(TAG, "startLocalCfg");

        final String packageName = "com.yinhe.iptvsetting";
        final String activityName = "StartActivity";

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setClassName(packageName, packageName + "." + activityName);

        try {
            applicationContext.startActivity(intent);
        }
        catch (Exception e) {
            return false;
        }

        return true;
    }
}

