package com.yinhe.bighomework.utils;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yinhe.bighomwork.R;


public class ScanServiceBaseAdapter extends BaseAdapter{
	
	ArrayList<ServiceInfo> list;
	Context context;
	public ScanServiceBaseAdapter(Context context,ArrayList<ServiceInfo> list){
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
		holder.channal.setText(list.get(position).getProgramName());
		return itemView;
	}
	
	
	class ViewHolder{
		TextView channal;
	}

}
