package com.yinhe.iptvsetting.adapter;

import java.util.ArrayList;

import com.yinhe.iptvsetting.R;
import com.yinhe.model.ListViewItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CheckItemAdapter extends BaseAdapter {
	private ArrayList<ListViewItem> mList;
	private Context mContext;
	private LayoutInflater mInflater;
	private int mSeleterPosition = -1;

	public CheckItemAdapter(ArrayList<ListViewItem> mList, Context context) {
		super();
		this.mList = mList;
		this.mContext = context;
		this.mInflater = LayoutInflater.from(mContext);
	}

	public CheckItemAdapter(int selectPosition) {
		this.mSeleterPosition = selectPosition;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		Holder holder;
		ListViewItem item = mList.get(position);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.check_list, null);
			holder = new Holder();
			holder.imageView = (ImageView) convertView
					.findViewById(R.id.img_check_status);
			holder.textView = (TextView) convertView
					.findViewById(R.id.tv_item_content);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}
		holder.textView.setText(item.getName() + "");
		if (mSeleterPosition == position) {
			holder.imageView.setVisibility(View.VISIBLE);
		} else {
			holder.imageView.setVisibility(View.INVISIBLE);
		}
		return convertView;
	}

	class Holder {
		TextView textView;
		ImageView imageView;
	}

	public void setdata(int seclect) {
		mSeleterPosition = seclect;
	}
}
