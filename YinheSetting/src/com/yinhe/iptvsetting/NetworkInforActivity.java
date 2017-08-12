
package com.yinhe.iptvsetting;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.NetManager;

public class NetworkInforActivity extends Activity implements OnClickListener {
    private static final String TAG = "NetworkDetection";
    private static final String ROUTER_IP = "www.baidu.com";
    private static final long NETWORK_STATUS_UPDATE_DELAY = 2000;

    private boolean isConnected = false;
    private boolean isEthernet = false;
    private boolean isWAN = false;
    private boolean isStopped = false;
    private boolean isDetecting = false;

    private static final int MSG_INITIA_UI_STATUS = 0;
    private static final int MSG_UPDATE_NETWORK_TYPE = 1;
    private static final int MSG_UPDATE_NETWORK_STATE = 2;

    private NetManager mNetManager;
    private Toast mToast;
    private TextView mTvNetwork_type;
    private TextView mTvNetwork_state;
    private TextView mTvIPAddress;
    private Button mBtnDetect;
    private String mIPAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.network_infor);
        mNetManager = NetManager.createNetManagerInstance(this);
        mTvNetwork_type = (TextView) findViewById(R.id.tv_network_type);
        mTvNetwork_state = (TextView) findViewById(R.id.tv_network_state);
        mTvIPAddress = (TextView) findViewById(R.id.tv_ip_address);

        mBtnDetect = (Button) findViewById(R.id.bt_ensure);
        mBtnDetect.setOnClickListener(this);

        isDetecting = true;
        new PingThread().start();
    }

    private class PingThread extends Thread {

        @Override
        public void run() {
            super.run();
            ObatainNetworkState();
            isDetecting = false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStopped = false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop");
        isStopped = true;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case MSG_INITIA_UI_STATUS:
                    mTvNetwork_type.setText(R.string.router_test);
                    mTvNetwork_state.setText(R.string.router_test);
                    mTvIPAddress.setText(R.string.router_test);
                    break;
                case MSG_UPDATE_NETWORK_TYPE:
                    if (isConnected) {
                        mTvNetwork_type.setText(isEthernet ? R.string.network_type_eth
                                : R.string.network_type_wifi);
                    } else {
                        mTvNetwork_type.setText(R.string.network_state_disconnected);
                        mTvNetwork_state.setText(R.string.network_state_disconnected);
                    }
                    mTvIPAddress.setText(mIPAddress);
                    break;
                case MSG_UPDATE_NETWORK_STATE:
                    mTvNetwork_state.setText(isWAN ? R.string.network_state_internet
                            : R.string.network_state_lan);
                    break;
                default:
                    break;
            }
        }
    };

    private void ObatainNetworkState() {
        Log.d(TAG, "ObatainNetworkState start : " + isStopped);
        if (isStopped) {
            return;
        }
        isConnected = false;
        mIPAddress = "";
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            return;
        }

        NetworkInfo[] infos = cm.getAllNetworkInfo();
        if (infos != null) {
            for (NetworkInfo ni : infos) {
                if (ni.isConnected()) {
                    isConnected = true;
                    if (FuncUtil.isNullOrEmpty(ni.getExtraInfo())) {
                        mIPAddress = mNetManager.obataintNetAddress().getIPaddress();
                        isEthernet = true;
                        break;
                    } else {
                        mIPAddress = mNetManager.getWifiIpAddresses();
                        isEthernet = false;
                    }
                }
            }
        }
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_NETWORK_TYPE, NETWORK_STATUS_UPDATE_DELAY);
        if (!isConnected) {
            return;
        }
        detectInternet(ROUTER_IP, 4, 0.1);
    }

    public void detectInternet(String remoteIpAddress, int pingTimes, double timeOut) {
        Log.d(TAG, "detectInternet start : " + isStopped);
        if (isStopped) {
            return;
        }
        long startTime = System.currentTimeMillis();
        String pingCommand = "ping" + " -c " + pingTimes + " -i " + timeOut + " " + remoteIpAddress;

        Runtime runtime = Runtime.getRuntime();
        Process proc = null;
        try {
            proc = runtime.exec(pingCommand);
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    proc.getInputStream()));
            String inline = br.readLine();
            Log.d(TAG, "detectInternet string result : " + inline);
            isWAN = !FuncUtil.isNullOrEmpty(inline);
            br.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        long fillTime = System.currentTimeMillis() - startTime - NETWORK_STATUS_UPDATE_DELAY;
        mHandler.sendEmptyMessageDelayed(MSG_UPDATE_NETWORK_STATE, (fillTime > 0) ? 0
                : Math.abs(fillTime));
    }

    @Override
    public void onClick(View v) {
        if (isDetecting) {
            if (mToast == null) {
                mToast = Toast.makeText(this, R.string.please_wait, Toast.LENGTH_SHORT);
            }
            mToast.show();
            return;
        }
        isDetecting = true;
        mHandler.sendEmptyMessage(MSG_INITIA_UI_STATUS);
        new PingThread().start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        NetManager.destroyNetManagerInstance();
    }
}
