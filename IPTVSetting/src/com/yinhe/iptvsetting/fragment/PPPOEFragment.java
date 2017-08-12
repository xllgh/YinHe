
package com.yinhe.iptvsetting.fragment;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.Secure;
import android.provider.Settings.System;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.EditTextBgToStar;
import com.yinhe.iptvsetting.common.LimitTextWather;
import com.yinhe.iptvsetting.common.NetManager;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.NetManager.DevType;
import com.yinhe.iptvsetting.view.BaseFragment;
import com.yinhe.iptvsetting.view.BaseFragment.OnNetTypeChangeListener;

public class PPPOEFragment extends BaseFragment {

    private NetManager mNetManager;
    private ProgressDialog myProgressDialog = null;
    private ContentResolver mContentResolver;
    private TextView mTvPppoeStatus;
    private EditText etUserName = null;
    private EditText etPassword = null;
    private Button buttonSure = null;
    private Button buttonCancle = null;
    private Button buttonDisconnect = null;
    private LinearLayout linearConnect = null;
    private LinearLayout linearDisConnect = null;

    private int mPppoeStatus = -1;

    private String mSTargetArea;

    public PPPOEFragment() {
        super();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mActivity = getActivity();

        View view;
        mSTargetArea = SystemProperties.get("ro.area");
        if ("suzhou".equals(mSTargetArea)) {
            view = inflater.inflate(R.layout.pppoe_fragment_sz, container,
                    false);
        } else {
            view = inflater.inflate(R.layout.pppoe_fragment, container, false);
        }
        initData(view);
        return view;
    }

    public void setPppoeStatus(int pppoeStatus) {
        mPppoeStatus = pppoeStatus;
    }

    private void initData(View view) {
        mNetManager = NetManager.getInstance();
        mContentResolver = mActivity.getContentResolver();

        myProgressDialog = new ProgressDialog(mActivity);
        linearConnect = (LinearLayout) view.findViewById(R.id.ll_connect);
        linearDisConnect = (LinearLayout) view.findViewById(R.id.disconnect);

        mTvPppoeStatus = (TextView) view.findViewById(R.id.pppoe_status);

        this.etUserName = (EditText) view.findViewById(R.id.et_user_name);
        this.etUserName.addTextChangedListener(new LimitTextWather(mActivity, 36));

        this.etPassword = (EditText) view.findViewById(R.id.et_net_password);
        final EditTextBgToStar toStar = new EditTextBgToStar();
        this.etPassword.setTransformationMethod(toStar);
        this.etPassword.addTextChangedListener(new LimitTextWather(mActivity, 16));

        if ("suzhou".equals(mSTargetArea)) {
            CheckBox chbShowPwd = (CheckBox) view
                    .findViewById(R.id.chb_show_pwd);
            chbShowPwd
                    .setOnCheckedChangeListener(new OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView,
                                boolean isChecked) {
                            // etPassword.setTransformationMethod(isChecked ?
                            // null
                            // : toStar);
                        }
                    });
        }

        etUserName.setText(mNetManager.getPPPoEUserName());
        etPassword.setText(mNetManager.getPPPoEPassword());

        buttonSure = (Button) view.findViewById(R.id.bt_sure);
        buttonSure.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
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

                String userName = etUserName.getText().toString().trim();
                String password = etPassword.getText().toString().trim();

                if (FuncUtil.isNullOrEmpty(userName)
                        || FuncUtil.isNullOrEmpty(password)) {
                    Toast.makeText(mActivity, R.string.please_iuput_righr,
                            Toast.LENGTH_LONG).show();
                    return;
                }

                reObtainNetAddress(OnNetTypeChangeListener.PPPOE_ON);
                mNetManager.startPppoe(userName, password);
                FuncUtil.showToast(mActivity, R.string.info_save_setting);

                mActivity.findViewById(R.id.tv_PPPOE).requestFocus();
            }
        });

        buttonCancle = (Button) view.findViewById(R.id.bt_canle);
        buttonCancle.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                mNetManager.disconnectPppoe();
                mActivity.findViewById(R.id.tv_PPPOE).requestFocus();
            }
        });

        buttonDisconnect = (Button) view.findViewById(R.id.bt_connected);
        buttonDisconnect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mNetManager.disconnectPppoe();
                FuncUtil.showToast(mActivity, R.string.disconnecting);
                mActivity.findViewById(R.id.tv_PPPOE).requestFocus();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        boolean isConnected = mNetManager.isPppoeConnected();

        if (isConnected) {
            mTvPppoeStatus.setVisibility(View.VISIBLE);
            linearConnect.setVisibility(View.GONE);
            linearDisConnect.setVisibility(View.VISIBLE);
        } else {
            mTvPppoeStatus.setVisibility(View.INVISIBLE);
            linearConnect.setVisibility(View.VISIBLE);
            linearDisConnect.setVisibility(View.GONE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        etUserName.setText(mNetManager.getPPPoEUserName());
        etPassword.setText(mNetManager.getPPPoEPassword());

        boolean isPppoeModde = mNetManager.isPppoeMode();

        if (isPppoeModde) {
            if (mPppoeStatus == NetManager.EVENT_CONNECT_SUCCESSED) {
                mTvPppoeStatus.setText(R.string.pppoe_connect);
                mTvPppoeStatus.setVisibility(View.VISIBLE);
                linearConnect.setVisibility(View.GONE);
                linearDisConnect.setVisibility(View.VISIBLE);
            } else if (mPppoeStatus == NetManager.EVENT_CONNECT_FAILED) {
                mTvPppoeStatus.setText(R.string.pppoe_connect_falied);
                mTvPppoeStatus.setVisibility(View.VISIBLE);
                linearConnect.setVisibility(View.VISIBLE);
                linearDisConnect.setVisibility(View.GONE);
            } else if (mPppoeStatus == NetManager.EVENT_CONNECT_FAILED_AUTH_FAIL) {
                mTvPppoeStatus.setText(R.string.pppoe_connect_error);
                mTvPppoeStatus.setVisibility(View.VISIBLE);
                linearConnect.setVisibility(View.VISIBLE);
                linearDisConnect.setVisibility(View.GONE);
            } else if (mPppoeStatus == NetManager.EVENT_CONNECTING) {
                mTvPppoeStatus.setText(R.string.pppoe_connecting);
                mTvPppoeStatus.setVisibility(View.VISIBLE);
                linearConnect.setVisibility(View.GONE);
                linearDisConnect.setVisibility(View.VISIBLE);
            } else {
                mTvPppoeStatus.setText(null);
                mTvPppoeStatus.setVisibility(View.VISIBLE);
                linearConnect.setVisibility(View.VISIBLE);
                linearDisConnect.setVisibility(View.GONE);
            }
        } else {
            mTvPppoeStatus.setVisibility(View.INVISIBLE);
            linearConnect.setVisibility(View.VISIBLE);
            linearDisConnect.setVisibility(View.GONE);
        }
    }

}
