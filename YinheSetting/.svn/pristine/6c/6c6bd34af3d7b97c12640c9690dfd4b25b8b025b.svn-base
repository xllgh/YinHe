
package com.android.settings.keeper;

import java.util.HashMap;
import java.util.HashSet;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.Settings.Secure;

import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.LogUtil;
import com.yinhe.iptvsetting.database.DBHelper;

public class keeperServer extends Service {
    private LogUtil mLogUtil = new LogUtil(keeperServer.class);

    private ActivityManager mActivityManager;

    private static HashSet<String> sLegalPackage = new HashSet<String>();
    static {
        sLegalPackage.add("com.ihome.android.tm");
        sLegalPackage.add("com.yinhe.iptvsetting");
        sLegalPackage.add("com.android.settings");
        // sLegalPackage.add("com.yhproduct.netsetting");
        // sLegalPackage.add("com.ihome.android.iptv");
        // sLegalPackage.add("com.ihome.android.launcher");
        // sLegalPackage.add("com.ihome.android.keepertest");
        // sLegalPackage.add("com.yinhe.setupwizard");
        // sLegalPackage.add("com.android.iptv");
        sLegalPackage.add("com.example.zhbtest");
    }

    private static final String KEY_ITV_ACS_USERNAME = "itv_acs_username";
    private static final String KEY_ITV_ACS_PASSWORD = "itv_acs_password";
    private static HashMap<String, String> sGetParamMap = new HashMap<String, String>();
    static {
        sGetParamMap.put(KEY_ITV_ACS_USERNAME, "Username");
        sGetParamMap.put(KEY_ITV_ACS_PASSWORD, "Password");
    }

    public void onCreate() {
        mLogUtil.d("keeperServer onCreate");
        mActivityManager = (ActivityManager) keeperServer.this
                .getSystemService(Context.ACTIVITY_SERVICE);
        super.onCreate();
        // SystemProperties.set("prop.keeperServer.state", "1");
    }

    @Override
    public void onDestroy() {
        mLogUtil.d("keeperServer onDestroy");
        // SystemProperties.set("prop.keeperServer.state", "0");
        DBHelper.close();
        super.onDestroy();
    }

    private String getAppNameByPID(int pid) {
        for (RunningAppProcessInfo processInfo : mActivityManager.getRunningAppProcesses()) {
            if (processInfo.pid == pid) {
                return processInfo.processName;
            }
        }
        return FuncUtil.STR_EMPTY;
    }

    private void checkPkg() throws RemoteException {
        String pkgName = getAppNameByPID(Binder.getCallingPid());

        mLogUtil.d("bind package : " + pkgName);
        if (FuncUtil.isNullOrEmpty(pkgName) || !sLegalPackage.contains(pkgName)) {
            throw new RemoteException("keeper: no permission to access: " +
                    pkgName);
        }

        // if (pkgName == null || (
        // pkgName.equals("com.yhproduct.netsetting") == false &&
        // pkgName.equals("com.ihome.android.iptv") == false &&
        // pkgName.equals("com.ihome.android.tm") == false &&
        // pkgName.equals("com.ihome.android.launcher") == false
        // &&
        // pkgName.equals("com.ihome.android.keepertest") == false &&
        // pkgName.equals("com.yinhe.setupwizard") == false &&
        // pkgName.equals("com.yinhe.iptvsetting") == false &&
        // pkgName.equals("com.android.settings") == false &&
        // pkgName.equals("com.android.iptv") == false
        // )) {
        // throw new RemoteException("keeper: no permission to access: " +
        // pkgName);
        // }
    }

    private final KeeperAidl.Stub mBinder = new KeeperAidl.Stub() {
        // static final String DVB_SYSTEM_VALUECHANGE_ITVUSERNAME =
        // "com.ihome.android.system.action.VALUECHANGE_ITVUSERNAME";
        // static final String DVB_SYSTEM_VALUECHANGE_ITVPASSWORD =
        // "com.ihome.android.system.action.VALUECHANGE_ITVPASSWORD";
        // static final String DVB_SYSTEM_VALUECHANGE_ACCESSMETHOD =
        // "com.ihome.android.system.action.VALUECHANGE_ACCESSMETHOD";
        // static final String DVB_SYSTEM_VALUECHANGE_DHCPUSERNAME =
        // "com.ihome.android.system.action.VALUECHANGE_DHCPUSERNAME";
        // static final String DVB_SYSTEM_VALUECHANGE_DHCPPASSWORD =
        // "com.ihome.android.system.action.VALUECHANGE_DHCPPASSWORD";
        // static final String DVB_SYSTEM_VALUECHANGE_PPPOEUSERNAME =
        // "com.ihome.android.system.action.VALUECHANGE_PPPOEUSERNAME";
        // static final String DVB_SYSTEM_VALUECHANGE_PPPOEPASSWORD =
        // "com.ihome.android.system.action.VALUECHANGE_PPPOEPASSWORD";
        // static final String DVB_SYSTEM_VALUECHANGE_HOMEPAGE =
        // "com.ihome.android.system.action.VALUECHANGE_HOMEPAGE";
        // static final String DVB_SYSTEM_VALUECHANGE_ACSURL =
        // "com.ihome.android.system.action.VALUECHANGE_ACSURL";

        // static final String DVB_SYSTEM_VALUECHANGE_IP =
        // "com.ihome.android.system.action.VALUECHANGE_IP";
        // private void keeperServerSendBroadcast(String msg) {
        // Intent mIntent = new Intent();
        // mIntent.setAction(msg);
        // sendBroadcast(mIntent);
        // }

        public String getPPPOEUserName() throws RemoteException {
            checkPkg();
            String ret = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.PPPOE_USER_NAME);

            if (ret != null) {
                return ret;
            }
            else {
                return FuncUtil.STR_EMPTY;
            }
        }

        public void setPPPOEUserName(String value) throws RemoteException {
            checkPkg();

            if (value != null) {
                Settings.Secure.putString(getContentResolver(), Settings.Secure.PPPOE_USER_NAME,
                        value);
            }
        }

        public String getPPPOEPasswd() throws RemoteException {
            checkPkg();
            String ret = Settings.Secure.getString(getContentResolver(),
                    Settings.Secure.PPPOE_USER_PASS);

            if (ret != null) {
                return ret;
            }
            else {
                return FuncUtil.STR_EMPTY;
            }
        }

        public void setPPPOEPasswd(String value) throws RemoteException {
            checkPkg();
            if (value != null) {
                Settings.Secure.putString(getContentResolver(), Settings.Secure.PPPOE_USER_PASS,
                        value);
            }
        }

        @Override
        public String getDHCPUserName() throws RemoteException {
            checkPkg();
            mLogUtil.d("getDHCPUserName() called.");
            return FuncUtil.STR_EMPTY;
        }

        @Override
        public void setDHCPUserName(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setDHCPUserName() called-value:" + value);
        }

        @Override
        public String getDHCPPasswd() throws RemoteException {
            checkPkg();
            mLogUtil.d("getDHCPPasswd() called.");
            return FuncUtil.STR_EMPTY;
        }

        @Override
        public void setDHCPPasswd(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setDHCPPasswd() called-value:" + value);
        }

        @Override
        public String getITVAuthUrl() throws RemoteException {
            checkPkg();
            mLogUtil.d("getITVAuthUrl() called.");
            return FuncUtil.STR_EMPTY;
        }

        @Override
        public void setITVAuthUrl(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setITVAuthUrl() called-value:" + value);
        }

        @Override
        public String getITVReserveAuthUrl() throws RemoteException {
            checkPkg();
            mLogUtil.d("getITVReserveAuthUrl() called.");
            return FuncUtil.STR_EMPTY;
        }

        @Override
        public void setITVReserveAuthUrl(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setITVReserveAuthUrl() called-value:" + value);
        }

        @Override
        public String getITVLogUrl() throws RemoteException {
            checkPkg();
            mLogUtil.d("getITVLogUrl() called.");
            return FuncUtil.STR_EMPTY;
        }

        @Override
        public void setITVLogUrl(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setITVLogUrl() called-value:" + value);
        }

        @Override
        public String getITVUserName() throws RemoteException {
            checkPkg();
            mLogUtil.d("getITVUserName() called.");
            return FuncUtil.STR_EMPTY;
        }

        @Override
        public void setITVUserName(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setITVUserName() called-value:" + value);
        }

        @Override
        public String getITVPasswd() throws RemoteException {
            checkPkg();
            mLogUtil.d("getITVPasswd() called.");
            return FuncUtil.STR_EMPTY;
        }

        @Override
        public void setITVPasswd(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setITVPasswd() called-value:" + value);
        }

        @Override
        public String getAccessType() throws RemoteException {
            checkPkg();
            mLogUtil.d("getAccessType() called.");
            return FuncUtil.STR_EMPTY;
        }

        @Override
        public void setAccessType(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setAccessType() called-value:" + value);
        }

        @Override
        public String getAccessMethod() throws RemoteException {
            checkPkg();
            mLogUtil.d("getAccessMethod() called.");
            return FuncUtil.STR_EMPTY;
        }

        @Override
        public void setAccessMethod(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setAccessMethod() called-value:" + value);
        }

        @Override
        public String getITVWGUrl() throws RemoteException {
            checkPkg();
            String[] selections = {
                    "name"
            };
            String[] selectionArgs = {
                    "URL"
            };
            DBHelper db = DBHelper.getInstance();
            String ret = db.query(selections, selectionArgs);
            mLogUtil.d("getITVWGUrl() called. ret = " + ret);
            return ret;
        }

        @Override
        public void setITVWGUrl(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setITVWGUrl() called-value:" + value);
            String[] selections = {
                    "name"
            };
            String[] selectionArgs = {
                    "URL"
            };
            DBHelper db = DBHelper.getInstance();
            db.insertOrUpdate(selections, selectionArgs, value);
        }

        @Override
        public String getITVWGUserName() throws RemoteException {
            checkPkg();
            mLogUtil.d("getITVWGUserName() called.");
            return FuncUtil.STR_EMPTY;
        }

        @Override
        public void setITVWGUserName(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setITVWGUserName() called-value:" + value);
        }

        @Override
        public String getITVWGPasswd() throws RemoteException {
            checkPkg();
            mLogUtil.d("getITVWGPasswd() called.");
            return FuncUtil.STR_EMPTY;
        }

        @Override
        public void setITVWGPasswd(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setITVWGPasswd() called-value:" + value);
        }

        @Override
        public String getITVUpgradeUrl() throws RemoteException {
            checkPkg();
            String ret = Secure.getString(getContentResolver(), FuncUtil.KEY_UPDATE_URL);
            mLogUtil.d("getITVUpgradeUrl() called. ret = " + ret);
            return ret;
        }

        @Override
        public void setITVUpgradeUrl(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setITVUpgradeUrl() called-value:" + value);
            Secure.putString(getContentResolver(),
                    FuncUtil.KEY_UPDATE_URL, value);
        }

        @Override
        public String getParam(String key) throws RemoteException {
            checkPkg();
            mLogUtil.d("getParam() called. key = " + key);
            if (FuncUtil.isNullOrEmpty(key) || !sGetParamMap.containsKey(key)) {
                mLogUtil.e("illegal key!");
                return null;
            }
            String[] selections = null;
            String[] selectionArgs = null;
            if (KEY_ITV_ACS_PASSWORD.equals(key)) {
                selections = new String[] {
                        "name", "path"
                };
                selectionArgs = new String[] {
                        sGetParamMap.get(key), "Device.ManagementServer.Password"
                };
            } else {
                selections = new String[] {
                        "name"
                };
                selectionArgs = new String[] {
                        sGetParamMap.get(key)
                };
            }
            DBHelper db = DBHelper.getInstance();
            String ret = db.query(selections, selectionArgs);
            mLogUtil.d("getParam() called. ret = " + ret);
            return ret;
        }

        @Override
        public void setParam(String key, String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setParam() called-key:" + key + "; value:" + value);
            if (FuncUtil.isNullOrEmpty(key) || !sGetParamMap.containsKey(key)) {
                mLogUtil.e("illegal key!");
                return;
            }

            String[] selections = {
                    "name"
            };
            String[] selectionArgs = {
                    sGetParamMap.get(key)
            };

            if (KEY_ITV_ACS_PASSWORD.equals(key)) {
                selections = new String[] {
                        "name", "path"
                };
                selectionArgs = new String[] {
                        sGetParamMap.get(key), "Device.ManagementServer.Password"
                };
            }
            DBHelper db = DBHelper.getInstance();
            db.insertOrUpdate(selections, selectionArgs, value);
        }

        @Override
        public String getITVLogInterval() throws RemoteException {
            checkPkg();
            mLogUtil.d("getITVLogInterval() called.");
            return FuncUtil.STR_EMPTY;
        }

        @Override
        public void setITVLogInterval(String value) throws RemoteException {
            checkPkg();
            mLogUtil.d("setITVLogInterval() called-value:" + value);
        }
    };

    public IBinder onBind(Intent intent) {
        return mBinder;
    }

}
