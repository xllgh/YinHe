
package com.yinhe.iptvsetting.common;

import android.util.Log;

public final class LogUtil {
    private static final boolean LOG_FLAG = true;
    private String mTag;

    public LogUtil(Class<?> cls) {
        mTag = cls.getSimpleName();
    }

    public void d(String msg) {
        if (LOG_FLAG) {
            Log.d(mTag, msg);
        }
    }

    public void e(String msg) {
        if (LOG_FLAG) {
            Log.e(mTag, msg);
        }
    }

}
