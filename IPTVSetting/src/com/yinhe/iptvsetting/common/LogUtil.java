
package com.yinhe.iptvsetting.common;

import android.util.Log;

public final class LogUtil {

    private static final boolean LOG_FLAG = true;
    private static final String LOG_TAG = "zhbn";

    private String mTag;

    public LogUtil(Class<?> cls) {
        mTag = cls.getSimpleName();
    }

    public LogUtil(String tag) {
        mTag = tag;
    }

    public void d(String msg) {
        if (LOG_FLAG) {
            Log.d(LOG_TAG, mTag + " : " + msg);
        }
    }

    public void e(String msg) {
        if (LOG_FLAG) {
            Log.e(LOG_TAG, mTag + " : " + msg);
        }
    }
}
