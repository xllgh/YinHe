package com.example.testcontentprovider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {
	public static final String databaseName="ContentProviderDB";
	public static final String table1Name="table1";
	public static final String table2Name="table2";
	public static final String createTable1="CREATE TABLE table1(NAME TEXT,SCORE INTEGER)";
	public static final String createTable2="CREATE TABLE table2(NAME TEXT, GENDAR TEXT)";
	
	
	public DBHelper(Context context) {
		super(context,databaseName,null,1);
	}

	private DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(createTable1);
		db.execSQL(createTable2);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
	}

}
