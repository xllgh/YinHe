package com.yinhe.bighomework.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yinhe.bighomework.obj.ChannelInfo;
import com.yinhe.bighomwork.R;

public class MyBaseAdapter extends BaseAdapter{
	
	ArrayList<ChannelInfo> list;
	Context context;
	public MyBaseAdapter(Context context,ArrayList<ChannelInfo> list){
		this.list=list;
		this.context=context;
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
		View itemView=LayoutInflater.from(context).inflate(R.layout.item_channal, null);
		ViewHolder holder=new ViewHolder();
		holder.channal=(TextView) itemView.findViewById(R.id.channal);
		String name=list.get(position).getChannelName().trim();
		name=TextUtils.isEmpty(name)?context.getString(R.string.unkown):name;
		holder.channal.setText(name);
		return itemView;
	}
	
	
	class ViewHolder{
		TextView channal;
	}

}
