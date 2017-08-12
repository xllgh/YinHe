package com.example.testcontentprovider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class DBUtils {
	Context context;
	DBHelper dbHelper;
	
	public  DBUtils(Context context){
		this.context=context;
		dbHelper=new DBHelper(context);
	}
	
	public void insertTable1(ContentValues values){
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		db.insert("table1", null, values);
	}
	
	public void insertTable2(ContentValues values){
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		db.insert("table2", null, values);
	}
	
//	Uri uri, String[] projection, String selection,
//	String[] selectionArgs, String sortOrder
	public Cursor queryTable1(String[] projection,String selection,String[] selectionArgs){
		SQLiteDatabase db=dbHelper.getReadableDatabase();
		Cursor cursor=db.query("table1", projection, selection, selectionArgs, null, null, null);
		return cursor;
	}
	
	public Cursor queryTable2(String[] projection,String selection,String[] selectionArgs){
		SQLiteDatabase db=dbHelper.getReadableDatabase();
		Cursor cursor=db.query("table2", projection, selection, selectionArgs, null, null, null);
		return cursor;
	}
	
	public int  deleteTable1Content(String selection,String[] selectionArgs){
		int row=-1;
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		row=db.delete("table1", selection, selectionArgs);
		return row;
	}
	
	public int deleteTable2Content(String selection,String[] selectionArgs){
		int row=-1;
		SQLiteDatabase db=dbHelper.getWritableDatabase();
		row=db.delete("table2", selection, selectionArgs);
		return row;
	}

}
