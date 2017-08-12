
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

public class RouteUserFragment extends Fragment implements OnClickListener,
        IRouterSettingCallback {

    private LogUtil mLogUtil = new LogUtil("RouteUserFragment");

    private Activity mActivity = null;

    private IRouterSetting mRouterSetting;

    private EditText mEtUserName = null;
    private EditText mEtPasswordOld = null;
    private EditText mEtPasswordNew = null;

    private Button mBtnSure = null;
    private Button mBtnCancle = null;

    private final static int MSG_GET_LOGIN_NAME_SUCCESS = 1;
    private final static int MSG_GET_LOGIN_NAME_FAILED = 2;
    private final static int MSG_GET_SET_PWD_SUCCESS = 3;
    private final static int MSG_GET_SET_PWD_FAILED = 4;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_GET_LOGIN_NAME_SUCCESS:
                    FuncUtil.showToast(mActivity, R.string.hint_get_info_success);
                    String user = (String) msg.obj;
                    mEtUserName.setText(FuncUtil.isNullOrEmpty(user) ? "user" : user);
                    break;
                case MSG_GET_LOGIN_NAME_FAILED:
                    FuncUtil.showToast(mActivity,
                            getString(R.string.hint_get_info_failed)
                                    + (msg.obj == null ? "" : " : " + (String) msg.obj));
                    break;
                case MSG_GET_SET_PWD_SUCCESS:
                    FuncUtil.showToast(mActivity, R.string.hint_setup_success);
                    break;
                case MSG_GET_SET_PWD_FAILED:
                    FuncUtil.showToast(mActivity,
                            getString(R.string.hint_setup_failed)
                                    + (msg.obj == null ? "" : " : " + (String) msg.obj));
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.route_set_user, container, false);
        initView(view);
        mActivity = getActivity();
        return view;
    }

    private void initView(View view) {

        mEtUserName = (EditText) view.findViewById(R.id.et_route_user_name);
        mEtPasswordOld = (EditText) view.findViewById(R.id.et_route_password_old);
        mEtPasswordNew = (EditText) view.findViewById(R.id.et_route_password_new);

        String userName = /* SettingsServiceBrigde.getPPPoEUserName() */"";
        String password = /* SettingsServiceBrigde.getPPPoEPassword() */"";
        mEtUserName.setText(FuncUtil.isNullOrEmpty(userName) ? FuncUtil.STR_EMPTY : userName);
        mEtPasswordOld.setText(FuncUtil.isNullOrEmpty(password) ? FuncUtil.STR_EMPTY : password);

        mBtnSure = (Button) view.findViewById(R.id.btn_sure);
        mBtnCancle = (Button) view.findViewById(R.id.btn_cancel);
        mBtnSure.setOnClickListener(this);
        mBtnCancle.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        mLogUtil.d("onResume");
        mRouterSetting = RouterSetting.getInstance(this);
        mRouterSetting.getLoginName();
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
    public void onRequestFinish(int requestType, RequestResult requestResult) {
        switch (requestType) {
            case RouterSetting.TYPE_GETLOGINNAME:
                if (requestResult == null) {
                    mLogUtil.e("onRequestFinish requestResult is null!");
                    mHandler.sendEmptyMessage(MSG_GET_LOGIN_NAME_FAILED);
                    return;
                }
                if (RequestResult.RESULT_SUCCESS.equals(requestResult.getResult())) {
                    Message msg = new Message();
                    msg.what = MSG_GET_LOGIN_NAME_SUCCESS;
                    msg.obj = requestResult.getData();
                    mHandler.sendMessage(msg);
                } else {
                    Message msg = new Message();
                    msg.what = MSG_GET_LOGIN_NAME_FAILED;
                    msg.obj = requestResult.getMsg();
                    mHandler.sendMessage(msg);
                }
                break;
            case RouterSetting.TYPE_SETLOGINPWD:
                if (requestResult == null) {
                    mLogUtil.e("onRequestFinish requestResult is null!");
                    mHandler.sendEmptyMessage(MSG_GET_SET_PWD_FAILED);
                    return;
                }

                if (RequestResult.RESULT_SUCCESS.equals(requestResult.getResult())) {
                    mHandler.sendEmptyMessage(MSG_GET_SET_PWD_SUCCESS);
                } else {
                    Message msg = new Message();
                    msg.what = MSG_GET_SET_PWD_FAILED;
                    msg.obj = requestResult.getMsg();
                    mHandler.sendMessage(msg);
                }
                break;
            default:
                mLogUtil.e("onRequestFinish requestType != TYPE_GETLOGINNAME && TYPE_SETLOGINPWD"
                        + ", type = "
                        + requestType);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sure:
                mRouterSetting.setLoginPwd(mEtPasswordOld.getText().toString(), mEtPasswordNew
                        .getText().toString());
                break;
            default:
                break;
        }
    }
}
