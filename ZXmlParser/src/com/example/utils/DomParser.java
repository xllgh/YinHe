package com.example.utils;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;

public class DomParser {
	
	static DocumentBuilderFactory dbf;
	static DocumentBuilder db;
	static Document d;
	private static final String xmlPath = "rivers.xml";

	
	public static void parse(Context context){
		dbf=DocumentBuilderFactory.newInstance();
		try {
			db=dbf.newDocumentBuilder();
			d=db.parse(context.getAssets().open(xmlPath));
			Element root=(Element) d.getDocumentElement();
			NodeList node=root.getElementsByTagName("river");
			River river=null;
			for(int i=0;i<node.getLength();i++){
				river=new River();
				
			}
			
			
			
			
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 
	}

}
