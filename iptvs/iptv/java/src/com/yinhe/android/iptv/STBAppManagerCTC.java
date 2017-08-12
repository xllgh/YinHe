package com.yinhe.android.iptv;

import android.content.Context;
import android.util.Log;
import android.content.pm.PackageManager;
import android.content.pm.PackageInfo;
import java.util.List;
import org.chromium.base.annotations.CalledByNative;
import org.chromium.base.annotations.JNINamespace;
import android.content.Intent;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.net.Uri;
import org.json.JSONArray;

@JNINamespace("content::iptv")
public class STBAppManagerCTC {
    private static final String TAG = "STBAppManagerCTC";
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
    private static boolean isAppInstalled(String appName) {
        Log.d(TAG, "isAppInstalled " + appName);
        if (applicationContext == null) {
            return false;
        }
        if (appName == null || appName.isEmpty()) {
            return false;
        }
        PackageManager pm = applicationContext.getPackageManager();
        List<PackageInfo> packageList
            = pm.getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (int i = 0; i != packageList.size(); ++i) {
            PackageInfo pi = packageList.get(i);
            if (pi.packageName.equals(appName)) {
                return true;
            }
        }
        return false;
    }

    @CalledByNative
    private static String getAppVersion(String appName) {
        Log.d(TAG, "getAppVersion " + appName);
        if (applicationContext == null) {
            return null;
        }
        if (appName == null || appName.isEmpty()) {
            return null;
        }

        PackageManager pm = applicationContext.getPackageManager();
        PackageInfo pi = null;
        try {
            pi = pm.getPackageInfo(appName, PackageManager.GET_UNINSTALLED_PACKAGES);
        }
        catch (Exception e) {
            return null;
        }

        return pi.versionName;
    }

    @CalledByNative
    private static boolean startAppByName(String appName) {
        Log.d(TAG, "startAppByName " + appName);
        if (applicationContext == null) {
            return false;
        }
        if (appName == null || appName.isEmpty()) {
            return false;
        }

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setPackage(appName);
        intent.addCategory("android.intent.category.LAUNCHER");
        intent.setAction("android.intent.action.MAIN");
        try {
            applicationContext.startActivity(intent);
        }
        catch (Exception e) {
            PackageManager pm = applicationContext.getPackageManager();
            Intent launchIntent = pm.getLaunchIntentForPackage(appName);
            if (launchIntent == null) {
                Log.d(TAG, "start " + appName + " fail");
                return false;
            }
            try {
                applicationContext.startActivity(launchIntent);
            }
            catch (Exception e1) {
                Log.d(TAG, "start " + appName + " fail");
                return false;
            }
        }

        return true;
    }

    @CalledByNative
    private static boolean restartAppByName(String appName) {
        Log.d(TAG, "restartAppByName " + appName);
        return false;
    }

    @CalledByNative
    private static boolean startAppByIntent(String intentMessage) {
        Log.d(TAG, "startAppByIntent " + intentMessage);
        if (applicationContext == null) {
            return false;
        }
        if (intentMessage == null || intentMessage.isEmpty()) {
            return false;
        }

        JSONTokener jsonParser = null;
        try {
            jsonParser = new JSONTokener(intentMessage);
        }
        catch (Exception e) {
            Log.d(TAG, "can not create JSONTokener");
            return false;
        }

        JSONObject intentStruct = null;
        try {
            intentStruct = (JSONObject)jsonParser.nextValue();
        }
        catch (Exception e) {
            Log.d(TAG, "can not read json value");
            return false;
        }

        int intentType = -1;
        try {
            intentType = intentStruct.getInt("intentType");
        }
        catch (Exception e) {
            intentType = -1;
        }

        if (intentType != 0 && intentType != 1) {
            Log.d(TAG, "intentType is vaild.intentType = " + intentType);
            return false;
        }

        Log.d(TAG, "intentType " + intentType);

        Intent startApkIntent = null;

        switch(intentType) {
            case 0: {
                String appName = null;
                String className = null;

                try {
                    appName = intentStruct.getString("appName");
                }
                catch (Exception e) {
                    appName = null;
                }
                if (appName == null || appName.isEmpty()) {
                    Log.d(TAG, "appName is vaild");
                    return false;
                }

                Log.d(TAG, "appName " + appName);

                try {
                    className = intentStruct.getString("className");
                }
                catch (Exception e) {
                    className = null;
                }
                if (className == null || className.isEmpty()) {
                    Log.d(TAG, "className is vaild");
                    return false;
                }

                int firstDotIndex = -1;
                firstDotIndex = className.indexOf('.');
                switch(firstDotIndex) {
                    case -1: {
                        className = appName + "." + className;
                        break;
                    }
                    case 0: {
                        className = appName + className;
                        break;
                    }
                }

                Log.d(TAG, "className " + className);

                startApkIntent = new Intent();
                startApkIntent.setClassName(appName, className);
                break;
            }
            case 1: {
                String actionName = null;

                try {
                    actionName = intentStruct.getString("action");
                }
                catch (Exception e) {
                    actionName = null;
                }
                if (actionName == null || actionName.isEmpty()) {
                    Log.d(TAG, "actionName is vaild");
                    return false;
                }

                Log.d(TAG, "actionName " + actionName);
                startApkIntent = new Intent();
                startApkIntent.setAction(actionName);
                break;
            }
            default: {
                startApkIntent = null;
                break;
            }
        }

        if (startApkIntent == null) {
            return false;
        }

        String intentDataString = null;
        try {
            intentDataString = intentStruct.getString("data");
        }
        catch (Exception e) {
            intentDataString = null;
        }

        if (intentDataString == null || intentDataString.isEmpty()) {
            Log.d(TAG, "no intent data");
        }
        else {
            Log.d(TAG, "data " + intentDataString);
            startApkIntent.setData(Uri.parse(intentDataString));
        }

        JSONArray extraDataArray = null;
        try {
            extraDataArray = intentStruct.getJSONArray("extra");
        }
        catch (Exception e) {
            extraDataArray = null;
        }

        if (extraDataArray == null) {
            Log.d(TAG, "no extra data");
        }
        else {
            int extraDataLength = extraDataArray.length();
            Log.d(TAG, "extra data count " + extraDataLength);
            for (int i = 0; i != extraDataLength; ++i) {
                JSONObject extraObject = null;
                try {
                    extraObject = extraDataArray.getJSONObject(i);
                }
                catch (Exception e) {
                    extraObject = null;
                }

                if (extraObject == null) {
                    Log.d(TAG, "invaild json object at index " + i + ", abort");
                    return false;
                }

                String tmpName = null;
                try {
                    tmpName = extraObject.getString("name");
                }
                catch (Exception e) {
                    tmpName = null;
                }

                if (tmpName == null || tmpName.isEmpty()) {
                    Log.d(TAG, "invaild extra name, abort");
                    return false;
                }

                String tmpValue = null;
                try {
                    tmpValue = extraObject.getString("value");
                }
                catch (Exception e) {
                    tmpValue = null;
                }

                if (tmpValue == null || tmpValue.isEmpty()) {
                    Log.d(TAG, "invaild extra value, abort");
                    return false;
                }

                Log.d(TAG, "name " + tmpName + "; value " + tmpValue);
                startApkIntent.putExtra(tmpName, tmpValue);
            }
        }

        startApkIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d(TAG, "startActivity");
        try {
            applicationContext.startActivity(startApkIntent);
        }
        catch (Exception e) {
            Log.d(TAG, "can not start intent " + intentMessage);
            return false;
        }

        return true;
    }

    @CalledByNative
    private static boolean installApp(String apkUrl) {
        if (apkUrl == null || apkUrl.isEmpty()) {
            Log.d(TAG, "installApp apkUrl is null or empty, abort");
            return false;
        }

        return false;
    }
}

