package com.yinhe.neteasenews.adapter;

import java.util.List;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

public class PicturePager extends PagerAdapter{
	private List<View> list;
	private int number;
	
	
	public PicturePager(List<View> list,int number){
		this.list=list;
		this.number=number;
	}
	

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0==arg1;
	}
	
	
	@Override
	public float getPageWidth(int position) {
		// TODO Auto-generated method stub
		return 1.0f/number;
	}
	
	
	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		// TODO Auto-generated method stub
		((ViewPager)container).addView(list.get(position));
		return list.get(position);
	}
	
	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		// TODO Auto-generated method stub
		((ViewPager)container).removeView(list.get(position));
		
	}

}
