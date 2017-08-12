
package com.yinhe.iptvsetting.view;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.IRouterSetting;
import com.yinhe.iptvsetting.common.LogUtil;
import com.yinhe.iptvsetting.common.RouterSetting;
import com.yinhe.iptvsetting.common.RouterSetting.IRouterSettingCallback;
import com.yinhe.iptvsetting.common.RouterSetting.RequestResult;

public class RouteAwifiFragment extends Fragment implements OnClickListener, IRouterSettingCallback {

    private LogUtil mLogUtil = new LogUtil("RouteAwifiFragment");

    private Activity mActivity = null;
    private IRouterSetting mRouterSetting;

    private EditText mEtSSID = null;
    private EditText mEtPWD = null;
    private EditText mEtName = null;
    private EditText mEtChannel = null;

    private Button mBtnSure = null;
    private Button mBtnCancle = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.route_set_awifi, container, false);
        initView(view);
        mActivity = getActivity();
        return view;
    }

    private void initView(View view) {
        mEtSSID = (EditText) view.findViewById(R.id.et_route_set_awifi_ssid);
        mEtPWD = (EditText) view.findViewById(R.id.et_route_set_awifi_pwd);
        mEtName = (EditText) view.findViewById(R.id.et_route_set_awifi_name);
        mEtChannel = (EditText) view.findViewById(R.id.et_route_set_awifi_channel);
        mEtName.setText("zan bu zhi chi");

        mBtnSure = (Button) view.findViewById(R.id.btn_sure);
        mBtnSure.setOnClickListener(this);
        mBtnCancle = (Button) view.findViewById(R.id.btn_cancel);
        mBtnCancle.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLogUtil.d("onResume");
        mRouterSetting = RouterSetting.getInstance(this);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        mLogUtil.d("onHiddenChanged " + hidden);
        if (!hidden) {
            mRouterSetting = RouterSetting.getInstance(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                String ssid = mEtSSID.getEditableText().toString();
                String pwd = mEtPWD.getEditableText().toString();
                String name = mEtName.getEditableText().toString();
                String channel = mEtChannel.getEditableText().toString();
                if (FuncUtil.isNullOrEmpty(ssid) || FuncUtil.isNullOrEmpty(pwd)) {
                    FuncUtil.showToast(mActivity, "ssid or pwd is null!");
                    return;
                }
                mRouterSetting.setWifi(ssid, pwd, "", "");
                break;
            case R.id.btn_cancel:
                break;
            default:
                break;
        }
    }

    @Override
    public void onRequestFinish(int requestType, RequestResult requestResult) {
        if (requestType != RouterSetting.TYPE_SETWIFI) {
            mLogUtil.e("onRequestFinish requestType != RouterSetting.TYPE_SETWIFI" + ", type = "
                    + requestType);
            return;
        }

        if (requestResult == null) {
            mLogUtil.e("onRequestFinish requestResult is null!");
            mHandler.sendEmptyMessage(MSG_SET_AWIFI_FAILED);
            return;
        }

        if (RequestResult.RESULT_SUCCESS.equals(requestResult.getResult())) {
            mHandler.sendEmptyMessage(MSG_SET_AWIFI_SUCCESS);
        } else {
            Message msg = new Message();
            msg.what = MSG_SET_AWIFI_FAILED;
            msg.obj = requestResult.getMsg();
            mHandler.sendMessage(msg);
        }
    }

    private final static int MSG_SET_AWIFI_FAILED = 0;
    private final static int MSG_SHOW_PROGRESS_DIALOG = 1;
    private final static int MSG_SET_AWIFI_SUCCESS = 2;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SET_AWIFI_FAILED:
                    FuncUtil.showToast(mActivity,
                            getString(R.string.hint_setup_failed)
                                    + (msg.obj == null ? "" : " : " + (String) msg.obj));
                    break;
                case MSG_SHOW_PROGRESS_DIALOG:
                    break;
                case MSG_SET_AWIFI_SUCCESS:
                    FuncUtil.showToast(mActivity, R.string.hint_setup_success);
                    break;
                default:
                    break;
            }
        };
    };
}
