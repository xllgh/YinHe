package com.example.hellotest ;
import com.example.hellotest.Student;
import com.example.hellotest.IRemoteListener;
interface IRemoteService{

	int getPid();
	
	Student getStudent();
	
	void setStudent(in Student student);
	
	void basicType(int tInt,long tLong);
	
	void setOnIRemoteServiceChangeListener(in IRemoteListener listener);
	
	
}