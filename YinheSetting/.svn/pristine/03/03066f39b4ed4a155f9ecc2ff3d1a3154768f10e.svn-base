
package com.yinhe.iptvsetting.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.NetManager;
import com.yinhe.iptvsetting.object.NetAddress;
import com.yinhe.iptvsetting.view.AddressInput;
import com.yinhe.iptvsetting.view.BaseFragment;

public class LANFragment extends BaseFragment {

    private static final String LOG_TAG = "LANFragment";

    private static final int MSG_UPDATE = 0;

    private AddressInput aiIPAddressA = null;
    private AddressInput aiNetmaskA = null;
    private AddressInput aiDefaultGatewayA = null;
    private AddressInput aiDNSApacheA = null;

    private NetManager mNetManager;

    private OnAddressLoadCompletedListener mAddressLoadCompletedListener = null;

    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE:

                    NetAddress netAddress = mNetManager.getNetAddress();

                    if (netAddress != null) {
                        aiIPAddressA.setAddress(netAddress.getIPaddress());
                        aiNetmaskA.setAddress(netAddress.getNetMask());
                        aiDefaultGatewayA.setAddress(netAddress.getGateWay());
                        aiDNSApacheA.setAddress(netAddress.getDNS1());
                    } else {
                        aiIPAddressA.setAddress(FuncUtil.STR_EMPTY);
                        aiNetmaskA.setAddress(FuncUtil.STR_EMPTY);
                        aiDefaultGatewayA.setAddress(FuncUtil.STR_EMPTY);
                        aiDNSApacheA.setAddress(FuncUtil.STR_EMPTY);
                    }

                    // aiIPAddressA.setAddress("172.16.147.2");
                    // aiNetmaskA.setAddress("255.255.0.0");
                    // aiDefaultGatewayA.setAddress("172.16.0.255");
                    // aiDNSApacheA.setAddress("172.16.0.111");

                    if (mAddressLoadCompletedListener != null) {
                        mAddressLoadCompletedListener.OnAddressLoadCompleted();
                    }
                    break;
            }
        }
    };

    public LANFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lan_fragment, container, false);
        mActivity = getActivity();
        initData(view);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        logD("onResume()");

        mHandler.sendEmptyMessage(MSG_UPDATE);
    }

    private void initData(View view) {
        mNetManager = NetManager.getInstance();

        this.aiIPAddressA = (AddressInput) view
                .findViewById(R.id.et_ip_address);
        this.aiIPAddressA.setEnabled(false);

        this.aiNetmaskA = (AddressInput) view.findViewById(R.id.et_netmask);
        this.aiNetmaskA.setEnabled(false);

        this.aiDefaultGatewayA = (AddressInput) view
                .findViewById(R.id.et_default_gateway);
        this.aiDefaultGatewayA.setEnabled(false);

        this.aiDNSApacheA = (AddressInput) view
                .findViewById(R.id.et_DNS_apache);
        this.aiDNSApacheA.setEnabled(false);

        Button btnSure = (Button) view.findViewById(R.id.bt_sure);
        btnSure.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!mNetManager.isEthernetStateEnabled()) {
                    FuncUtil.showToast(mActivity,
                            R.string.warn_wire_disconnected);
                    mActivity.findViewById(R.id.button_on).requestFocus();
                    return;
                }

                if (!mNetManager.isEthernetOn()) {
                    FuncUtil.showToast(mActivity, R.string.eth_phy_link_down_check);
                    return;
                }

                boolean ret = turnOnStatic();
                if (ret) {
                    reObtainNetAddress(OnNetTypeChangeListener.LAN_ON);
                }
                mActivity.findViewById(R.id.tv_LAN).requestFocus();
            }
        });
        Button btnCancle = (Button) view.findViewById(R.id.bt_cancle);
        btnCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // 点击取消按钮后，光标回到标签，
                mActivity.findViewById(R.id.tv_LAN).requestFocus();
            }
        });
    }

    /**
     * 【确定】按钮点击之后，IP地址保存。
     */
    private boolean turnOnStatic() {

        String ipaddr = aiIPAddressA.getAddress();
        String gateway = aiDefaultGatewayA.getAddress();
        String mask = aiNetmaskA.getAddress();
        String dns1 = aiDNSApacheA.getAddress();

        if (FuncUtil.isNullOrEmpty(ipaddr)
                || FuncUtil.isNullOrEmpty(gateway)
                || FuncUtil.isNullOrEmpty(mask)
                || FuncUtil.isNullOrEmpty(dns1)) {
            FuncUtil.showToast(mActivity, R.string.collocate_message_is_wrong);
            aiIPAddressA.editTextRequestFocus();
            return false;
        }
        mNetManager.startStaticIP(ipaddr, gateway, mask, dns1);
        FuncUtil.showToast(mActivity, R.string.info_save_setting);
        return true;
    }

    /**
     * 设置地址获取结束监听器。
     * 
     * @param onAddressLoadCompletedListener
     */
    public void setOnAddressLoadCompletedListener(
            OnAddressLoadCompletedListener onAddressLoadCompletedListener) {
        mAddressLoadCompletedListener = onAddressLoadCompletedListener;
    }

    public interface OnAddressLoadCompletedListener {
        void OnAddressLoadCompleted();
    }

    private void logD(String msg) {
        Log.d(LOG_TAG, msg);
    }
}
