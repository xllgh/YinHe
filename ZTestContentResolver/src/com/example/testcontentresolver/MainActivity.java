package com.example.testcontentresolver;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {
	Button btnQuery;
	Button btnDelete;
	Button btnInsert;
	ContentResolver mContentResolver;
	int i = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnQuery = (Button) findViewById(R.id.query);
		btnDelete = (Button) findViewById(R.id.delete);
		btnInsert = (Button) findViewById(R.id.insert);

		btnQuery.setOnClickListener(this);
		btnDelete.setOnClickListener(this);
		btnInsert.setOnClickListener(this);

		mContentResolver = getContentResolver();
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.query: {
			Uri uri = Uri
					.parse("content://com.example.testcontentprovider.ExampleProvider/table1");

			Cursor cursor = mContentResolver.query(uri, null, null, null, null);
			while (cursor != null && cursor.moveToNext()) {
				Log.e("xll", cursor.getString(0) + "," + cursor.getString(1));
			}
			break;
		}
		case R.id.delete:

			break;

		case R.id.insert: {
			Uri uri = Uri
					.parse("content://com.example.testcontentprovider.ExampleProvider/table1");
			ContentValues values = new ContentValues();
			values.put("NAME", "name" + i++);
			values.put("SCORE", "score" + i++);
			mContentResolver.insert(uri, values);
			break;
		}

		default:
			break;
		}

	}
}
