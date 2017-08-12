
package com.yinhe.iptvsetting.common;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.ConnectException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.NoRouteToHostException;
import java.net.UnknownHostException;

import com.yinhe.iptvsetting.object.NtpMessage;

import android.content.Context;
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
    
    public static final String KEY_UPDATE_URL = "update_url";

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
        return str == null || STR_EMPTY.equals(str.trim());
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
        return "4 GB".concat(STR_SLASH
                + Formatter.formatFileSize(context, availableBlocks * blockSize));
    }

    public static boolean getNtpTime(String server) {
        sLogUtil.d("getNtpTime");
        if (isNullOrEmpty(server)) {
            return false;
        }
        sLogUtil.d("getNtpTime server = " + server);
        final int SERVICESTATUS_SUCCEED = 1;
        int retry = 2;
        int port = 123;
        int timeout = 3000;

        // get the address and NTP address request
        InetAddress ipv4Addr = null;
        try {
            ipv4Addr = InetAddress.getByName(server);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }

        int serviceStatus = -1;
        DatagramSocket socket = null;
        long responseTime = -1;
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(timeout); // will force the
            // InterruptedIOException

            for (int attempts = 0; attempts <= retry && serviceStatus != SERVICESTATUS_SUCCEED; attempts++) {
                try {
                    // Send NTP request
                    byte[] data = new NtpMessage().toByteArray();
                    DatagramPacket outgoing = new DatagramPacket(data, data.length, ipv4Addr, port);
                    long sentTime = System.currentTimeMillis();
                    socket.send(outgoing);

                    // Get NTP Response
                    // byte[] buffer = new byte[512];
                    DatagramPacket incoming = new DatagramPacket(data, data.length);
                    socket.receive(incoming);
                    responseTime = System.currentTimeMillis() - sentTime;
                    double destinationTimestamp = (System.currentTimeMillis() / 1000.0) + 2208988800.0;
                    // 这里要加2208988800，是因为获得到的时间是格林尼治时间，所以要变成东八区的时间，否则会与与北京时间有8小时的时差

                    // Validate NTP Response
                    // IOException thrown if packet does not decode as expected.
                    NtpMessage msg = new NtpMessage(incoming.getData());
                    double localClockOffset = ((msg.receiveTimestamp - msg.originateTimestamp) + (msg.transmitTimestamp - destinationTimestamp)) / 2;

                    sLogUtil.d("poll: valid NTP request received the local clock offset is "
                            + localClockOffset + ", responseTime= " + responseTime + "ms");
                    sLogUtil.d("poll: NTP message : " + msg.toString());
                    serviceStatus = 1;
                } catch (InterruptedIOException ex) {
                    // Ignore, no response received.
                    sLogUtil.e("InterruptedIOException: " + ex);
                }
            }
        } catch (NoRouteToHostException e) {
            sLogUtil.e("No route to host exception for address: " + ipv4Addr);
        } catch (ConnectException e) {
            // Connection refused. Continue to retry.
            e.fillInStackTrace();
            sLogUtil.e("Connection exception for address: " + ipv4Addr);
        } catch (IOException ex) {
            ex.fillInStackTrace();
            sLogUtil.e("IOException while polling address: " + ipv4Addr);
        } finally {
            if (socket != null)
                socket.close();
        }

        // Store response time if available
        //
        sLogUtil.d("serviceStatus==" + serviceStatus);
        sLogUtil.d("responsetime==" + responseTime);

        return serviceStatus == SERVICESTATUS_SUCCEED;
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

}
