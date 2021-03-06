/*
 * Copyright (C) 2010 The Android-x86 Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Author: Yi Sun <beyounn@gmail.com>
 */

package com.android.settings;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.util.Iterator;
import java.util.regex.*;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;
import com.android.settings.DisplaySettings.MyTask;
import com.android.settings.ethernet.EthernetEnabler;
import com.android.settings.ethernet.pppoeLoginDialog;

import android.view.Gravity;
import android.widget.Switch;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkUtils;
import android.net.DhcpInfo;
import android.net.RouteInfo;
import android.net.LinkProperties;
import android.net.LinkAddress;
import android.net.ethernet.EthernetManager;
import android.net.pppoe.PppoeManager;
import android.net.EthernetDataTracker;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.INetworkManagementService;
import android.os.Message;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.database.ContentObserver;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;

public class EthernetSettings extends SettingsPreferenceFragment implements
        Preference.OnPreferenceChangeListener {
    private boolean DEBUG = false;
    private final IntentFilter mIntentFilter;
    private PppoeManager pppoeManager;
    private EthernetManager mEthManager;
    private static final String TAG = "EthernetSetting";
    private EthernetEnabler mEthEnabler;
    private CheckBoxPreference mDhcpIP;
    private CheckBoxPreference mStaticIP;
    private CheckBoxPreference mPppoe;
    private Preference mIPaddress;
    private Preference mGateWay;
    private Preference mNetMask;
    private Preference mDNS1;
    private Preference mDNS2;
    private EditText mUsername;
    private EditText mPassword;

    private static final String KEY_STATIC = "staticnetwork_use_dhcp_ip";
    private static final String KEY_DHCP = "staticnetwork_use_static_ip";
    private static final String KEY_GATE_WAY = "gateway";
    private static final String KEY_IP = "ip_address";
    private static final String KEY_DNS1 = "dns1";
    private static final String KEY_DNS2 = "dns2";
    private static final String KEY_NETMASK = "netmask";
    private static final String KEY_DEVICE_LIST = "device_list";
    private static final String DEFAULT_DNS = "0.0.0.0";
    private String KEY_ETH_INTERFACE = "eth0";
    private ConnectivityManager mConnectivityManager;
    private INetworkManagementService mNwService;
    private int Ethernet_current_status = -1;

    private static final int MENU_ID_ADVANCED = Menu.FIRST;

    private final BroadcastReceiver mEthSettingsReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int message = -1;
            int rel = -1;

            ContentResolver resolver = getActivity().getContentResolver();
            if (DEBUG)
                Log.i(TAG, "BroadcastReceiver");
            if (DEBUG)
                Log.i(TAG, "getConnectMode after ethnetchange "
                        + mEthManager.getEthernetMode().toString());
            enableConnectModeBox(true);
            if (intent.getAction().equals(
                    EthernetManager.ETHERNET_STATE_CHANGED_ACTION)) {
                message = intent.getIntExtra(
                        EthernetManager.EXTRA_ETHERNET_STATE, rel);
                switch (message) {
                case EthernetManager.EVENT_DHCP_CONNECT_SUCCESSED:
                    //if (false == mStaticIP.isChecked()) {
                        if (null == mDNS1 || null == mDNS2 || null == mGateWay
                                || null == mNetMask) {
                            updateUi();
                        }
                        showPreference(mDhcpIP);
                        showEthIP();
                        if (DEBUG)
                            Log.i(TAG, "DHCP UP OK : " + message);
                    //}
                    Ethernet_current_status = EthernetManager.EVENT_DHCP_CONNECT_SUCCESSED;

                    break;
                case EthernetManager.EVENT_DHCP_CONNECT_FAILED:
                    if (mDhcpIP.isChecked()) {
                        if (EthernetManager.ETHERNET_AUTORECONNECT_ENABLED
                            == mEthManager.getAutoReconnectState()) {
                            mDhcpIP.setSummary(getString(R.string.ipv6_connect_failed_auto));
                        } else {
                            mDhcpIP.setSummary(getString(R.string.eth_disconected));
                        }
                        if (DEBUG)
                            Log.i(TAG, " DHCP UP failed  : " + message);
                    }
                    Ethernet_current_status = EthernetManager.EVENT_DHCP_CONNECT_FAILED;
                    break;
                case EthernetManager.EVENT_DHCP_DISCONNECT_SUCCESSED:
                    if (!mDhcpIP.isChecked() && !mStaticIP.isChecked()) {
                        mDhcpIP.setSummary(getString(R.string.eth_dhcp_cancel));
                        if (DEBUG)
                            Log.i(TAG, " DHCP down OK  : " + message);
                    } else if (mDhcpIP.isChecked()) {
                        mDhcpIP.setSummary(getString(R.string.eth_disconected));
                    } else {
                        mDhcpIP.setSummary("");
                    }
                    Ethernet_current_status = EthernetManager.EVENT_DHCP_DISCONNECT_SUCCESSED;
                    break;
                case EthernetManager.EVENT_DHCP_DISCONNECT_FAILED:
                    if (DEBUG)
                        Log.i(TAG, " DHCP down failed  : " + message);
                    Ethernet_current_status = EthernetManager.EVENT_DHCP_DISCONNECT_FAILED;
                    break;
                case EthernetManager.EVENT_DHCP_AUTORECONNECTING:
                    if (mDhcpIP.isChecked()) {
                        mDhcpIP.setSummary(getString(R.string.auto_reconnecting));
                    }
                    Ethernet_current_status = EthernetManager.EVENT_DHCP_AUTORECONNECTING;
                    break;
                case EthernetManager.EVENT_STATIC_CONNECT_SUCCESSED:
                    if (null == mDNS1 || null == mDNS2 || null == mGateWay
                            || null == mNetMask) {
                        updateUi();
                    }
                    if (!checkIP()) {
                        getIP();
                    }
                    showPreference(mStaticIP);
                    mStaticIP.setSummary(getString(R.string.eth_static_connect));
                    if (DEBUG)
                        Log.i(TAG, " Static UP OK  : " + message);
                    Ethernet_current_status = EthernetManager.EVENT_STATIC_CONNECT_SUCCESSED;
                    break;
                case EthernetManager.EVENT_STATIC_CONNECT_FAILED:
                    mStaticIP.setSummary(getString(R.string.eth_disconected));
                    if (DEBUG)
                        Log.i(TAG, " Static UP failed  : " + message);
                    Ethernet_current_status = EthernetManager.EVENT_STATIC_CONNECT_FAILED;
                    break;
                case EthernetManager.EVENT_STATIC_DISCONNECT_SUCCESSED:
                    if (!mStaticIP.isChecked() && !mDhcpIP.isChecked()) {
                        mStaticIP
                                .setSummary(getString(R.string.eth_static_disconnect_successed));
                    } else if (mStaticIP.isChecked()) {
                        mStaticIP
                                .setSummary(getString(R.string.eth_disconected));
                    }
                    if (DEBUG)
                        Log.i(TAG, " Static Down OK  : " + message);
                    Ethernet_current_status = EthernetManager.EVENT_STATIC_DISCONNECT_SUCCESSED;
                    break;
                case EthernetManager.EVENT_STATIC_DISCONNECT_FAILED:
                    if (DEBUG)
                        Log.i(TAG, " Static Down failed  : " + message);
                    Ethernet_current_status = EthernetManager.EVENT_STATIC_DISCONNECT_FAILED;
                    break;
                case EthernetManager.EVENT_PHY_LINK_UP:
                    if (mStaticIP.isChecked()) {
                        mDhcpIP.setSummary("");
                    } else if (mDhcpIP.isChecked()) {
                        mStaticIP.setSummary("");
                        mDhcpIP.setSummary(R.string.eth_dhcp_getting);
                    }
                    if (DEBUG)
                        Log.i(TAG, " EVENT_PHY_LINK_UP  : " + message);
                    Ethernet_current_status = EthernetManager.EVENT_PHY_LINK_UP;
                    break;
                case EthernetManager.EVENT_PHY_LINK_DOWN:
                    int SwitchEnable = mEthManager.getEthernetPersistedState();
                    if (SwitchEnable == EthernetManager.ETHERNET_STATE_ENABLED) {
                        if (mStaticIP.isChecked()) {
                            mDhcpIP.setSummary("");
                            mStaticIP
                                    .setSummary(R.string.eth_phy_link_down_check);
                        } else if (mDhcpIP.isChecked()) {
                            cleanIP();
                            mDhcpIP.setSummary(R.string.eth_phy_link_down_check);
                        } else if (mPppoe.isChecked()) {
                            cleanIP();
                            mPppoe.setSummary(R.string.eth_phy_link_down_check);
                        }
                    }
                    if (DEBUG)
                        Log.i(TAG, " EVENT_PHY_LINK_DOWN  : " + message);
                    Ethernet_current_status = EthernetManager.EVENT_PHY_LINK_DOWN;
                    break;
                default:
                    break;
                }
            } else {
                if (intent.getAction().equals(
                        PppoeManager.PPPOE_STATE_CHANGED_ACTION)) {
                    message = intent.getIntExtra(
                            PppoeManager.EXTRA_PPPOE_STATE, rel);
                    switch (message) {
                    case PppoeManager.EVENT_CONNECT_SUCCESSED:
                        updateUi();
                        showEthIP();
                        showPreference(mPppoe);
                        mPppoe.setSummary(getString(R.string.eth_static_connect));
                        Ethernet_current_status = PppoeManager.EVENT_CONNECT_SUCCESSED;
                        //try {
                        //    mNwService.setIpForwardingEnabled(true);
                        //} catch (RemoteException e) {
                        //   Log.e(TAG,"pppoe connect success, setIpForwardingEnabled failed");
                        //}
                        break;
                    case PppoeManager.EVENT_CONNECT_FAILED:
                        if (mPppoe.isChecked()) {
                            if (EthernetManager.ETHERNET_AUTORECONNECT_ENABLED
                                == mEthManager.getAutoReconnectState()) {
                                mPppoe.setSummary(getString(R.string.ipv6_connect_failed_auto));
                            } else {
                                mPppoe.setSummary(getString(R.string.eth_disconected));
                            }
                        }
                        Ethernet_current_status = PppoeManager.EVENT_CONNECT_FAILED;
                        break;
                    case PppoeManager.EVENT_DISCONNECT_SUCCESSED:
                        if (mPppoe.isChecked()) {
                            mPppoe.setSummary(getString(R.string.eth_dhcp_cancel));
                        }
                        Ethernet_current_status = PppoeManager.EVENT_DISCONNECT_SUCCESSED;
                        break;
                    case PppoeManager.EVENT_DISCONNECT_FAILED:
                        Ethernet_current_status = PppoeManager.EVENT_DISCONNECT_FAILED;
                        break;
                    case PppoeManager.EVENT_CONNECTING:
                        enableConnectModeBox(false);
                        if (mPppoe.isChecked()) {
                            mPppoe.setSummary(R.string.eth_dhcp_getting);
                        }
                        Ethernet_current_status = PppoeManager.EVENT_CONNECTING;
                        break;
                    case PppoeManager.EVENT_AUTORECONNECTING:
                        if (mPppoe.isChecked()) {
                            mPppoe.setSummary(R.string.auto_reconnecting);
                        }
                        Ethernet_current_status = PppoeManager.EVENT_AUTORECONNECTING;
                        break;
                    case PppoeManager.EVENT_CONNECT_FAILED_AUTH_FAIL:
                        Log.d(TAG, "pppoe auth failed!, please check username and passwd");
                        if (mPppoe.isChecked()) {
                            mPppoe.setSummary(R.string.pppoe_auth_failed);
                        }
                        Ethernet_current_status = PppoeManager.EVENT_CONNECT_FAILED_AUTH_FAIL;
                        break;
                    default:
                        break;
                    }
                }
            }
        }
    };

    public void () {
        if (Ethernet_current_status != -1) {
            upadteEthernetSummary(Ethernet_current_status);
        }
        if (null != mIPaddress)
            mIPaddress.setOnPreferenceChangeListener(this);
        if (null != mGateWay)
            mGateWay.setOnPreferenceChangeListener(this);
        if (null != mNetMask)
            mNetMask.setOnPreferenceChangeListener(this);
        if (null != mDNS1)
            mDNS1.setOnPreferenceChangeListener(this);
        if (null != mDNS2)
            mDNS2.setOnPreferenceChangeListener(this);
    }

    private void enableConnectModeBox(boolean state){
        //mDhcpIP.setEnabled(state);
        //mStaticIP.setEnabled(state);
        //mPppoe.setEnabled(state);
    }

    public void upadteEthernetSummary(int status) {
        switch (status) {
        case EthernetManager.EVENT_DHCP_CONNECT_SUCCESSED:
            if (mDhcpIP.isChecked()) {
                mDhcpIP.setSummary(getString(R.string.eth_static_connect));
            }
            break;
        case EthernetManager.EVENT_DHCP_CONNECT_FAILED:
            if (mDhcpIP.isChecked()) {
                mDhcpIP.setSummary(getString(R.string.eth_disconected));
            } else {
                mDhcpIP.setSummary(getString(R.string.eth_dhcp_cancel));
            }
            break;
        case EthernetManager.EVENT_DHCP_DISCONNECT_SUCCESSED:
            if (!mDhcpIP.isChecked() && !mStaticIP.isChecked()) {
                mDhcpIP.setSummary(getString(R.string.eth_dhcp_cancel));
                if (DEBUG)
                    Log.i(TAG, " DHCP down OK  : " + status);
            } else if (mDhcpIP.isChecked()) {
                mDhcpIP.setSummary(getString(R.string.eth_disconected));
            }
            break;
        case EthernetManager.EVENT_DHCP_DISCONNECT_FAILED:
            break;
        case EthernetManager.EVENT_STATIC_CONNECT_SUCCESSED:
            mStaticIP.setSummary(getString(R.string.eth_static_connect));
            break;
        case EthernetManager.EVENT_STATIC_CONNECT_FAILED:
            if (mStaticIP.isChecked()) {
                mStaticIP.setSummary(getString(R.string.eth_disconected));
            } else {
                mStaticIP
                        .setSummary(getString(R.string.eth_static_disconnect_successed));
            }
            break;
        case EthernetManager.EVENT_STATIC_DISCONNECT_SUCCESSED:
            if (!mStaticIP.isChecked() && !mDhcpIP.isChecked()) {
                mStaticIP
                        .setSummary(getString(R.string.eth_static_disconnect_successed));
            } else if (mStaticIP.isChecked()) {
                mStaticIP.setSummary(getString(R.string.eth_disconected));
            } else if (mDhcpIP.isChecked()) {
                mDhcpIP.setSummary(R.string.eth_dhcp_getting);
            }
            if (DEBUG)
                Log.i(TAG, " Static Down OK  : " + status);
            break;
        case EthernetManager.EVENT_STATIC_DISCONNECT_FAILED:
            break;
        case PppoeManager.EVENT_CONNECT_SUCCESSED:
            mPppoe.setSummary(getString(R.string.eth_static_connect));
            break;
        case PppoeManager.EVENT_CONNECT_FAILED:
            if (mPppoe.isChecked()) {
                mPppoe.setSummary(getString(R.string.eth_disconected));
            } else {
                mPppoe.setSummary(getString(R.string.eth_static_disconnect_successed));
            }
            break;
        case PppoeManager.EVENT_DISCONNECT_SUCCESSED:
            if (mPppoe.isChecked()) {
                mPppoe.setSummary(getString(R.string.eth_disconected));
            } else {
                mPppoe.setSummary(getString(R.string.eth_static_disconnect_successed));
            }
            break;
        case PppoeManager.EVENT_DISCONNECT_FAILED:
            break;
        case EthernetManager.EVENT_PHY_LINK_UP:
            if (mStaticIP.isChecked()) {
                mDhcpIP.setSummary("");
            } else if (mDhcpIP.isChecked()) {
                mStaticIP.setSummary("");
                mDhcpIP.setSummary(R.string.eth_dhcp_getting);
            }
            break;
        default:
            break;
        }
    }

    public EthernetSettings() {
        if (DEBUG)
            Log.e(TAG, "EthernetSettings");
        mIntentFilter = new IntentFilter(
                EthernetManager.ETHERNET_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(EthernetManager.NETWORK_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(PppoeManager.PPPOE_STATE_CHANGED_ACTION);
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pppoeManager = (PppoeManager) getSystemService(Context.PPPOE_SERVICE);

        final Activity activity = getActivity();
        Switch actionBarSwitch = new Switch(activity);

        if (activity instanceof PreferenceActivity) {
            PreferenceActivity preferenceActivity = (PreferenceActivity) activity;
            if (preferenceActivity.onIsHidingHeaders() || !preferenceActivity.onIsMultiPane()) {
                final int padding = activity.getResources().getDimensionPixelSize(
                        R.dimen.action_bar_switch_padding);
                actionBarSwitch.setPaddingRelative(0, 0, padding, 0);
                activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                        ActionBar.DISPLAY_SHOW_CUSTOM);
                activity.getActionBar().setCustomView(actionBarSwitch, new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        Gravity.CENTER_VERTICAL | Gravity.END));
            }
        }
        mEthEnabler = new EthernetEnabler(activity, actionBarSwitch);
        mEthManager = mEthEnabler.getManager();
        if(mEthManager.getEthernetPersistedState()==EthernetManager.ETHERNET_STATE_ENABLED)
        {
            actionBarSwitch.setChecked(true);
        }
        else
        {
            actionBarSwitch.setChecked(false);
        }

        mConnectivityManager = mEthEnabler.getConnectivityManager();
        mNwService = mEthEnabler.getNetworkManagerment();// to get mac
        KEY_ETH_INTERFACE = mEthManager.getInterfaceName().toString();

        addPreferencesFromResource(R.xml.ethernet_settings);
        initUI();
        setHasOptionsMenu(true);
        if(Ethernet_current_status == PppoeManager.EVENT_CONNECTING)
            enableConnectModeBox(false);
        else if(mEthManager.getEthernetState() == EthernetManager.ETHERNET_STATE_ENABLED)
            enableConnectModeBox(true);
    }

    private void initUI() {
        mDhcpIP = (CheckBoxPreference) findPreference("ethernet_use_dhcp");
        mStaticIP = (CheckBoxPreference) findPreference("ethernet_use_static");
        mPppoe = (CheckBoxPreference) findPreference("ethernet_use_pppoe");

        mIPaddress = (Preference) findPreference(KEY_IP);
        mGateWay = (Preference) findPreference(KEY_GATE_WAY);
        mNetMask = (Preference) findPreference(KEY_NETMASK);
        mDNS1 = (Preference) findPreference(KEY_DNS1);
        mDNS2 = (Preference) findPreference(KEY_DNS2);
        if (null != mIPaddress)
            mIPaddress.setOnPreferenceChangeListener(this);
        if (null != mGateWay)
            mGateWay.setOnPreferenceChangeListener(this);
        if (null != mNetMask)
            mNetMask.setOnPreferenceChangeListener(this);
        if (null != mDNS1)
            mDNS1.setOnPreferenceChangeListener(this);
        if (null != mDNS2)
            mDNS2.setOnPreferenceChangeListener(this);
        if (DEBUG)
            Log.e(TAG,
                    "number of Interface : " + mEthManager.getTotalInterface());
        if (EthernetManager.ETHERNET_CONNECT_MODE_DHCP.equals(
                mEthManager.getEthernetMode())) {
            if(Ethernet_current_status == EthernetManager.EVENT_DHCP_CONNECT_SUCCESSED ||
                Ethernet_current_status == EthernetManager.EVENT_DHCP_CONNECT_FAILED ) {
                showPreference(mDhcpIP);
            }
        } else if (EthernetManager.ETHERNET_CONNECT_MODE_MANUAL.equals(
                mEthManager.getEthernetMode())) {
            if(Ethernet_current_status == EthernetManager.EVENT_STATIC_CONNECT_SUCCESSED ||
                    Ethernet_current_status == EthernetManager.EVENT_STATIC_CONNECT_FAILED ) {
                showPreference(mStaticIP);
            }
            showStaticIP(); // show IP from DB
        } else if (EthernetManager.ETHERNET_CONNECT_MODE_PPPOE.equals(
                mEthManager.getEthernetMode())) {
            if(Ethernet_current_status == PppoeManager.EVENT_CONNECT_SUCCESSED ||
                Ethernet_current_status == PppoeManager.EVENT_CONNECT_FAILED) {
                showPreference(mPppoe);
            }
        }
    }

    private void showPreference (CheckBoxPreference preference) {
        if (preference == mDhcpIP) {
            mDhcpIP.setChecked(true);
            mStaticIP.setChecked(false);
            mStaticIP.setSummary("");
            mPppoe.setChecked(false);
            mPppoe.setSummary("");
        } else if (preference == mStaticIP) {
            mDhcpIP.setChecked(false);
            mDhcpIP.setSummary("");
            mStaticIP.setChecked(true);
            mPppoe.setChecked(false);
            mPppoe.setSummary("");
        } else if (preference == mPppoe) {
            mDhcpIP.setChecked(false);
            mDhcpIP.setSummary("");
            mStaticIP.setChecked(false);
            mStaticIP.setSummary("");
            mPppoe.setChecked(true);
        }
    }

    private void showStaticIP() {
        DhcpInfo dhcpInfo = mEthManager.getSavedEthernetIpInfo();

        String mNM = NetworkUtils.intToInetAddress(dhcpInfo.netmask).getHostAddress();
        if (null != mNetMask)
            mNetMask.setTitle(mNM);
        String mIP = NetworkUtils.intToInetAddress(dhcpInfo.ipAddress).getHostAddress();
        if (null != mIPaddress)
            mIPaddress.setTitle(mIP);

        String mGW = NetworkUtils.intToInetAddress(dhcpInfo.gateway).getHostAddress();
        if (null != mGateWay) {
            mGateWay.setTitle(mGW);
            mGateWay.setDefaultValue(mGW);
        }
        String mDns1 = NetworkUtils.intToInetAddress(dhcpInfo.dns1).getHostAddress();
        if (null != mDNS1)
            mDNS1.setTitle(mDns1);
        String mDns2 = NetworkUtils.intToInetAddress(dhcpInfo.dns2).getHostAddress();
        if (null != mDNS2)
            mDNS2.setTitle(mDns2);
    }

    /**
     * show dhcp IP when dhcp get success message
     */
    public void showEthIP() {
        if (DEBUG) Log.w(TAG, "begin to show Dhcp NetMask IP gateway dns");
        LinkProperties linkProperties = mConnectivityManager
            .getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        Iterator<LinkAddress> addrs = linkProperties.getLinkAddresses().iterator();
        if (!addrs.hasNext()) {
            Log.e(TAG, "showDhcpIP:can not get LinkAddress!!");
            return;
        }

        LinkAddress linkAddress = null;
        InetAddress Netmask = null;
        int prefixLength = 0;
        while (addrs.hasNext()) {
            linkAddress = addrs.next();
            prefixLength = linkAddress.getNetworkPrefixLength();
            if(prefixLength < 0 || prefixLength > 32)
                continue;
            int NetmaskInt = NetworkUtils.prefixLengthToNetmaskInt(prefixLength);
            Netmask = NetworkUtils.intToInetAddress(NetmaskInt);
            break;
        }

        if(Netmask == null) {
            Log.e(TAG, "showDhcpIP:can not get Netmask!!");
            return;
        }

        String mNM = Netmask.getHostAddress();
        Log.i(TAG, "netmask:  " + mNM);
        mNetMask.setTitle(mNM);
        mNetMask.setDefaultValue(mNM);

        try {
            String mIP = linkAddress.getAddress().getHostAddress();
            if (DEBUG) Log.i(TAG, "mIP" + mIP);
            if (null != mIPaddress) {
                mIPaddress.setTitle(mIP);
                mIPaddress.setDefaultValue(mIP);
            }

            String mGW = "";
            for (RouteInfo route : linkProperties.getRoutes()) {
                if (route.isDefaultRoute()) {
                    mGW = route.getGateway().getHostAddress();
                    if (DEBUG)Log.e(TAG, "Gateway:  " + mGW);
                    break;
                }
            }

            mGateWay.setTitle(mGW);
            mGateWay.setDefaultValue(mGW);

            mDNS1.setTitle(DEFAULT_DNS);
            mDNS1.setDefaultValue(DEFAULT_DNS);
            mDNS2.setTitle(DEFAULT_DNS);
            mDNS2.setDefaultValue(DEFAULT_DNS);

            Iterator<InetAddress> dnses = linkProperties.getDnses().iterator();
            String mDns1 = null,mDns2 = null;
            while(dnses.hasNext()) {
                mDns1 = dnses.next().getHostAddress();
                if (DEBUG) Log.i(TAG, "DNS1: " + mDns1);
                if ((null != mDNS1) && !checkIpv4Addr(mDns1))
                    continue;
            }

            if (null != mDNS1 ) {
                mDNS1.setTitle(mDns1);
                mDNS1.setDefaultValue(mDns1);
            }

            while (dnses.hasNext()) {
                mDns2 = dnses.next().getHostAddress();
                if (DEBUG) Log.i(TAG, "DNS2: " + mDns2);
                if (null != mDNS2 && !checkIpv4Addr(mDns2))
                    continue;
            }

            if (null != mDNS2) {
                mDNS2.setTitle(mDns2);
                mDNS2.setDefaultValue(mDns2);
            }

        } catch (NullPointerException e) {
            Log.w(TAG, "can not get IP" + e);
        }
        mDhcpIP.setSummary(getString(R.string.eth_dhcp_successed));
    }

    public void cleanIP() {
        if (DEBUG)
            Log.e(TAG, " clean IP() ");
        mDhcpIP.setSummary("");
        mStaticIP.setSummary("");
        mPppoe.setSummary("");
        if (null != mIPaddress)
            mIPaddress.setTitle("");
        if (null != mGateWay)
            mGateWay.setTitle("");
        if (null != mNetMask)
            mNetMask.setTitle("");
        if (null != mDNS1)
            mDNS1.setTitle("");
        if (null != mDNS2)
            mDNS2.setTitle("");
    }

    @Override
    public void onResume() {
        this.getActivity()
                .registerReceiver(mEthSettingsReceiver, mIntentFilter);
        registerObserver();
        checkSwitchDB();

        if (mEthEnabler != null) {
                mEthEnabler.resume();
        }
        super.onResume();
    }

    private void registerObserver() {
        this.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.ETHERNET_IP), true,
                dbObserver);
        this.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.ETHERNET_ROUTE),
                true, dbObserver);
        this.getContentResolver()
                .registerContentObserver(
                        Settings.Secure
                                .getUriFor(Settings.Secure.ETHERNET_PREFIXLENGTH),
                        true, dbObserver);
        this.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.ETHERNET_DNS_1),
                true, dbObserver);
        this.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.ETHERNET_DNS_2),
                true, dbObserver);
        this.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.ETHERNET_ON), true,
                switchObserver);
    }

    private ContentObserver dbObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (DEBUG)
                Log.e(TAG, "IP Changed  beging to handle DB Changed"
                        + selfChange);
            mEthManager.setEthernetEnabled(false);
            getIP();
            mEthManager.setEthernetEnabled(true);
        }
    };
    private ContentObserver switchObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            if (DEBUG)
                Log.e(TAG, "switch Changed   handle DB Changed" + selfChange);
            checkSwitchDB();
        }
    };

    protected void checkSwitchDB() {
        int SwitchEnable = mEthManager.getEthernetPersistedState();
        if (SwitchEnable == EthernetManager.ETHERNET_STATE_DISABLED) {
            int count = this.getPreferenceScreen().getPreferenceCount();
            for (int i = 0; i < count; i++) {
                this.getPreferenceScreen().getPreference(i)
                        .setEnabled(false);
            }
            cleanIP();
        } else if (SwitchEnable == EthernetManager.ETHERNET_STATE_ENABLED) {
            int count = this.getPreferenceScreen().getPreferenceCount();
            for (int i = 0; i < count; i++) {
                this.getPreferenceScreen().getPreference(i)
                        .setEnabled(true);
            }
            if (mStaticIP.isChecked()) {
                checkDB();
                getIP();// show ip from DB to title
            }
        }
    }

    @Override
    public void onPause() {
        this.getActivity().unregisterReceiver(mEthSettingsReceiver);
        getContentResolver().unregisterContentObserver(dbObserver);
        getContentResolver().unregisterContentObserver(switchObserver);

        if (mEthEnabler != null) {
            mEthEnabler.pause();
        }
        super.onPause();
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        boolean dhcp_state = false;
        if (preference == mDhcpIP) {
            if (null == mDNS1 || null == mDNS2 || null == mGateWay
                    || null == mNetMask) {
                updateUi();
            }
            mStaticIP.setChecked(false);
            mPppoe.setChecked(false);
            cleanIP();
            mStaticIP.setSummary("");
            mPppoe.setSummary("");
            if (mDhcpIP.isChecked()) {
                mDhcpIP.setSummary(getString(R.string.eth_dhcp_getting));
                enableConnectModeBox(false);
                startDhcp();
                Ethernet_current_status = EthernetManager.EVENT_PHY_LINK_UP;
            } else {
                mEthManager.setEthernetEnabled(false);
                mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_NONE,null);
            }
        } else if (preference == mStaticIP) {
            if (null == mDNS1 || null == mDNS2 || null == mGateWay
                    || null == mNetMask) {
                updateUi();
            }
            mDhcpIP.setChecked(false);
            mDhcpIP.setSummary("");
            mStaticIP.setSummary("");
            mPppoe.setChecked(false);
            mPppoe.setSummary("");
            if (mStaticIP.isChecked()) {
                if (DEBUG)
                    Log.i(TAG, "Static is Checked now set IP by yourself ");
                enableConnectModeBox(false);
                startStatic();
            } else {
                mStaticIP
                        .setSummary(R.string.eth_static_disconnect_successed);
                mEthManager.setEthernetEnabled(false);
                DhcpInfo dhcpInfo = new DhcpInfo();
                dhcpInfo.ipAddress = 0;
                dhcpInfo.gateway = 0;
                dhcpInfo.netmask = 0;
                dhcpInfo.dns1 = 0;
                dhcpInfo.dns2 = 0;
                mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_NONE,dhcpInfo);
            }
        } else if (preference == mPppoe) {
            if (null == mDNS1 || null == mDNS2 || null == mGateWay
                    || null == mNetMask) {
                updateUi();
            }
            if (mPppoe.isChecked()) {
                //startPppoe();
                mPppoe.setChecked(false);
                enableConnectModeBox(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View view = factory.inflate(R.layout.pppoe_login_layout, null);
                mUsername = (EditText) view.findViewById(R.id.login_username);
                mPassword = (EditText) view.findViewById(R.id.login_password);
                String user_name = pppoeManager.getPppoeUsername();
                String password = pppoeManager.getPppoePassword();
                if (null != user_name) {
                    mUsername.setText(user_name);
                }
                if (null != password) {
                    mPassword.setText(password);
                }
                builder.setView(view);
                builder.setPositiveButton(R.string.wifi_connect,
                    new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (null == mUsername || null == mUsername.getText()) {
                                return;
                            } else {
                                pppoeManager.setPppoeUsername(mUsername.getText().toString());
                            }
                            if (null == mPassword || null == mPassword.getText()) {
                                return;
                            } else {
                                pppoeManager.setPppoePassword(mPassword.getText().toString());
                            }
                            cleanIP();
                            mPppoe.setSummary("");
                            startPppoe();
                            showPreference(mPppoe);
                            dialog.dismiss();
                        }
                    });
                builder.setNegativeButton(R.string.pppoe_dialog_cannel,
                    new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                builder.create().show();
            } else {
                cleanIP();
                mEthManager.setEthernetEnabled(false);
                mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_NONE,null);
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    private void startPppoe() {
        mEthManager.setEthernetEnabled(false);
        mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_PPPOE, null);
        mEthManager.setEthernetEnabled(true);
    }

    private void startDhcp() {
        mEthManager.setEthernetEnabled(false);
        mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_DHCP,null);
        mEthManager.setEthernetEnabled(true);
    }

    private void startStatic() {
        mEthManager.setEthernetEnabled(false);
        if (checkIP()) {
            setIP();// set ip from title to DB
        } else {
            checkDB();
            getIP();// show ip from DB to title
            setIP();
            if (DEBUG)
                Log.e(TAG, " get IP from DB and show it in the pannel ");
        }
        mEthManager.setEthernetEnabled(true);
    }

    private boolean checkIP() {
        if (null != mIPaddress.getTitle()) {
            if (mIPaddress.getTitle().length() > 0) {
                return true;
            }
        }
        return false;
    }

    public void getIP() {

        DhcpInfo dhcpInfo = mEthManager.getSavedEthernetIpInfo();

        String IP = NetworkUtils.intToInetAddress(dhcpInfo.ipAddress).getHostAddress();
        if (null != mIPaddress)
            mIPaddress.setTitle(IP);

        String mGW = NetworkUtils.intToInetAddress(dhcpInfo.gateway).getHostAddress();

        if (null != mGateWay)
            mGateWay.setTitle(mGW);

        String mNM = NetworkUtils.intToInetAddress(dhcpInfo.netmask).getHostAddress();
        if (null != mNetMask)
            mNetMask.setTitle(mNM);

        String dns1 = NetworkUtils.intToInetAddress(dhcpInfo.dns1).getHostAddress();
        if (null != mDNS1)
            mDNS1.setTitle(dns1);

        String dns2 = NetworkUtils.intToInetAddress(dhcpInfo.dns2).getHostAddress();
        if (null != mDNS2)
            mDNS2.setTitle(dns2);
    }

    /**
     * check settings DB ,if it is empty set a default value,else do nothing
     *
     * @return
     */
    private void checkDB() {
        DhcpInfo dhcpInfo = mEthManager.getSavedEthernetIpInfo();

        String ipAddress = NetworkUtils.intToInetAddress(dhcpInfo.ipAddress).getHostAddress();

        if (DEBUG)
            Log.e(TAG, "checkDB() ipAddress : " + ipAddress);
        if (ipAddress == null) {
            if (DEBUG)
                Log.e(TAG, "settings.secure.ETHERNET_IP is empty");
            setdefIP();
        }
    }

    /**
     * set IP gateway ... from pannel to DB when dhcp success
     */
    private void setIP() {
        if (DEBUG)
            Log.e(TAG, "setIP() from pannel to DB");
        String ipAddress = "";
        if (null != mIPaddress.getTitle()) {
            ipAddress = mIPaddress.getTitle().toString();
        }
        String DNS1 = "";
        if (null != mDNS1) {
            if (null != mDNS1.getTitle()) {
                DNS1 = mDNS1.getTitle().toString();
            }
        }
        String DNS2 = "";
        if (null != mDNS2) {
            if (null != mDNS2.getTitle()) {
                DNS2 = mDNS2.getTitle().toString();
            }
        }
        String gateWay = "";
        if (null != mGateWay) {
            if (null != mGateWay.getTitle()) {
                gateWay = mGateWay.getTitle().toString();
            }
        }
        String netMask = "";
        if (null != mNetMask) {
            if (null != mNetMask.getTitle()) {
                netMask = mNetMask.getTitle().toString();
            }
        }

        InetAddress ipaddr = NetworkUtils.numericToInetAddress(ipAddress);
        InetAddress getwayaddr = NetworkUtils.numericToInetAddress(gateWay);
        InetAddress inetmask = NetworkUtils.numericToInetAddress(netMask);
        InetAddress idns1 = NetworkUtils.numericToInetAddress(DNS1);
        InetAddress idns2 = NetworkUtils.numericToInetAddress(DNS2);

        DhcpInfo dhcpInfo = new DhcpInfo();
        try {
            dhcpInfo.ipAddress = NetworkUtils.inetAddressToInt(ipaddr);
            dhcpInfo.gateway = NetworkUtils.inetAddressToInt(getwayaddr);
            dhcpInfo.netmask = NetworkUtils.inetAddressToInt(inetmask);
            dhcpInfo.dns1 = NetworkUtils.inetAddressToInt(idns1);
            dhcpInfo.dns2 = NetworkUtils.inetAddressToInt(idns2);
        } catch(IllegalArgumentException e) {
            Log.e(TAG, "Invalid ipv4 address");
        }

        mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL, dhcpInfo);
    }

    /**
     * set default IP gateway .. to DB when dhcp failed and DB is empty
     */
    private void setdefIP() {
        if (DEBUG)
            Log.e(TAG, "setdefIP() ");
        String ipAddress = "0.0.0.0";
        String DNS1 = "0.0.0.0";
        String DNS2 = "0.0.0.0";
        String gateWay = "0.0.0.0";
        int prefixLength = 32;

        InetAddress ipaddr = NetworkUtils.numericToInetAddress(ipAddress);
        InetAddress getwayaddr = NetworkUtils.numericToInetAddress(gateWay);
        InetAddress idns1 = NetworkUtils.numericToInetAddress(DNS1);
        InetAddress idns2 = NetworkUtils.numericToInetAddress(DNS2);

        DhcpInfo dhcpInfo = new DhcpInfo();
        dhcpInfo.ipAddress = NetworkUtils.inetAddressToInt((Inet4Address)ipaddr);
        dhcpInfo.gateway = NetworkUtils.inetAddressToInt((Inet4Address)getwayaddr);
        dhcpInfo.netmask = NetworkUtils.prefixLengthToNetmaskInt(prefixLength);
        dhcpInfo.dns1 = NetworkUtils.inetAddressToInt((Inet4Address)idns1);
        dhcpInfo.dns2 = NetworkUtils.inetAddressToInt((Inet4Address)idns2);
    }


    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
        startStatic();
        if (DEBUG)
            Log.w(TAG, "Something Changed try startStitic()");
        return false;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Auto-generated method stub
        menu.add(Menu.NONE, MENU_ID_ADVANCED, 0, R.string.wifi_menu_advanced)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // TODO Auto-generated method stub
        switch (item.getItemId()) {
        case MENU_ID_ADVANCED:
            if (getActivity() instanceof PreferenceActivity) {
                ((PreferenceActivity) getActivity()).startPreferencePanel(
                        AdvancedEthernetSettings.class.getCanonicalName(),
                        null, R.string.ethernet_advanced_titlebar, null, this,
                        0);
            } else {
                startFragment(this,
                        AdvancedEthernetSettings.class.getCanonicalName(), -1,
                        null);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkIpv4Addr(String addr) {
        boolean flags = false;
        String check_str = "(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])\\.(\\d{1,2}|1\\d\\d|2[0-4]\\d|25[0-5])";

        try {
            Pattern pzm = Pattern.compile(check_str);
            Matcher mat = pzm.matcher(addr);
            flags =mat.matches();
        }catch(Exception e){
            e.printStackTrace();
            flags = false;
        }
        return flags;
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                             