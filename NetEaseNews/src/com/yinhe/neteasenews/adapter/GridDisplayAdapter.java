package com.yinhe.neteasenews.adapter;

import java.util.List;

import android.content.Context;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.yinhe.neteasenews.R;
import com.yinhe.neteasenews.entry.Category;

public class GridDisplayAdapter extends BaseAdapter {

	private List<Category> mList;
	private Context mContext;
	private boolean mIsDeleteBtnVisible; //删除图片是否显示
	private boolean mIsTopButton; //是否是第一牌控件

	private LocationListener mLocationListener;

	public GridDisplayAdapter(Context context, List<Category> array,
			PopupWindow popupWindow, TextView tvMore) {
		super();
		this.mList = array;
		this.mContext = context;
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
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_grid, null);
			holder.btn = (Button) convertView.findViewById(R.id.btn_column);
			holder.iv = (ImageView) convertView.findViewById(R.id.iv_delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.btn.setText(mList.get(position).getName());

		//是否显示删除图片
		if (mIsDeleteBtnVisible) {
			holder.iv.setVisibility(View.VISIBLE);
		} else {
			holder.iv.setVisibility(View.GONE);
		}

		//长按Button，显示删除图片
		holder.btn.setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
				mIsDeleteBtnVisible = !mIsDeleteBtnVisible;
				notifyDataSetChanged();
				return true;
			}
		});
		holder.btn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mLocationListener.getLocation(position, mIsDeleteBtnVisible);
			}
		});

		setKeyListener(holder.btn, position);

		return convertView;
	}

	/**
	 * function:处理GridView的第一排Button的向上按键事件
	 * @param btn
	 * @param position
	 */
	private void setKeyListener(Button btn, int position) {
		if (position < 8) {
			btn.setOnKeyListener(new OnKeyListener() {

				@Override
				public boolean onKey(View v, int keyCode, KeyEvent event) {
					if (KeyEvent.KEYCODE_DPAD_UP == keyCode) {
						if(mIsTopButton){
							mLocationListener.changeFocusable();
							mIsTopButton = !mIsTopButton;
							return true;
						}
						mIsTopButton = !mIsTopButton;
					}
					if (KeyEvent.KEYCODE_DPAD_DOWN == keyCode){
						mIsTopButton = false;
					}
					return false;
				}
			});
		}
	}

	class ViewHolder {
		Button btn;
		ImageView iv;
	}

	public interface LocationListener {
		
		/*点击GridView中的button时调用此方法，关闭PopupWindow*/
		void getLocation(int location, boolean visible);

		/*监听遥控事件发生时调用此方法*/
		void changeFocusable();
	}

	public void setLocationListener(LocationListener locationListener) {
		mLocationListener = locationListener;
	}

}
