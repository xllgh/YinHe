package com.yinhe.neteasenews.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class NewsFragmentAdapter  extends FragmentStatePagerAdapter {

	static final int NUM_ITEMS=100;
	ArrayList<Fragment> list;
	public NewsFragmentAdapter(FragmentManager fm,ArrayList<Fragment> list) {
		super(fm);
		this.list=list;
	
	}

	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return list.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

}
