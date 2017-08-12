package com.android.settings.ethernet;

import android.content.Context;
import android.preference.DialogPreference;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.net.ethernet.*;

import com.android.settings.R;

public class Ipv6Dialog extends DialogPreference implements TextWatcher {

    private static final String TAG="Ipv6Dialog";
    private boolean debug = false;
    private EditText mIP;
    private EditText mGateWay;
    private EditText mPrefixLength;
    private EditText mDns1;
    private EditText mDns2;
    private Context mContext;
    private String mIPValue;
    private String mGateWayValue;
    private int mPrefixLengthValue;
    private String mDns1Value;
    private String mDns2Value;
    private EthernetManager mEthManager;
    private EthernetEnabler mEthEnabler;

    public Ipv6Dialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.xml.ipv6_dialog);
        mEthEnabler = new EthernetEnabler(context, null);
        mEthManager = mEthEnabler.getManager();
    }

    @Override
    protected void onBindDialogView(View view) {
        // TODO Auto-generated method stub
        if(debug)
            Log.d(TAG,"onBindDialogView");
        mIP = (EditText) view.findViewById(R.id.ipv6_ipaddress);
        mGateWay = (EditText) view.findViewById(R.id.ipv6_gateway);
        mPrefixLength = (EditText) view.findViewById(R.id.ipv6_prefix_length);
        mDns1 = (EditText) view.findViewById(R.id.ipv6_dns1);
        mDns2 = (EditText) view.findViewById(R.id.ipv6_dns2);
        super.onBindDialogView(view);

        mIPValue = mEthManager.getIpv6DatabaseAddress();
        mPrefixLengthValue = mEthManager.getIpv6DatabasePrefixlength();
        mGateWayValue = mEthManager.getIpv6DatabaseGateway();
        mDns1Value = mEthManager.getIpv6DatabaseDns1();
        mDns2Value = mEthManager.getIpv6DatabaseDns2();

        mIP.setText(mIPValue);
        mGateWay.setText(mGateWayValue);
        mPrefixLength.setText(mPrefixLengthValue+"");
        mDns1.setText(mDns1Value);
        mDns2.setText(mDns2Value);

        mIP.addTextChangedListener(this);
        mGateWay.addTextChangedListener(this);
        mPrefixLength.addTextChangedListener(this);
        mDns1.addTextChangedListener(this);
        mDns2.addTextChangedListener(this);
    }
    @Override
    public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub
        mIPValue = mIP.getText().toString();
        mGateWayValue = mGateWay.getText().toString();
        mPrefixLengthValue = Integer.parseInt(mPrefixLength.getText().toString());
        mDns1Value = mDns1.getText().toString();
        mDns2Value = mDns2.getText().toString();
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
        // TODO Auto-generated method stub
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // TODO Auto-generated method stub
        super.onDialogClosed(positiveResult);
        if(positiveResult){
            if(debug)
                Log.d(TAG,"to save the data");
            mEthManager.enableIpv6(false);
            mEthManager.setIpv6DatabaseInfo(mIPValue, mPrefixLengthValue, mGateWayValue, mDns1Value, mDns2Value);
            mEthManager.enableIpv6(true);
        }
    }
}
