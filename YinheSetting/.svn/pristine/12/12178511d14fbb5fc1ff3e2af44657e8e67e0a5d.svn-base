
package com.yinhe.iptvsetting.common;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import com.yinhe.iptvsetting.object.NetAddress;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.ProxyProperties;
import android.net.RouteInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiConfiguration.IpAssignment;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Log;
import android.view.View;

import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.net.NetworkUtils;
import android.os.INetworkManagementService;
import android.os.ServiceManager;

public class NetManager {

    private static final String TAG = "NetManager";

    public static final int ETHERNET_STATE_ENABLED = EthernetManager.ETHERNET_STATE_ENABLED;
    public static final int ETHERNET_STATE_DISABLED = EthernetManager.ETHERNET_STATE_DISABLED;

    public static final String ETHERNET_STATE_CHANGED_ACTION = EthernetManager.ETHERNET_STATE_CHANGED_ACTION;
    public static final String EXTRA_ETHERNET_STATE = EthernetManager.EXTRA_ETHERNET_STATE;
    public static final int EVENT_PHY_LINK_UP = EthernetManager.EVENT_PHY_LINK_UP;
    public static final int EVENT_PHY_LINK_DOWN = EthernetManager.EVENT_PHY_LINK_DOWN;
    public static final int EVENT_DHCP_CONNECT_SUCCESSED = EthernetManager.EVENT_DHCP_CONNECT_SUCCESSED;
    public static final int EVENT_DHCP_CONNECT_FAILED = EthernetManager.EVENT_DHCP_CONNECT_FAILED;
    public static final int EVENT_STATIC_CONNECT_SUCCESSED = EthernetManager.EVENT_STATIC_CONNECT_SUCCESSED;
    public static final int EVENT_STATIC_CONNECT_FAILED = EthernetManager.EVENT_STATIC_CONNECT_FAILED;

    public static final String PPPOE_STATE_CHANGED_ACTION = PppoeManager.PPPOE_STATE_CHANGED_ACTION;
    public static final String EXTRA_PPPOE_STATE = PppoeManager.EXTRA_PPPOE_STATE;
    public static final int EVENT_CONNECT_SUCCESSED = PppoeManager.EVENT_CONNECT_SUCCESSED;
    public static final int EVENT_CONNECT_FAILED = PppoeManager.EVENT_CONNECT_FAILED;
    public static final int EVENT_DISCONNECT_SUCCESSED = PppoeManager.EVENT_DISCONNECT_SUCCESSED;
    public static final int EVENT_DISCONNECT_FAILED = PppoeManager.EVENT_DISCONNECT_FAILED;
    public static final int EVENT_CONNECTING = PppoeManager.EVENT_CONNECTING;
    public static final int EVENT_CONNECT_FAILED_AUTH_FAIL = PppoeManager.EVENT_CONNECT_FAILED_AUTH_FAIL;

    private Context mContext;
    private ContentResolver mContentResolver;

    private WifiManager mWifiManager;
    private EthernetManager mEthManager;
    private PppoeManager mPppoeManager;
    private INetworkManagementService mNwService;
    private ConnectivityManager mConnectivityManager;

    private BroadcastReceiver mBroadcastReceiver;

    private static final String DEFAULT_DNS = "0.0.0.0";

    private String mDHCPUserName;
    private String mDHCPPassword;
    private String mPPPoEUserName;
    private String mPPPoEPassword;

    private ProgressDialog mProgressDialog = null;

    public enum DevType {
        DT_PPP, DT_ETH, DT_WLAN
    }

    private static NetManager mInstance = null;

    private boolean mIsObtainingAddress;

    private boolean mIsEthernetOn = true;

    /**
     * 有线接入模块，网络地址信息。
     */
    protected NetAddress mNetAddress;

    public NetAddress getNetAddress() {
        return mNetAddress;
    }

    public void setNetAddress(NetAddress netAddress) {
        this.mNetAddress = netAddress;
    }

    /**
     * 是否正在获取网络地址。
     * 
     * @return
     */
    public boolean isObtainingAddress() {
        return mIsObtainingAddress;
    }

    /**
     * 设置是否正在获取网络地址。
     * 
     * @param isObtainingAddress
     */
    public void setIsObtainingAddress(boolean isObtainingAddress) {
        this.mIsObtainingAddress = isObtainingAddress;
    }

    private NetManager(Context context) {
        mContext = context;
        mContentResolver = mContext.getContentResolver();

        mProgressDialog = new ProgressDialog(mContext);

        mWifiManager = (WifiManager) mContext
                .getSystemService(Context.WIFI_SERVICE);
        mPppoeManager = (PppoeManager) mContext.getSystemService(Context.PPPOE_SERVICE);
        mEthManager = (EthernetManager) mContext
                .getSystemService(Context.ETHERNET_SERVICE);
        IBinder b = ServiceManager
                .getService(Context.NETWORKMANAGEMENT_SERVICE);
        mNwService = INetworkManagementService.Stub.asInterface(b);
        mConnectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    /**
     * 创建单例。
     * 
     * @param context
     * @return
     */
    public static NetManager createNetManagerInstance(Context context) {
        mInstance = new NetManager(context);
        return mInstance;
    }

    public static void destroyNetManagerInstance() {
        mInstance = null;
    }

    /**
     * 获取单例。
     * 
     * @return
     */
    public static NetManager getInstance() {
        if (mInstance == null) {
            Log.e("NetManager", "NetManager mInstance is null!");
        }
        return mInstance;
    }

    /**
     * 有线接入是否开启。
     * 
     * @return
     */
    public boolean isEthernetStateEnabled() {
        return mEthManager.getEthernetPersistedState() == ETHERNET_STATE_ENABLED;
    }

    /**
     * 网线是否插入。
     * 
     * @return
     */
    public boolean isEthernetOn() {
        return mIsEthernetOn;
    }

    /**
     * 设置网线接入状态。
     * 
     * @param isEthernetOn
     */
    public void setEthernetOnOrOff(boolean isEthernetOn) {
        mIsEthernetOn = isEthernetOn;
    }

    public String getEthernetMode() {
        return mEthManager.getEthernetMode();
    }

    public boolean isPppoeMode() {
        boolean isPppoeMode = EthernetManager.ETHERNET_CONNECT_MODE_PPPOE.equals(getEthernetMode());
        logD("isPppoeMode : " + isPppoeMode);
        return isPppoeMode;
    }

    public boolean isStaticIpMode() {
        boolean isStaticIpMode = EthernetManager.ETHERNET_CONNECT_MODE_MANUAL
                .equals(getEthernetMode());
        logD("isStaticIpMode : " + isStaticIpMode);
        return isStaticIpMode;
    }

    public boolean isDhcpMode() {
        boolean isDhcpMode = EthernetManager.ETHERNET_CONNECT_MODE_DHCP.equals(getEthernetMode());
        logD("isDhcpMode : " + isDhcpMode);
        return isDhcpMode;
    }

    public String getEthernetMacAddress() {
        String macAddress = null;
        try {
            macAddress = mNwService.getInterfaceConfig("eth0")
                    .getHardwareAddress();
        } catch (RemoteException remote) {
            Log.w(TAG, "RemoteException  " + remote);
        }

        return FuncUtil.isNullOrEmpty(macAddress) ? FuncUtil.STR_EMPTY : macAddress.toString()
                .toUpperCase();
    }

    /**
     * 获取IP地址。
     * 
     * @return
     */
    public NetAddress obataintNetAddress() {
        NetAddress netAddress = new NetAddress();
        LinkProperties linkProperties = mConnectivityManager
                .getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        Iterator<LinkAddress> addrs = linkProperties.getLinkAddresses().iterator();
        if (!addrs.hasNext()) {
            Log.e(TAG, "showDhcpIP:can not get LinkAddress!!");
            return null;
        }
        LinkAddress linkAddress = addrs.next();
        int prefixLength = linkAddress.getNetworkPrefixLength();
        int NetmaskInt = NetworkUtils.prefixLengthToNetmaskInt(prefixLength);
        InetAddress Netmask = NetworkUtils.intToInetAddress(NetmaskInt);
        String mNM = Netmask.getHostAddress();
        Log.i(TAG, "netmask:  " + mNM);
        netAddress.setNetMask(mNM);

        try {
            String mIP = linkAddress.getAddress().getHostAddress();
            netAddress.setIPaddress(mIP);

            String mGW = "";
            for (RouteInfo route : linkProperties.getRoutes()) {
                if (route.isDefaultRoute()) {
                    mGW = route.getGateway().getHostAddress();
                    break;
                }
            }
            netAddress.setGateWay(mGW);
            Iterator<InetAddress> dnses = linkProperties.getDnses().iterator();
            if (!dnses.hasNext()) {
                Log.e(TAG, "showDhcpIP:empty dns!!");
                netAddress.setmDNS1(DEFAULT_DNS);
                netAddress.setmDNS2(DEFAULT_DNS);
            } else {
                String mDns1 = dnses.next().getHostAddress();
                netAddress.setmDNS1(mDns1);
                if (!dnses.hasNext()) {
                    Log.e(TAG, "showDhcpIP:empty dns2!!");
                    netAddress.setmDNS2(DEFAULT_DNS);
                } else {
                    String mDns2 = dnses.next().getHostAddress();
                    netAddress.setmDNS2(mDns2);
                }
            }
        } catch (NullPointerException e) {
            Log.w(TAG, "can not get IP" + e);
        }
        return netAddress;
    }

    /**
     * 设置有线接入静态IP
     * 
     * @param ipAddress
     * @param gateWay
     * @param netMask
     * @param DNS1
     */
    public void startStaticIP(String ipAddress, String gateWay, String netMask, String DNS1) {
        mEthManager.setEthernetEnabled(false);
        InetAddress ipaddr = NetworkUtils.numericToInetAddress(ipAddress);
        InetAddress getwayaddr = NetworkUtils.numericToInetAddress(gateWay);
        InetAddress inetmask = NetworkUtils.numericToInetAddress(netMask);
        InetAddress idns1 = NetworkUtils.numericToInetAddress(DNS1);
        // InetAddress idns2 = NetworkUtils.numericToInetAddress(DNS2);

        DhcpInfo dhcpInfo = new DhcpInfo();
        dhcpInfo.ipAddress = NetworkUtils.inetAddressToInt((Inet4Address) ipaddr);
        dhcpInfo.gateway = NetworkUtils.inetAddressToInt((Inet4Address) getwayaddr);
        dhcpInfo.netmask = NetworkUtils.inetAddressToInt((Inet4Address) inetmask);
        dhcpInfo.dns1 = NetworkUtils.inetAddressToInt((Inet4Address) idns1);
        // dhcpInfo.dns2 = NetworkUtils.inetAddressToInt((Inet4Address) idns2);

        mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL, dhcpInfo);
        mEthManager.setEthernetEnabled(true);
    }

    public void startPppoe(String userName, String password) {
        Log.d(TAG, "startPppoe");
        mPppoeManager.setPppoeUsername(userName);
        mPppoeManager.setPppoePassword(password);
        mEthManager.setEthernetEnabled(false);
        mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_PPPOE, null);
        mEthManager.setEthernetEnabled(true);
    }

    public void disconnectPppoe() {
        Log.d(TAG, "disconnectPppoe");
        mEthManager.setEthernetEnabled(false);
        // Pppoe断开之后，原生设置默认设置为
        // mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_NONE,
        // null);
        // IPTV设置为了UI显示效果等设置为局域网DHCP
        mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_DHCP, null);
        mEthManager.setEthernetEnabled(true);
    }

    public void startDhcp() {
        Log.d(TAG, "startDhcp");
        mEthManager.setEthernetEnabled(false);
        mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_DHCP, null);
        mEthManager.setEthernetEnabled(true);
    }

    public void enableEthernet(boolean enable) {
        boolean mEthernetState = (mEthManager.getEthernetState() == EthernetManager.ETHERNET_STATE_ENABLED ? true
                : false);
        if (enable == false) {
            if (EthernetManager.ETHERNET_CONNECT_MODE_PPPOE.equals(mEthManager.getEthernetMode())
                    && mEthManager.getEthernetState() == ETHERNET_STATE_ENABLED) {
                System.putInt(mContext.getContentResolver(), "pppoe_enable", 1);
                new ForgetPppoe(false).execute();
            }
        } else if (enable == true) {
            if (EthernetManager.ETHERNET_CONNECT_MODE_PPPOE.equals(mEthManager.getEthernetMode())
                    && mEthManager.getEthernetState() == ETHERNET_STATE_ENABLED
                    && mPppoeManager.getPppoeState() == PppoeManager.PPPOE_STATE_CONNECT) {
                new LoginPppoe().execute();
            }
        }
        Message msg = new Message();
        msg.what = 1;
        if (enable) {
            msg.arg1 = 1;
        } else {
            msg.arg1 = 0;
        }
        mHandler.sendMessageDelayed(msg, 500);
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            boolean enable = true;
            if (ETHERNET_STATE_ENABLED == msg.arg1) {
                enable = true;
            } else if (ETHERNET_STATE_DISABLED == msg.arg1) {
                enable = false;
            }
            switch (what) {
                case 1:
                    mEthManager.enableEthernet(enable);
            }
            super.handleMessage(msg);
        }
    };

    /**
     * 开启DHCP。
     */
    public void startDhcpPlus(String userName, String password) {
        mEthManager.setDhcpOption60(true, userName, password);
        startDhcp();
    }

    /**
     * 关闭DHCP。
     */
    public void stopDhcpPlus(String userName, String password) {
        mEthManager.setDhcpOption60(false, userName, password);
    }

    class LoginPppoe extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String username = mPppoeManager.getPppoeUsername();
            String password = mPppoeManager.getPppoePassword();
            String ifname = null;
            if (EthernetManager.ETHERNET_CONNECT_MODE_PPPOE.equals(mEthManager.getEthernetMode())
                    && mEthManager.getEthernetState() == ETHERNET_STATE_ENABLED) {
                ifname = mEthManager.getInterfaceName();
            }
            mPppoeManager.connect(username, password, ifname);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    class ForgetPppoe extends AsyncTask<Void, Void, String> {

        private boolean isDislayDialog;

        public ForgetPppoe(boolean displayDialog) {
            isDislayDialog = displayDialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            logD("ForgetPppoe onPreExecute()");
            if (isDislayDialog && mProgressDialog != null) {
                mProgressDialog.show();
            }
        }

        @Override
        protected String doInBackground(Void... params) {
            String ifname = null;
            if (EthernetManager.ETHERNET_CONNECT_MODE_PPPOE.equals(mEthManager.getEthernetMode())
                    && mEthManager.getEthernetState() == ETHERNET_STATE_ENABLED) {
                ifname = mEthManager.getInterfaceName();
            }
            mPppoeManager.disconnect(ifname);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.dismiss();
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    public boolean isPppoeConnected() {
        int pppoeState = getPppoeState();
        if (PppoeManager.PPPOE_STATE_CONNECT == pppoeState
                || PppoeManager.PPPOE_STATE_CONNECTING == pppoeState) {
            return true;
        }
        return false;
    }

    public int getPppoeState() {
        return mPppoeManager.getPppoeState();
    }

    public String getDHCPUserName() {
        return mEthManager.getDhcpOption60Login();
    }

    public String getDHCPPassword() {
        return mEthManager.getDhcpOption60Password();
    }

    public String getPPPoEUserName() {
        return mPppoeManager.getPppoeUsername();
    }

    public String getPPPoEPassword() {
        return mPppoeManager.getPppoePassword();
    }

    private void logD(String msg) {
        Log.d(TAG, msg);
    }

    private void logE(String msg) {
        Log.e(TAG, msg);
    }

    public interface NetworkStateChangedListener {
        void changed(final String action, final int state);
    }

    public void registerBroadcastReceiver(Context context,
            final NetworkStateChangedListener mNetworkStateChangedListener) {
        mBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                int message = -1;
                int rel = -1;
                logD("mBroadcastReceiver onReceive():");
                logD("intent.getAction = " + action);
                if (action
                        .equals(ETHERNET_STATE_CHANGED_ACTION)) {
                    message = intent.getIntExtra(
                            EXTRA_ETHERNET_STATE, rel);
                    switch (message) {
                        case EVENT_PHY_LINK_UP:
                            logD("onReceive EthernetManager.EVENT_PHY_LINK_UP send");
                            mNetworkStateChangedListener.changed(ETHERNET_STATE_CHANGED_ACTION,
                                    EVENT_PHY_LINK_UP);
                            break;
                        case EVENT_PHY_LINK_DOWN:
                            logD("onReceive EthernetManager.EVENT_PHY_LINK_DOWN");
                            mNetworkStateChangedListener.changed(ETHERNET_STATE_CHANGED_ACTION,
                                    EVENT_PHY_LINK_DOWN);
                            break;
                        case EVENT_DHCP_CONNECT_SUCCESSED:
                            logD("EthernetManager.EVENT_DHCP_CONNECT_SUCCESSED = "
                                    + EVENT_DHCP_CONNECT_SUCCESSED);
                            mNetworkStateChangedListener.changed(ETHERNET_STATE_CHANGED_ACTION,
                                    EVENT_DHCP_CONNECT_SUCCESSED);
                            break;
                        case EVENT_DHCP_CONNECT_FAILED:
                            logD("EthernetManager.EVENT_DHCP_CONNECT_FAILED = "
                                    + EVENT_DHCP_CONNECT_FAILED);
                            mNetworkStateChangedListener.changed(ETHERNET_STATE_CHANGED_ACTION,
                                    EVENT_DHCP_CONNECT_FAILED);
                            break;
                        case EVENT_STATIC_CONNECT_SUCCESSED:
                            logD("EthernetManager.EVENT_STATIC_CONNECT_SUCCESSED = "
                                    + EVENT_STATIC_CONNECT_SUCCESSED);
                            mNetworkStateChangedListener.changed(ETHERNET_STATE_CHANGED_ACTION,
                                    EVENT_STATIC_CONNECT_SUCCESSED);
                            break;
                        case EVENT_STATIC_CONNECT_FAILED:
                            logD("EthernetManager.EVENT_STATIC_CONNECT_FAILED = "
                                    + EVENT_STATIC_CONNECT_FAILED);
                            mNetworkStateChangedListener.changed(ETHERNET_STATE_CHANGED_ACTION,
                                    EVENT_STATIC_CONNECT_FAILED);
                        default:
                            break;
                    }
                } else if (action
                        .equals(PPPOE_STATE_CHANGED_ACTION)) {
                    message = intent.getIntExtra(
                            EXTRA_PPPOE_STATE, rel);
                    switch (message) {
                        case EVENT_CONNECT_SUCCESSED:
                            logD("PppoeManager.EVENT_CONNECT_SUCCESSED");
                            mNetworkStateChangedListener.changed(PPPOE_STATE_CHANGED_ACTION,
                                    EVENT_CONNECT_SUCCESSED);
                            // newIntent.setAction("com.android.ihome.action.pppoe");
                            // newIntent.putExtra("pppoe", "true");
                            // sendBroadcast(newIntent);
                            break;
                        case EVENT_CONNECT_FAILED:
                            logD("PppoeManager.EVENT_CONNECT_FAILED");
                            mNetworkStateChangedListener.changed(PPPOE_STATE_CHANGED_ACTION,
                                    EVENT_CONNECT_FAILED);
                            break;
                        case EVENT_DISCONNECT_SUCCESSED:
                            logD("PppoeManager.EVENT_DISCONNECT_SUCCESSED");
                            // Intent disconnectIntent = new Intent();
                            // disconnectIntent.setAction("com.android.ihome.action.pppoe");
                            // disconnectIntent.putExtra("pppoe", "false");
                            // sendBroadcast(disconnectIntent);
                            mNetworkStateChangedListener.changed(PPPOE_STATE_CHANGED_ACTION,
                                    EVENT_DISCONNECT_SUCCESSED);
                            break;
                        case EVENT_DISCONNECT_FAILED:
                            logD("PppoeManager.EVENT_DISCONNECT_FAILED");
                            break;
                        case EVENT_CONNECTING:
                            logD("PppoeManager.EVENT_CONNECTING");
                            mNetworkStateChangedListener.changed(PPPOE_STATE_CHANGED_ACTION,
                                    EVENT_CONNECTING);
                            break;
                        case EVENT_CONNECT_FAILED_AUTH_FAIL:
                            logD("PppoeManager.EVENT_CONNECT_FAILED_AUTH_FAIL");
                            mNetworkStateChangedListener.changed(PPPOE_STATE_CHANGED_ACTION,
                                    EVENT_CONNECT_FAILED_AUTH_FAIL);
                            break;
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ETHERNET_STATE_CHANGED_ACTION);
        intentFilter.addAction(PPPOE_STATE_CHANGED_ACTION);
        context.registerReceiver(mBroadcastReceiver, intentFilter);
    }

    public String getWifiIpAddresses() {
        LinkProperties prop = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_WIFI);
        return formatIpAddresses(prop);
    }

    private static String formatIpAddresses(LinkProperties prop) {
        if (prop == null)
            return null;
        Iterator<InetAddress> iter = prop.getAllAddresses().iterator();
        // If there are no entries, return null
        if (!iter.hasNext())
            return null;
        // Concatenate all available addresses, comma separated
        String addresses = "";
        while (iter.hasNext()) {
            addresses += iter.next().getHostAddress();
            break;
//            if (iter.hasNext())
//                addresses += "\n";
        }
        return addresses;
    }
    
    public String getWifiDNS() {
        final String DEFAULT_DNS = "0.0.0.0";
        String strDns = null;
        LinkProperties linkProperties = mConnectivityManager
                .getLinkProperties(ConnectivityManager.TYPE_WIFI);
        Iterator<LinkAddress> addrs = linkProperties.getLinkAddresses().iterator();
        if (!addrs.hasNext()) {
            return null;
        }

        try {
            Iterator<InetAddress> dnses = linkProperties.getDnses().iterator();
            if (!dnses.hasNext()) {
                strDns = DEFAULT_DNS;
            } else {
                strDns = dnses.next().getHostAddress();
            }
        } catch (NullPointerException e) {
        }
        return strDns;
    }

    public void unRegisterBroadcastReceiver(Context context) {
        if (mBroadcastReceiver != null) {
            context.unregisterReceiver(mBroadcastReceiver);
        }
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    