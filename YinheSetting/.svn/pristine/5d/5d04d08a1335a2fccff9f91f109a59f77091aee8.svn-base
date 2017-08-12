package com.yinhe.iptvsetting.adapter;

import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

public class FragmentPagerAdapter extends
		android.support.v4.app.FragmentPagerAdapter {

	private List<Fragment> data = null;
	
	public FragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	public FragmentPagerAdapter(FragmentManager fm ,List<Fragment> data) {
		super(fm);
		// TODO Auto-generated constructor stub
		this.data = data;
	}
	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		return this.data.get(arg0);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return super.getItemPosition(object);
	}
	
}
