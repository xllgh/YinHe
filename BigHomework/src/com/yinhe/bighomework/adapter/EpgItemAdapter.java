package com.yinhe.bighomework.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yinhe.bighomework.obj.EITInfo;
import com.yinhe.bighomwork.R;

public class EpgItemAdapter extends BaseAdapter {

	private List<EITInfo> itemList = new ArrayList<EITInfo>();
	private Context mCtx;

	public EpgItemAdapter(Context mCtx, List<EITInfo> itemList) {
		this.itemList = itemList;
		this.mCtx = mCtx;
	}

	public void setPeopleList(List<EITInfo> itemList) {
		this.itemList = itemList;
	}

	@Override
	public int getCount() {
		return itemList.size();
	}

	@Override
	public Object getItem(int position) {
		return itemList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		if (convertView != null && convertView.getTag() != null) {
			viewHolder = (ViewHolder) convertView.getTag();
		} else {
			viewHolder = new ViewHolder();

			convertView = LayoutInflater.from(mCtx).inflate(R.layout.item_epg,
					null);

			viewHolder.nameTV = (TextView) convertView.findViewById(R.id.name);
			viewHolder.timeTV = (TextView) convertView.findViewById(R.id.time);
			viewHolder.durationTV = (TextView) convertView
					.findViewById(R.id.duration);

			convertView.setTag(viewHolder);

		}

		EITInfo item = itemList.get(position);
		viewHolder.nameTV.setText(item.getName());
		viewHolder.timeTV.setText(String.format(
				mCtx.getString(R.string.playTime), item.getStartTime()));
		viewHolder.durationTV.setText(String.format(
				mCtx.getString(R.string.playEndtime), item.getEndTime()));
		return convertView;

	}

	static class ViewHolder {
		TextView nameTV;
		TextView timeTV;
		TextView durationTV;
	}
}
