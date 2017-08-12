package com.android.settings.g3;

import android.content.Context;
import android.preference.DialogPreference;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.android.settings.R;
import android.net.g3.G3Manager;

public class G3Dialog extends DialogPreference implements TextWatcher {

    private static final String TAG="G3Dialog";
    private boolean debug = true;
    private EditText apnName;
    private EditText dialNumber;
    private EditText pinCode;
    private EditText userName;
    private EditText password;
    private Context mContext;
    private String userNameValue;
    private String apnNameValue;
    private String dialNumberValue;
    private String pwdValue;
    private String pinCodeValue;
    private G3Manager g3Manager;
    public G3Dialog(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.xml.third_gen_dialog);
        mContext = context;
        G3Enabler mG3Enabler = new G3Enabler(context, null);
        g3Manager = mG3Enabler.getManager();

    }

    @Override
    protected void onBindDialogView(View view) {
        // TODO Auto-generated method stub
        if(debug)
            Log.d(TAG,"onBindDialogView");
        apnName = (EditText) view.findViewById(R.id.apn_name);
        dialNumber = (EditText) view.findViewById(R.id.dial_number);
        pinCode = (EditText) view.findViewById(R.id.pin_code);
        userName = (EditText) view.findViewById(R.id.user_name);
        password = (EditText) view.findViewById(R.id.user_password);
        super.onBindDialogView(view);

        userNameValue = Settings.Secure.getString(mContext.getContentResolver(),Settings.Secure.G3_USER_NAME);
        apnNameValue = Settings.Secure.getString(mContext.getContentResolver(),Settings.Secure.G3_APN_NAME);
        dialNumberValue = Settings.Secure.getString(mContext.getContentResolver(),Settings.Secure.G3_DIAL_NUMBER);
        pwdValue = Settings.Secure.getString(mContext.getContentResolver(),Settings.Secure.G3_USER_PASS);
        pinCodeValue = Settings.Secure.getString(mContext.getContentResolver(),Settings.Secure.G3_PIN_CODE);

        apnName.setText(apnNameValue);
        dialNumber.setText(dialNumberValue);
        pinCode.setText(pinCodeValue);
        password.setText(pwdValue);
        userName.setText(userNameValue);


        apnName.addTextChangedListener(this);
        dialNumber.addTextChangedListener(this);
        pinCode.addTextChangedListener(this);
        userName.addTextChangedListener(this);
        password.addTextChangedListener(this);

    }
    @Override
    public void afterTextChanged(Editable arg0) {
        // TODO Auto-generated method stub
        apnNameValue = apnName.getText().toString();
        dialNumberValue = dialNumber.getText().toString();
        pinCodeValue = pinCode.getText().toString();
        pwdValue = password.getText().toString();
        userNameValue = userName.getText().toString();
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
            Settings.Secure.putString(mContext.getContentResolver(),Settings.Secure.G3_USER_NAME,userNameValue);
            Settings.Secure.putString(mContext.getContentResolver(),Settings.Secure.G3_APN_NAME,apnNameValue);
            Settings.Secure.putString(mContext.getContentResolver(),Settings.Secure.G3_DIAL_NUMBER,dialNumberValue);
            Settings.Secure.putString(mContext.getContentResolver(),Settings.Secure.G3_USER_PASS,pwdValue);
            Settings.Secure.putString(mContext.getContentResolver(),Settings.Secure.G3_PIN_CODE,pinCodeValue);

            if(g3Manager.getG3ConnectStatus() == G3Manager.G3_CONNECT_STATE_CONNECTED)
                g3Manager.setG3State(G3Manager.G3_STATE_DISABLED);
            g3Manager.setConnectMode(G3Manager.G3_CONNECT_MODE_MANUAL);
            g3Manager.setG3State(G3Manager.G3_STATE_ENABLED);

        }
    }

}
