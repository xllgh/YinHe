package com.android.settings.ethernet;

import java.net.InetAddress;
import java.net.Inet4Address;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.preference.DialogPreference;
import android.net.NetworkUtils;
import android.net.DhcpInfo;
import android.net.ethernet.EthernetManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import java.net.UnknownHostException;
import java.io.IOException;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;

import com.android.settings.R;

public class EthernetDialog extends DialogPreference implements TextWatcher{
    private static final String TAG = "EthnetDialog";
    private boolean DEBUG = false;
    private EditText mIP;
    private EditText mGateWay;
    private EditText mNetMask;
    private EditText mDNS1;
    private EditText mDNS2;
    private EthernetManager mEthManager;
    private EthernetEnabler mEthEnabler;
    private Context mContext;

    public EthernetDialog(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        setDialogLayoutResource(R.xml.ethernet_dialog);
        mEthEnabler = new EthernetEnabler(context, null);
        mEthManager = mEthEnabler.getManager();
        mContext = context;
    }

    protected void onBindDialogView(View view)
    {
        super.onBindDialogView(view);
        mIP = (EditText)view.findViewById(R.id.eth_ipaddress);
        mGateWay = (EditText)view.findViewById(R.id.eth_gateway);
        mNetMask = (EditText)view.findViewById(R.id.eth_network);
        mDNS1 = (EditText)view.findViewById(R.id.eth_dns1);
        mDNS2 = (EditText)view.findViewById(R.id.eth_dns2);
        mIP.addTextChangedListener(this);
        mGateWay.addTextChangedListener(this);
        mNetMask.addTextChangedListener(this);
        mDNS1.addTextChangedListener(this);
        mDNS2.addTextChangedListener(this);

        initUI();
    }
    

    private void initUI(){
        showIP();
        focus();
  }

private  void focus() {

    String KEY = this.getKey();
    if(DEBUG) Log.i(TAG, "IP focus");
    if(KEY.equals("ip_address")){
        mIP.requestFocus();
    }else if(KEY.equals("gateway")){
        mGateWay.requestFocus();
    }else if(KEY.equals("netmask")){
        mNetMask.requestFocus();
    }else if(KEY.equals("dns1")){
        mDNS1.requestFocus();
    }else if(KEY.equals("dns2")){
        mDNS2.requestFocus();
    }
}

/**
 * get IP gateway netmask dns from DB and show them to the dialog
 */
  private void showIP() {
        DhcpInfo dhcpInfo = mEthManager.getSavedEthernetIpInfo();

        String IP = NetworkUtils.intToInetAddress(dhcpInfo.ipAddress).getHostAddress();
        mIP.setText(IP);

        String mGW = NetworkUtils.intToInetAddress(dhcpInfo.gateway).getHostAddress();
        mGateWay.setText(mGW);

        String mNM = NetworkUtils.intToInetAddress(dhcpInfo.netmask).getHostAddress();
        mNetMask.setText(mNM);

        String dns1 = NetworkUtils.intToInetAddress(dhcpInfo.dns1).getHostAddress();
        mDNS1.setText(dns1);

        String dns2 = NetworkUtils.intToInetAddress(dhcpInfo.dns2).getHostAddress();
        mDNS2.setText(dns2);
}

    protected void onDialogClosed(boolean positiveResult){

        if(!positiveResult)
        {
          if(DEBUG) Log.e(TAG, "DiaLog Click cancel ");
        }else{
            if(DEBUG) Log.e(TAG, "Dialog Click OK");
                setIP();
        }
    }

   private final Handler handler=new Handler(){
                public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                              switch (msg.what) {
                                case 1:
                                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                                        builder.setMessage("IP  ALREADY  IN  USE , PLEASE  TRY  ANOTHER");
                                        builder.setTitle("ALERT");
                                        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

                                                        @Override
                                                        public void onClick(DialogInterface dialog, int whichButton) {
                                                        // TODO Auto-generated method stub
                                                        dialog.dismiss();
                                                        }
                                                        });
                                        builder.create().show();
                                    break;
                                }
                };
      };


/**
 * when click the Button OK set new IP netmask dns gateway from dialog to DB
 */
  private void setIP() {
        if(DEBUG) Log.e(TAG, "DiaLog Click OK to set IP from dialog to DB");
        final String IP = mIP.getText().toString();
      final String DNS1 = mDNS1.getText().toString();
      final String DNS2 = mDNS2.getText().toString();
        String GATEWAY = mGateWay.getText().toString();
        String NETMASK = mNetMask.getText().toString();

        final DhcpInfo dhcpInfo = new DhcpInfo();

        if(IP.length()>0)
        {
            InetAddress ipaddr = NetworkUtils.numericToInetAddress(IP);
            dhcpInfo.ipAddress = NetworkUtils.inetAddressToInt((Inet4Address)ipaddr);
        }

        if(GATEWAY.length()>0)
        {
            InetAddress getwayaddr = NetworkUtils.numericToInetAddress(GATEWAY);
            dhcpInfo.gateway = NetworkUtils.inetAddressToInt((Inet4Address)getwayaddr);
        }

        if(NETMASK.length()>0)
        {
            InetAddress inetmask = NetworkUtils.numericToInetAddress(NETMASK);
            dhcpInfo.netmask = NetworkUtils.inetAddressToInt((Inet4Address)inetmask);
        }

        if(DNS1.length()>0)
        {
            InetAddress idns1 = NetworkUtils.numericToInetAddress(DNS1);
            dhcpInfo.dns1 = NetworkUtils.inetAddressToInt((Inet4Address)idns1);
        }

        if(DNS2.length()>0)
        {
            InetAddress idns2 = NetworkUtils.numericToInetAddress(DNS2);
            dhcpInfo.dns2 = NetworkUtils.inetAddressToInt((Inet4Address)idns2);
        }
      //check ip, netmask & gateway is in a net
      String[] array_ip = new String[4];
      String[] array_gateway = new String[4];
      String[] array_netmask = new String[4];

      array_ip = IP.split("\\.");
      array_gateway = GATEWAY.split("\\.");
      array_netmask = NETMASK.split("\\.");

      try{
        if(  ((Integer.parseInt(array_ip[0])&Integer.parseInt(array_netmask[0])) != (Integer.parseInt(array_gateway[0])&Integer.parseInt(array_netmask[0])))
        || ((Integer.parseInt(array_ip[1])&Integer.parseInt(array_netmask[1])) != (Integer.parseInt(array_gateway[1])&Integer.parseInt(array_netmask[1])))
        || ((Integer.parseInt(array_ip[2])&Integer.parseInt(array_netmask[2])) != (Integer.parseInt(array_gateway[2])&Integer.parseInt(array_netmask[2])))
        || ((Integer.parseInt(array_ip[3])&Integer.parseInt(array_netmask[3])) != (Integer.parseInt(array_gateway[3])&Integer.parseInt(array_netmask[3]))) )
        {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage("IP  AND  GATEWAY  NOT  IN  A  NET");
        builder.setTitle("ALERT");
        builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int whichButton) {
                // TODO Auto-generated method stub
                dialog.dismiss();
            }
        });
        builder.create().show();
        return;
        }
      }catch(Exception e){
          Log.e(TAG, "IP or NETMASK or GATEWAY STRING split->NUMBER error" + e);
      }

      new Thread() {
    public void run () {
	    EthernetManager mEthernetManager = (EthernetManager) mContext.getSystemService(Context.ETHERNET_SERVICE);
        DhcpInfo dhcpInfo_temp = mEthernetManager.getSavedEthernetIpInfo();
        boolean isDNSChanged = false;
        if (!(DNS1.equals(dhcpInfo_temp.dns1)) || !(DNS2.equals(dhcpInfo_temp.dns2))) {
            isDNSChanged = true;
        }
                boolean isConflict = false;
                        if (EthernetDialog.this.checkReachableByIP(IP)) {
                            isConflict = true;
                        }

        Log.i(TAG, "isConflict :" + isConflict);

        if(!isConflict || isDNSChanged)
            mEthManager.setEthernetMode(EthernetManager.ETHERNET_CONNECT_MODE_MANUAL, dhcpInfo);
        else{
            Message attaget = Message.obtain();
                                        attaget.what = 1;
                                        handler.sendMessage(attaget);
        }
    }

      }.start();
  }

  private boolean checkReachableByIP(String strIP) {
        boolean re = false;
        try {
            re = InetAddress.getByName(strIP).isReachable(1000);
        } catch (UnknownHostException e2) {
            e2.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }
        return re;
  }

@Override
public void afterTextChanged(Editable newvalue) {
    String ipCheck = mIP.getText().toString();
    String gwCheck = mGateWay.getText().toString();
    String netmaskCheck = mNetMask.getText().toString();
    String dns1Check = mDNS1.getText().toString();
    String dns2Check = mDNS2.getText().toString();

    if(IPcheck(ipCheck) && IPcheck(gwCheck) && IPcheck(netmaskCheck) && IPcheck(dns1Check) && IPcheck(dns2Check)){
        setOkBtn(true);
    }else{
        setOkBtn(false);
    }
}

private boolean IPcheck(String text) {
    String Text = text;
    if(DEBUG) Log.e(TAG, " text "+ text);

    String[] arrText = Text.split("\\.");
    int i = arrText.length;
    if(DEBUG) Log.e(TAG, " arrText.length "+ i);
    int N = text.length();
    if(N == 0){
        return true;
    }else{
        if(text.substring(N-1,N).equals(".")){
            if(DEBUG) Log.e(TAG, "text.substring(N-1,N) "+ text.substring(N-1,N).toString()+" N :"+ N);
            return false;
        }else if(i != 4){
            if(DEBUG) Log.e(TAG, " arrText.length wrong "+ i);
            return false;
        }else{
            try {
                String V0 = arrText[0].toString();
                String V1 = arrText[1].toString();
                String V2 = arrText[2].toString();
                String V3 = arrText[3].toString();
                int var0 = Integer.parseInt(V0);
                int var1 = Integer.parseInt(V1);
                int var2 = Integer.parseInt(V2);
                int var3 = Integer.parseInt(V3);
                boolean Zero = true;
                if(V0.substring(0, 1).equals("0") && V0.length() != 1){
                    Zero = false;
                }else if (V1.substring(0, 1).equals("0") && V1.length() != 1){
                    Zero = false;
                }else if (V2.substring(0, 1).equals("0") && V2.length() != 1){
                    Zero = false;
                }else if (V3.substring(0, 1).equals("0") && V3.length() != 1){
                    Zero = false;
                }else{
                    Zero = true;
                }

                boolean V =(0 <= var0 && var0 <= 255 && 0 <= var1 && var1 <= 255 && 0 <= var2 && var2 <= 255 && 0 <= var3 && var3 <= 255);
                if(Zero && V){
                    return true;
                }else{
                    if(DEBUG) Log.e(TAG, "Zero = "+ Zero +"    V = " + V);
                    return false;
                }
            }catch (NumberFormatException e) {
                return false;
            }
        }
    }
}

private void setOkBtn(boolean Enable) {
    Dialog d = getDialog();
    if (d instanceof AlertDialog) {
        if(DEBUG) Log.e(TAG, "Button AlertDialog ");
        Button b = ((AlertDialog) d).getButton(AlertDialog.BUTTON_POSITIVE);
        b.setEnabled(Enable);
    }
}

@Override
public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    // TODO Auto-generated method stub
}

@Override
public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    // TODO Auto-generated method stub
}

}
