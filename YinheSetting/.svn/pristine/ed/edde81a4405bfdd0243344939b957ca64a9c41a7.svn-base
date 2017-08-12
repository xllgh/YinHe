package com.yinhe.iptvsetting.adapter;

import java.util.List;

import android.app.Activity;
import android.net.NetworkInfo.DetailedState;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yinhe.iptvsetting.R;
import com.yinhe.iptvsetting.common.FuncUtil;
import com.yinhe.iptvsetting.object.AccessPoint;

public class WifiAdapter extends BaseAdapter {
	private List<AccessPoint> mDataList = null;
	private LayoutInflater inflater = null;
	private String[] formats = null;

	public WifiAdapter() {
		super();
	}

	public WifiAdapter(List<AccessPoint> data, Activity acitvity) {
		super();
		this.mDataList = data;
		this.inflater = acitvity.getLayoutInflater();
		this.formats = acitvity.getResources().getStringArray(
				R.array.wifi_status);
	}

	@Override
	public int getCount() {
		return mDataList.size();
	}

	@Override
	public Object getItem(int position) {
		return mDataList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHold viewHold = null;
		if (convertView == null) {
			convertView = this.inflater.inflate(R.layout.wifi_item, null);
			viewHold = new ViewHold();

			viewHold.tvName = (TextView) convertView
					.findViewById(R.id.tv_wifi_name);
			viewHold.ivLever = (ImageView) convertView
					.findViewById(R.id.iv_signal_lever);
			viewHold.tvIsConnect = (TextView) convertView
					.findViewById(R.id.tv_is_connect);

			convertView.setTag(viewHold);
		} else {
			viewHold = (ViewHold) convertView.getTag();

		}

		AccessPoint item = mDataList.get(position);
		viewHold.tvName.setText("" + item);

		if (item.mRssi == Integer.MAX_VALUE) {
			viewHold.ivLever.setImageDrawable(null);
		} else {
			viewHold.ivLever.setImageLevel(item.getLevel());
			viewHold.ivLever.setImageResource(R.drawable.wifi_signal);
			viewHold.ivLever
					.setImageState(
							(item.security != AccessPoint.SECURITY_NONE) ? AccessPoint.STATE_SECURED
									: AccessPoint.STATE_NONE, true);
		}

		DetailedState state = item.getState();
		viewHold.tvIsConnect.setText(state == null ? null : FuncUtil
				.getWifiState(state, formats));

		return convertView;
	}

	public class ViewHold {
		public TextView tvName = null;
		public ImageView ivLever = null;
		public TextView tvIsConnect = null;
	}
}
