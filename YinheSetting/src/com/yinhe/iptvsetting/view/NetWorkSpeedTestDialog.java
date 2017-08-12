
package com.yinhe.iptvsetting.view;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.LogUtil;
import com.yinhe.iptvsetting.common.PreferencesUtils;
import com.yinhe.iptvsetting.common.TimeUtils;

public class NetWorkSpeedTestDialog extends AlertDialog {

    private static final Uri CONTENT_URI = Uri.parse("content://downloads/my_downloads");
    public static final String DEFAULT_DOWNLOAD_URL = "http://www.yhrdc.com:2051/jslt_ott/yinhe-test.zip";
    public static final String DEFAULT_FILE_NAME = "yinhe-test.zip";
    public static final String SP_KEY_DOWNLOAD_URL = "nt_download_url";
    public static final String SP_KEY_DOWNLOAD_FILE_NAME = "nt_download_file_name";

    private static final int MSG_UPDATE = 1;
    private static final int MSG_UPDATE_COMPLETED = 2;
    private static final int MSG_FILE_NOT_FOUND = 3;

    private LogUtil mLogUtil = new LogUtil(NetWorkSpeedTestDialog.class);
    private Context mContext;
    private ContentObserver mContentObserver;

    private DownloadManager mDownloadManager = null;
    private File base = Environment.getExternalStorageDirectory();

    private long startTime = 0, endTime = 0;

    private long downloadedFileSize = 0, totalFileSize = 0, lastDoloadedSize = 0,
            minSize = 0, maxSize = 0;
    private long mDownloadID = -1;
    private String mDownloadedFileName = null;

    private String mDownloadUrl;
    private String mFileName;

    private boolean isStop = false;
    private EditText mEtPath;
    private EditText mEtFileName;
    private TextView mTvTotalSize;
    private TextView mTvDownLoadSize;
    private TextView mTvRealTimeSpeed;
    private TextView mTvMaxSpeed;
    private TextView mTvMinSpeed;
    private TextView mTvAverageSpeed;
    private Button mBtnDownload;

    private static final int[] KEY_CODE_FACTORY_PSW = {
            KeyEvent.KEYCODE_2, KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_8, KeyEvent.KEYCODE_2,
            KeyEvent.KEYCODE_5, KeyEvent.KEYCODE_8
    };
    private ArrayList<Integer> mEditorRecorder = new ArrayList<Integer>();
    private boolean isEditorMode = false;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE:
                    mLogUtil.d("handleMessage startTime = " + startTime);
                    if (totalFileSize != -1) {
                        long sizeSec = downloadedFileSize - lastDoloadedSize;

                        maxSize = sizeSec > maxSize ? sizeSec : maxSize;
                        if (sizeSec != 0) {
                            minSize = (minSize == 0 || sizeSec < minSize) ? sizeSec : minSize;
                        }

                        mTvTotalSize.setText(getSizePerSec(totalFileSize));
                        mTvDownLoadSize.setText(getSizePerSec(downloadedFileSize));
                        mTvRealTimeSpeed.setText(getSizePerSec(sizeSec)
                                + mContext.getString(R.string.download_each_sec));
                        lastDoloadedSize = downloadedFileSize;

                        mTvMaxSpeed.setText(getSizePerSec(maxSize)
                                + mContext.getString(R.string.download_each_sec));
                        mTvMinSpeed.setText(getSizePerSec(minSize)
                                + mContext.getString(R.string.download_each_sec));
                        endTime = TimeUtils.getCurrentTimeInLong();
                        mLogUtil.d("endTime = " + endTime);
                        mTvAverageSpeed.setText(getSizePerSec(1000 * downloadedFileSize
                                / (endTime - startTime))
                                + mContext.getString(R.string.download_each_sec));
                    }

                    if (downloadedFileSize != 0
                            && (totalFileSize == downloadedFileSize || totalFileSize == -1)) {
                        totalFileSize = 0;
                        lastDoloadedSize = 0;
                        downloadedFileSize = 0;
                        minSize = 0;
                        maxSize = 0;
                        return;
                    }
                    this.sendEmptyMessageDelayed(MSG_UPDATE, 1000);
                    break;
                case MSG_UPDATE_COMPLETED:
                    checkDownloadStatus();
                    break;
                case MSG_FILE_NOT_FOUND:
                    FuncUtil.showToast(mContext, R.string.download_err_file_not_found);
                    break;
                default:
                    break;
            }
        }
    };

    public NetWorkSpeedTestDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
        mDownloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
        mContentObserver = new ContentObserver(mHandler) {
            @Override
            public void onChange(boolean selfChange) {
                mLogUtil.d("mContentObserver onChange()");
                Cursor cursor = mDownloadManager.query(new DownloadManager.Query()
                        .setFilterById(mDownloadID));
                if (cursor == null || !cursor.moveToFirst()) {
                    return;
                }
                totalFileSize = cursor
                        .getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                downloadedFileSize = cursor.getLong(cursor
                        .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                mDownloadedFileName = cursor.getString(cursor
                        .getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                cursor.close();

                mLogUtil.d("totalFileSize = " + totalFileSize);
                mLogUtil.d("downloadedFileSize = " + downloadedFileSize);
                mLogUtil.d("mDownloadedFileName = " + mDownloadedFileName);
                checkDownloadStatus();
                super.onChange(selfChange);
            }
        };

    }

    @Override
    protected void onStart() {
        super.onStart();
        mLogUtil.d("onStart");
        mContext.getContentResolver().registerContentObserver(CONTENT_URI, true,
                mContentObserver);

        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        mContext.registerReceiver(mDownloadReciver, filter);
        new DownloadThread().start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // startFacService(keyCode);
        // disable the Download configuration in this case.
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        mLogUtil.d("onStop");
        isStop = true;
        mHandler.removeCallbacksAndMessages(null);
        if (mContentObserver != null) {
            mContext.getContentResolver().unregisterContentObserver(mContentObserver);
        }
        if (mDownloadReciver != null) {
            mContext.unregisterReceiver(mDownloadReciver);
        }
        if (mDownloadID != -1) {
            mDownloadManager.remove(mDownloadID);
        }
        deleteFile();
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mLogUtil.d("onCreate");
        View view = getLayoutInflater().inflate(R.layout.dialog_download, null);
        setView(view);
        initView(view);
        super.onCreate(savedInstanceState);
    }

    void initView(View view) {
        mEtPath = (EditText) view.findViewById(R.id.et_download_path);
        mEtFileName = (EditText) view.findViewById(R.id.et_download_file_name);
        mTvTotalSize = (TextView) view.findViewById(R.id.tv_download_total_size);
        mTvDownLoadSize = (TextView) view.findViewById(R.id.tv_download_size);
        mTvRealTimeSpeed = (TextView) view.findViewById(R.id.tv_download_real_time_speed);
        mTvAverageSpeed = (TextView) view.findViewById(R.id.tv_download_average_speed);
        mTvMaxSpeed = (TextView) view.findViewById(R.id.tv_download_max_speed);
        mTvMinSpeed = (TextView) view.findViewById(R.id.tv_download_min_speed);

        mLogUtil.d(Environment.getExternalStorageDirectory().getAbsolutePath());

        mBtnDownload = (Button) view.findViewById(R.id.btn_cancel);
        mBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditorMode) {
                    mDownloadUrl = mEtPath.getText().toString();
                    mFileName = mEtFileName.getText().toString();

                    if (FuncUtil.isNullOrEmpty(mDownloadUrl) || FuncUtil.isNullOrEmpty(mFileName)) {
                        FuncUtil.showToast(mContext, "输入不能为空！");
                        return;
                    }

                    mEtPath.setVisibility(View.GONE);
                    mEtFileName.setVisibility(View.GONE);

                    PreferencesUtils.putString(mContext, SP_KEY_DOWNLOAD_URL, mDownloadUrl);
                    PreferencesUtils.putString(mContext, SP_KEY_DOWNLOAD_FILE_NAME, mFileName);
                    mBtnDownload.setText(R.string.btn_cancel);
                    new DownloadThread().start();
                    isEditorMode = false;
                } else {
                    NetWorkSpeedTestDialog.this.dismiss();
                }
            }
        });

        mDownloadUrl = PreferencesUtils.getString(mContext, SP_KEY_DOWNLOAD_URL,
                DEFAULT_DOWNLOAD_URL);
        mFileName = PreferencesUtils.getString(mContext, SP_KEY_DOWNLOAD_FILE_NAME,
                DEFAULT_FILE_NAME);

        mLogUtil.d("Url = " + mDownloadUrl + "; fileName = " + mFileName);
        mEtPath.setText(mDownloadUrl);
        mEtFileName.setText(mFileName);
    }

    private String getSizePerSec(long size) {
        return Formatter.formatFileSize(mContext, size);
    }

    class DownloadThread extends Thread {
        @Override
        public void run() {
            super.run();
            startTime = TimeUtils.getCurrentTimeInLong();
            mLogUtil.d("DownloadThread startTime = " + startTime);
            mHandler.sendEmptyMessage(MSG_UPDATE);
            DownloadManager.Request dmReq = new DownloadManager.Request(
                    Uri.parse(mDownloadUrl));
            dmReq.setMimeType("application/zip");
            Uri mDestinationUri = Uri.withAppendedPath(Uri.fromFile(base),
                    mFileName);
            dmReq.setDestinationUri(mDestinationUri);

            File file = new File(base.getAbsolutePath() + "/" + mFileName);
            while (file.exists()) {
                if (isStop) {
                    break;
                }

                file.delete();
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if (isTargetFileReachable()) {
                mDownloadID = mDownloadManager.enqueue(dmReq);
                mLogUtil.d("mDownloadID = " + mDownloadID);
            }
        }
    }

    private void startFacService(int keyCode) {
        mEditorRecorder.add(keyCode);
        int recordSize = mEditorRecorder.size();
        if ((mEditorRecorder.get(recordSize - 1)) != KEY_CODE_FACTORY_PSW[recordSize - 1]) {
            mEditorRecorder.clear();
        } else if (KEY_CODE_FACTORY_PSW.length == recordSize) {
            mEditorRecorder.clear();
            isEditorMode = true;
            mEtPath.setVisibility(View.VISIBLE);
            mEtFileName.setVisibility(View.VISIBLE);
            mBtnDownload.setText(R.string.btn_sure);
        }
    }

    private BroadcastReceiver mDownloadReciver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String aciton = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(aciton)) {
                Bundle extras = intent.getExtras();
                if (mDownloadID == extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID)) {
                    mHandler.sendEmptyMessage(MSG_UPDATE_COMPLETED);
                }
            }
        }
    };

    private void checkDownloadStatus() {
        mLogUtil.d("checkDownloadStatus()");
        Cursor cursor = mDownloadManager.query(new DownloadManager.Query()
                .setFilterById(mDownloadID));
        if (cursor == null || !cursor.moveToFirst()) {
            return;
        }

        int iStatus = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        mLogUtil.d("******iStatus:" + iStatus);
        mLogUtil.d("DownloadManager.STATUS_SUCCESSFUL:" + DownloadManager.STATUS_SUCCESSFUL);
        if (DownloadManager.STATUS_SUCCESSFUL == iStatus) {
            deleteFile();
        } else if (DownloadManager.STATUS_FAILED == iStatus) {
            try
            {
                int reason = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                printerrorlog(reason);
                mDownloadManager.remove(mDownloadID);
                mDownloadID = -1;
            } catch (Exception e) {
                e.printStackTrace();
                mLogUtil.e("download again error e:" + e.toString());
            }
        }

        cursor.close();
    }

    private void printerrorlog(int reason) {
        mLogUtil.d("printerrorlog=" + reason);
        switch (reason)
        {
            case DownloadManager.ERROR_CANNOT_RESUME: // 不能够继续，由于一些其他原因。
                mLogUtil.d("******ERROR_CANNOT_RESUME");
                break;
            case DownloadManager.ERROR_DEVICE_NOT_FOUND: // 外部存储设备没有找到，比如SD卡没有插入。
                mLogUtil.d("******ERROR_DEVICE_NOT_FOUND");
                break;
            case DownloadManager.ERROR_FILE_ALREADY_EXISTS: // 要下载的文件已经存在了，Android123提示下载管理类是不会覆盖已经存在的文件，所以如果需要重新下载，请先删除以前的文件。
                mLogUtil.d("******ERROR_FILE_ALREADY_EXISTS");
                break;
            case DownloadManager.ERROR_FILE_ERROR: // 可能由于SD卡原因导致了文件错误。
                mLogUtil.d("******ERROR_FILE_ERROR");
                break;
            case DownloadManager.ERROR_INSUFFICIENT_SPACE: // 由于SD卡空间不足造成的 。
                mLogUtil.d("******ERROR_INSUFFICIENT_SPACE");
                break;
            case DownloadManager.ERROR_TOO_MANY_REDIRECTS: // 这个Http有太多的重定向，导致无法正常下载
                mLogUtil.d("******ERROR_TOO_MANY_REDIRECTS");
                break;
            case DownloadManager.ERROR_UNHANDLED_HTTP_CODE: // 无法获取http出错的原因，比如说远程服务器没有响应。
                mLogUtil.d("******ERROR_UNHANDLED_HTTP_CODE");
                break;
            case DownloadManager.ERROR_UNKNOWN: // 未知的错误类型.
                mLogUtil.d("******ERROR_UNKNOWN");
                break;
            default:
                break;
        }

        FuncUtil.showToast(mContext, "测速文件下载失败(错误码：" + reason + ")，请重新测速。");
    }

    private void deleteFile() {
        File file = new File(base.getAbsolutePath() + "/" + mFileName);
        if (file.exists()) {
            file.delete();
        }

        if (!FuncUtil.isNullOrEmpty(mDownloadedFileName)) {
            File downloadedFile = new File(mDownloadedFileName);
            if (downloadedFile.exists()) {
                downloadedFile.delete();
            }
        }
    }

    private boolean isTargetFileReachable() {
        mLogUtil.d("isTargetFileReachable()");
        try {
            URL url = new URL(mDownloadUrl);
            URLConnection mURLConnection = url.openConnection();
            mURLConnection.setConnectTimeout(5000);
            InputStream is = mURLConnection.getInputStream();

            if (is != null) {
                return true;
            }
        } catch (Exception e)
        {
            mLogUtil.e(e.getMessage());
        }

        mHandler.sendEmptyMessage(MSG_FILE_NOT_FOUND);
        return false;
    }

}
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                               