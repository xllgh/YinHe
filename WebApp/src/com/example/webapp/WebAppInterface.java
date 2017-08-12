package com.example.webapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

public class WebAppInterface {
	Context context;
	WebView webView;
	
	public WebAppInterface(Context context,WebView webView) {
		this.context=context;
		this.webView=webView;
	}
	
	@JavascriptInterface
	public void javaInvokeHtml1(){
		((Activity)context).runOnUiThread(new Runnable(){

			@Override
			public void run() {
				webView.loadUrl("javascript:javaInvokeHtml('haha')");
			}
		});
	}

	@JavascriptInterface
	public void htmlInvokeJava1(String toast) {
		Log.e(">>>>>>>>>>>>>>", "showToast");
		Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
	}

	@JavascriptInterface
	public String htmlInvokeJava2() {
		return "htmlInvokeJava2";

	}

}
