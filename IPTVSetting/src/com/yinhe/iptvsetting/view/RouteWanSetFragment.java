
package com.yinhe.iptvsetting.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.EditTextBgToStar;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.IRouterSetting;
import com.yinhe.iptvsetting.common.LogUtil;
import com.yinhe.iptvsetting.common.RouterSetting;
import com.yinhe.iptvsetting.common.RouterSetting.IRouterSettingCallback;
import com.yinhe.iptvsetting.common.RouterSetting.RequestResult;

public class RouteWanSetFragment extends Fragment implements IRouterSettingCallback,
        OnClickListener {
    private LogUtil mLogUtil = new LogUtil("RouteWanSetFragment");

    private IRouterSetting mRouterSetting;
    private ProgressDialog mProgressDialog;
    private Activity mActivity = null;
    private PopupWindow mPopupWindow = null;

    public static final int CONTENT_TYPE_OBTAINNING = 0;
    public static final int CONTENT_TYPE_DETAILS = 1;
    public static final int CONTENT_TYPE_ERROR = 2;
    private int mContentType = CONTENT_TYPE_DETAILS;

    private AddressInput mTvIpAddress = null;
    private AddressInput mTvNetmask = null;
    private AddressInput mTvDefaultGateway = null;
    private AddressInput mTvDns = null;

    private TextView mTvState = null;
    private TextView mTvAccessType;
    private Button mBtnSure = null;
    private Button mBtnCancle = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mActivity = getActivity();
        View view = inflater.inflate(R.layout.route_set_wan, container, false);
        initView(view);
        return view;
    }

    private void initView(View view) {
        final TextView tvTitle = (TextView) view.findViewById(R.id.tv_lbl_access_type);
        RelativeLayout rlAccessType = (RelativeLayout) view.findViewById(R.id.rl_access_type);
        rlAccessType.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupWindow(v);
            }
        });
        rlAccessType.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                tvTitle.setTextColor(hasFocus ? Color.YELLOW : Color.WHITE);
            }
        });

        mTvIpAddress = (AddressInput) view.findViewById(R.id.et_ip_address);
        mTvNetmask = (AddressInput) view.findViewById(R.id.et_netmask);
        mTvDefaultGateway = (AddressInput) view.findViewById(R.id.et_default_gateway);
        mTvDns = (AddressInput) view.findViewById(R.id.et_dns);

        // mTvIpAddress.setEnabled(false);
        // mTvNetmask.setEnabled(false);
        // mTvDefaultGateway.setEnabled(false);
        // mTvDns.setEnabled(false);

        mTvState = (TextView) view.findViewById(R.id.tv_ethernet_state);

        mTvAccessType = (TextView) view.findViewById(R.id.tv_access_type);

        mProgressDialog = new ProgressDialog(mActivity);

        mBtnSure = (Button) view.findViewById(R.id.btn_sure);
        mBtnCancle = (Button) view.findViewById(R.id.btn_cancel);
        mBtnSure.setOnClickListener(this);
        mBtnCancle.setOnClickListener(this);
        mBtnSure.setVisibility(View.GONE);
        mBtnCancle.setVisibility(View.GONE);
    }

    private void showPopupWindow(View view) {
        if (mPopupWindow == null) {
            LinearLayout contentView = (LinearLayout) LayoutInflater.from(mActivity).inflate(
                    R.layout.pop_window, null);
            mPopupWindow = new PopupWindow(contentView,
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

            int count = contentView.getChildCount();
            for (int i = 0; i < count; i++) {
                Button btnChangeType = (Button) contentView.getChildAt(i);
                btnChangeType.setOnClickListener(this);
            }

            mPopupWindow.setTouchable(true);
            mPopupWindow.setAnimationStyle(R.style.pop_window_animation);
            mPopupWindow.setBackgroundDrawable(mActivity.getResources().getDrawable(
                    R.drawable.bg_popupwindow));
            mPopupWindow.showAsDropDown(view, 750, -75);
        } else if (!mPopupWindow.isShowing()) {
            mPopupWindow.showAsDropDown(view, 750, -75);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mLogUtil.d("onResume() type = " + mContentType);
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
    public void onRequestFinish(int requestType, RequestResult requestResult) {
        if (requestType != RouterSetting.TYPE_SETINTERNET) {
            mLogUtil.e("onRequestFinish requestType != RouterSetting.TYPE_SETINTERNET"
                    + ", type = "
                    + requestType);
            return;
        }

        if (requestResult == null) {
            mLogUtil.e("onRequestFinish requestResult is null!");
            mHandler.sendEmptyMessage(MSG_SET_WAN_FAILED);
            return;
        }

        if (RequestResult.RESULT_SUCCESS.equals(requestResult.getResult())) {
            mHandler.sendEmptyMessage(MSG_SET_WAN_SUCCESS);
        } else {
            Message msg = new Message();
            msg.what = MSG_SET_WAN_FAILED;
            msg.obj = requestResult.getMsg();
            mHandler.sendMessage(msg);
        }
    }

    private final static int MSG_SHOW_PROGRESS_DIALOG = 0;
    private final static int MSG_SET_WAN_SUCCESS = 1;
    private final static int MSG_SET_WAN_FAILED = 2;
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_PROGRESS_DIALOG:
                    if (mProgressDialog == null) {
                        mProgressDialog = new ProgressDialog(mActivity);
                    }
                    mProgressDialog.show();
                    break;
                case MSG_SET_WAN_FAILED:
                    FuncUtil.showToast(mActivity,
                            getString(R.string.hint_setup_failed)
                                    + (msg.obj == null ? "" : " : " + (String) msg.obj));
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                case MSG_SET_WAN_SUCCESS:
                    FuncUtil.showToast(mActivity, R.string.hint_setup_success);
                    if (mProgressDialog != null && mProgressDialog.isShowing()) {
                        mProgressDialog.dismiss();
                    }
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ethernet_auto:
                mTvAccessType.setText(R.string.dhcp);
                mRouterSetting.setInternet(IRouterSetting.InternetType.DHCP, null, null, null,
                        null, null, null);
                mPopupWindow.dismiss();
                mBtnSure.setVisibility(View.GONE);
                mBtnCancle.setVisibility(View.GONE);
                mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_DIALOG);
                break;
            case R.id.btn_ethernet_manual:
                mTvAccessType.setText(R.string.static_ip);
                mPopupWindow.dismiss();
                mBtnSure.setVisibility(View.VISIBLE);
                mBtnCancle.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_ethernet_pppoe:
                mTvAccessType.setText(R.string.pppoe);
                createLoginDialog();
                mPopupWindow.dismiss();
                mBtnSure.setVisibility(View.GONE);
                mBtnCancle.setVisibility(View.GONE);
                break;
            case R.id.btn_sure:
                String ip = FuncUtil.ipToHexString(mTvIpAddress.getAddress());
                String mask = FuncUtil.ipToHexString(mTvNetmask.getAddress());
                String gateway = FuncUtil.ipToHexString(mTvDefaultGateway.getAddress());
                String dns = FuncUtil.ipToHexString(mTvDns.getAddress());
                if (FuncUtil.isNullOrEmpty(ip) || FuncUtil.isNullOrEmpty(mask)
                        || FuncUtil.isNullOrEmpty(gateway) || FuncUtil.isNullOrEmpty(dns)) {
                    FuncUtil.showToast(mActivity, R.string.collocate_message_is_wrong);
                } else {
                    mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_DIALOG);
                    mRouterSetting.setInternet(IRouterSetting.InternetType.MANUAL, ip, mask,
                            gateway,
                            dns, null, null);
                }
                break;
            case R.id.btn_cancel:
                break;
            default:
                break;
        }
    }

    private void createLoginDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(
                mActivity, R.style.style_dialog);
        LayoutInflater inflater = mActivity.getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_route_login, null);
        final EditText etUserName = (EditText) view
                .findViewById(R.id.et_username);
        etUserName.setText("20160526");
        final EditText etPsw = (EditText) view
                .findViewById(R.id.et_password);
        etPsw.setText("16580000");
        final EditTextBgToStar toStar = new EditTextBgToStar();
        etPsw.setTransformationMethod(toStar);
        CheckBox chbShowPwd = (CheckBox) view
                .findViewById(R.id.show_password);
        chbShowPwd.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                etPsw.setTransformationMethod(isChecked ?
                        null
                        : toStar);
            }
        });
        builder.setTitle("PPPoE Setup");
        builder.setView(view);
        builder.setPositiveButton(R.string.sure,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mRouterSetting.setInternet(IRouterSetting.InternetType.PPPOE, null, null,
                                null,
                                null, etUserName.getEditableText().toString(), etPsw
                                        .getEditableText().toString());
                        mHandler.sendEmptyMessage(MSG_SHOW_PROGRESS_DIALOG);
                    }
                });
        builder.setNegativeButton(R.string.cancel,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                dialog.dismiss();
            }
        });
        builder.create();
        AlertDialog al = builder.show();
        al.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        final Button btnOK = al.getButton(DialogInterface.BUTTON_POSITIVE);
        btnOK.setEnabled(canLogin(etUserName, etPsw));
        TextWatcher tw = new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                btnOK.setEnabled(canLogin(etUserName, etPsw));
            }
        };
        etPsw.addTextChangedListener(tw);
    }

    private boolean canLogin(EditText et1, EditText et2) {
        String str1 = et1.getEditableText().toString();
        String str2 = et1.getEditableText().toString();
        if (FuncUtil.isNullOrEmpty(str1) || FuncUtil.isNullOrEmpty(str2)) {
            return false;
        }

        return str1.length() >= 1 && str2.length() >= 1;
    }
}
