package com.yinhe.dtv.ca;

import android.util.Log;

import com.yinhe.dtv.cdcas.CdcasAdapter;

/**
 * Created by HB on 2016/8/11.
 */
public class CdcasManager implements CdcasAdapter.ICdcasPorting {

    static final private String LOG_TAG = "CdcasManager";

    final private CdcasSc mCdcasSc;
    final private CdcasAdapter mCdcasAdapter;
    final private CdcasDemux mCdcasDemux;

    final private Object mListenerLock;
    private ICdcasListener mCdcasListener;

    public interface ICdcasListener {
        void ScrSetCW(int ecmPid, byte[] oddKey, byte[] evenKey);

        void ShowBuyMessage(String message,int msgType);
        
        void onInsertSmartCard();
        
        void onRemoveSmartCard();
    }
    static final private CdcasManager mCdcasManagerInstance = new CdcasManager();

    static public CdcasManager getInstance() {
        return mCdcasManagerInstance;
    }

    private CdcasManager() {
        mListenerLock = new Object();
        mCdcasSc = new CdcasSc(this);
        mCdcasAdapter = new CdcasAdapter(this);
        mCdcasDemux = new CdcasDemux(mCdcasAdapter);
        mCdcasAdapter.init(0);
        mCdcasSc.start();
    }

    public void setListener(ICdcasListener listener) {
        synchronized (mListenerLock) {
        	Log.e("ttttttttttttt", "cdcasManager setListener");
            mCdcasListener = listener;
        }
    }

    public void insertSmartCard() {
        Log.v(LOG_TAG, "scInsert");
        mCdcasAdapter.scInsert();
        mCdcasListener.onInsertSmartCard();
    }

    public void removeSmartCard() {
        Log.v(LOG_TAG, "scRemove");
        mCdcasAdapter.scRemove();
        mCdcasListener.onRemoveSmartCard();
    }

    public void setEmmPid(int emmPid) {
        mCdcasAdapter.setEmmPid(emmPid);
    }

    public void formatFlashBuffer() {
        mCdcasAdapter.formatBuffer();
    }

    public void startService(int ecmPid, int serviceId) {
        mCdcasDemux.cancelAll();
        mCdcasAdapter.setEcmPid(CdcasAdapter.CDCA_LIST_FIRST, 0, null);
        mCdcasAdapter.setEcmPid(CdcasAdapter.CDCA_LIST_ADD, ecmPid, new int[]{serviceId});
        mCdcasAdapter.setEcmPid(CdcasAdapter.CDCA_LIST_OK, 0, null);
    }

    public void stopService() {
        mCdcasDemux.cancelAll();
        mCdcasAdapter.setEcmPid(CdcasAdapter.CDCA_LIST_FIRST, 0, null);
        mCdcasAdapter.setEcmPid(CdcasAdapter.CDCA_LIST_OK, 0, null);
    }


    @Override
    public boolean SetPrivateDataFilter(int reqID, byte[] filter, byte[] mask, int pid, int waitSeconds) {
        Log.v(LOG_TAG, "SetPrivateDataFilter  reqID:" + reqID + "  pid:" + pid +
                "  tableId:" + (filter[0] & 0xFF) + "  mask:" + (mask[0] & 0xFF));
        mCdcasDemux.request(reqID, filter, mask, pid, waitSeconds);
        return true;
    }

    @Override
    public void ReleasePrivateDataFilter(int reqID, int pid) {
        Log.v(LOG_TAG, "ReleasePrivateDataFilter :" + reqID);
        mCdcasDemux.cancel(reqID, pid);
    }

    @Override
    public byte[] SCReset() {
        byte[] atr = mCdcasSc.reset();
        Log.v(LOG_TAG, "SCReset " + atr.length);
        return atr;
    }

    @Override
    public byte[] SCPBRun(byte[] command) {
        byte[] result = mCdcasSc.transcode(command);
        Log.v(LOG_TAG, "SCPBRun " + command.length + "," + result.length);
        return result;
    }

    @Override
    public void ScrSetCW(int ecmPid, byte[] oddKey, byte[] evenKey, boolean tapingEnabled) {
        Log.v(LOG_TAG, "setCW for ecmPID=" + ecmPid);
        synchronized (mListenerLock) {
            if (mCdcasListener != null) {
                mCdcasListener.ScrSetCW(ecmPid, oddKey, evenKey);
            }
        }
    }

    @Override
    public void ShowBuyMessage(int ecmPid, int messageType) {
        synchronized (mListenerLock) {
            if (mCdcasListener != null) {
                mCdcasListener.ShowBuyMessage(CdcasAdapter.CDCA_MESSAGES[messageType],messageType);
            	Log.e("ttttttttttttt", "mCdcasListener is not null");

            }else{
            	Log.e("ttttttttttttt", "mCdcasListener is null");
            }
        }
    }

    @Override
    public void EntitleChanged(int tvsID) {
        Log.v(LOG_TAG, "EntitleChanged " + tvsID);
    }

    @Override
    public void DetitleReceived(int status) {
        Log.v(LOG_TAG, "DetitleReceived " + status);
    }

    @Override
    public long GetSTBID() {
        return 0;
    }

    @Override
    public int SCFunction(byte[] data) {
        return 0;
    }

    @Override
    public void StartIppvBuyDlg(int messageType, int ecmPid, CdcasAdapter.SCDCAIppvBuyInfo ippvProgram) {

    }

    @Override
    public void HideIPPVDlg(int ecmPid) {

    }

    @Override
    public void EmailNotifyIcon(int show, int emailID) {

    }

    @Override
    public void ShowOSDMessage(int style, String message) {

    }

    @Override
    public void HideOSDMessage(int style) {

    }

    @Override
    public void RequestFeeding(boolean readStatus) {

    }

    @Override
    public void LockService(CdcasAdapter.SCDCALockService lockService) {

    }

    @Override
    public void UNLockService() {

    }

    @Override
    public void ShowFingerMessage(int ecmPID, int cardID) {

    }

    @Override
    public void ShowFingerMessageExt(int ecmPID, String fingerMsg) {

    }

    @Override
    public void ActionRequest(int tvsid, int actionType) {

    }

    @Override
    public void ActionRequestExt(int tvsid, int actionType, byte[] data) {

    }

    @Override
    public void ShowCurtainNotify(int ecmPID, int curtainCode) {

    }

    @Override
    public void ShowProgressStrip(int progress, int mark) {

    }

    @Override
    public CdcasAdapter.SCDCATime GetCurrentTime() {
        return null;
    }
}
