
package com.yinhe.iptvsetting.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.NetworkInfo.DetailedState;
import android.os.Environment;
import android.os.StatFs;
import android.text.format.Formatter;
import android.widget.Toast;

public final class FuncUtil {

    private static LogUtil sLogUtil = new LogUtil(FuncUtil.class);

    public static final String STR_EMPTY = "";
    public static final String STR_DOT = ".";
    public static final String STR_SLASH = "/";
    public static final String STR_TRUE = "true";
    public static final String STR_FALSE = "false";

    private static final String SHARED_PREFS_NAME = "com.yinhe.iptvsetting";
    private static final String SP_KEY_IS_720P = "is720P";

    private static Toast sToast = null;

    private FuncUtil() {
    }

    /**
     * 判断字符串是否为空。
     * 
     * @param str 判断对象。
     * @return true Null或空<br>
     *         false 非Null或空
     */
    public static boolean isNullOrEmpty(String str) {
        return str == null || STR_EMPTY.equals(str);
    }

    /**
     * 获取WIFI AP状态字符串。
     * 
     * @param DetailedState WIFI状态实例。
     * @param String [] 对应状态数组。
     * @return String WIFI状态
     */
    public static String getWifiState(DetailedState state, String[] formats) {
        int index = state.ordinal();
        if (index >= formats.length || formats[index].length() == 0) {
            return null;
        }
        return String.format(formats[index]);
    }

    /**
     * show the toast.
     * 
     * @param context
     * @param stringID
     */
    public static void showToast(Context context, int stringID) {
        String msg = context.getResources().getString(stringID);
        showToast(context, msg);
    }

    public static void showToast(Context context, String msg) {
        if (sToast == null) {
            sToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
        }
        sToast.setText(msg);
        sToast.show();
    }

    public static SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(SHARED_PREFS_NAME,
                Context.MODE_PRIVATE);
    }

    public static String getSavedDisplayMode(Context context) {
        SharedPreferences prefs = getSharedPreferences(context);
        return prefs.getString(SP_KEY_IS_720P, STR_FALSE);
    }

    public static void saveDisplayMode(Context context, String is720P) {
        SharedPreferences prefs = getSharedPreferences(context);
        prefs.edit().putString(SP_KEY_IS_720P, is720P)
                .commit();
    }

    public static boolean is720pMode(Context context) {
        return STR_TRUE.equals(FuncUtil.getSavedDisplayMode(context));
    }

    /**
     * 获取手机CPU信息
     * 
     * @return
     */
    public static String getCpuInfo()
    {
        // String cpuInfoFile = "/proc/cpuinfo";
        // String strReadLine = "";
        // String cpuName = "";
        // String[] arrayOfString;
        // try
        // {
        // FileReader fr = new FileReader(cpuInfoFile);
        // BufferedReader localBufferedReader = new BufferedReader(fr, 8192);
        // strReadLine = localBufferedReader.readLine();
        // strReadLine = localBufferedReader.readLine();
        // arrayOfString = strReadLine.split(":");
        // if (arrayOfString != null && arrayOfString.length > 1) {
        // cpuName = arrayOfString[1];
        // }
        // localBufferedReader.close();
        // } catch (IOException e)
        // {
        // Log.d(TAG, "getCpuInfo error : " + e);
        // }
        // return cpuName.trim();
        return "HI3798M";
    }

    /**
     * 获取内存信息。
     * 
     * @return 内存信息（总量：可用）
     */
    public static String getMemoryInfo(Context context)
    {
        String dir = "/proc/meminfo";
        String availMemory = "";
        String totalMemory = "";
        try {
            FileReader fr = new FileReader(dir);
            BufferedReader br = new BufferedReader(fr, 2048);
            String memoryLine = br.readLine();
            if (memoryLine.contains("MemTotal")) {
                String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemTotal:"));
                totalMemory = Formatter.formatFileSize(context,
                        Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l);
            }
            memoryLine = br.readLine();
            if (memoryLine.contains("MemFree")) {
                String subMemoryLine = memoryLine.substring(memoryLine.indexOf("MemFree:"));
                availMemory = Formatter.formatFileSize(context,
                        Integer.parseInt(subMemoryLine.replaceAll("\\D+", "")) * 1024l);
            }
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return totalMemory.concat(STR_SLASH).concat(availMemory);
    }

    /**
     * 获取内置存储空间信息。
     * 
     * @return（总量：可用）
     */
    public static String getExternalStorageInfo(Context context) {
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSize();
        long availableBlocks = stat.getAvailableBlocks();
        return "8 GB".concat(STR_SLASH
                + Formatter.formatFileSize(context, availableBlocks * blockSize));
    }

    public static String getSystemProp(String key) {
        if (isNullOrEmpty(key)) {
            return null;
        }
        return android.os.SystemProperties.get(key);
    }

    public static void setSystemProp(String key, String value) {
        android.os.SystemProperties.set(key, value);
    }

    /**
     * @param ip format:xxx.xxx.xxx.xxx
     * @return
     */
    public static String ipToHexString(String ip) {
        if (isNullOrEmpty(ip)) {
            sLogUtil.e("ipToHexString() ip is null");
            return null;
        }
        String[] ips = ip.split("\\.");
        if (ips == null || ips.length != 4) {
            sLogUtil.e("ipToHexString() ip is illegal");
            return null;
        }
        StringBuilder sb = new StringBuilder();
        for (String string : ips) {
            String tmp = Integer.toHexString(Integer.valueOf(string));
            if (tmp.length() == 1) {
                sb.append("0");
            }
            sb.append(tmp);
        }
        return sb.toString();
    }

}
