package com.yinhe.bighomework.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yinhe.bighomework.obj.SDTInfo;
import com.yinhe.bighomwork.R;

public class ServiceAdapter extends BaseAdapter{
	
	ArrayList<SDTInfo> list;
	Context context;
	public ServiceAdapter(Context context,ArrayList<SDTInfo> list){
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
	
	int firstIndex=0;

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View itemView=LayoutInflater.from(context).inflate(R.layout.item_channal, null);
		ViewHolder holder=new ViewHolder();

		if(list.size()>0){
			firstIndex=Integer.valueOf(list.get(0).getIndex());
		}
		
		SDTInfo sdtInfo=list.get(position);
		holder.channal=(TextView) itemView.findViewById(R.id.channal);
		holder.index=(TextView) itemView.findViewById(R.id.index);
		holder.frequency=(TextView) itemView.findViewById(R.id.frequency);
		String name=sdtInfo.getServerName();
		name=TextUtils.isEmpty(name)?context.getString(R.string.unkown):name;
		holder.channal.setText(name);

		int currentIndex=Integer.valueOf(sdtInfo.getIndex());
		holder.index.setText(currentIndex-firstIndex+1+"");
		holder.frequency.setText(String.format(context.getString(R.string.tvFrequency), sdtInfo.getFrequency()));
		return itemView;
	}
	
	
	class ViewHolder{
		TextView channal;
		TextView index;
		TextView frequency;
	}

}
