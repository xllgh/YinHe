package com.example.testcontentprovider;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class ExampleProvider extends ContentProvider{
	private static UriMatcher mUirMatcher=new UriMatcher(1);//TODO
	private DBUtils dbUtils=null;
	
	static {
		mUirMatcher.addURI("com.example.testcontentprovider.ExampleProvider", "table1", 1);
		mUirMatcher.addURI("com.example.testcontentprovider.ExampleProvider", "table2", 2);
	}
	
	
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int row=-2;
		switch (mUirMatcher.match(uri)) {
		case 1:
			row=dbUtils.deleteTable1Content(selection, selectionArgs);
			break;
			
		case 2:
			row=dbUtils.deleteTable2Content(selection, selectionArgs);
			break;

		default:
			break;
		}
		return row;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (mUirMatcher.match(uri)) {
		case 1:
			dbUtils.insertTable1(values);			
			break;
			
		case 2:
			dbUtils.insertTable2(values);
			break;

		default:
			break;
		}
		return uri;
	}

	@Override
	public boolean onCreate() {
		dbUtils=new DBUtils(this.getContext().getApplicationContext());
		mUirMatcher.addURI("com.example.testcontentprovider", "table1", 1);
		mUirMatcher.addURI("com.example.testcontentprovider", "table2", 2);
		return true;
	}
	
	private int getMatchCode(Uri uri){
		
		return -1;
	}
	

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		Cursor cursor=null;
		
		switch(mUirMatcher.match(uri)){
		case 1:
			cursor=dbUtils.queryTable1(projection, selection, selectionArgs);
			break;
			
		case 2:
			cursor=dbUtils.queryTable2(projection, selection, selectionArgs);
			break;
		}
		
		
		return cursor;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
