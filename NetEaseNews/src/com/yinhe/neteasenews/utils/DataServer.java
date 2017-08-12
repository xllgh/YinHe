package com.yinhe.neteasenews.utils;

import java.util.List;

import android.content.Context;

import com.yinhe.neteasenews.entry.Category;
import com.yinhe.neteasenews.entry.News;

public class DataServer {
	
	private Context mContext;
	
	public DataServer(Context context){
		mContext  = context;
	}
	
	public void changeCateRanking(int cateId,int ranking){
		
	}
	
	public List<Category> getCateList(){
		return null;
	}
	
	public List<News> getNewsList(int cateId){
		return new XMLParse(mContext).getNewsList(0);
	}

}
