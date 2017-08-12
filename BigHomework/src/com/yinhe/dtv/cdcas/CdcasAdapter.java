package com.yinhe.dtv.cdcas;

import android.util.Log;

/**
 * Created by HB on 2016/5/22.
 */
public class CdcasAdapter {

    static final private String LOG_TAG = "CdcasAdapter";

    static final public int CDCA_RC_OK = 0x00;
    static final public int CDCA_RC_UNKNOWN = 0x01;
    static final public int CDCA_RC_POINTER_INVALID = 0x02;
    static final public int CDCA_RC_CARD_INVALID = 0x03;
    static final public int CDCA_RC_PIN_INVALID = 0x04;
    static final public int CDCA_RC_DATASPACE_SMALL = 0x06;
    static final public int CDCA_RC_CARD_PAIROTHER = 0x07;
    static final public int CDCA_RC_DATA_NOT_FIND = 0x08;
    static final public int CDCA_RC_PROG_STATUS_INVALID = 0x09;
    static final public int CDCA_RC_CARD_NO_ROOM = 0x0A;
    static final public int CDCA_RC_WORKTIME_INVALID = 0x0B;
    static final public int CDCA_RC_IPPV_CANNTDEL = 0x0C;
    static final public int CDCA_RC_CARD_NOPAIR = 0x0D;
    static final public int CDCA_RC_WATCHRATING_INVALID = 0x0E;
    static final public int CDCA_RC_CARD_NOTSUPPORT = 0x0F;
    static final public int CDCA_RC_DATA_ERROR = 0x10;
    static final public int CDCA_RC_FEEDTIME_NOT_ARRIVE = 0x11;
    static final public int CDCA_RC_CARD_TYPEERROR = 0x12;
    static final public int CDCA_RC_CAS_FAILED = 0x20;
    static final public int CDCA_RC_OPER_FAILED = 0x21;
    static final public int CDCA_RC_UNSUPPORT = 0x22;
    static final public int CDCA_RC_LEN_ERROR = 0x23;
    static final public int CDCA_RC_SCHIP_ERROR = 0x24;

    static final public int CDCA_MESSAGE_CANCEL_TYPE = 0x00;        /* 取消当前的显示 */
    static final public int CDCA_MESSAGE_BADCARD_TYPE = 0x01;       /* 无法识别卡 */
    static final public int CDCA_MESSAGE_EXPICARD_TYPE = 0x02;      /* 智能卡过期，请更换新卡 */
    static final public int CDCA_MESSAGE_INSERTCARD_TYPE = 0x03;    /* 加扰节目，请插入智能卡 */
    static final public int CDCA_MESSAGE_NOOPER_TYPE = 0x04;        /* 不支持节目运营商 */
    static final public int CDCA_MESSAGE_BLACKOUT_TYPE = 0x05;      /* 条件禁播 */
    static final public int CDCA_MESSAGE_OUTWORKTIME_TYPE = 0x06;   /* 当前时段被设定为不能观看 */
    static final public int CDCA_MESSAGE_WATCHLEVEL_TYPE = 0x07;    /* 节目级别高于设定的观看级别 */
    static final public int CDCA_MESSAGE_PAIRING_TYPE = 0x08;       /* 智能卡与本机顶盒不对应 */
    static final public int CDCA_MESSAGE_NOENTITLE_TYPE = 0x09;     /* 没有授权 */
    static final public int CDCA_MESSAGE_DECRYPTFAIL_TYPE = 0x0A;   /* 节目解密失败 */
    static final public int CDCA_MESSAGE_NOMONEY_TYPE = 0x0B;       /* 卡内金额不足 */
    static final public int CDCA_MESSAGE_ERRREGION_TYPE = 0x0C;     /* 区域不正确 */
    static final public int CDCA_MESSAGE_NEEDFEED_TYPE = 0x0D;      /* 子卡需要和母卡对应，请插入母卡 */
    static final public int CDCA_MESSAGE_ERRCARD_TYPE = 0x0E;       /* 智能卡校验失败，请联系运营商 */
    static final public int CDCA_MESSAGE_UPDATE_TYPE = 0x0F;        /* 智能卡升级中，请不要拔卡或者关机 */
    static final public int CDCA_MESSAGE_LOWCARDVER_TYPE = 0x10;    /* 请升级智能卡 */
    static final public int CDCA_MESSAGE_VIEWLOCK_TYPE = 0x11;      /* 请勿频繁切换频道 */
    static final public int CDCA_MESSAGE_MAXRESTART_TYPE = 0x12;    /* 智能卡暂时休眠，请5分钟后重新开机 */
    static final public int CDCA_MESSAGE_FREEZE_TYPE = 0x13;        /* 智能卡已冻结，请联系运营商 */
    static final public int CDCA_MESSAGE_CALLBACK_TYPE = 0x14;      /* 智能卡已暂停，请回传收视记录给运营商 */
    static final public int CDCA_MESSAGE_CURTAIN_TYPE = 0x15;       /*高级预览节目，该阶段不能免费观看*/
    static final public int CDCA_MESSAGE_CARDTESTSTART_TYPE = 0x16; /*升级测试卡测试中...*/
    static final public int CDCA_MESSAGE_CARDTESTFAILD_TYPE = 0x17; /*升级测试卡测试失败，请检查机卡通讯模块*/
    static final public int CDCA_MESSAGE_CARDTESTSUCC_TYPE = 0x18;  /*升级测试卡测试成功*/
    static final public int CDCA_MESSAGE_NOCALIBOPER_TYPE = 0x19;   /*卡中不存在移植库定制运营商*/
    static final public int CDCA_MESSAGE_STBLOCKED_TYPE = 0x20;     /* 请重启机顶盒 */
    static final public int CDCA_MESSAGE_STBFREEZE_TYPE = 0x21;     /* 机顶盒被冻结 */
    static final public int CDCA_MESSAGE_UNSUPPORTDEVICE_TYPE = 0x30;/*不支持的终端类型（编号：0xXXXX）*/

    static final public String[] CDCA_MESSAGES = new String[]{
            " 取消当前的显示 ",
            " 无法识别卡 ",
            " 智能卡过期，请更换新卡 ",
            " 加扰节目，请插入智能卡 ",
            " 不支持节目运营商 ",
            " 条件禁播 ",
            " 当前时段被设定为不能观看 ",
            " 节目级别高于设定的观看级别 ",
            " 智能卡与本机顶盒不对应 ",
            " 没有授权 ",
            " 节目解密失败 ",
            " 卡内金额不足 ",
            " 区域不正确 ",
            " 子卡需要和母卡对应，请插入母卡 ",
            " 智能卡校验失败，请联系运营商 ",
            " 智能卡升级中，请不要拔卡或者关机 ",
            " 请升级智能卡 ",
            " 请勿频繁切换频道 ",
            " 智能卡暂时休眠，请5分钟后重新开机 ",
            " 智能卡已冻结，请联系运营商 ",
            " 智能卡已暂停，请回传收视记录给运营商 ",
            "高级预览节目，该阶段不能免费观看",
            "升级测试卡测试中...",
            "升级测试卡测试失败，请检查机卡通讯模块",
            "升级测试卡测试成功",
            "卡中不存在移植库定制运营商",
            " 请重启机顶盒 ",
            " 机顶盒被冻结 ",
            "0x22",
            "0x23",
            "0x24",
            "0x25",
            "0x26",
            "0x27",
            "0x28",
            "0x29",
            "0x2A",
            "0x2B",
            "0x2C",
            "0x2D",
            "0x2E",
            "0x2F",
            "不支持的终端类型（编号：0xXXXX）"
    };

    static final public int CDCA_SC_OUT = 0x00;
    static final public int CDCA_SC_REMOVING = 0x01;
    static final public int CDCA_SC_INSERTING = 0x02;
    static final public int CDCA_SC_IN = 0x03;
    static final public int CDCA_SC_ERROR = 0x04;
    static final public int CDCA_SC_UPDATE = 0x05;
    static final public int CDCA_SC_UPDATE_ERR = 0x06;

    static final public int CDCA_LIST_OK = 0x00;
    static final public int CDCA_LIST_FIRST = 0x01;
    static final public int CDCA_LIST_ADD = 0x02;

    static final public int CDCA_Email_IconHide = 0x00;
    static final public int CDCA_Email_New = 0x01;
    static final public int CDCA_Email_SpaceExhaust = 0x02;

    static final public int CDCA_OSD_TOP = 0x01;
    static final public int CDCA_OSD_BOTTOM = 0x02;
    static final public int CDCA_OSD_FULLSCREEN = 0x03;
    static final public int CDCA_OSD_HALFSCREEN = 0x04;

    static final public int CDCA_IPPV_FREEVIEWED_SEGMENT = 0x00;
    static final public int CDCA_IPPV_PAYVIEWED_SEGMENT = 0x01;
    static final public int CDCA_IPPT_PAYVIEWED_SEGMENT = 0x02;

    static final public int CDCA_IPPVPRICETYPE_TPPVVIEW = 0x0;
    static final public int CDCA_IPPVPRICETYPE_TPPVVIEWTAPING = 0x1;

    static final public int CDCA_IPPVSTATUS_BOOKING = 0x01;
    static final public int CDCA_IPPVSTATUS_VIEWED = 0x03;

    static final public int CDCA_Detitle_All_Read = 0x00;
    static final public int CDCA_Detitle_Received = 0x01;
    static final public int CDCA_Detitle_Space_Small = 0x02;
    static final public int CDCA_Detitle_Ignore = 0x03;

    static final public int CDCA_SCALE_RECEIVEPATCH = 1;
    static final public int CDCA_SCALE_PATCHING = 2;

    static final public int NTSM_OK = 0x0000;
    static final public int NTSM_DATA = 0x8000;
    static final public int NTSM_ERR_UNSUPPORT = 0x9100;
    static final public int NTSM_ERR_INS = 0x9200;
    static final public int NTSM_ERR_TIMEOVER = 0x9300;
    static final public int NTSM_ERR_XX = 0x9400;

    static final public int CDCAS_SYSTEM_ID = 0x4A02;

    public interface ICdcasPorting {
        boolean SetPrivateDataFilter(int reqID, byte[] filter, byte[] mask, int pid, int waitSeconds);

        void ReleasePrivateDataFilter(int reqID, int pid);

        void ScrSetCW(int ecmPid, byte[] oddKey, byte[] evenKey, boolean tapingEnabled);

        byte[] SCReset();

        byte[] SCPBRun(byte[] command);

        void EntitleChanged(int tvsID);

        void DetitleReceived(int status);

        long GetSTBID();

        int SCFunction(byte[] data);

        void StartIppvBuyDlg(int messageType, int ecmPid, CdcasAdapter.SCDCAIppvBuyInfo ippvProgram);

        void HideIPPVDlg(int ecmPid);

        void EmailNotifyIcon(int show, int emailID);

        void ShowOSDMessage(int style, String message);

        void HideOSDMessage(int style);

        void RequestFeeding(boolean readStatus);

        void LockService(CdcasAdapter.SCDCALockService lockService);

        void UNLockService();

        void ShowBuyMessage(int ecmPid, int messageType);

        void ShowFingerMessage(int ecmPID, int cardID);

        void ShowFingerMessageExt(int ecmPID, String fingerMsg);

        void ActionRequest(int tvsid, int actionType);

        void ActionRequestExt(int tvsid, int actionType, byte[] data);

        void ShowCurtainNotify(int ecmPID, int curtainCode);

        void ShowProgressStrip(int progress, int mark);

        CdcasAdapter.SCDCATime GetCurrentTime();
    }


    static {
        System.loadLibrary("cdcas_jni");
    }

    static private ICdcasPorting mCdcasPorting;

    public CdcasAdapter(ICdcasPorting porting) {
        mCdcasPorting = porting;
    }

    public void init(int threadPrior) {
        nativeInit(threadPrior);
    }

    public boolean isCDCa(int caSystemID) {
        return nativeIsCDCa(caSystemID);
    }

    public void formatBuffer() {
        nativeFormatBuffer();
    }

    public void requestMaskBuffer() {
        nativeRequestMaskBuffer();
    }

    public void requestUpdateBuffer() {
        nativeRequestUpdateBuffer();
    }

    public void setEcmPid(int type, int ecmPid, int[] serviceID) {
        nativeSetEcmPid(type, ecmPid, serviceID);
    }

    public void setEmmPid(int emmPid) {
        Log.v(LOG_TAG, "setEmmPid :" + emmPid);
        nativeSetEmmPid(emmPid);
    }

    public void privateDataGot(int reqId, boolean timeOut, int pid, byte[] receiveData) {
        Log.v(LOG_TAG, "privateDataGot :" + reqId + "\ttimeout:" + timeOut);
        nativePrivateDataGot(reqId, timeOut, pid, receiveData);
    }

    public boolean scInsert() {
        return nativeSCInsert();
    }

    public void scRemove() {
        nativeSCRemove();
    }

    public String getCardSN() {
        return nativeGetCardSN();
    }

    public int changePin(byte[] oldPin, byte[] newPin) {
        return nativeChangePin(oldPin, newPin);
    }

    public int setRating(byte[] pin, int rating) {
        return nativeSetRating(pin, rating);
    }

    public int getRating() {
        return nativeGetRating();
    }

    public int SetWorkTime(byte[] pin, WorkTime workTime) {
        return nativeSetWorkTime(pin, workTime);
    }

    public WorkTime getWorkTime() {
        return nativeGetWorkTime();
    }

    public int[] getOperatorIds() {
        return nativeGetOperatorIds();
    }

    public String getOperatorInfo(int tvsid) {
        return nativeGetOperatorInfo(tvsid);
    }

    public int getVer() {
        return nativeGetVer();
    }

    public int[] getACList(int tvsid) {
        return nativeGetACList(tvsid);
    }

    public int[] getSlotIDs(int tvsid) {
        return nativeGetSlotIDs(tvsid);
    }

    public SCDCATVSSlotInfo getSlotInfo(int tvsid, int slotId) {
        return nativeGetSlotInfo(tvsid, slotId);
    }

    public int getCardFreezeStatus(int tvsid) {
        return nativeGetCardFreezeStatus(tvsid);
    }

    public SCDCAEntitle[] getServiceEntitles(int tvsid) {
        return nativeGetServiceEntitles(tvsid);
    }

    public SCDCACurtainInfo[] getCurtainRecords(int tvsid) {
        return nativeGetCurtainRecords(tvsid);
    }

    public int[] getEntitleIDs(int tvsid) {
        return nativeGetEntitleIDs(tvsid);
    }

    public int[] getDetitleChkNums(int tvsid) {
        return nativeGetDetitleChkNums(tvsid);
    }

    public boolean getDetitleReaded(int tvsid) {
        return nativeGetDetitleReaded(tvsid);
    }

    public boolean delDetitleChkNum(int tvsid, int detitleChkNum) {
        return nativeDelDetitleChkNum(tvsid, detitleChkNum);
    }

    public int isPaired() {
        return nativeIsPaired();
    }

    public byte[] getPairedSTBIDs() {
        return nativeGetPairedSTBIDs();
    }

    public int getPlatformID() {
        return nativeGetPlatformID();
    }

    public int getTerminalTypeID() {
        return nativeGetTerminalTypeID();
    }

    public int stopIPPVBuyDlg(boolean buyProgram, int ecmPid, byte[] pin, SCDCAIPPVPrice prise) {
        return nativeStopIPPVBuyDlg(buyProgram, ecmPid, pin, prise);
    }

    public SCDCAIppvInfo[] getIPPVProgram(int tvsid) {
        return nativeGetIPPVProgram(tvsid);
    }

    public SCDCAEmailHead[] getEmailHeads(int count, int index) {
        return nativeGetEmailHeads(count, index);
    }

    public SCDCAEmailHead getEmailHead(int emailID) {
        return nativeGetEmailHead(emailID);
    }

    public String getEmailContent(int emailID) {
        return nativeGetEmailContent(emailID);
    }

    public void delEmail(int emailID) {
        nativeDelEmail(emailID);
    }

    public int getEmailSpaceInfo() {
        return nativeGetEmailSpaceInfo();
    }

    public ChildStatus GetOperatorChildStatus(int tvsid) {
        return nativeGetOperatorChildStatus(tvsid);
    }

    public byte[] readFeedDataFromParent(int tvsid) {
        return nativeReadFeedDataFromParent(tvsid);
    }

    public int writeFeedDataToChild(int tvsid, byte[] feedData) {
        return nativeWriteFeedDataToChild(tvsid, feedData);
    }

    public void refreshInterface() {
        Log.v(LOG_TAG, "refreshInterface");
        nativeRefreshInterface();
    }

    static private boolean CDSTBCA_SetPrivateDataFilter(int reqID, byte[] filter, byte[] mask,
                                                        int pid, int waitSeconds) {
        return mCdcasPorting.SetPrivateDataFilter(reqID, filter, mask, pid, waitSeconds);
    }

    static private void CDSTBCA_ReleasePrivateDataFilter(int reqID, int pid) {
        mCdcasPorting.ReleasePrivateDataFilter(reqID, pid);
    }

    static private void CDSTBCA_ScrSetCW(int ecmPID, byte[] oddKey, byte[] evenKey, boolean tapingEnabled) {
        mCdcasPorting.ScrSetCW(ecmPID, oddKey, evenKey, tapingEnabled);
    }

    static private byte[] CDSTBCA_SCReset() {
        return mCdcasPorting.SCReset();
    }

    static private byte[] CDSTBCA_SCPBRun(byte[] command) {
        return mCdcasPorting.SCPBRun(command);
    }

    static private void CDSTBCA_EntitleChanged(int tvsID) {
        mCdcasPorting.EntitleChanged(tvsID);
    }

    static private void CDSTBCA_DetitleReceived(int status) {
        mCdcasPorting.DetitleReceived(status);
    }

    static private long CDSTBCA_GetSTBID() {
        return mCdcasPorting.GetSTBID();
    }

    static private int CDSTBCA_SCFunction(byte[] data) {
        return mCdcasPorting.SCFunction(data);
    }

    static private void CDSTBCA_StartIppvBuyDlg(int messageType, int ecmPid, SCDCAIppvBuyInfo ippvProgram) {
        mCdcasPorting.StartIppvBuyDlg(messageType, ecmPid, ippvProgram);
    }

    static private void CDSTBCA_HideIPPVDlg(int ecmPid) {
        mCdcasPorting.HideIPPVDlg(ecmPid);
    }

    static private void CDSTBCA_EmailNotifyIcon(int show, int emailID) {
        Log.v(LOG_TAG, "CDSTBCA_EmailNotifyIcon(" + show + "):" + emailID);
        mCdcasPorting.EmailNotifyIcon(show, emailID);
    }

    static private void CDSTBCA_ShowOSDMessage(int style, String message) {
        Log.v(LOG_TAG, "CDSTBCA_ShowOSDMessage(" + style + "):" + message);
        mCdcasPorting.ShowOSDMessage(style, message);
    }

    static private void CDSTBCA_HideOSDMessage(int style) {
        Log.v(LOG_TAG, "CDSTBCA_HideOSDMessage:" + style);
        mCdcasPorting.HideOSDMessage(style);
    }

    static private void CDSTBCA_RequestFeeding(boolean readStatus) {
        mCdcasPorting.RequestFeeding(readStatus);
    }

    static private void CDSTBCA_LockService(SCDCALockService lockService) {
        mCdcasPorting.LockService(lockService);
    }

    static private void CDSTBCA_UNLockService() {
        mCdcasPorting.UNLockService();
    }

    static private void CDSTBCA_ShowBuyMessage(int ecmPID, int messageType) {
        Log.v(LOG_TAG, "CDSTBCA_ShowBuyMessage(" + ecmPID + "):" + messageType);
        mCdcasPorting.ShowBuyMessage(ecmPID, messageType);
    }

    static private void CDSTBCA_ShowFingerMessage(int ecmPID, int cardID) {
        Log.v(LOG_TAG, "CDSTBCA_ShowFingerMessage(" + ecmPID + "):" + cardID);
        mCdcasPorting.ShowFingerMessage(ecmPID, cardID);
    }

    static private void CDSTBCA_ShowFingerMessageExt(int ecmPID, String fingerMsg) {
        mCdcasPorting.ShowFingerMessageExt(ecmPID, fingerMsg);
    }

    static private void CDSTBCA_ActionRequest(int tvsid, int actionType) {
        mCdcasPorting.ActionRequest(tvsid, actionType);
    }

    static private void CDSTBCA_ActionRequestExt(int tvsid, int actionType, byte[] data) {
        mCdcasPorting.ActionRequestExt(tvsid, actionType, data);
    }

    static private void CDSTBCA_ShowCurtainNotify(int ecmPID, int curtainCode) {
        mCdcasPorting.ShowCurtainNotify(ecmPID, curtainCode);
    }

    static private void CDSTBCA_ShowProgressStrip(int progress, int mark) {
        mCdcasPorting.ShowProgressStrip(progress, mark);
    }

    static private SCDCATime CDSTBCA_GetCurrentTime() {
        return mCdcasPorting.GetCurrentTime();
    }

    static public class SCDCATime {
        public int mUTCTime;
        public int mTimeZoneBias;
        public int mDaylightBias;
        public boolean mInDaylight;

        public SCDCATime(int UTCTime, int timeZoneBias, int daylightBias, boolean inDaylight) {
            mUTCTime = UTCTime;
            mTimeZoneBias = timeZoneBias;
            mDaylightBias = daylightBias;
            mInDaylight = inDaylight;
        }
    }

    static public class SCDCALockService {
        public int mFrequency;
        public int mSymbolRate;
        public int mPcrPid;
        public int mModulation;
        SCDCAComponent[] mComponents;
        public int mFecOuter;
        public int mFecInner;
        String mBeforeInfo;
        String mQuitInfo;
        String mEndInfo;

        public SCDCALockService(int frequency, int symbolRate, int pcrPid, int modulation,
                                SCDCAComponent[] components, int fecOuter, int fecInner,
                                String beforeInfo, String quitInfo, String endInfo) {
            mFrequency = frequency;
            mSymbolRate = symbolRate;
            mPcrPid = pcrPid;
            mModulation = modulation;
            mComponents = components;
            mFecOuter = fecOuter;
            mFecInner = fecInner;
            mBeforeInfo = beforeInfo;
            mQuitInfo = quitInfo;
            mEndInfo = endInfo;
        }
    }

    static public class SCDCAComponent {
        public int mCompPID;
        public int mECMPID;
        public int mCompType;

        public SCDCAComponent(int compPID, int ecmPID, int compType) {
            mCompPID = compPID;
            mECMPID = ecmPID;
            mCompType = compType;
        }
    }

    static public class SCDCAIppvBuyInfo {
        public int mProductID;
        public int mTvsID;
        public int mSlotID;
        SCDCAIPPVPrice[] mPrice;
        public int mExpiredDate;
        public int mIntervalMin;

        public SCDCAIppvBuyInfo(int productID, int tvsID, int slotID,
                                SCDCAIPPVPrice[] price, int expiredDate, int intervalMin) {
            mProductID = productID;
            mTvsID = tvsID;
            mSlotID = slotID;
            mPrice = price;
            mExpiredDate = expiredDate;
            mIntervalMin = intervalMin;
        }
    }

    static public class WorkTime {
        public int mStartHour;
        public int mStartMin;
        public int mStartSec;
        public int mEndHour;
        public int mEndMin;
        public int mEndSec;

        public WorkTime(int startHour, int startMin, int startSec,
                        int endHour, int endMin, int endSec) {
            mStartHour = startHour;
            mStartMin = startMin;
            mStartSec = startSec;
            mEndHour = endHour;
            mEndMin = endMin;
            mEndSec = endSec;
        }
    }

    static public class SCDCATVSSlotInfo {
        public int mCreditLimit;
        public int mBalance;

        public SCDCATVSSlotInfo(int creditLimit, int balance) {
            mCreditLimit = creditLimit;
            mBalance = balance;
        }
    }

    static public class SCDCAEntitle {
        public int mProductID;
        public int mBeginDate;
        public int mExpireDate;
        public boolean mCanTape;

        public SCDCAEntitle(int productID, int beginDate, int expireDate, boolean canTape) {
            mProductID = productID;
            mBeginDate = beginDate;
            mExpireDate = expireDate;
            mCanTape = canTape;
        }
    }

    static public class SCDCACurtainInfo {
        public int mProgramID;
        public int mStartWatchTime;
        public int mWatchTotalCount;
        public int mWatchTotalTime;

        public SCDCACurtainInfo(int programID, int startWatchTime, int watchTotalCount, int watchTotalTime) {
            mProgramID = programID;
            mStartWatchTime = startWatchTime;
            mWatchTotalCount = watchTotalCount;
            mWatchTotalTime = watchTotalTime;
        }
    }

    static public class SCDCAIPPVPrice {
        public int mPrice;
        public int mPriceCode;

        public SCDCAIPPVPrice(int price, int priceCode) {
            mPrice = price;
            mPriceCode = priceCode;
        }
    }

    static public class SCDCAIppvInfo {
        public int mProductID;
        public int mBookEdFlag;
        public int mCanTape;
        public int mPrice;
        public int mExpiredDate;
        public int mSlotID;

        public SCDCAIppvInfo(int productID, int bookEdFlag, int canTape,
                             int price, int expiredDate, int slotID) {
            mProductID = productID;
            mBookEdFlag = bookEdFlag;
            mCanTape = canTape;
            mPrice = price;
            mExpiredDate = expiredDate;
            mSlotID = slotID;
        }
    }

    static public class SCDCAEmailHead {
        public int mActionID;
        public int mCreateTime;
        public boolean mImportance;
        public String mEmailHead;
        public boolean mNewEmail;

        public SCDCAEmailHead(int actionID, int createTime, boolean importance,
                              String emailHead, boolean newEmail) {
            mActionID = actionID;
            mCreateTime = createTime;
            mImportance = importance;
            mEmailHead = emailHead;
            mNewEmail = newEmail;
        }
    }

    static public class ChildStatus {
        public boolean mIsChild;
        public int mDelayTime;
        public int mLastFeedTime;
        String mParentCardSN;
        public boolean mCanFeed;

        public ChildStatus(boolean isChild, int delayTime, int lastFeedTime,
                           String parentCardSN, boolean canFeed) {
            mIsChild = isChild;
            mDelayTime = delayTime;
            mLastFeedTime = lastFeedTime;
            mParentCardSN = parentCardSN;
            mCanFeed = canFeed;
        }
    }

    native static private boolean nativeInit(int threadPrior);

    native static private void nativeClose();

    native static private boolean nativeIsCDCa(int caSystemID);

    native static private void nativeFormatBuffer();

    native static private void nativeRequestMaskBuffer();

    native static private void nativeRequestUpdateBuffer();

    native static private void nativeSetEcmPid(int type, int ecmPid, int[] serviceID);

    native static private void nativeSetEmmPid(int emmPid);

    native static private void nativePrivateDataGot(int reqId, boolean timeOut, int pid, byte[] receiveData);

    native static private boolean nativeSCInsert();

    native static private void nativeSCRemove();

    native static private String nativeGetCardSN();

    native static private int nativeChangePin(byte[] oldPin, byte[] newPin);

    native static private int nativeSetRating(byte[] pin, int rating);

    native static private int nativeGetRating();

    native static private int nativeSetWorkTime(byte[] pin, WorkTime workTime);

    native static private WorkTime nativeGetWorkTime();

    native static private int[] nativeGetOperatorIds();

    native static private String nativeGetOperatorInfo(int tvsid);

    native static private int nativeGetVer();

    native static private int[] nativeGetACList(int tvsid);

    native static private int[] nativeGetSlotIDs(int tvsid);

    native static private SCDCATVSSlotInfo nativeGetSlotInfo(int tvsid, int slotId);

    native static private int nativeGetCardFreezeStatus(int tvsid);

    native static private SCDCAEntitle[] nativeGetServiceEntitles(int tvsid);

    native static private SCDCACurtainInfo[] nativeGetCurtainRecords(int tvsid);

    native static private int[] nativeGetEntitleIDs(int tvsid);

    native static private int[] nativeGetDetitleChkNums(int tvsid);

    native static private boolean nativeGetDetitleReaded(int tvsid);

    native static private boolean nativeDelDetitleChkNum(int tvsid, int detitleChkNum);

    native static private int nativeIsPaired();

    native static private byte[] nativeGetPairedSTBIDs();

    native static private int nativeGetPlatformID();

    native static private int nativeGetTerminalTypeID();

    native static private int nativeStopIPPVBuyDlg(boolean buyProgram, int ecmPid, byte[] pin, SCDCAIPPVPrice prise);

    native static private SCDCAIppvInfo[] nativeGetIPPVProgram(int tvsid);

    native static private SCDCAEmailHead[] nativeGetEmailHeads(int count, int index);

    native static private SCDCAEmailHead nativeGetEmailHead(int emailID);

    native static private String nativeGetEmailContent(int emailID);

    native static private void nativeDelEmail(int emailID);

    native static private int nativeGetEmailSpaceInfo();

    native static private ChildStatus nativeGetOperatorChildStatus(int tvsid);

    native static private byte[] nativeReadFeedDataFromParent(int tvsid);

    native static private int nativeWriteFeedDataToChild(int tvsid, byte[] feedData);

    native static private void nativeRefreshInterface();
}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                    