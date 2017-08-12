
package com.yinhe.iptvsetting.object;

import android.content.Intent;

public class MainItem {
    private String mName;
    private int mBackgroundResId;
    private Intent mAppIntent;

    public String getmName() {
        return mName;
    }

    public void setmName(String name) {
        mName = name;
    }

    public int getBackgroundResId() {
        return mBackgroundResId;
    }

    public void setBackgroundResId(int resId) {
        mBackgroundResId = resId;
    }

    public Intent getAppIntent() {
        return mAppIntent;
    }

    public void setAppIntent(Intent appIntent) {
        mAppIntent = appIntent;
    }

}
