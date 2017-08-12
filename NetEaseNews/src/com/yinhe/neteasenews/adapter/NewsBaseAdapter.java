package com.yinhe.neteasenews.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.yinhe.neteasenews.R;
import com.yinhe.neteasenews.entry.News;

public class NewsBaseAdapter extends BaseAdapter {

	Context mContext;
	private List<News> mListNews;

	public NewsBaseAdapter(Context context,
			List<News> list) {
		this.mContext = context;
		mListNews = list;
	}

	@Override
	public int getCount() {
		return mListNews.size();
	}

	@Override
	public Object getItem(int position) {
		return mListNews.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		News news = mListNews.get(position);
		ViewHolder holder = new ViewHolder();
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.item_news_single, null);
		holder.img = (ImageView) view.findViewById(R.id.imgView);
		holder.title = (TextView) view.findViewById(R.id.title);
		holder.content = (TextView) view.findViewById(R.id.content);
		String imgUrl = news.getThumbnail();
		String title = news.getTitle();
		String content = news.getSummary();

		if (imgUrl == null) {
			holder.img.setBackgroundResource(R.drawable.ic_launcher);
		} else {
			
			Drawable drawable = Drawable.createFromPath(mContext.getFilesDir().toString()+"/server"+imgUrl);
			Log.e(this.getClass().getName(), imgUrl);
			holder.img.setBackgroundDrawable(drawable);
		}

		holder.title.setText(title);
		holder.content.setText(content);

		return view;
	}

	class ViewHolder {
		ImageView img;
		TextView title;
		TextView content;
	}
}
