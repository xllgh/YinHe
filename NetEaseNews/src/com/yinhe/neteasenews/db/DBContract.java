    package com.yinhe.neteasenews.db;

import android.provider.BaseColumns;

/*
 * 数据库命名规范
 * */

public class DBContract {

	public static final class TABLE_CATEGORY implements BaseColumns{
		public static String tableName="t_category";
		public static String id="id";
		public static String name="name";
		public static String ranking="ranking";
	}
	
	public static final class TABLE_NEWS implements BaseColumns{
		public static String tableName="t_news";
		public static String id="id";
		public static String title="title";
		public static String imgUrl="imgUrl";
		public static String content="content";
		
	}
	
}
