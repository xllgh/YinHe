package com.example.hellotest;

import android.os.Parcel;
import android.os.Parcelable;

public class Student implements Parcelable{
	private int age;
	private String name;
	private String id;
	
	
	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public int getAge() {
		return age;
	}


	public void setAge(int age) {
		this.age = age;
	}

	


	@Override
	public String toString() {
		return "Student [age=" + age + ", name=" + name + ", id=" + id + "]";
	}


	public Student(){
		
	}
	
	
	private Student(Parcel in){
		age=in.readInt();
		name=in.readString();
		id=in.readString();
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeInt(age);
		dest.writeString(name);
		dest.writeString(id);
		
	}
	
	public static final Parcelable.Creator<Student> CREATOR=
			new Parcelable.Creator<Student>() {

				@Override
				public Student createFromParcel(Parcel source) {
					// TODO Auto-generated method stub
					return new Student(source);
				}

				@Override
				public Student[] newArray(int size) {
					// TODO Auto-generated method stub
					return new Student[size];
				}
			};

}
