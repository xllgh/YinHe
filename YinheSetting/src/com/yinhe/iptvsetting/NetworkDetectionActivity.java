
package com.yinhe.iptvsetting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.LogUtil;
import com.yinhe.iptvsetting.common.NetManager;
import com.yinhe.iptvsetting.common.PreferencesUtils;
import com.yinhe.iptvsetting.view.NetWorkSpeedTestDialog;

public class NetworkDetectionActivity extends Activity implements OnClickListener {

    private LogUtil mLogUtil = new LogUtil(NetworkDetectionActivity.class);
    private NetManager mNetManager;

    private final static int INDEX_TYPE = 0;
    private final static int INDEX_IP = INDEX_TYPE + 1;
    private final static int INDEX_STATE = INDEX_IP + 1;
    private final static int INDEX_OTT = INDEX_STATE + 1;
    private final static int INDEX_DNS = INDEX_OTT + 1;
    private final static int INDEX_NTP = INDEX_DNS + 1;

    private static final int TYPE_DEFAULT = -1;
    private static final int TYPE_DETECTING = 0;
    private static final int TYPE_OK = 1;
    private static final int TYPE_WARN = 2;
    private static final int TYPE_NG = 3;

    // hwtv.js165.com
    private static final String DEFAULT_ADDRESS_OTT = "hwtv.js165.com";
    private static final String DEFAULT_ADDRESS_NTP_MAIN = "122.96.53.87";
    private static final String DEFAULT_ADDRESS_NTP_SECOND = "122.96.53.88";

    private static final int MSG_UPDATE_UI = 0;

    private static final String ROUTER_IP = "www.baidu.com";
    private static int PING_TIMES = 4;
    private static double TIME_OUT = 1;

    private static final String SP_KEY_OTT = "ott_server_address";
    // private static final String SP_KEY_DNS = "dns_server_address";
    private static final String SP_KEY_NTP_MAIN = "ntp_server_address_main";
    private static final String SP_KEY_NTP_SECOND = "ntp_server_address_second";

    private int mNetworkTypeStatus = TYPE_DETECTING;
    private int mNetworkWanStatus = TYPE_DEFAULT;
    private int mNetworkIpStatus = TYPE_DEFAULT;
    private int mNetworkOTTStatus = TYPE_DEFAULT;
    private int mNetworkDNSStatus = TYPE_DEFAULT;
    private int mNetworkNTPStatus = TYPE_DEFAULT;

    private String mStrOTTServer;
    private String mStrDNSServer;
    private String mStrNTPServerMain;
    private String mStrNTPServerSecond;
    private String mCurrentNtpServer;

    private boolean isConnected = false;
    private boolean isEthernet = false;
    private boolean isDetecting = false;
    private boolean isStopped = false;

    private String mIPAddress;

    private Button mBtnDownload;
    private Button mBtnRedetect;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE_UI:
                    mLogUtil.d("handler mNetworkTypeStatus = " + mNetworkTypeStatus);
                    mLogUtil.d("handler mNetworkWanStatus = " + mNetworkWanStatus);
                    mLogUtil.d("handler mNetworkOTTStatus = " + mNetworkOTTStatus);
                    mLogUtil.d("handler mNetworkDNSStatus = " + mNetworkDNSStatus);
                    mLogUtil.d("handler mNetworkNTPStatus = " + mNetworkNTPStatus);

                    boolean enable = mNetworkWanStatus == TYPE_OK;
                    mBtnDownload.setEnabled(enable);
                    mBtnDownload.setFocusable(enable);
                    mBtnDownload.setTextColor(enable ? Color.WHITE : Color.GRAY);

                    mBaseAdapter.notifyDataSetChanged();
                    
                    if (TYPE_NG == mNetworkDNSStatus) {
                        sendBroadcast(new Intent("com.yinhe.iptvsetting.ACTION_DNS_UNREACHABLE"));
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private BaseAdapter mBaseAdapter = new BaseAdapter() {
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.network_info_item,
                        null);
                viewHolder = new ViewHolder();
                viewHolder.mIvState = (ImageView) convertView
                        .findViewById(R.id.iv_network_info_state);
                viewHolder.mTvLabel = (TextView) convertView
                        .findViewById(R.id.tv_network_info_label);
                viewHolder.mTvContent = (TextView) convertView
                        .findViewById(R.id.tv_network_info_content);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            switch (position) {
                case INDEX_TYPE:
                    setImageViewState(viewHolder.mIvState, mNetworkTypeStatus);
                    viewHolder.mTvLabel.setText(R.string.network_type);
                    if (isConnected) {
                        viewHolder.mTvContent.setText(isEthernet ? R.string.network_type_eth
                                : R.string.network_type_wifi);
                    } else {
                        viewHolder.mTvContent.setText(R.string.network_state_disconnected);
                    }
                    break;
                case INDEX_IP:
                    setImageViewState(viewHolder.mIvState, mNetworkIpStatus);
                    viewHolder.mTvLabel.setText(R.string.ip_address);
                    viewHolder.mTvContent.setText(mIPAddress);
                    break;
                case INDEX_STATE:
                    setImageViewState(viewHolder.mIvState, mNetworkWanStatus);
                    viewHolder.mTvLabel.setText(R.string.network_state);
                    if (isConnected) {
                        if (mNetworkWanStatus == TYPE_DETECTING) {
                            viewHolder.mTvContent.setText(R.string.router_test);
                        } else if (mNetworkWanStatus == TYPE_OK) {
                            viewHolder.mTvContent.setText(R.string.network_state_internet);
                        } else if (mNetworkWanStatus == TYPE_WARN) {
                            viewHolder.mTvContent.setText(R.string.network_state_lan);
                        }
                    } else {
                        viewHolder.mTvContent.setText(R.string.network_state_disconnected);
                    }
                    break;
                case INDEX_OTT:
                    setImageViewState(viewHolder.mIvState, mNetworkOTTStatus);
                    viewHolder.mTvLabel.setText(R.string.ott_system_address);
                    viewHolder.mTvContent.setText(mStrOTTServer);
                    break;
                case INDEX_DNS:
                    setImageViewState(viewHolder.mIvState, mNetworkDNSStatus);
                    viewHolder.mTvLabel.setText(R.string.dns_address);
                    viewHolder.mTvContent.setText(mStrDNSServer);
                    break;
                case INDEX_NTP:
                    setImageViewState(viewHolder.mIvState, mNetworkNTPStatus);
                    viewHolder.mTvLabel.setText(R.string.ntp_address_main);
                    viewHolder.mTvContent.setText(mCurrentNtpServer);
                    break;
                default:
                    break;
            }

            Drawable drawable = viewHolder.mIvState.getBackground();
            if (drawable instanceof AnimationDrawable) {
                AnimationDrawable ad = (AnimationDrawable) drawable;
                if (ad.isRunning()) {
                    ad.stop();
                }
                ad.start();
            }
            return convertView;
        }

        private void setImageViewState(ImageView iv, int type) {
            switch (type) {
                case TYPE_DEFAULT:
                    iv.setVisibility(View.INVISIBLE);
                    break;
                case TYPE_DETECTING:
                    iv.setVisibility(View.VISIBLE);
                    iv.setBackgroundResource(R.drawable.detecting);
                    break;
                case TYPE_OK:
                    iv.setVisibility(View.VISIBLE);
                    iv.setBackgroundResource(R.drawable.state_ok);
                    break;
                case TYPE_WARN:
                    iv.setVisibility(View.VISIBLE);
                    iv.setBackgroundResource(R.drawable.state_warn);
                    break;
                case TYPE_NG:
                    iv.setVisibility(View.VISIBLE);
                    iv.setBackgroundResource(R.drawable.state_ng);
                    break;
                default:
                    break;
            }
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public int getCount() {
            return INDEX_NTP + 1;
        }
    };

    class ViewHolder {
        ImageView mIvState;
        TextView mTvLabel;
        TextView mTvContent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_info);
        mNetManager = NetManager.createNetManagerInstance(this);
        initView();
        startDetect();
    }

    private void initView() {
        getConfigration();

        ListView listView = (ListView) findViewById(R.id.lv_network_info);
        listView.setAdapter(mBaseAdapter);
        listView.setFocusable(false);
        Button btnConfig = (Button) findViewById(R.id.btn_nw_config);
        mBtnRedetect = (Button) findViewById(R.id.btn_nw_detect);
        mBtnDownload = (Button) findViewById(R.id.btn_nw_download);
        btnConfig.setOnClickListener(this);
        mBtnRedetect.setOnClickListener(this);
        mBtnDownload.setOnClickListener(this);
    }

    private void getConfigration() {
        String nptserver1 = Settings.Secure.getString(
                getContentResolver(), Settings.Secure.NTP_SERVER);
        String nptserver2 = Settings.Secure.getString(
                getContentResolver(), Settings.Secure.NTP_SERVER2);
        mStrOTTServer = PreferencesUtils.getString(this, SP_KEY_OTT, DEFAULT_ADDRESS_OTT);
        mStrNTPServerMain = FuncUtil.isNullOrEmpty(nptserver1) ?
                DEFAULT_ADDRESS_NTP_MAIN : nptserver1;
        mStrNTPServerSecond = FuncUtil.isNullOrEmpty(nptserver2) ?
                DEFAULT_ADDRESS_NTP_SECOND : nptserver2;
        mCurrentNtpServer = mStrNTPServerMain;
    }

    private void createConfigrationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                this, R.style.style_dialog);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_network_config, null);

        final EditText etOtt = (EditText) view
                .findViewById(R.id.et_nw_ott);
        final EditText etDNS = (EditText) view
                .findViewById(R.id.et_nw_dns);
        final EditText etNTPMain = (EditText) view
                .findViewById(R.id.et_nw_ntp_main);
        final EditText etNTPSecond = (EditText) view
                .findViewById(R.id.et_nw_ntp_second);

        etOtt.setText(mStrOTTServer);
        etDNS.setText(mStrDNSServer);
        etNTPMain.setText(mStrNTPServerMain);
        etNTPSecond.setText(mStrNTPServerSecond);

        builder.setView(view);
        builder.setTitle(R.string.network_info_config);
        builder.setPositiveButton(R.string.btn_save,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String ott = etOtt.getText().toString();
                        String dns = etDNS.getText().toString();
                        String ntpMain = etNTPMain.getText().toString();
                        String ntpSecond = etNTPSecond.getText().toString();

                        if (isAddressAvalible(ott)) {
                        } else {
                            return;
                        }

                        if (isAddressAvalible(dns)) {
                        } else {
                            return;
                        }

                        if (isAddressAvalible(ntpMain)) {
                        } else {
                            return;
                        }

                        if (isAddressAvalible(ntpSecond)) {
                        } else {
                            return;
                        }
                        mStrOTTServer = ott;
                        // mStrDNSServer = dns;
                        mStrNTPServerMain = ntpMain;
                        mStrNTPServerSecond = ntpSecond;

                        mCurrentNtpServer = mStrNTPServerMain;

                        PreferencesUtils.putString(NetworkDetectionActivity.this, SP_KEY_OTT, ott);
                        // PreferencesUtils.putString(NetworkDetectionActivity.this,
                        // SP_KEY_DNS, dns);
                        PreferencesUtils.putString(NetworkDetectionActivity.this, SP_KEY_NTP_MAIN,
                                ntpMain);
                        PreferencesUtils.putString(NetworkDetectionActivity.this,
                                SP_KEY_NTP_SECOND, ntpSecond);

                        Settings.Secure.putString(getContentResolver(),
                                Settings.Secure.NTP_SERVER, ntpMain);
                        Settings.Secure.putString(getContentResolver(),
                                Settings.Secure.NTP_SERVER2, ntpSecond);

                        mBaseAdapter.notifyDataSetChanged();

                        startDetect();
                    }
                });
        builder.setNegativeButton(R.string.btn_cancel, null);
        AlertDialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        dialog.show();
        // final Button btnOK =
        // mConfigDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        // btnOK.setEnabled(false);
        // psEditText.addTextChangedListener(new TextWatcher() {
        // @Override
        // public void onTextChanged(CharSequence s, int start, int before, int
        // count) {
        // }
        //
        // @Override
        // public void beforeTextChanged(CharSequence s, int start, int count,
        // int after) {
        // }
        //
        // @Override
        // public void afterTextChanged(Editable s) {
        // btnOK.setEnabled(s.length() >= 5);
        // }
        // });
    }

    private boolean isAddressAvalible(String address) {
        // TODO
        return true;
    }

    private void startDetect() {
        if (isDetecting) {
            FuncUtil.showToast(this, R.string.please_wait);
        } else {
            new DetectThread().start();
        }
    }

    private class DetectThread extends Thread {
        @Override
        public void run() {
            super.run();
            isDetecting = true;
            // init
            mNetworkTypeStatus = TYPE_DETECTING;
            mNetworkWanStatus = TYPE_DEFAULT;
            mNetworkIpStatus = TYPE_DEFAULT;
            mNetworkOTTStatus = TYPE_DEFAULT;
            mNetworkDNSStatus = TYPE_DEFAULT;
            mNetworkNTPStatus = TYPE_DEFAULT;
            mHandler.sendEmptyMessage(MSG_UPDATE_UI);
            detectNetworkState();
            if (!isConnected || isStopped) {
                isDetecting = false;
                return;
            }
            detectInternet();
            if (isStopped) {
                isDetecting = false;
                return;
            }
            detectOTT();
            if (isStopped) {
                isDetecting = false;
                return;
            }
            detectDNS();
            if (isStopped) {
                isDetecting = false;
                return;
            }
            detectNTP();
            isDetecting = false;
        }
    }

    private void detectNetworkState() {
        mLogUtil.d("detectNetworkState");
        if (isStopped) {
            return;
        }
        long startTime = System.currentTimeMillis();
        isConnected = false;
        mIPAddress = "";
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        obatain: {
            if (cm == null) {
                break obatain;
            }

            NetworkInfo[] infos = cm.getAllNetworkInfo();
            if (infos == null) {
                break obatain;
            }
            mStrDNSServer = null;
            for (NetworkInfo ni : infos) {
                if (ni.isConnected()) {
                    isConnected = true;
                    if (FuncUtil.isNullOrEmpty(ni.getExtraInfo())) {
                        mIPAddress = mNetManager.obataintNetAddress().getIPaddress();
                        mStrDNSServer = mNetManager.obataintNetAddress().getDNS1();
                        isEthernet = true;
                        break obatain;
                    } else {
                        mIPAddress = mNetManager.getWifiIpAddresses();
                        mStrDNSServer = mNetManager.getWifiDNS();
                        isEthernet = false;
                    }
                }
            }
        }

        if (!isConnected) {
            mNetworkTypeStatus = TYPE_NG;
            mNetworkWanStatus = TYPE_DEFAULT;
            mNetworkIpStatus = TYPE_DEFAULT;
            mNetworkOTTStatus = TYPE_DEFAULT;
            mNetworkDNSStatus = TYPE_DEFAULT;
            mNetworkNTPStatus = TYPE_DEFAULT;
        } else {
            mNetworkTypeStatus = TYPE_OK;
            mNetworkIpStatus = TYPE_OK;
            mNetworkWanStatus = TYPE_DETECTING;
        }
        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
    }

    private void detectInternet() {
        mLogUtil.d("detectInternet");
        mNetworkWanStatus = TYPE_DETECTING;
        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
        long startTime = System.currentTimeMillis();
        String pingCommand = "ping" + " -c " + PING_TIMES + " -i " + TIME_OUT + " " + ROUTER_IP;

        boolean isOK = false;
        Runtime runtime = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = runtime.exec(pingCommand);
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            String inline = br.readLine();
            while (!FuncUtil.isNullOrEmpty(inline)) {
                mLogUtil.d("detectInternet readLine = " + inline);
                if (inline.contains("time=")) {
                    isOK = true;
                    break;
                }
                inline = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sleepTwoS(startTime);
        mNetworkWanStatus = isOK ? TYPE_OK : TYPE_WARN;
        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
    }

    private void detectOTT() {
        mLogUtil.d("detectOTT OTTServer = " + mStrOTTServer);
        if (FuncUtil.isNullOrEmpty(mStrOTTServer)) {
            return;
        }
        mNetworkOTTStatus = TYPE_DETECTING;
        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
        long startTime = System.currentTimeMillis();
        // String pingCommand = "ping" + " -c " + PING_TIMES + " -i " + TIME_OUT
        // + " " + mStrOTTServer;
        //
        // Runtime runtime = Runtime.getRuntime();
        // Process proc = null;
        // try {
        // proc = runtime.exec(pingCommand);
        // BufferedReader br = new BufferedReader(new InputStreamReader(
        // proc.getInputStream()));
        // boolean isOK = false;
        // String inline = br.readLine();
        // while (!FuncUtil.isNullOrEmpty(inline)) {
        // mLogUtil.d("detectOTT readLine = " + inline);
        // if (inline.contains("time=")) {
        // isOK = true;
        // break;
        // }
        // inline = br.readLine();
        // }
        // mNetworkOTTStatus = isOK ? TYPE_OK : TYPE_NG;
        // br.close();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        boolean isOK = false;
        try {
            InetAddress ip = InetAddress.getByName(mStrOTTServer);
            mLogUtil.d("getByName:" + ip);
            isOK = true;
        } catch (IOException e) {
            mLogUtil.e("openStream error:" + e.getMessage());
        }
        sleepTwoS(startTime);
        mNetworkOTTStatus = isOK ? TYPE_OK : TYPE_NG;
        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
    }

    private void detectDNS() {
        mLogUtil.d("detectDNS DNSServer = " + mStrDNSServer);
        if (FuncUtil.isNullOrEmpty(mStrDNSServer)) {
            return;
        }
        mNetworkDNSStatus = TYPE_DETECTING;
        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
        long startTime = System.currentTimeMillis();
        boolean isOK = false;
        String pingCommand = "ping" + " -c " + PING_TIMES + " -i " + TIME_OUT + " " + mStrDNSServer;

        Runtime runtime = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = runtime.exec(pingCommand);
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            String inline = br.readLine();
            while (!FuncUtil.isNullOrEmpty(inline)) {
                mLogUtil.d("detectDNS readLine = " + inline);
                if (inline.contains("time=")) {
                    isOK = true;
                    break;
                }
                inline = br.readLine();
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        sleepTwoS(startTime);
        mNetworkDNSStatus = isOK ? TYPE_OK : TYPE_NG;
        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
    }

    private void detectNTP() {
        mNetworkNTPStatus = TYPE_DETECTING;
        mCurrentNtpServer = mStrNTPServerMain;
        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
        long startTime = System.currentTimeMillis();
        boolean isOK = false;
        isOK = FuncUtil.getNtpTime(mCurrentNtpServer);
        if (!isOK) {
            mCurrentNtpServer = mStrNTPServerSecond;
            mHandler.sendEmptyMessage(MSG_UPDATE_UI);
            isOK = FuncUtil.getNtpTime(mCurrentNtpServer);
        }
        sleepTwoS(startTime);
        mNetworkNTPStatus = isOK ? TYPE_OK : TYPE_NG;
        mHandler.sendEmptyMessage(MSG_UPDATE_UI);
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStopped = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isStopped = true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetManager.destroyNetManagerInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_nw_config:
                if (isDetecting) {
                    FuncUtil.showToast(NetworkDetectionActivity.this, R.string.please_wait);
                } else {
                    createConfigrationDialog();
                }
                break;
            case R.id.btn_nw_detect:
                startDetect();
                break;
            case R.id.btn_nw_download:
                NetWorkSpeedTestDialog dialog = new NetWorkSpeedTestDialog(this,
                        R.style.style_dialog);
                dialog.show();
                dialog.setOnCancelListener(new OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        mBtnRedetect.requestFocus();
                    }
                });
                dialog.setOnDismissListener(new OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mBtnRedetect.requestFocus();
                    }
                });
                break;
            default:
                break;
        }
    }

    private void sleepTwoS(long start) {
        long delayTime = 2000 - (System.currentTimeMillis() - start);
        if (delayTime > 0) {
            mLogUtil.d("sleepTwoS delay " + delayTime);
            SystemClock.sleep(delayTime);
        }
    }

}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                       