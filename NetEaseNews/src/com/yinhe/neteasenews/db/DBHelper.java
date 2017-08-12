package com.yinhe.neteasenews.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.yinhe.neteasenews.db.DBContract.TABLE_CATEGORY;
import com.yinhe.neteasenews.db.DBContract.TABLE_NEWS;

/*
 * 数据库管理辅助类
 * */


public class DBHelper extends SQLiteOpenHelper{

	private static final int DATABASE_VERSION=1;
	private static final String DATABASE_NAME="netease_news";
	private static final String TEXT_TYPE=" TEXT ";
	private static final String INTEGER_TYPE=" INTEGER ";
	private static final String CREATE_CATEGORY_TABLE="CREATE TABLE "+TABLE_CATEGORY.tableName+"("
			+TABLE_CATEGORY.id+INTEGER_TYPE+" PRIMARY KEY AUTOINCREMENT ,"
			+TABLE_CATEGORY.name+TEXT_TYPE+","
			+TABLE_CATEGORY.ranking+INTEGER_TYPE+");";
	
	

	private static final String CREATE_NEWS_TABLE="CREATE TABLE "+TABLE_NEWS.tableName+"("
			+TABLE_NEWS.id+INTEGER_TYPE+" PRIMARY KEY AUTOINCREMENT ,"
			+TABLE_NEWS.title+TEXT_TYPE+","
			+TABLE_NEWS.imgUrl+TEXT_TYPE+","
			+TABLE_NEWS.content+TEXT_TYPE+");";
	
	private static  DBHelper helper=null;
	
	public DBHelper(Context context, String name, CursorFactory factory,
			int version) {
		super(context, DATABASE_NAME, factory, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}
	
	public DBHelper(Context context){
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_CATEGORY_TABLE);
		db.execSQL(CREATE_NEWS_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	}

}
