package com.android.settings;

import android.app.FragmentTransaction;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.RemoteException;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.os.SystemProperties;
import android.net.ethernet.*;
import android.net.ethernet.EthernetManager;
import android.net.EthernetDataTracker;
import android.os.INetworkManagementService;
import android.os.SystemProperties;
import android.net.pppoe.*;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;

import com.android.settings.R;
import com.android.settings.ethernet.EthernetEnabler;
import com.android.settings.ethernet.Option61Dialog;
import com.android.settings.ethernet.OptionDialog;
import com.android.settings.ethernet.pppoeLoginDialog;

public class AdvancedEthernetSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private ListPreference mDeviceList;
    private IntentFilter mIntentFilter;
    private Preference mMAC;
    private CheckBoxPreference mIpv6_dhcp;
    private CheckBoxPreference mIpv6_static;
    private Preference mIpv6_address;
    private static final String KEY_DEVICE_LIST = "device_list";
    private String KEY_ETH_INTERFACE = "eth0";
    private EthernetManager mEthManager;
    private EthernetEnabler mEthEnabler;
    private INetworkManagementService mNwService;
    private PppoeManager pppoeManager;
    private String iptvEnable = "null";
    private final String TAG = AdvancedEthernetSettings.class.getSimpleName();
    private static final String KEY_IPV6_DHCP = "ipv6_dhcp_checkbox";
    private static final String KEY_IPV6_STATIC = "ipv6_static_checkbox";
    private static final String KEY_OPTION = "option";
    private static final String KEY_MAC = "mac_address";
    private static final String KEY_OPTION_60 = "option_60_dhcp";
    private static final String KEY_OPTION_125 = "option_125_dhcp";
    private static final String KEY_GATE_WAY = "ipv6_gw";
    private static final String KEY_IP = "ipv6_ip";
    private static final String KEY_DNS1 = "ipv6_dns1";
    private static final String KEY_DNS2 = "ipv6_dns2";
    private static final String KEY_PREFIX_LENGTH = "ipv6_prefix_length";
    private static final String KEY_ETH_IPV6 = "eth_ipv6";
    private static final String KEY_IPV6_IP_CATEGORY = "ipv6_ip_category";
    private static final String KEY_IPV6_GW_CATEGORY = "ipv6_gw_category";
    private static final String KEY_IPV6_PREFIX_LENGTH_CATEGORY = "ipv6_prefix_length_category";
    private static final String KEY_IPV6_DNS_CATEGORY = "ipv6_dns_category";
    private static final String KEY_AUTO_RECONNECT = "auto_reconnect_net";
    private static String OPTION="option60";
    private CheckBoxPreference mOption60;
    private CheckBoxPreference mOption125;
    private CheckBoxPreference mAutoReconnect;

    private Preference mIPaddress;
    private Preference mGateWay;
    private Preference mPrefixLength;
    private Preference mDNS1;
    private Preference mDNS2;

    private Preference eth_ipv6_category;
    private Preference ipv6_ip_category;
    private Preference ipv6_gw_category;
    private Preference ipv6_prefix_length_category;
    private Preference ipv6_dns_category;

    private EditText mLogin_option60;
    private EditText mPwd_option60;
    private EditText mInfo_option125;
    private String option60_login;
    private String option60_pwd;
    private String option125_info;

    private BroadcastReceiver mEthernetSettingReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            int message = -1;
            int rel = -1;
            String ipv6_add;

            ContentResolver resolver = getActivity().getContentResolver();

            if (intent.getAction().equals(
                    EthernetManager.IPV6_STATE_CHANGED_ACTION)) {
                message = intent.getIntExtra(
                        EthernetManager.EXTRA_ETHERNET_STATE, rel);
                switch (message) {
                case EthernetManager.EVENT_DHCPV6_CONNECT_SUCCESSED:
                    if (mIpv6_dhcp.isChecked()) {
                        mIpv6_dhcp.setSummary(getString(R.string.ipv6_connect));
                        show_ipv6Info();
                    }else{
                        mIpv6_dhcp.setSummary(R.string.ipv6_disable);
                    }
                    break;
                case EthernetManager.EVENT_DHCPV6_CONNECT_FAILED:
                    if (mIpv6_dhcp.isChecked()) {
                        if (EthernetManager.ETHERNET_AUTORECONNECT_ENABLED
                            == mEthManager.getAutoReconnectState()) {
                            mIpv6_dhcp.setSummary(getString(R.string.ipv6_connect_failed_auto));
                            show_ipv6Info();
                        } else {
                            mIpv6_dhcp.setSummary(getString(R.string.ipv6_connect_failed));
                            show_ipv6Info();
                        }
                    }else{
                        mIpv6_dhcp.setSummary(R.string.ipv6_disable);
                    }
                    break;
                case EthernetManager.EVENT_DHCPV6_DISCONNECT_SUCCESSED:
                    if (mIpv6_dhcp.isChecked()) {
                        mIpv6_dhcp.setSummary(getString(R.string.ipv6_disconnect));
                        show_ipv6Info();
                    }else{
                        mIpv6_dhcp.setSummary(R.string.ipv6_disable);
                    }
                    break;
                case EthernetManager.EVENT_DHCPV6_DISCONNECT_FAILED:
                    if (mIpv6_dhcp.isChecked()) {
                        mIpv6_dhcp.setSummary(getString(R.string.ipv6_disconnet_failed));
                        show_ipv6Info();
                    }else{
                        mIpv6_dhcp.setSummary(R.string.ipv6_disable);
                    }
                    break;
                case EthernetManager.EVENT_DHCPV6_AUTORECONNECTING:
                    if (mIpv6_dhcp.isChecked()) {
                        mIpv6_dhcp.setSummary(getString(R.string.auto_reconnecting));
                    }
                    break;
                case EthernetManager.EVENT_PHY_LINK_UP:
                    if (mIpv6_dhcp.isChecked()) {
                        mIpv6_dhcp.setSummary(R.string.eth_dhcp_getting);
                        show_ipv6Info();
                    }else{
                        mIpv6_dhcp.setSummary(R.string.ipv6_disable);
                    }
                    break;
                case EthernetManager.EVENT_PHY_LINK_DOWN:
                    if (mIpv6_dhcp.isChecked()) {
                        mIpv6_dhcp.setSummary(R.string.eth_phy_link_down_check);
                        show_ipv6Info();
                    }else{
                        mIpv6_dhcp.setSummary(R.string.ipv6_disable);
                    }
                    break;
                case EthernetManager.EVENT_STATIC6_CONNECT_SUCCESSED:
                    if(mIpv6_static.isChecked()){
                        mIpv6_static.setSummary(getString(R.string.ipv6_connect));
                        show_ipv6Info();
                    }else{
                        mIpv6_static.setSummary(R.string.ipv6_disable);
                    }
                    break;
                case EthernetManager.EVENT_STATIC6_CONNECT_FAILED:
                    if(mIpv6_static.isChecked()){
                        mIpv6_static.setSummary(getString(R.string.ipv6_connect_failed));
                        show_ipv6Info();
                    }else{
                        mIpv6_static.setSummary(R.string.ipv6_disable);
                    }
                    break;
                case EthernetManager.EVENT_STATIC6_DISCONNECT_SUCCESSED:
                    if(mIpv6_static.isChecked()){
                        mIpv6_static.setSummary(getString(R.string.ipv6_disconnect));
                        show_ipv6Info();
                    }else{
                        mIpv6_static.setSummary(R.string.ipv6_disable);
                    }
                    break;
                case EthernetManager.EVENT_STATIC6_DISCONNECT_FAILED:
                    if(mIpv6_static.isChecked()){
                        mIpv6_static.setSummary(getString(R.string.ipv6_disconnet_failed));
                        show_ipv6Info();
                    }else{
                        mIpv6_static.setSummary(R.string.ipv6_disable);
                    }
                    break;
                default:
                    break;
                }
            }
        };
    };
    private final boolean DEBUG = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.ethernet_advanced_settings);
        iptvEnable = SystemProperties.get("ro.product.target", "ott");
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mIntentFilter = new IntentFilter(EthernetManager.IPV6_STATE_CHANGED_ACTION);
        mEthEnabler = new EthernetEnabler(getActivity(), null);
        mEthManager = mEthEnabler.getManager();
        KEY_ETH_INTERFACE = mEthManager.getInterfaceName().toString();
        mNwService = mEthEnabler.getNetworkManagerment();// to get mac
        pppoeManager = (PppoeManager) getSystemService(Context.PPPOE_SERVICE);
    }

    private void registerObserver() {
        this.getContentResolver().registerContentObserver(
                Settings.Secure.getUriFor(Settings.Secure.ETHERNET_ON), true,
                switchObserver);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        // TODO Auto-generated method stub
        String key = preference.getKey();
        if (KEY_DEVICE_LIST.equals(key)) {
            mEthManager.enableEthernet(false);
            KEY_ETH_INTERFACE = newValue.toString();
            mDeviceList.setValue(KEY_ETH_INTERFACE);
            mDeviceList.setTitle(mDeviceList.getEntry().toString());
            mEthManager.setInterfaceName(KEY_ETH_INTERFACE); // put into DB
            mEthManager.enableEthernet(true);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        if ("telecom".equals(iptvEnable) || "unicom".equals(iptvEnable)) {
            this.getActivity().registerReceiver(mEthernetSettingReceiver, mIntentFilter);
        } else {
            getPreferenceScreen().removePreference(findPreference(KEY_ETH_IPV6));
            getPreferenceScreen().removePreference(findPreference(KEY_OPTION));
        }
        registerObserver();
        initPreferences();
        checkSwitchDB();
        String errornub = System.getProperty("pppoe.error");

        Log.i(TAG, "onResume: check connect status");
        Log.i(TAG, "onResume: mode:" + mEthManager.getEthernetMode6() + "  status:" +
            mEthManager.checkDhcpv6Status(mEthManager.getInterfaceName()));
        if (((mEthManager.getEthernetMode6() == EthernetManager.ETHERNET_CONNECT_MODE_DHCP)
           && mEthManager.checkDhcpv6Status(mEthManager.getInterfaceName()))
           || (mEthManager.getEthernetMode6() == EthernetManager.ETHERNET_CONNECT_MODE_MANUAL))
           show_ipv6Info();

        super.onResume();
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        if ("telecom".equals(iptvEnable) || "unicom".equals(iptvEnable)) {
            this.getActivity().unregisterReceiver(mEthernetSettingReceiver);
        }
        getContentResolver().unregisterContentObserver(switchObserver);
        super.onPause();
    }

    private void initPreferences() {

        mMAC = (Preference) findPreference(KEY_MAC);
        mDeviceList = (ListPreference) findPreference(KEY_DEVICE_LIST);
        mMAC.setOnPreferenceChangeListener(this);
        mDeviceList.setOnPreferenceChangeListener(this);
        if (KEY_ETH_INTERFACE.equals("eth0")) {
            mDeviceList.setTitle(R.string.network_cable0);
            mDeviceList.setValue(KEY_ETH_INTERFACE);
        } else {
            mDeviceList.setValue(KEY_ETH_INTERFACE);
            mDeviceList.setTitle(R.string.network_cable1);
        }

        mIPaddress = (Preference) findPreference(KEY_IP);
        mGateWay = (Preference) findPreference(KEY_GATE_WAY);
        mPrefixLength = (Preference) findPreference(KEY_PREFIX_LENGTH);
        mDNS1 = (Preference) findPreference(KEY_DNS1);
        mDNS2 = (Preference) findPreference(KEY_DNS2);

        eth_ipv6_category = findPreference(KEY_ETH_IPV6);
        ipv6_ip_category = findPreference(KEY_IPV6_IP_CATEGORY);
        ipv6_prefix_length_category = findPreference(KEY_IPV6_PREFIX_LENGTH_CATEGORY);
        ipv6_gw_category = findPreference(KEY_IPV6_GW_CATEGORY);
        ipv6_dns_category = findPreference(KEY_IPV6_DNS_CATEGORY);

        mAutoReconnect =  (CheckBoxPreference)findPreference(KEY_AUTO_RECONNECT);
        if (EthernetManager.ETHERNET_AUTORECONNECT_ENABLED
            == mEthManager.getAutoReconnectState()) {
            mAutoReconnect.setChecked(true);
        } else {
            mAutoReconnect.setChecked(false);
        }

        if ("telecom".equals(iptvEnable) || "unicom".equals(iptvEnable)) {
            mIpv6_dhcp = (CheckBoxPreference)findPreference(KEY_IPV6_DHCP);
            mIpv6_static = (CheckBoxPreference) findPreference(KEY_IPV6_STATIC);
            mOption60 = (CheckBoxPreference)findPreference(KEY_OPTION_60);
            mOption125 = (CheckBoxPreference)findPreference(KEY_OPTION_125);

            if (EthernetManager.OPTION60_STATE_ENABLED
                == mEthManager.getDhcpOption60State()) {
                mOption60.setChecked(true);
            } else {
                mOption60.setChecked(false);
            }

            if (EthernetManager.OPTION125_STATE_ENABLED
                == mEthManager.getDhcpOption125State()) {
                mOption125.setChecked(true);
            } else {
                mOption125.setChecked(false);
            }
        } else {
            getPreferenceScreen().removePreference(ipv6_ip_category);
            getPreferenceScreen().removePreference(ipv6_prefix_length_category);
            getPreferenceScreen().removePreference(ipv6_gw_category);
            getPreferenceScreen().removePreference(ipv6_dns_category);
        }

        if(DEBUG){
            Log.i(TAG,"TotalInterface:"+mEthManager.getTotalInterface());
        }
        if (mEthManager.getTotalInterface() < 2) {
            getPreferenceScreen()
                    .removePreference(findPreference("eth_device"));
        }
        try {
            try {
                if(DEBUG){
                    Log.i(TAG,"macAddress:"+ mNwService.getInterfaceConfig(KEY_ETH_INTERFACE)
                            .getLinkAddress().toString());
                    Log.i(TAG,"getHardwareAddress:"+ mNwService.getInterfaceConfig(KEY_ETH_INTERFACE)
                            .getHardwareAddress().toString());
                }
                mMAC.setTitle(mNwService.getInterfaceConfig(KEY_ETH_INTERFACE)
                        .getHardwareAddress().toString());// show mac
            } catch (NullPointerException e) {
                Log.i(TAG, "NullPointerException  " + e);
            }
        } catch (RemoteException remote) {
            Log.i(TAG, "RemoteException  " + remote);
        }
    }

    private ContentObserver switchObserver = new ContentObserver(new Handler()) {
        @Override
        public void onChange(boolean selfChange) {
            checkSwitchDB();
        }
    };

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
            Preference preference) {
        if (preference == mIpv6_dhcp) {
            if(mIpv6_dhcp.isChecked()){
                mIpv6_static.setChecked(false);
                mEthManager.setEthernetMode6(EthernetManager.ETHERNET_CONNECT_MODE_DHCP);
                mEthManager.enableIpv6(true);
                mIpv6_dhcp.setSummary(R.string.eth_dhcp_getting);
                mIpv6_static.setSummary("");
            }else {
                mEthManager.enableIpv6(false);
                mIpv6_dhcp.setSummary(R.string.ipv6_disable);
            }
        } else if (preference == mIpv6_static) {
            if(mIpv6_static.isChecked()){
                mIpv6_dhcp.setChecked(false);
                mEthManager.enableIpv6(false);
                mEthManager.setEthernetMode6(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL);
                mEthManager.enableIpv6(true);
                mIpv6_static.setSummary(R.string.eth_dhcp_getting);
                mIpv6_dhcp.setSummary("");
            }else{
                mEthManager.enableIpv6(false);
                mIpv6_static.setSummary("");
            }
        } else if (preference == mOption60) {
            if (mOption60.isChecked()) {
                mOption60.setChecked(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View view = factory.inflate(R.layout.pppoe_login_layout, null);
                mLogin_option60 = (EditText) view.findViewById(R.id.login_username);
                mPwd_option60 = (EditText) view.findViewById(R.id.login_password);
                option60_login = mEthManager.getDhcpOption60Login();
                option60_pwd = mEthManager.getDhcpOption60Password();

                if (null != option60_login) {
                    mLogin_option60.setText(option60_login);
                }
                if (null != option60_pwd) {
                    mPwd_option60.setText(option60_pwd);
                }
                builder.setView(view);
                builder.setPositiveButton(R.string.option_save,
                    new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (null == mLogin_option60 || null == mLogin_option60.getText()) {
                                return;
                            }
                            if (null == mPwd_option60 || null == mPwd_option60.getText()) {
                                return;
                            }
                            mEthManager.setDhcpOption60(true, mLogin_option60.getText().toString(),
                                mPwd_option60.getText().toString());
                            mOption60.setChecked(true);
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
                option60_login = mEthManager.getDhcpOption60Login();
                option60_pwd = mEthManager.getDhcpOption60Password();
                mEthManager.setDhcpOption60(false, option60_login, option60_login);
            }
        } else if (preference == mOption125) {
            if (mOption125.isChecked()) {
                mOption125.setChecked(false);
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                LayoutInflater factory = LayoutInflater.from(getActivity());
                final View view = factory.inflate(R.layout.option125_layout, null);
                mInfo_option125= (EditText) view.findViewById(R.id.login_info);
                option125_info = mEthManager.getDhcpOption125Info();
                if (null != option125_info) {
                    mInfo_option125.setText(option125_info);
                }

                builder.setView(view);
                builder.setPositiveButton(R.string.option_save,
                    new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            if (null == mInfo_option125 || null == mInfo_option125.getText()) {
                                return;
                            }
                            mEthManager.setDhcpOption125(true, mInfo_option125.getText().toString());
                            mOption125.setChecked(true);
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
                option125_info = mEthManager.getDhcpOption125Info();
                mEthManager.setDhcpOption125(false, option125_info);
            }
        } else if (preference == mAutoReconnect) {
            if (mAutoReconnect.isChecked()) {
                mEthManager.setAutoReconnectState(true);
            } else {
                mEthManager.setAutoReconnectState(false);
            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    protected void checkSwitchDB() {
        ContentResolver resolver = getActivity().getContentResolver();
        int SwitchEnable = mEthManager.getEthernetPersistedState();
        if (SwitchEnable == 0) {
            mDeviceList.setEnabled(false);
        } else if (SwitchEnable == 1) {
            mDeviceList.setEnabled(true);
        }
    }

    protected void  show_ipv6Info() {
        if(EthernetManager.ETHERNET_CONNECT_MODE_DHCP.equals(mEthManager.getEthernetMode6())) {
            String iface = mEthManager.getInterfaceName();
            if(mIPaddress != null) {
                mIPaddress.setTitle(mEthManager.getDhcpv6Ipaddress(iface));
            }
            if(mGateWay != null){
                mGateWay.setTitle(mEthManager.getDhcpv6Gateway());
            }
            if(mPrefixLength != null){
                mPrefixLength.setTitle(mEthManager.getDhcpv6Prefixlen(iface));
            }
            if(mDNS1 != null){
                mDNS1.setTitle(mEthManager.getDhcpv6Dns(iface, 1));
            }
            if(mDNS2 != null){
                mDNS2.setTitle(mEthManager.getDhcpv6Dns(iface, 2));
            }

            Log.i(TAG, "show_ipv6Info, mode dhcpv6, ip:" + mEthManager.getDhcpv6Ipaddress(iface) +
                ",gw:" + mEthManager.getDhcpv6Gateway() + ",prefix:" + mEthManager.getDhcpv6Prefixlen(iface) +
                ", dns1:"+ mEthManager.getDhcpv6Dns(iface, 1) + ", dns2:" + mEthManager.getDhcpv6Dns(iface, 2));
        } else {
            if(mIPaddress != null) {
                mIPaddress.setTitle(mEthManager.getIpv6DatabaseAddress());
            }
            if(mGateWay != null){
                mGateWay.setTitle(mEthManager.getIpv6DatabaseGateway());
            }
            if(mPrefixLength != null){
                mPrefixLength.setTitle(mEthManager.getIpv6DatabasePrefixlength()+"");
            }
            if(mDNS1 != null){
                mDNS1.setTitle(mEthManager.getIpv6DatabaseDns1());
            }
            if(mDNS2 != null){
                mDNS2.setTitle(mEthManager.getIpv6DatabaseDns2());
            }
            Log.i(TAG, "show_ipv6Info, mode static ipv6, ip:" + mEthManager.getIpv6DatabaseAddress() +
                ",gw:" + mEthManager.getIpv6DatabaseGateway() + ",prefix:" + mEthManager.getIpv6DatabasePrefixlength() +
                ", dns1:"+ mEthManager.getIpv6DatabaseDns1() + ", dns2:" + mEthManager.getIpv6DatabaseDns2());
        }
    }

}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            