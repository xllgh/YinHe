
package com.yinhe.iptvsetting;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.yinhe.iptvsetting.activity.SoundAndDisplayFragmentActivity;
import com.yinhe.iptvsetting.activity.SystemInforActivity;
import com.yinhe.iptvsetting.activity.RouteSettingsActivity;
import com.yinhe.iptvsetting.activity.WireConnectFragmentActivity;
import com.yinhe.iptvsetting.activity.WirelessConnectActivity;
import com.yinhe.iptvsetting.common.LogUtil;
import com.yinhe.iptvsetting.view.ImageReflect;
import com.yinhe.iptvsetting.view.ScaleAnimEffect;
import com.yinhe.securityguard.SecurityCat;

public class MainActivity extends Activity implements OnClickListener, OnFocusChangeListener {
    private LogUtil mLogUtil = new LogUtil(MainActivity.class);

    private long first_time;
    private long temp_time = 0;
    private int clickNum = 0;

    private SecurityCat mSecurityCat = null;
    private ServiceConnection mSecurityConnection = null;

    private ImageView[] ivReflect = new ImageView[3];
    private ImageView whiteBorder = null;

    private FrameLayout WireAccess = null;
    private FrameLayout WirelessAccess = null;
    private FrameLayout DisplayAndSound = null;
    private FrameLayout SeniorSet = null;
    private FrameLayout VersionInfor = null;
    private FrameLayout ApacheInfor = null;

    private ScaleAnimEffect animEffect;

    private static final int MSG_SERVICE_CONNECTED = 0;
    private static final int MSG_SERVICE_CHECK = 1;
    private static final int MSG_SERVICE_RECONNECT = 2;

    private int mServiceConnectCount = 0;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SERVICE_CONNECTED:
                    if (mSecurityCat != null) {
                        try {
                            mSecurityCat.startLocalCheck();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case MSG_SERVICE_CHECK:
                    mLogUtil.d("MSG_SERVICE_CHECK");
                    if (mSecurityCat != null) {
                        return;
                    }
                    // ActivityManager am = (ActivityManager)TODO
                    // getSystemService(ACTIVITY_SERVICE);
                    // am.forceStopPackage(MainActivity.this.getPackageName());
                    break;
                case MSG_SERVICE_RECONNECT:
                    if (mServiceConnectCount == 2) {
                        return;
                    }
                    mServiceConnectCount++;
                    bindService();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_main);

        initData();

        bindService();
    }

    private void initData() {
        this.WireAccess = (FrameLayout) findViewById(R.id.fl_wire_connect);
        this.WireAccess.setOnFocusChangeListener(this);
        this.WireAccess.setOnClickListener(this);

        this.WirelessAccess = (FrameLayout) findViewById(R.id.fl_wireless_access);
        this.WirelessAccess.setOnFocusChangeListener(this);
        this.WirelessAccess.setOnClickListener(this);

        this.DisplayAndSound = (FrameLayout) findViewById(R.id.fl_display_and_sound);
        this.DisplayAndSound.setOnFocusChangeListener(this);
        this.DisplayAndSound.setOnClickListener(this);

        this.SeniorSet = (FrameLayout) findViewById(R.id.fl_senior_set);
        this.SeniorSet.setOnFocusChangeListener(this);
        this.SeniorSet.setOnClickListener(this);

        this.VersionInfor = (FrameLayout) findViewById(R.id.fl_version_infor);
        this.VersionInfor.setOnFocusChangeListener(this);
        this.VersionInfor.setOnClickListener(this);

        this.ApacheInfor = (FrameLayout)
                findViewById(R.id.fl_apache_infor);
        this.ApacheInfor.setOnFocusChangeListener(this);
        this.ApacheInfor.setOnClickListener(this);

        whiteBorder = (ImageView) findViewById(R.id.white_boder);

        this.ivReflect[0] = (ImageView) findViewById(R.id.iv_reflect_senior_set);
        this.ivReflect[1] = (ImageView) findViewById(R.id.iv_reflect_version_infor);
        this.ivReflect[2] = (ImageView) findViewById(R.id.iv_reflect_apache_infor);

        Bitmap bitmap0 = ImageReflect.convertViewToBitmap(SeniorSet);
        Bitmap bitmap1 = ImageReflect.convertViewToBitmap(VersionInfor);
        Bitmap bitmap2 = ImageReflect.convertViewToBitmap(ApacheInfor);

        this.ivReflect[0].setImageBitmap(ImageReflect.createCutReflectedImage(this,
                bitmap0, 0));
        this.ivReflect[1].setImageBitmap(ImageReflect.createCutReflectedImage(this,
                bitmap1, 0));
        this.ivReflect[2].setImageBitmap(ImageReflect.createCutReflectedImage(this,
                bitmap2, 0));

        if (!bitmap0.isRecycled()) {
            bitmap0.recycle();
        }

        if (!bitmap1.isRecycled()) {
            bitmap1.recycle();
        }

        if (!bitmap2.isRecycled()) {
            bitmap2.recycle();
        }

        this.animEffect = new ScaleAnimEffect();
    }

    @Override
    public void onClick(View arg0) {
        Intent intent = null;
        switch (arg0.getId()) {
            case R.id.fl_wire_connect:
                intent = new Intent(this,
                        WireConnectFragmentActivity.class);
                break;
            case R.id.fl_wireless_access:
                intent = new Intent(this, WirelessConnectActivity.class);
                break;
            case R.id.fl_display_and_sound:
                intent = new Intent(this,
                        SoundAndDisplayFragmentActivity.class);
                break;
            case R.id.fl_senior_set:
                intent = new Intent();
                intent.setClassName("com.android.settings",
                        "com.android.settings.Settings");
                intent.setAction("com.yinhe.iptvsetting");
                break;
            case R.id.fl_version_infor:
                intent = new Intent(this, SystemInforActivity.class);
                break;
            case R.id.fl_apache_infor:
                intent = new Intent(this, RouteSettingsActivity.class);
                break;
        }
        if (intent != null) {
            startActivityForResult(intent, 0);
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_6 && event.getAction() == KeyEvent.ACTION_DOWN) {
            clickNum++;
            first_time = System.currentTimeMillis();
            if (temp_time != 0) {
                if ((first_time - temp_time) < 2000) {
                    if (clickNum == 6) {
                        Intent intent = new Intent();
                        intent.setComponent(new ComponentName("com.yhproduct.netsetting",
                                "com.yhproduct.netsetting.MainActivity"));
                        startActivity(intent);

                        first_time = temp_time = 0;
                        clickNum = 0;
                    }
                } else {
                    first_time = temp_time = 0;
                    clickNum = 0;
                }
            }
            temp_time = first_time;

            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == SoundAndDisplayFragmentActivity.RESULT_VALUE) {
            Intent intent = new Intent(this,
                    SoundAndDisplayFragmentActivity.class);
            intent.putExtra(SoundAndDisplayFragmentActivity.SHOW_FONT, true);
            startActivityForResult(intent, 0);
        }
    }

    @Override
    public void onFocusChange(final View v, boolean hasFocus) {
        if (hasFocus) {
            this.animEffect.setAttributs(1.0F, 1.125F, 1.0F, 1.125F, 100L);
            Animation localAnimation = this.animEffect.createAnimation();
            localAnimation.setAnimationListener(new AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    moveWhiteBorder(v);
                }
            });
            v.startAnimation(localAnimation);
            v.bringToFront();
            whiteBorder.bringToFront();
        } else {
            whiteBorder.setVisibility(View.GONE);
            v.setBackground(null);
            this.animEffect.setAttributs(1.125F, 1.0F, 1.125F, 1.0F, 100L);
            v.startAnimation(animEffect.createAnimation());
        }

    }

    private void moveWhiteBorder(View v) {
        if ((this.whiteBorder != null)) {
            this.whiteBorder.setVisibility(View.VISIBLE);
            whiteBorder.setX(v.getX() - (whiteBorder.getWidth() - v.getWidth()) / 2);
            whiteBorder.setY(v.getY() - (whiteBorder.getHeight() - v.getHeight()) / 2);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        unBindService();
    }

    private void bindService() {
        if (mSecurityConnection != null) {
            unbindService(mSecurityConnection);
        }
        mSecurityConnection = new ServiceConnection() {
            @Override
            public void onServiceDisconnected(ComponentName name) {
                mSecurityCat = null;
            }

            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mLogUtil.d("onServiceConnected");
                mSecurityCat = SecurityCat.Stub.asInterface(service);
                mHandler.sendEmptyMessage(MSG_SERVICE_CONNECTED);
            }
        };
        Intent intent = new Intent("com.yinhe.mysecurityguard");
        boolean isSuccess = bindService(intent, mSecurityConnection, Context.BIND_AUTO_CREATE);
        if (!isSuccess) {
            mHandler.sendEmptyMessageDelayed(MSG_SERVICE_RECONNECT, 2000);
        }

        mHandler.sendEmptyMessageDelayed(MSG_SERVICE_CHECK, Double.valueOf(Math.random() * 10000)
                .intValue() + 10000);
    }

    private void unBindService() {
        if (mSecurityConnection != null) {
            unbindService(mSecurityConnection);
        }
    }
}
