package com.example.hellotest;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class RemoteService extends Service{
	
	private IRemoteListener.Stub listener=new IRemoteListener.Stub() {
		
		@Override
		public void valuesChange(int values) throws RemoteException {
			// TODO Auto-generated method stub
			Log.e("ccccccccccc", "student.getAge"+values);
			
		}
	};
	
	
	
	private final IRemoteService.Stub mBinder=new IRemoteService.Stub() {
		Student student;
		IRemoteListener listener;
		@Override
		public int getPid() throws RemoteException {
			// TODO Auto-generated method stub
			return 7;
		}
		
		@Override
		public void basicType(int tInt, long tLong) throws RemoteException {
			// TODO Auto-generated method stub
		}

		@Override
		public Student getStudent() throws RemoteException {
			// TODO Auto-generated method stub
			return student;
		}

		@Override
		public void setStudent(Student student) throws RemoteException {
			// TODO Auto-generated method stub
			this.student=student;
			listener.valuesChange(student.getAge());
		}

		@Override
		public void setOnIRemoteServiceChangeListener(IRemoteListener listener)
				throws RemoteException {
			// TODO Auto-generated method stub
			this.listener=listener;
			
		}
		
	};

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		Log.e(" Server AIDL Server", "onBind");
		Student student=new Student();
		student.setAge(100);
		student.setId("777");
		student.setName("HAHA");
		
		Student student2=new Student();
		student2.setAge(55);
		student2.setId("888");
		student.setName("qqq");
		try {
			mBinder.setOnIRemoteServiceChangeListener(listener);
			mBinder.setStudent(student);
			mBinder.setStudent(student2);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mBinder;
	}

}
