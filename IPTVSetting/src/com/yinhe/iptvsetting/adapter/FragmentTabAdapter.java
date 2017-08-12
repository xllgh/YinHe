package com.yinhe.iptvsetting.adapter;

import java.util.List;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.LinearLayout;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.view.BaseFragment;

public class FragmentTabAdapter {
	private FragmentActivity fragmentActivity = null;
	private List<BaseFragment> fragments = null;
	private int fragmentContentId;
	private LinearLayout parentContainer = null;// 垂直包容TextVuew 的父控件，用它来获取
												// title的个数
	private int currentTab = -1;

	public FragmentTabAdapter() {
		super();
	}

	public FragmentTabAdapter(FragmentActivity fragmentActivity,
			List<BaseFragment> fragments, int fragmentContentId,
			LinearLayout parentContainer) {
		super();
		this.fragmentActivity = fragmentActivity;
		this.fragments = fragments;
		this.fragmentContentId = fragmentContentId;
		this.parentContainer = parentContainer;
	}

	public void showFocuseFragment(View view) {
		for (int i = 0; i < this.parentContainer.getChildCount(); i++) {
			if (view.getId() == this.parentContainer.getChildAt(i).getId()) {

				BaseFragment fragment = this.fragments.get(i);
				if (fragment == getCorrentFragment()) {
					return;
				}
				if (currentTab != -1) {
					getCorrentFragment().onPause();
				}

				if (fragment.isAdded()) {
					fragment.onResume();
				} else {
					FragmentTransaction ft = obtainFragmentTransaction(i);
					ft.add(fragmentContentId, fragment);
					ft.commitAllowingStateLoss();
				}
				showFragment(i);
			}
		}
	}

	public FragmentTransaction obtainFragmentTransaction(int index) {
		FragmentTransaction ft = this.fragmentActivity
				.getSupportFragmentManager().beginTransaction();
		if (index > currentTab) {
			ft.setCustomAnimations(R.anim.slide_left_in, R.anim.slide_left_out);
		} else {
			ft.setCustomAnimations(R.anim.slide_right_in,
					R.anim.slide_right_out);
		}
		return ft;

	}

	public BaseFragment getCorrentFragment() {
		return currentTab == -1 ? null : this.fragments.get(currentTab);
	}

	public int getCorrentFragmentNumber() {
		return currentTab;
	}

	public void showFragment(int index) {
		for (int i = 0; i < this.fragments.size(); i++) {
			BaseFragment fragment = this.fragments.get(i);
			FragmentTransaction ft = obtainFragmentTransaction(index);
			if (index == i) {
				ft.show(fragment);
			} else {
				ft.hide(fragment);
			}
			ft.commitAllowingStateLoss();
		}

		currentTab = index;
	}
}
