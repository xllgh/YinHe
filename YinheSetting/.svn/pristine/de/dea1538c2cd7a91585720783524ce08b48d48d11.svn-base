
package com.yinhe.iptvsetting.database;

import java.io.File;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.common.LogUtil;

public final class DBHelper {
    private LogUtil mLogUtil = new LogUtil(DBHelper.class);

    private static final String DATABASE_PATH = "/private/cwmp_agent_conf/tr.db";
    private static final String DATABASE_TABLE = "tr";

    private File orignalDBFile;
    private SQLiteDatabase mDB;

    private int maxId = -1;

    private static DBHelper sInstance;

    public static DBHelper getInstance() {
        if (sInstance == null) {
            sInstance = new DBHelper();
        }

        return sInstance;
    }

    private DBHelper() {
        orignalDBFile = new File(DATABASE_PATH);
        if (!orignalDBFile.exists()) {
            mLogUtil.e("DBFile no exists!");
            return;
        }
        mDB = SQLiteDatabase.openDatabase(orignalDBFile.getPath(), null,
                SQLiteDatabase.OPEN_READWRITE | SQLiteDatabase.NO_LOCALIZED_COLLATORS);
    }

    public String query(String[] columns,
            String[] columnArgs) {
        int selectionsLength = columns == null ? 0 : columns.length;
        int selectionArgsLength = columnArgs == null ? 0 : columnArgs.length;
        String ret = FuncUtil.STR_EMPTY;
        if (selectionsLength != selectionArgsLength ) {
            mLogUtil.e("query illegal params");
            return ret;
        }
        if (!isDBValid()) {
            return ret;
        }
        
        String selection = null;
        if (selectionsLength > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(columns[0]).append("=?");
            for (int i = 1; i < selectionsLength; i++) {
                sb.append(" and ").append(columns[i]).append("=?");
            }
            selection = sb.toString();
        }
        
        String[] sColum = {
                "value"
        };
        Cursor cur = mDB.query(DATABASE_TABLE, sColum, selection, columnArgs, null, null, null);
        if (cur != null) {
            if (cur.moveToFirst()) {
                ret = cur.getString(0);
            }
            cur.close();
        }
        return ret;
    }

    private boolean select(String selection,
            String[] selectionArgs) {
        boolean ret = false;
        if (!isDBValid()) {
            return false;
        }
        Cursor cursor = mDB.query(DATABASE_TABLE, null, selection, selectionArgs, null, null, null);
        if (cursor == null) {
            return false;
        }
        ret = cursor.moveToFirst();
        cursor.close();

        Cursor cursor2 = mDB.rawQuery("select max(id) from tr", null);
        if (cursor2 != null) {
            if (cursor2.moveToFirst()) {
                maxId = cursor2.getInt(0);
            }
            cursor2.close();
        }

        return ret;
    }

    /**
     * if the DB contains the data match columns and columnArgs, update
     * otherwise insert.
     * 
     * @param columns Columns
     * @param columnArgs The values for Columns
     * @param valueArg the value of "value" column
     */
    public void insertOrUpdate(String[] columns, String[] columnArgs, String valueArg) {
        int selectionsLength = columns == null ? 0 : columns.length;
        int selectionArgsLength = columnArgs == null ? 0 : columnArgs.length;
        if (selectionsLength != selectionArgsLength || FuncUtil.isNullOrEmpty(valueArg)) {
            mLogUtil.e("insertOrUpdate illigal params");
            return;
        }
        final ContentValues cv = new ContentValues();
        cv.put("value", valueArg);

        String selection = null;
        if (selectionsLength > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append(columns[0]).append("=?");
            cv.put(columns[0], columnArgs[0]);
            for (int i = 1; i < selectionsLength; i++) {
                sb.append(" and ").append(columns[i]).append("=?");
                cv.put(columns[i], columnArgs[i]);
            }
            selection = sb.toString();
        }

        long ret = -1;
        if (!select(selection, columnArgs)) {
            if (isDBValid()) {
                cv.put("id", (maxId == -1 ? 999 : maxId + 1));
                cv.put("pid", 999);
                ret = mDB.insert(DATABASE_TABLE, null, cv);
            }
        } else {
            if (isDBValid()) {
                ret = mDB.update(DATABASE_TABLE, cv, selection, columnArgs);
            }
        }

        if (ret == -1) {
            mLogUtil.e("insertOrUpdate error");
        }
    }

    public static void close() {
        if (sInstance != null) {
            sInstance.closeDatabase();
        }
    }

    private void closeDatabase() {
        if (mDB != null && mDB.isOpen()) {
            mDB.close();
        }
    }

    private boolean isDBValid() {
        if (mDB == null) {
            if (orignalDBFile == null) {
                orignalDBFile = new File(DATABASE_PATH);
            }
            if (!orignalDBFile.exists()) {
                mLogUtil.e("orignalDBFile no exists!");
                return false;
            }
        }

        if (!mDB.isOpen()) {
            mDB = SQLiteDatabase.openDatabase(orignalDBFile.getPath(), null,
                    SQLiteDatabase.OPEN_READWRITE);
        }
        return mDB != null && mDB.isOpen();
    }
}
