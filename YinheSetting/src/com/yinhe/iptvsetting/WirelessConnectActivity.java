
package com.yinhe.iptvsetting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import android.net.wifi.WpsResult;
import com.android.internal.util.AsyncChannel;
import com.yinhe.iptvsetting.R;

import com.yinhe.iptvsetting.adapter.WifiAdapter;
import com.yinhe.iptvsetting.common.EditTextBgToStar;
import com.yinhe.iptvsetting.object.AccessPoint;
import com.yinhe.iptvsetting.object.Wifi;

/**
 * 无线接入Activity。
 * 
 * @author zhbn
 */
public class WirelessConnectActivity extends Activity implements
        OnFocusChangeListener {

    private static final String TAG = "WirelessConnectActivity";

    private Button btOn = null;
    private Button btOFF = null;
    private ListView myListView = null;
    private List<AccessPoint> data = null;
    private WifiAdapter myAdapter = null;
    private LinearLayout myLayout = null;
    private ContentResolver cr;

    private final String WIFI_ETH_KEY = "wifi_switch";
    private final String WIFI_KEY = "wifi_on";
    private final String CABLE_KEY = "ethernet_on";

    private static final int MSG_UPDATE = 0;

    private WifiManager mWifiManager;
    private ScannerHandler mScanner;

    private WifiManager.ActionListener mConnectListener;

    private BroadcastReceiver mReceiver;
    private IntentFilter mFilter;
    // Combo scans can take 5-6s to complete - set to 10s.
    private final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;
    private Toast myToast = null;
    private WifiInfo mLastInfo;
    private DetailedState mLastState;
    private AtomicBoolean mConnected = new AtomicBoolean(false);

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_UPDATE:
                    myAdapter.notifyDataSetChanged();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.wireless_connect);
        initData();

        AccessPoint.isFactoryMode = getIntent().getBooleanExtra("yinhe_factoryMode", false);
        if (AccessPoint.isFactoryMode) {
            turnOnWifi();
        }
    }

    private void initData() {
        mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        // mFilter.addAction(WifiManager.ERROR_ACTION); hqq

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleEvent(context, intent);
            }
        };
        mScanner = new ScannerHandler();

        cr = getContentResolver();
        mWifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        myLayout = (LinearLayout) super.findViewById(R.id.ll_bg);
        btOn = (Button) super.findViewById(R.id.button_on);
        btOn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                turnOnWifi();
            }
        });

        btOFF = (Button) super.findViewById(R.id.button_off);
        btOFF.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                myLayout.setBackgroundResource(R.drawable.off);// wifi关闭
                if (!data.isEmpty()) {
                    data.clear();
                    myHandler.sendEmptyMessage(MSG_UPDATE);
                }
                Settings.Secure.putInt(cr, WIFI_KEY, 0);//
                mWifiManager.setWifiEnabled(false);
            }
        });

        this.btOn.setOnFocusChangeListener(this);
        this.btOFF.setOnFocusChangeListener(this);

        myListView = (ListView) super.findViewById(R.id.my_listView);
        data = new ArrayList<AccessPoint>();
        myAdapter = new WifiAdapter(data, this);
        myListView.setAdapter(myAdapter);
        myListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                final AccessPoint item = data.get(arg2);
                accessPointClick(item);
            }
        });

        myListView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                myListView.setSelector(hasFocus ? R.drawable.list_selector
                        : R.drawable.list_null);
            }
        });

        mConnectListener = new WifiManager.ActionListener() {
            public void onSuccess() {
            }

            public void onFailure(int reason) {
                Toast.makeText(WirelessConnectActivity.this,
                        "Connect failed!",
                        Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void accessPointClick(AccessPoint item) {
        WifiConfiguration wifiConfiguration = item.getConfig();
        // 未保存
        if (wifiConfiguration == null) {
            // 不需密码
            if (AccessPoint.SECURITY_NONE == item.security) {
                connectToWIFI(item, "");
            } else {// 需要密码
                createPasswordInputDialog(item);
            }
        } else {// 已保存
            createConfigedDialog(item);
        }
    }

    private void createConfigedDialog(final AccessPoint item) {
        WifiInfo info = item.getInfo();
        AlertDialog.Builder builder = new AlertDialog.Builder(
                WirelessConnectActivity.this, R.style.style_dialog);
        builder.setTitle(item.ssid);

        if (info == null) {
            builder.setPositiveButton(R.string.wifi_connect,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,
                                int which) {
                            mWifiManager.connect(item.getConfig(), mConnectListener);
                        }
                    });
        }

        builder.setNeutralButton(R.string.wifi_forget, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                WifiConfiguration tempConfig = IsExsits(item.ssid);
                if (tempConfig != null) {
                    mWifiManager
                            .removeNetwork(tempConfig.networkId);
                }
            }
        });
        builder.setNegativeButton(R.string.cancle,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog,
                            int which) {
                        dialog.dismiss();
                    }
                });
        builder.create();
        builder.show();
    }

    private void createPasswordInputDialog(final AccessPoint item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                WirelessConnectActivity.this, R.style.style_dialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_password_input, null);
        final EditText psEditText = (EditText) view
                .findViewById(R.id.et_password);
        psEditText.setHint(R.string.please_input_wifi_password);
        final EditTextBgToStar toStar = new EditTextBgToStar();
        psEditText.setTransformationMethod(toStar);
        CheckBox chbShowPwd = (CheckBox) view
                .findViewById(R.id.show_password);
        chbShowPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                psEditText.setTransformationMethod(isChecked ?
                        null
                        : toStar);
            }
        });
        builder.setView(view);
        builder.setTitle(item.ssid);
        builder.setPositiveButton(R.string.wifi_connect,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        connectToWIFI(item, psEditText.getText().toString().trim());
                    }
                });

        builder.setNegativeButton(R.string.cancle,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        builder.create();
        AlertDialog al = builder.show();
        final Button btnOK = al.getButton(DialogInterface.BUTTON_POSITIVE);
        btnOK.setEnabled(false);
        psEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnOK.setEnabled(s.length() >= 8);
            }
        });
    }

    private void connectToWIFI(AccessPoint item, String password) {
        WifiConfiguration myWifiConfiguration = createNewWifi(item, password);
        boolean isAddNetWork = addNetwork(myWifiConfiguration);
        logD("connectToWIFI() isAddNetWork" + isAddNetWork);
        logD("connectToWIFI() item.isConnect" + item.isConnect);

        if (item.isConnect != isAddNetWork) {
            item.isConnect = isAddNetWork;
            myHandler.sendEmptyMessage(MSG_UPDATE);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        Button btn = (Button) v;
        btn.setTextColor(hasFocus ? getResources().getColor(
                R.color.focused_color) : Color.WHITE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(mReceiver, mFilter);

        updateAccessPoints();

        try {

            int isONWifi = Settings.Secure.getInt(cr, WIFI_KEY);
            if (isONWifi == 1) {
                myLayout.setBackgroundResource(R.drawable.on);
            } else {
                myLayout.setBackgroundResource(R.drawable.off);// wifi关闭
            }

        } catch (SettingNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(mReceiver);
        mScanner.pause();
    }

    private class ScannerHandler extends Handler {
        private int mRetry = 0;

        void resume() {
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }

        void forceScan() {
            removeMessages(0);
            sendEmptyMessage(0);
        }

        void pause() {
            mRetry = 0;
            removeMessages(0);
        }

        @Override
        public void handleMessage(Message message) {
            if (mWifiManager.startScan()) {
                mRetry = 0;
            } else if (++mRetry >= 3) {
                mRetry = 0;
                Toast.makeText(WirelessConnectActivity.this,
                        R.string.wifi_fail_to_scan, Toast.LENGTH_LONG).show();
                return;
            }
            sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
        }
    }

    private void handleEvent(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                    WifiManager.WIFI_STATE_UNKNOWN));
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
                || WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION
                        .equals(action)
                || WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
            updateAccessPoints();
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            // Ignore supplicant state changes when network is connected
            // TODO: we should deprecate SUPPLICANT_STATE_CHANGED_ACTION and
            // introduce a broadcast that combines the supplicant and network
            // network state change events so the apps dont have to worry about
            // ignoring supplicant state change when network is connected
            // to get more fine grained information.
            SupplicantState state = (SupplicantState) intent.getParcelableExtra(
                    WifiManager.EXTRA_NEW_STATE);
            if (!mConnected.get() && SupplicantState.isHandshakeState(state)) {
                updateConnectionState(WifiInfo.getDetailedStateOf(state));
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo info = (NetworkInfo) intent
                    .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            mConnected.set(info.isConnected());
            updateAccessPoints();
            updateConnectionState(info.getDetailedState());
        } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
            updateConnectionState(null);
        }

        /*
         * else if (WifiManager.ERROR_ACTION.equals(action)) { int errorCode =
         * intent.getIntExtra(WifiManager.EXTRA_ERROR_CODE, 0); switch
         * (errorCode) { case WifiManager.WPS_OVERLAP_ERROR:
         * Toast.makeText(context, R.string.wifi_wps_overlap_error,
         * Toast.LENGTH_SHORT).show(); break; } }
         */
    }

    private void updateWifiState(int state) {
        invalidateOptionsMenu();

        switch (state) {
            case WifiManager.WIFI_STATE_ENABLED:
                mScanner.resume();
                return; // not break, to avoid the call to pause() below
            case WifiManager.WIFI_STATE_ENABLING:
                addMessagePreference(R.string.wifi_starting);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                addMessagePreference(R.string.wifi_empty_list_wifi_off);
                break;
        }

        mLastInfo = null;
        mLastState = null;
        mScanner.pause();
    }

    private void addMessagePreference(int messageId) {
        if (myToast == null) {
            myToast = Toast.makeText(WirelessConnectActivity.this, messageId,
                    Toast.LENGTH_LONG);
        } else {
            myToast.setText(messageId);
        }
        myToast.show();
    }

    /**
     * Shows the latest access points available with supplimental information
     * like the strength of network and the security for it.
     */
    private void updateAccessPoints() {
        final int wifiState = mWifiManager.getWifiState();

        switch (wifiState) {
            case WifiManager.WIFI_STATE_ENABLED:
                // AccessPoints are automatically sorted with TreeSet.
                final Collection<AccessPoint> accessPoints = constructAccessPoints();
                if (!data.isEmpty()) {
                    data.clear();
                }
                for (AccessPoint accessPoint : accessPoints) {
                    data.add(accessPoint);
                }
                myHandler.sendEmptyMessage(MSG_UPDATE);
                break;
            case WifiManager.WIFI_STATE_ENABLING:
                break;
            case WifiManager.WIFI_STATE_DISABLING:
                addMessagePreference(R.string.wifi_is_stopping);
                break;
            case WifiManager.WIFI_STATE_DISABLED:
                addMessagePreference(R.string.wifi_empty_list_wifi_off);
                break;
        }
    }

    /** Returns sorted list of access points */
    private List<AccessPoint> constructAccessPoints() {
        ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();
        /**
         * Lookup table to more quickly update AccessPoints by only considering
         * objects with the correct SSID. Maps SSID -> List of AccessPoints with
         * the given SSID.
         */
        Multimap<String, AccessPoint> apMap = new Multimap<String, AccessPoint>();

        final List<WifiConfiguration> configs = mWifiManager
                .getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                AccessPoint accessPoint = new AccessPoint(
                        WirelessConnectActivity.this, config);
                accessPoint.update(mLastInfo, mLastState);
                accessPoints.add(accessPoint);
                apMap.put(accessPoint.ssid, accessPoint);
            }
        }

        final List<ScanResult> results = mWifiManager.getScanResults();
        if (results != null) {
            for (ScanResult result : results) {
                // Ignore hidden and ad-hoc networks.
                if (result.SSID == null || result.SSID.length() == 0
                        || result.capabilities.contains("[IBSS]")) {
                    continue;
                }

                boolean found = false;
                for (AccessPoint accessPoint : apMap.getAll(result.SSID)) {
                    if (accessPoint.update(result))
                        found = true;
                }
                if (!found) {
                    AccessPoint accessPoint = new AccessPoint(
                            WirelessConnectActivity.this, result);
                    accessPoints.add(accessPoint);
                    apMap.put(accessPoint.ssid, accessPoint);
                }
            }
        }

        // Pre-sort accessPoints to speed preference insertion
        Collections.sort(accessPoints);
        return accessPoints;
    }

    /** A restricted multimap for use in constructAccessPoints */
    private class Multimap<K, V> {
        private HashMap<K, List<V>> store = new HashMap<K, List<V>>();

        /** retrieve a non-null list of values with key K */
        List<V> getAll(K key) {
            List<V> values = store.get(key);
            return values != null ? values : Collections.<V> emptyList();
        }

        void put(K key, V val) {
            List<V> curVals = store.get(key);
            if (curVals == null) {
                curVals = new ArrayList<V>(3);
                store.put(key, curVals);
            }
            curVals.add(val);
        }
    }

    private void updateConnectionState(DetailedState state) {
        /* sticky broadcasts can call this when wifi is disabled */
        if (!mWifiManager.isWifiEnabled()) {
            mScanner.pause();
            return;
        }

        if (state == DetailedState.OBTAINING_IPADDR) {
            mScanner.pause();
        } else {
            mScanner.resume();
        }

        mLastInfo = mWifiManager.getConnectionInfo();
        if (state != null) {
            mLastState = state;
        }
    }

    private WifiConfiguration createNewWifi(AccessPoint item, String Password) {

        String SSID = item.ssid;
        int type = item.security;

        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = this.IsExsits(SSID);

        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        if (type == AccessPoint.SECURITY_NONE) // WIFICIPHER_NOPASS
        {
            // config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (type == AccessPoint.SECURITY_WEP) // WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (type == AccessPoint.SECURITY_EAP
                || type == AccessPoint.SECURITY_PSK) // WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    private WifiConfiguration IsExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private void turnOnWifi() {
        int wifiApState = mWifiManager.getWifiApState();
        if (wifiApState == WifiManager.WIFI_AP_STATE_ENABLING ||
                wifiApState == WifiManager.WIFI_AP_STATE_ENABLED) {
            mWifiManager.setWifiApEnabled(null, false);
        }
        myLayout.setBackgroundResource(R.drawable.on); // wifi打开
        // Settings.Secure.putInt(cr, CABLE_KEY, 0);//
        Settings.Secure.putInt(cr, WIFI_KEY, 1);//
        mWifiManager.setWifiEnabled(true);
    }

    // 添加一个网络并连接
    public boolean addNetwork(WifiConfiguration wcg) {
        int wcgID = mWifiManager.addNetwork(wcg);
        mWifiManager.connect(wcg, mConnectListener);
        return mWifiManager.enableNetwork(wcgID, true);
    }

    private void logD(String msg) {
        Log.d(TAG, msg);
    }
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   