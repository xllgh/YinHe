package com.yinhe.neteasenews.entry;

public class News {
	
	private String thumbnail;
	private String title;
	private String summary;
	private int cateId;
	
	public News(){
		
	}
	
	public News(String thumbnail,String title,String summary,int cateId){
		this.thumbnail = thumbnail;
		this.title = title;
		this.summary = summary;
		this.cateId = cateId;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public int getCateId() {
		return cateId;
	}

	public void setCateId(int cateId) {
		this.cateId = cateId;
	}
	
	
}
