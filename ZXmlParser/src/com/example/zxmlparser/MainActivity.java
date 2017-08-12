package com.example.zxmlparser;

import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.os.Bundle;
import android.util.Xml;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.example.utils.PullParse;
import com.example.utils.River;
import com.example.utils.SAXHandler;

public class MainActivity extends Activity implements OnClickListener{
	Button xmlSax;
	Button xmlPull;
	Button xmlUtil;
	Button xmlDom;
	TextView xmlContent;
	private static final String xmlPath="rivers.xml";
	ArrayList<River> rivers=new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		findView();
	}
	
	
	public void findView(){
		xmlSax=(Button)findViewById(R.id.xmlSax);
		xmlPull=(Button) findViewById(R.id.xmlPull);
		xmlUtil=(Button) findViewById(R.id.xmlUtil);
		xmlDom=(Button) findViewById(R.id.xmlDom);
		xmlContent=(TextView) findViewById(R.id.xmlContent);
		
		xmlSax.setOnClickListener(this);
		xmlPull.setOnClickListener(this);
		xmlUtil.setOnClickListener(this);
		xmlDom.setOnClickListener(this);
	}


	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.xmlSax:
			try {
				SAXParserFactory spf=SAXParserFactory.newInstance();
				SAXParser sp=spf.newSAXParser();
				XMLReader xmlReader=sp.getXMLReader();
				SAXHandler mSaxHandler=new SAXHandler();
				xmlReader.setContentHandler(mSaxHandler);
				xmlReader.parse(new InputSource(this.getAssets().open(xmlPath)));
				rivers.clear();
				if(mSaxHandler.getRivers()!=null){
					rivers.addAll(mSaxHandler.getRivers());
				}
				xmlContent.setText(rivers.toString());
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			break;
			
		case R.id.xmlPull:
			PullParse pullParse=new PullParse(MainActivity.this);
			pullParse.parse();
			rivers.clear();
			rivers.addAll(pullParse.getRivers());
			xmlContent.setText("xmlPull:\n"+rivers.toString());
			break;
			
		case R.id.xmlUtil:
			SAXHandler mSaxHandler=new SAXHandler();
			try {
				Xml.parse(this.getAssets().open(xmlPath), Xml.Encoding.UTF_8, mSaxHandler);
				rivers.clear();
				if(mSaxHandler.getRivers()!=null){
					rivers.addAll(mSaxHandler.getRivers());
				}
				xmlContent.setText(rivers.toString());
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case R.id.xmlDom:
			break;

		default:
			break;
		}
		
	}


	 
}
