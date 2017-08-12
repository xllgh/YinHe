package com.yinhe.neteasenews.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.yinhe.neteasenews.db.DBContract.TABLE_CATEGORY;
import com.yinhe.neteasenews.db.DBContract.TABLE_NEWS;
import com.yinhe.neteasenews.entry.Category;

public class DBUtils {
	Context context;
	DBHelper dbHelper;
	SQLiteDatabase db;

	public DBUtils(Context context) {
		this.context = context;
		dbHelper = new DBHelper(context);
	}

	public long addCategory(Category cate) {
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TABLE_CATEGORY.id, cate.getId());
		values.put(TABLE_CATEGORY.name, cate.getName());
		values.put(TABLE_CATEGORY.ranking, cate.getRanking());
		long row = db.insert(TABLE_CATEGORY.tableName, null, values);
		return row;
	}

	public void addCategoryList(List<Category> cateList) {
		for (Category cate : cateList) {
			addCategory(cate);
		}
	}
	
	public void addManyCategory(ArrayList<String> list){
		db = dbHelper.getWritableDatabase();
		for (String str : list) {
			ContentValues values = new ContentValues();
			values.put(TABLE_CATEGORY.name, str);
			db.insert(TABLE_CATEGORY.tableName, null, values);
		}
	}

	public long changeCategoryRank(int id, int rank) {
		db = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(TABLE_CATEGORY.ranking, rank + "");
		long row = db.update(TABLE_CATEGORY.tableName, values, TABLE_CATEGORY.id+"=?",
				new String[] { id+"" });
		db.close();
		return row;
	}

	public long delCategory(String cName) {
		db = dbHelper.getWritableDatabase();
		long row = db.delete(TABLE_CATEGORY.tableName, TABLE_CATEGORY.name
				+ "=?", new String[] { cName });
		db.close();
		return row;
	}

	public ArrayList<Category> getCategoryList() {
		ArrayList<Category> listCate = new ArrayList<Category>();
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CATEGORY.tableName, null, TABLE_CATEGORY.ranking+"!=-1", null,
				null, null, TABLE_CATEGORY.ranking);
		if (cursor == null) {
			return null;
		}
		Category cate = null;
		int indexID = cursor.getColumnIndex(TABLE_CATEGORY.id);
		int indexName = cursor.getColumnIndex(TABLE_CATEGORY.name);
		int indexRanking = cursor.getColumnIndex(TABLE_CATEGORY.ranking);
		while (cursor.moveToNext()) {
			cate = new Category(cursor.getInt(indexID),
					cursor.getString(indexName), cursor.getInt(indexRanking));
			listCate.add(cate);
		}
		db.close();
		return listCate;
	}
	
	public ArrayList<Category> getHideCategoryList() {
		ArrayList<Category> listCate = new ArrayList<Category>();
		db = dbHelper.getReadableDatabase();
		Cursor cursor = db.query(TABLE_CATEGORY.tableName, null, TABLE_CATEGORY.ranking+"==-1", null,
				null, null, TABLE_CATEGORY.ranking);
		if (cursor == null) {
			return null;
		}
		Category cate = null;
		int indexID = cursor.getColumnIndex(TABLE_CATEGORY.id);
		int indexName = cursor.getColumnIndex(TABLE_CATEGORY.name);
		int indexRanking = cursor.getColumnIndex(TABLE_CATEGORY.ranking);
		while (cursor.moveToNext()) {
			cate = new Category(cursor.getInt(indexID),
					cursor.getString(indexName), cursor.getInt(indexRanking));
			listCate.add(cate);
		}
		db.close();
		return listCate;
	}

	// public void insertManyNews(ArrayList<HashMap<String, String>> list) {
	// if (list == null && list.size() < 1)
	// return;
	// db = dbHelper.getReadableDatabase();
	// ContentValues values = new ContentValues();
	// for (HashMap<String, String> map : list) {
	// values.put(TABLE_NEWS.imgUrl, map.get(TABLE_NEWS.imgUrl));
	// values.put(TABLE_NEWS.content, map.get(TABLE_NEWS.content));
	// values.put(TABLE_NEWS.title, map.get(TABLE_NEWS.title));
	// db.insert(TABLE_NEWS.tableName, null, values);
	// }
	// }
	//
	// public ArrayList<HashMap<String, String>> getNews() {
	//
	// db = dbHelper.getReadableDatabase();
	// Cursor cursor = db.query(TABLE_NEWS.tableName, null, null, null, null,
	// null, null);
	// if (cursor == null)
	// return null;
	//
	// ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String,
	// String>>();
	// while (cursor.moveToNext()) {
	// HashMap<String, String> map = new HashMap<String, String>();
	// map.put(TABLE_NEWS.title,
	// cursor.getString(cursor.getColumnIndex(TABLE_NEWS.title)));
	// map.put(TABLE_NEWS.imgUrl,
	// cursor.getString(cursor.getColumnIndex(TABLE_NEWS.imgUrl)));
	// map.put(TABLE_NEWS.content,
	// cursor.getString(cursor.getColumnIndex(TABLE_NEWS.content)));
	// map.put(TABLE_NEWS.imgUrl,
	// cursor.getString(cursor.getColumnIndex(TABLE_NEWS.imgUrl)));
	// list.add(map);
	// }
	// return list;
	// }
}
