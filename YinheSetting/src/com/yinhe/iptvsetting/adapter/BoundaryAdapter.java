package com.yinhe.iptvsetting.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SeekBar;
import android.widget.TextView;

import com.yinhe.iptvsetting.R;

/**
 * 画面边界Adapter。
 * 
 * @author zhbn
 * 
 */
public class BoundaryAdapter extends BaseAdapter {

	private static final String TAG = "BoundaryAdapter";

	private LayoutInflater mInflater;

	private Context mContext;

	private String[] mArryItems;

	/**
	 * 各个边界的值。
	 */
	private int mPercents[];

	public BoundaryAdapter(Context context, int arrs[]) {
		super();
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mPercents = arrs;
		mArryItems = mContext.getResources().getStringArray(R.array.boundary);
	}

	@Override
	public int getCount() {
		return mPercents.length;
	}

	@Override
	public Object getItem(int position) {
		return mPercents[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setData(int index, int value) {
		if (mPercents != null || mPercents.length > index) {
			mPercents[index] = value;
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Log.d(TAG, "getView position = " + position);
		Holder holder;
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.range_bar, null);
			holder = new Holder();
			holder.textView = (TextView) convertView.findViewById(R.id.title);
			holder.seekBar = (SeekBar) convertView.findViewById(R.id.range_bar);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		holder.textView.setText(mArryItems[position]);
		holder.seekBar.setProgress(mPercents[position]);

		return convertView;
	}

	class Holder {
		TextView textView;
		SeekBar seekBar;
	}
}
