package com.yinhe.dtv.ca;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.yinhe.dtv.DtvBuffer;
import com.yinhe.dtv.DtvChannel;
import com.yinhe.dtv.DtvDemux;
import com.yinhe.dtv.DtvFilter;
import com.yinhe.dtv.DtvManager;
import com.yinhe.dtv.DtvSignal;
import com.yinhe.dtv.cdcas.CdcasAdapter;

/**
 * Created by HB on 2016/8/11.
 */
public class CdcasDemux extends Thread {

    final private CdcasAdapter mCdcasAdapter;
    final private DtvDemux mDemux;
    final private DtvSignal mDtvSignal;
    final private HashMap<Integer, Request> mRequestMap;
    final private HashMap<Integer, ChannelAndBuffer> mChannelAndBufferMap;

    public CdcasDemux(CdcasAdapter adapter) {
        mCdcasAdapter = adapter;
        mRequestMap = new HashMap<>();
        mChannelAndBufferMap = new HashMap<>();
        mDemux = DtvManager.getInstance().getDemux(0);
        mDtvSignal = mDemux.createSignal();
        start();
    }

    @Override
    public void run() {
        while (true) {
            boolean signaled = mDtvSignal.wait(100);
            checkTimeout();
            if (!signaled) continue;
            checkData();
        }
    }

    public void request(int reqId, byte[] filter, byte[] mask, int pid, int waitSeconds) {

        int id = (pid << 16) | (reqId & 0xFFFF);

        Request r = new Request();
        r.mRequestId = reqId;
        r.mWaitSeconds = waitSeconds;
        r.mPid = pid;
        r.mMatch = filter;
        r.mMask = mask;
        r.mStartTime = System.nanoTime() / 1000000;

        synchronized (mRequestMap) {
            cancelNoLock(id);
            getChannelAndBuffer(r);
            mRequestMap.put(id, r);
        }
    }

    public void cancel(int reqId, int pid) {
        synchronized (mRequestMap) {
            cancelNoLock((pid << 16) | (reqId & 0xFFFF));
        }
    }

    public void cancelAll() {
        LinkedList<Request> cancelList = new LinkedList<>();

        synchronized (mRequestMap) {
            for (Request r : mRequestMap.values()) {
                if ((r.mRequestId & 0x80) == 0x80) {
                    cancelList.add(r);
                }
            }
            for (Request r : cancelList) {
                cancelNoLock((r.mPid << 16) | (r.mRequestId & 0xFFFF));
            }
        }
    }

    private void cancelNoLock(int id) {
        Request r = mRequestMap.remove(id);
        if (r == null) return;

        releaseChannelAndBuffer(r);
        r.mFilter.release();
    }

    private void getChannelAndBuffer(Request r) {
        ChannelAndBuffer cb = mChannelAndBufferMap.get(r.mPid);
        if (cb == null) {
            cb = new ChannelAndBuffer();
            cb.mBuffer = mDemux.createBuffer(1024 * 64);
            cb.mChannel = mDemux.createChannel(DtvChannel.SECTION,
                    DtvDemux.DEMUX_CHANNEL_CRC_BY_SYNTAX_AND_DISCARD, 1024 * 16);
            cb.mBuffer.associateSignal(mDtvSignal);
            cb.mChannel.setPid(r.mPid);
            cb.mChannel.linkBuffer(cb.mBuffer);
            cb.mChannel.start();
            mChannelAndBufferMap.put(r.mPid, cb);
        }

        byte[] negate = new byte[r.mMatch.length];
        Arrays.fill(negate, (byte) 0);
        r.mFilter = mDemux.createFilter(r.mMask, r.mMatch, negate);
        r.mFilter.associateChannel(cb.mChannel);

        cb.mRequestSet.add(r);
    }

    private void releaseChannelAndBuffer(Request r) {
        ChannelAndBuffer cb = mChannelAndBufferMap.get(r.mPid);
        if (cb == null) return;

        cb.mRequestSet.remove(r);
        if (cb.mRequestSet.size() == 0) {
            cb.mChannel.stop();
            cb.mChannel.unlinkBuffer();
            cb.mBuffer.disassociateSignal();
            cb.mChannel.release();
            cb.mBuffer.release();
            mChannelAndBufferMap.remove(r.mPid);
        }
    }

    private void checkTimeout() {
        LinkedList<Request> timeOutList = new LinkedList<>();
        long time = System.nanoTime() / 1000000;

        synchronized (mRequestMap) {
            for (Request r : mRequestMap.values()) {
                if (r.mWaitSeconds == 0) continue;
                if (time - r.mStartTime < r.mWaitSeconds * 2000) continue;
                timeOutList.add(r);
            }
            for (Request r : timeOutList) {
                if ((r.mRequestId & 0x80) == 0x80) {
                    cancelNoLock(r.mRequestId | (r.mPid << 16));
                }
            }
        }
        for (Request r : timeOutList) {
            mCdcasAdapter.privateDataGot(r.mRequestId, true, r.mPid, null);
        }
    }

    private void checkData() {
        LinkedList<Request> dataList = new LinkedList<>();

        synchronized (mRequestMap) {
            for (ChannelAndBuffer cb : mChannelAndBufferMap.values()) {
                byte[][] data = cb.mBuffer.readData(1);
                if (data == null || data.length == 0) continue;

                if (cb.mRequestSet.size() == 1) {
                    for (Request r : cb.mRequestSet) {
                        r.mData = data[0];
                        dataList.add(r);
                    }
                } else {
                    for (Request r : cb.mRequestSet) {
                        if (!dataMatch(data[0], r.mMatch, r.mMask)) continue;
                        r.mData = data[0];
                        dataList.add(r);
                    }
                }
            }
            for (Request r : dataList) {
                if ((r.mRequestId & 0x80) == 0x80) {
                    cancelNoLock(r.mRequestId | (r.mPid << 16));
                }
            }
        }
        for (Request r : dataList) {
            mCdcasAdapter.privateDataGot(r.mRequestId, false, r.mPid, r.mData);
        }
    }

    private static boolean dataMatch(byte[] data, byte[] match, byte[] mask) {
        if (data.length < match.length) return false;
        int len = match.length;
        if (len > mask.length) len = mask.length;

        for (int di, i = 0; i < len; i++) {
            if (i > 0) di = i + 2;
            else di = i;

            if ((data[di] & mask[i]) != (match[i] & mask[i])) return false;
        }
        return true;
    }


    private static class Request {
        public int mRequestId;
        public int mPid;
        public byte[] mMatch;
        public byte[] mMask;
        public DtvFilter mFilter;
        public int mWaitSeconds;
        public long mStartTime;
        public byte[] mData;
    }

    private static class ChannelAndBuffer {
        public DtvBuffer mBuffer;
        public DtvChannel mChannel;
        public HashSet<Request> mRequestSet;

        public ChannelAndBuffer() {
            mRequestSet = new HashSet<>();
        }
    }

}
