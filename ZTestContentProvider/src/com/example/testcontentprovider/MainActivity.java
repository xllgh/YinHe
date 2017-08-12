package com.example.testcontentprovider;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;

public class MainActivity extends Activity {
	DBUtils dbUtils;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		dbUtils=new DBUtils(this);
		ContentValues contentValues=new ContentValues();
		contentValues.put("NAME", "xll");
		contentValues.put("SCORE", "100");
		dbUtils.insertTable1(contentValues);
		
	}

	
}
