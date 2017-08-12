package com.example.aidlclient;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.example.hellotest.IRemoteService;
import com.example.hellotest.Student;

public class MainActivity extends Activity {

	private static String TAG = "AIDL Client";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		Intent intent2=new Intent("com.example.hellotest.RemoteService");
		bindService(intent2, mConnection, Context.BIND_AUTO_CREATE);
	}

	protected void onDestroy() {
		unbindService(mConnection);
	};
	
	IRemoteService mIRemoteService;
	

	private ServiceConnection mConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onServiceDisconnected");
			mIRemoteService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			Log.e(TAG, "onServiceConnected");
			int pid;
			mIRemoteService = IRemoteService.Stub.asInterface(service);
			try {
				pid=mIRemoteService.getPid();
				Student student=mIRemoteService.getStudent();
				Log.e("student", student.toString());
				Log.e(TAG, "getPid:"+pid);
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	};

}
