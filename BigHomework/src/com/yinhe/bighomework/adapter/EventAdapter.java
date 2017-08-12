package com.yinhe.bighomework.adapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yinhe.bighomework.obj.EITInfo;
import com.yinhe.bighomwork.R;

public class EventAdapter extends BaseAdapter {

	ArrayList<EITInfo> list;
	Context context;

	public EventAdapter(Context context, ArrayList<EITInfo> list) {
		this.list = list;
		this.context = context;

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		
		ViewHolder holder = new ViewHolder();
		View itemView = LayoutInflater.from(context).inflate(R.layout.item_eit,
				null);
		holder.view=itemView;
		holder.serverName = (TextView) itemView.findViewById(R.id.serverName);
		holder.time = (TextView) itemView.findViewById(R.id.time);
		holder.duration = (TextView) itemView.findViewById(R.id.duration);
		EITInfo eitInfo = list.get(position);

		String time = new SimpleDateFormat(
				context.getString(R.string.timeFomart)).format(new Date(eitInfo
				.getStartTime()));
//		String duration=eitInfo.getDuration()/60000+"";
		String name=eitInfo.getName();
		
//		holder.duration.setText(duration);
		holder.time.setText(time);
		holder.serverName.setText(name);
		return itemView;
	}

	class ViewHolder {
		View view;
		TextView serverName;
		TextView time;
		TextView duration;
	}

}
