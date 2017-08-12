package com.yinhe.iptvsetting.view;

import android.app.Activity;
import android.content.Context;


/**
 * 分辨率转换类
 * 
 * @author hqq
 * */
public class DensityUtil {

	public static int getScreenHeight(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getHeight();
	}
	
	public static int getScreenWidth(Activity activity) {
		return activity.getWindowManager().getDefaultDisplay().getWidth();
	}
	
	
	/**
	 * 根据分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue){
		
		final float scale = context.getResources().getDisplayMetrics().density;
		
		return (int) (dpValue * scale + 0.5f);
		
	}
	
	
	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
}
