package com.yinhe.dtv.ca;

import android.annotation.SuppressLint;
import android.util.Log;

import com.yinhe.dtv.DtvManager;
import com.yinhe.dtv.DtvSmartCard;

import java.util.Arrays;

/**
 * Created by HB on 2016/8/11.
 */
public class CdcasSc extends Thread {

    static final private String LOG_TAG = "CdcasSc";

    final private CdcasManager mCdcasManager;

    final private DtvSmartCard mSmartCard;

    private boolean mSmartCardInserted = false;

    public CdcasSc(CdcasManager manager) {
        mCdcasManager = manager;
        mSmartCard = DtvManager.getInstance().getSmartCard(0);
    }

    @SuppressLint("NewApi")
	public byte[] reset() {
        byte[] atr = new byte[256];
        int len = 0;
        try {
            len = mSmartCard.reset(atr, 256);
        } catch (Exception e) {
            return new byte[0];
        }

        return Arrays.copyOf(atr, len);
    }

    @SuppressLint("NewApi")
	public byte[] transcode(byte[] command) {
        byte[] reply = new byte[256];
        for (int i = 0; i < 3; i++) {
            try {
                int len = mSmartCard.transcode(command, command.length, reply, 256, 1000);
                return Arrays.copyOf(reply, len);
            } catch (Exception e) {
                Log.w(LOG_TAG, "transcode Error :" + i);
            }
        }
        return new byte[0];
    }


    @Override
    public void run() {

        while (true) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }

            boolean newCardIn = mSmartCard.getStatus().connectStatus !=
                    DtvSmartCard.Status.SCI_STATUS_NOCARD;

            if (mSmartCardInserted == newCardIn) continue;
            mSmartCardInserted = newCardIn;

            try {
                if (newCardIn) {
                    mCdcasManager.insertSmartCard();
                } else {
                    mCdcasManager.removeSmartCard();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
