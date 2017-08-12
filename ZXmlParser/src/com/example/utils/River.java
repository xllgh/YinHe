package com.example.utils;

import java.util.HashMap;

public class River {
	private String intro;
	private String imgUrl;
	HashMap<String,String> metaMap;
	
	public River() {
		metaMap=new HashMap<String,String>();
	}
	
	public void saveMetaData(String key ,String value){
		metaMap.put(key, value);
	}
	
	public HashMap<String,String> getMetaData(){
		return metaMap;
	}
	
	public String getIntro() {
		return intro;
	}
	public void setIntro(String intro) {
		this.intro = intro;
	}
	public String getImgUrl() {
		return imgUrl;
	}
	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}
	
	@Override
	public String toString() {
		return "intro:"+intro+"\n"
				+"imgUrl:"+imgUrl+"\n"
				+metaMap.toString()+"\n\n";
	}
	

}
