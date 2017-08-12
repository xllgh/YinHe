package com.yinhe.android.iptv;

import android.content.Context;
import android.util.Log;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import android.content.Intent;

@JNINamespace("content::iptv")
public class DeviceCallback {
    public interface OnVKSendListener {
        public boolean send();
    }

    private static final String TAG = "DeviceCallback";

    private static Context applicationContext = null;

    private static OnVKSendListener vkSendListener = null;

    public static void init(Context context) {
        if (context == null) {
            return;
        }
        if (applicationContext != null) {
            return;
        }

        applicationContext = context.getApplicationContext();
    }

    public static void setOnVKSendListener(OnVKSendListener listener) {
        vkSendListener = listener;
    }

    @CalledByNative
    private static boolean SendVK() {
        Log.d(TAG, "SendVK");
        if (vkSendListener != null) {
            vkSendListener.send();
        }
        return true;
    }
}
