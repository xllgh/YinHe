package com.yinhe.neteasenews.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.yinhe.neteasenews.R;
import com.yinhe.neteasenews.entry.Category;
import com.yinhe.neteasenews.ui.MyGridView;

public class GridUndisplayAdapter extends BaseAdapter{

	private List<Category> mList;
	private Context mContext;
	private MyGridView mMyGridView;  //上一个GridView，用于焦点的重设置
	
	private UndisplayLocationListener mUndisplayLocationListener;
	
	public GridUndisplayAdapter(Context context,List<Category> array, MyGridView gridView) {
		super();
		this.mList=array;
		this.mContext=context;
		this.mMyGridView = gridView;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null){
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.item_grid, null);
			holder.btn = (Button)convertView.findViewById(R.id.btn_column);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.btn.setText(mList.get(position).getName());
		holder.btn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mUndisplayLocationListener.getUndisplayLocation(position);
			}
		});
		
		//重新控制焦点
		if (mList != null && mList.size() > 0 && position == mList.size()-1){
			mMyGridView.setFocusable(true);
			mMyGridView.setFocusableInTouchMode(true);
			mMyGridView.requestFocus();
		}
		return convertView;
	}
	
	class ViewHolder{
		Button btn;
	}

	public interface UndisplayLocationListener{
		/*在次GridView删除点击按钮*/
		void getUndisplayLocation(int location);
	}
	
	public void setUndisplayLocationListener(UndisplayLocationListener locationListener){
		mUndisplayLocationListener=locationListener;
	}
	
}
