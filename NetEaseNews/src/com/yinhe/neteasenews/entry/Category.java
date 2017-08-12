package com.yinhe.neteasenews.entry;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

public class Category implements Parcelable{

	private int id;
	private String name;
	private int ranking;
	
	public Category(){
		
	}
	
	public Category(int id,String name,int ranking){
		this.id = id;
		this.name = name;
		this.ranking = ranking;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(ranking);
	}
	public static final Parcelable.Creator<Category> CREATOR = new Parcelable.Creator<Category>() {

		@Override
		public Category createFromParcel(Parcel source) {
			int id = source.readInt();
			String name = source.readString();
			int ranking = source.readInt();
			return new Category(id,name, ranking);
		}

		@Override
		public Category[] newArray(int size) {
			return new Category[size];
		}
	};

}
