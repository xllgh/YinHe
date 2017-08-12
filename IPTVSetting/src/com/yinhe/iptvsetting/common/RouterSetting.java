
package com.yinhe.iptvsetting.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.json.JSONException;
import org.json.JSONObject;

public class RouterSetting implements IRouterSetting {

    static LogUtil sLogUtil = new LogUtil("RouterSetting");

    public static String sRouteAddress = null;

    public static final int TYPE_LOGINROUTER = 1;
    public static final int TYPE_SETINTERNET = 2;
    public static final int TYPE_SETNETWORK = 3;
    public static final int TYPE_SETWIFI = 4;
    public static final int TYPE_GETLOGINNAME = 5;
    public static final int TYPE_GETACTIVESTATE = 6;
    public static final int TYPE_SETLOGINPWD = 7;
    public static final int TYPE_RESTART = 8;
    public static final int TYPE_RESTORE = 9;
    public static final int TYPE_GETREGISTERINFO = 10;

    private static RouterSetting sRouterSettingInstance = null;
    private static HashSet<IRouterSettingCallback> sRouterSettingCallbacks = new HashSet<IRouterSettingCallback>();
    private ThreadPoolExecutor mThreadPoolExecutor;

    private RouterSetting() {
        mThreadPoolExecutor = new ThreadPoolExecutor(1, 10, 60, TimeUnit.DAYS,
                new LinkedBlockingDeque<Runnable>());
    };

    public static RouterSetting getInstance(IRouterSettingCallback callback) {
        if (sRouterSettingInstance == null) {
            sRouterSettingInstance = new RouterSetting();
        }
        sRouterSettingCallbacks.add(callback);
        sLogUtil.d("getInstance() sRouterSettingCallbacks size = " + sRouterSettingCallbacks.size());

        return sRouterSettingInstance;
    }

    public static void destroyInstance() {
        sLogUtil.d("destroyInstance");
        if (sRouterSettingInstance != null) {
            sRouterSettingInstance = null;
        }
        if (sRouterSettingCallbacks != null) {
            sRouterSettingCallbacks.clear();
        }
    }

    @Override
    public void loginRouter(String userName, String passwd) {
        mThreadPoolExecutor.execute(new SettingTask(TYPE_LOGINROUTER, userName, passwd));
    }

    @Override
    public void setInternet(String type, String ipAddress, String mask, String gateWay, String dns,
            String userName, String passwd) {
        mThreadPoolExecutor.execute(new SettingTask(TYPE_SETINTERNET, type, ipAddress, mask,
                gateWay, dns,
                userName, passwd));
    }

    @Override
    public void setNetwork(String ipAddress, String mask, String gateWay) {
        mThreadPoolExecutor.execute(new SettingTask(TYPE_SETNETWORK, ipAddress, mask, gateWay));
    }

    @Override
    public void setWifi(String ssid, String pwd, String name, String channel) {
        mThreadPoolExecutor.execute(new SettingTask(TYPE_SETWIFI, ssid, pwd, name, channel));
    }

    @Override
    public void getLoginName() {
        mThreadPoolExecutor.execute(new SettingTask(TYPE_GETLOGINNAME));
    }

    @Override
    public void getActiveState() {
        mThreadPoolExecutor.execute(new SettingTask(TYPE_GETACTIVESTATE));
    }

    @Override
    public void setLoginPwd(String oldPasswd, String newPasswd) {
        mThreadPoolExecutor.execute(new SettingTask(TYPE_SETLOGINPWD, oldPasswd, newPasswd));
    }

    @Override
    public void restart() {
        mThreadPoolExecutor.execute(new SettingTask(TYPE_RESTART));
    }

    @Override
    public void restore() {
        mThreadPoolExecutor.execute(new SettingTask(TYPE_RESTORE));
    }

    @Override
    public void getRegisterInfo() {
        mThreadPoolExecutor.execute(new SettingTask(TYPE_GETREGISTERINFO));
    }

    private class SettingTask implements Runnable {
        private int mTaskType;
        private String[] mTaskParams;

        public SettingTask(int taskType, String... taskParams) {
            mTaskType = taskType;
            mTaskParams = taskParams;
        }

        @Override
        public void run() {
            String url = null;
            String result = null;
            String command = getCommand(mTaskType);
            sLogUtil.d("getCommand command = " + command);
            if (FuncUtil.isNullOrEmpty(command)) {
                sLogUtil.e("SettingTask command is null!");
                return;
            }
            switch (mTaskType) {
                case TYPE_LOGINROUTER:
                    sLogUtil.d("SettingTask TYPE_LOGINROUTER start");
                    if (mTaskParams == null || mTaskParams.length != 2) {
                        sLogUtil.e("SettingTask params error : " + mTaskParams);
                        return;
                    }
                    url = String.format(command, mTaskParams[0], mTaskParams[1]);
                    result = postRequest(url);
                    sLogUtil.d("SettingTask TYPE_LOGINROUTER end");
                    break;
                case TYPE_SETINTERNET:
                    sLogUtil.d("SettingTask TYPE_SETINTERNET start");
                    if (mTaskParams == null
                            || mTaskParams.length != 7) {
                        sLogUtil.e("SettingTask params error : " + mTaskParams);
                        return;
                    }

                    if (InternetType.DHCP.equals(mTaskParams[0])) {
                        url = String.format(command, InternetType.DHCP);
                    } else if (InternetType.PPPOE.equals(mTaskParams[0])) {
                        url = String.format(command, InternetType.PPPOE);
                        url = url.concat("&username=" + mTaskParams[5]).concat(
                                "&passwd=" + mTaskParams[6]);
                    } else if (InternetType.MANUAL.equals(mTaskParams[0])) {
                        url = String.format(command,
                                InternetType.MANUAL);
                        url = url.concat("&ipaddress=" + mTaskParams[1]);
                        url = url.concat("&mask=" + mTaskParams[2]);
                        url = url.concat("&gateway=" + mTaskParams[3]);
                        url = url.concat("&dns=" + mTaskParams[4]);
                    } else {
                        sLogUtil.e("SettingTask params error mTaskParams[0] : " + mTaskParams[0]);
                        return;
                    }
                    result = postRequest(url);
                    sLogUtil.d("SettingTask TYPE_SETINTERNET end");
                    break;
                case TYPE_SETNETWORK:
                    sLogUtil.d("SettingTask TYPE_SETNETWORK start");
                    if (mTaskParams == null
                            || mTaskParams.length < 2 || mTaskParams.length > 3) {
                        sLogUtil.e("SettingTask params error : " + mTaskParams);
                        return;
                    }
                    url = String.format(command, mTaskParams[0], mTaskParams[1]);
                    if (mTaskParams.length == 3) {
                        url.concat("&gateway=" + mTaskParams[2]);
                    }
                    result = postRequest(url);
                    sLogUtil.d("SettingTask TYPE_SETNETWORK end");
                    break;
                case TYPE_SETWIFI:
                    sLogUtil.d("SettingTask TYPE_SETWIFI start");
                    if (mTaskParams == null
                            || mTaskParams.length != 4) {
                        sLogUtil.e("SettingTask params error : " + mTaskParams);
                        return;
                    }
                    url = String.format(command, mTaskParams[0], mTaskParams[1]);
                    if (!FuncUtil.isNullOrEmpty(mTaskParams[2])) {
                        url = url.concat("&name=" + mTaskParams[2]);
                    }

                    if (!FuncUtil.isNullOrEmpty(mTaskParams[3])) {
                        url = url.concat("&channel=" + mTaskParams[3]);
                    }

                    sLogUtil.d("SettingTask TYPE_SETWIFI url = " + url);
                    result = postRequest(url);
                    sLogUtil.d("SettingTask TYPE_SETWIFI end");
                    break;
                case TYPE_GETLOGINNAME:
                    sLogUtil.d("SettingTask TYPE_GETLOGINNAME start");
                    url = command;
                    result = postRequest(url);
                    sLogUtil.d("SettingTask TYPE_GETLOGINNAME end");
                    break;
                case TYPE_GETACTIVESTATE:
                    sLogUtil.d("SettingTask TYPE_GETACTIVESTATE start");
                    url = command;
                    result = postRequest(url);
                    sLogUtil.d("SettingTask TYPE_GETACTIVESTATE end");
                    break;
                case TYPE_SETLOGINPWD:
                    sLogUtil.d("SettingTask TYPE_SETLOGINPWD start");
                    if (mTaskParams == null || mTaskParams.length != 2) {
                        sLogUtil.e("SettingTask params error : " + mTaskParams);
                        return;
                    }
                    url = String.format(command, mTaskParams[0], mTaskParams[1]);
                    result = postRequest(url);
                    sLogUtil.d("SettingTask TYPE_SETLOGINPWD end");
                    break;
                case TYPE_RESTART:
                    sLogUtil.d("SettingTask TYPE_RESTART start");
                    url = command;
                    result = postRequest(url);
                    sLogUtil.d("SettingTask TYPE_RESTART end");
                    break;
                case TYPE_RESTORE:
                    sLogUtil.d("SettingTask TYPE_RESTORE start");
                    url = command;
                    result = postRequest(url);
                    sLogUtil.d("SettingTask TYPE_RESTORE end");
                    break;
                case TYPE_GETREGISTERINFO:
                    sLogUtil.d("SettingTask TYPE_GETREGISTERINFO start");
                    url = command;
                    result = postRequest(url);
                    sLogUtil.d("SettingTask TYPE_GETREGISTERINFO end");
                    break;
                default:
                    break;
            }
            callBack(mTaskType, result);
        }

        private void callBack(int taskType, String json) {
            for (IRouterSettingCallback callback : sRouterSettingCallbacks) {
                callback.onRequestFinish(taskType, parseJson(json));
            }
        }
    }

    private String getCommand(int commandType) {
        String command = null;
        sLogUtil.d("getCommand commandType = " + commandType);
        switch (commandType) {
            case TYPE_LOGINROUTER:
                command = COMMAND_LOGINROUTER;
                break;
            case TYPE_SETINTERNET:
                command = COMMAND_SETINTERNET;
                break;
            case TYPE_SETNETWORK:
                command = COMMAND_SETNETWORK;
                break;
            case TYPE_SETWIFI:
                command = COMMAND_SETWIFI;
                break;
            case TYPE_GETLOGINNAME:
                command = COMMAND_GETLOGINNAME;
                break;
            case TYPE_GETACTIVESTATE:
                command = COMMAND_GETACTIVESTATE;
                break;
            case TYPE_SETLOGINPWD:
                command = COMMAND_SETLOGINPWD;
                break;
            case TYPE_RESTART:
                command = COMMAND_RESTART;
                break;
            case TYPE_RESTORE:
                command = COMMAND_RESTORE;
                break;
            case TYPE_GETREGISTERINFO:
                command = COMMAND_GETREGISTERINFO;
            default:
                break;
        }
        if (FuncUtil.isNullOrEmpty(command)) {
            return null;
        }
        String routeAddres = FuncUtil.isNullOrEmpty(sRouteAddress) ? DEFAULT_ROUTE_ADDRESS
                : sRouteAddress;
        return COMMAND_PROTOCOL + routeAddres + COMMAND_PORT + command;
    }

    private String postRequest(String url) {
        String ret = null;
        if (FuncUtil.isNullOrEmpty(url)) {
            sLogUtil.e("postRequest url is null!");
            return ret;
        }
        HttpURLConnection conn_mgr;
        URL conn_update;
        sLogUtil.d("postRequest url = " + url);
        try {
            conn_update = new URL(url);
            conn_mgr = (HttpURLConnection) conn_update.openConnection();
            conn_mgr.setRequestProperty("User-Agent", "YHBrowser");
            conn_mgr.setInstanceFollowRedirects(false);
            conn_mgr.setConnectTimeout(30000); // 30s
            conn_mgr.setReadTimeout(30000);

            int code = conn_mgr.getResponseCode();
            sLogUtil.d("getResponseCode = " + code);
            if (code == 200) {
                InputStream is = conn_mgr.getInputStream();
                if (is != null) {
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is));
                    StringBuilder sb = new StringBuilder();
                    String line = null;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line + "\n");
                    }
                    is.close();
                    ret = sb.toString().trim();
                    sLogUtil.d("json = " + ret);
                }
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            sLogUtil.d("MalformedURLException  " + e);
        } catch (IOException e) {
            e.printStackTrace();
            sLogUtil.d("IOException  " + e);
        }
        return ret;
    }

    private RequestResult parseJson(String json) {
        if (FuncUtil.isNullOrEmpty(json)) {
            sLogUtil.e("parseJson json is null");
            return null;
        }
        RequestResult result = new RequestResult();
        JSONObject jr = null;
        try {
            jr = new JSONObject(json);
            sLogUtil.d("parseJson  " + jr.toString());
            result.setResult(jr.getString("result"));
            result.setMsg(jr.getString("msg"));
        } catch (JSONException e) {
            sLogUtil.e("base JSONException : " + e);
            return null;
        }

        try {
            result.setData(jr.getString("data"));
        } catch (JSONException e) {
            sLogUtil.d("data JSONException : " + e);
        }

        try {
            result.setGwMac(jr.getString("gw_mac"));
        } catch (JSONException e) {
            sLogUtil.d("gw_mac JSONException : " + e);
        }

        try {
            result.setGwAddress(jr.getString("gw_address"));
        } catch (JSONException e) {
            sLogUtil.d("gw_address JSONException : " + e);
        }

        try {
            result.setGwProt(jr.getString("gw_port"));
        } catch (JSONException e) {
            sLogUtil.d("gw_port JSONException : " + e);
        }

        try {
            result.setSoftVer(jr.getString("soft_ver"));
        } catch (JSONException e) {
            sLogUtil.d("soft_ver JSONException : " + e);
        }
        return result;
    }

    public class RequestResult {
        public static final String RESULT_SUCCESS = "0";
        String mResult = null;
        String mMsg = null;
        String mData = null;
        String mGwMac = null;
        String mGwAddress = null;
        String mGwProt = null;
        String mSoftVer = null;

        public String getResult() {
            return mResult;
        }

        public void setResult(String result) {
            mResult = result;
        }

        public String getMsg() {
            return mMsg;
        }

        public void setMsg(String msg) {
            mMsg = msg;
        }

        public String getData() {
            return mData;
        }

        public void setData(String data) {
            mData = data;
        }

        public String getGwMac() {
            return mGwMac;
        }

        public void setGwMac(String gwMac) {
            mGwMac = gwMac;
        }

        public String getGwAddress() {
            return mGwAddress;
        }

        public void setGwAddress(String gwAddress) {
            mGwAddress = gwAddress;
        }

        public String getGwProt() {
            return mGwProt;
        }

        public void setGwProt(String gwProt) {
            mGwProt = gwProt;
        }

        public String getSoftVer() {
            return mSoftVer;
        }

        public void setSoftVer(String softVer) {
            mSoftVer = softVer;
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("mResult = " + mResult);
            sb.append(",mMsg = " + mResult);
            sb.append(",mData = " + mData);
            sb.append(",mGwMac = " + mGwMac);
            sb.append(",mGwAddress = " + mGwAddress);
            sb.append(",mGwProt = " + mGwProt);
            sb.append(",mSoftVer = " + mSoftVer);
            return sb.toString();
        }
    }

    public interface IRouterSettingCallback {
        void onRequestFinish(int requestType, RequestResult requestResult);
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             