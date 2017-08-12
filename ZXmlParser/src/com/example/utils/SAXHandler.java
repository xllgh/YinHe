package com.example.utils;

import java.util.ArrayList;
import java.util.List;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.text.TextUtils;
import android.util.Log;

public class SAXHandler extends DefaultHandler{
	
	private List<River> rivers;
	private River river;
	private String tagName;
	
	
	public List<River> getRivers(){
		return rivers;
	}
	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		Log.e("xll startDocument" ," ");
		rivers=new ArrayList<River>();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		super.startElement(uri, localName, qName, attributes);
		Log.e("xll startElement", "uri:"+uri+" localName:"+localName+" qName:"+qName+" Atrributes:"+attributes.toString());
	    tagName=localName.length()!=0?localName:qName;
		if(tagName.equalsIgnoreCase("river")){
			river=new River();
			for(int i=0;i<attributes.getLength();i++){
				river.saveMetaData(attributes.getLocalName(i), attributes.getValue(i));
			}
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		String content=new String(ch,start,length);
		content=TextUtils.isEmpty(content)?null:content.trim();
		Log.e("xll characters",tagName+"  content:"+content);
		super.characters(ch, start, length);
		if(!TextUtils.isEmpty(tagName)&&!TextUtils.isEmpty(content)){
			if(tagName.equalsIgnoreCase("introduction")){
				river.setIntro(new String(ch,start,length));
			}else if(tagName.equalsIgnoreCase("imageurl")){
				river.setImgUrl(new String(ch,start,length));
			}
		}
	}
	
	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		Log.e("xll endElement", "uri:"+uri+" localName:"+localName+" qName:"+qName);

		tagName=localName.length()!=0?localName:qName;
		if(!TextUtils.isEmpty(tagName)&&tagName.equalsIgnoreCase("river")){
			rivers.add(river);
			river=null;
		}
		tagName=null;
	}
	
	
	@Override
	public void endDocument() throws SAXException {
		super.endDocument();
		Log.e("xll endDocument" ," ");

	}

}
