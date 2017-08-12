package com.example.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.util.Xml;

public class PullParse {
	private static final String xmlPath = "rivers.xml";
	XmlPullParser xmlPullParser = Xml.newPullParser();
	InputStream mInputStream;
	Context context;
	River river;
	ArrayList<River> list=new ArrayList<River>();

	public PullParse(Context context) {
		this.context = context;
	}
	
	public ArrayList<River> getRivers(){
		return list;
	}

	public void parse() {
		try {
			mInputStream = context.getAssets().open(xmlPath);
			xmlPullParser.setInput(mInputStream, "utf-8");
			int tagEvent = xmlPullParser.getEventType();
			while (tagEvent != XmlPullParser.END_DOCUMENT) {
				String tagName = xmlPullParser.getName();
				switch (tagEvent) {
				case XmlPullParser.START_TAG:
					if (tagName.equalsIgnoreCase("river")) {
						river = new River();
						for (int i = 0; i < xmlPullParser.getAttributeCount(); i++) {
							river.saveMetaData(
									xmlPullParser.getAttributeName(i),
									xmlPullParser.getAttributeValue(i));
						}

					}else if(tagName.equalsIgnoreCase("introduction")){
						if(river!=null){
							river.setIntro(xmlPullParser.nextText());
						}
					}else if(tagName.equalsIgnoreCase("imageurl")){
						if(river!=null){
							river.setImgUrl(xmlPullParser.nextText());
						}
					}
					break;
					
					
				case XmlPullParser.END_TAG:
					if(xmlPullParser.getName().equalsIgnoreCase("river")){
						list.add(river);
						river=null;
					}
					break;

				default:
					break;
				}
				
				tagEvent=xmlPullParser.nextTag();
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
