package com.yinhe.neteasenews.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.content.Context;
import android.os.Environment;

import com.yinhe.neteasenews.entry.Category;
import com.yinhe.neteasenews.entry.News;

public class XMLParse {

	private Context mContext;

	public XMLParse(Context context) {
		mContext = context;
	}

	public List<News> getNewsList(int cateId) {
		 String fileName = mContext.getFilesDir().toString() + "/server/"
		 + cateId + "/newslist.xml";

//		String fileName = Environment.getExternalStorageDirectory()
//				+ File.separator + "WangYi" + File.separator + cateId
//				+ "/newslist.xml";
		String fileUri = new File(fileName).toURI().toString();
		List<News> newsList = new ArrayList<News>();
		News news = null;
		boolean isValidNews = false;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(fileUri);
			NodeList rootNodeList = document.getChildNodes();
			for (int i = 0; i < rootNodeList.getLength(); i++) {
				Node rootNode = rootNodeList.item(i);
				NodeList nodeList = rootNode.getChildNodes();
				for (int j = 0; j < nodeList.getLength(); j++) {
					Node node = nodeList.item(j);
					NodeList newsMeta = node.getChildNodes();
					isValidNews = false;
					news = new News();
					for (int k = 0; k < newsMeta.getLength(); k++) {
						if (newsMeta.item(k).getNodeName() != "#text") {
							isValidNews = true;
							if ("thumbnail".equals(newsMeta.item(k)
									.getNodeName())) {
								news.setThumbnail(newsMeta.item(k)
										.getTextContent());
							} else if ("title".equals(newsMeta.item(k)
									.getNodeName())) {
								news.setTitle(newsMeta.item(k).getTextContent());
							} else if ("summary".equals(newsMeta.item(k)
									.getNodeName())) {
								news.setSummary(newsMeta.item(k)
										.getTextContent());
							} else if ("cateid".equals(newsMeta.item(k)
									.getNodeName())) {
								news.setCateId(Integer.parseInt(newsMeta
										.item(k).getTextContent()));
							} else {

							}
						}
					}
					if (isValidNews) {
						newsList.add(news);
					} else {
						;
					}
				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return newsList;
	}

	public List<Category> getDefaultCates() {
		String fileName = mContext.getFilesDir().toString()
				+ "/server/catelist.xml";
		String fileUri = new File(fileName).toURI().toString();
		List<Category> cateList = new ArrayList<Category>();
		Category cate = null;
		boolean isValidCate = false;
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document document = db.parse(fileUri);
			NodeList rootNodeList = document.getChildNodes();
			for (int i = 0; i < rootNodeList.getLength(); i++) {
				Node rootNode = rootNodeList.item(i);
				NodeList nodeList = rootNode.getChildNodes();
				for (int j = 0; j < nodeList.getLength(); j++) {
					Node node = nodeList.item(j);
					NodeList newsMeta = node.getChildNodes();
					isValidCate = false;
					cate = new Category();
					for (int k = 0; k < newsMeta.getLength(); k++) {
						if (newsMeta.item(k).getNodeName() != "#text") {
							isValidCate = true;
							if ("id".equals(newsMeta.item(k).getNodeName())) {
								cate.setId(Integer.parseInt(newsMeta.item(k)
										.getTextContent()));
							} else if ("name".equals(newsMeta.item(k)
									.getNodeName())) {
								cate.setName(newsMeta.item(k).getTextContent());
							} else if ("ranking".equals(newsMeta.item(k)
									.getNodeName())) {
								cate.setRanking(Integer.parseInt(newsMeta.item(
										k).getTextContent()));
							} else {

							}
						}
					}
					if (isValidCate) {
						cateList.add(cate);
					} else {
						;
					}
				}
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return cateList;
	}

}
