/**
 * 
 */
package com.its.core.util;

import java.io.File;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * 创建日期 2012-9-20 下午01:49:59
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class XMLParser {
	private static final Log log = LogFactory.getLog(XMLParser.class);
	
	private String xmlString = null;
	
	private File xmlFile = null;
	
	private String encoding = "UTF-8";
	
	private Document document = null;
	
	private Map<String,String> propertyCache = new HashMap<String,String>();
	
	public XMLParser(String xmlString) throws Exception{
		this.xmlString = xmlString;
		this.document = this.parseDocument(this.xmlString);
	}
	
	public XMLParser(String xmlString,String encoding) throws Exception{
		this.xmlString = xmlString;
		this.encoding = encoding;
		this.document = this.parseDocument(this.xmlString);
	}	
	
	public XMLParser(File xmlFile) throws Exception{
		this.xmlFile = xmlFile;
		this.document = this.parseDocument(this.xmlFile);
	}	
	
	/**
	 * Returns the value of the specified property.
	 * @param name the name of the property to get.
	 * @return the value of the specified property.
	 */
	public String getProperty(String name) {	
		if (propertyCache.containsKey(name)) {
			return (String) this.getPropertyCache().get(name);
		}		
		Element element = this.getDocument().getRootElement();
		return this.getProperty(element, name, name);
	}
	
	private String getProperty(Element element,String name,String key) {
		if(element==null) return null;	
		
		/*
		if (propertyCache.containsKey(key)) {
			return (String) this.getPropertyCache().get(key);
		}
		*/
		
		String resultValue = null;		
		String[] propName = this.parsePropertyName(name);
		int len = propName.length;
		for (int i = 0; i < len; i++) {
			//System.out.println(i+"\t"+propName[i]+"\t"+element);			
			if(i==len-1){
				resultValue = element.getTextTrim();			
			}
			else{
				String attributeValue = element.getAttributeValue(propName[i+1]);
				element = element.getChild(propName[i+1]);
				if(element==null && StringHelper.isNotEmpty(attributeValue) && i==len-2){
					resultValue = attributeValue;
					break;
				}
			}
			if(element==null){
				resultValue = null;
				break;
			}
		}
		
		if(resultValue!=null){
			this.getPropertyCache().put(key, resultValue);
		}
		return resultValue;
	}	
	
	public int getPropertyNum(String name){		
		return this.getPropertyNum(this.getDocument().getRootElement(), name);
	}
	
	public String getProperty(String prefix,int no,String postfix) {		
		String key = prefix+"."+no+"."+postfix;
		if (this.getPropertyCache().containsKey(key)) {
			return (String) this.getPropertyCache().get(key);
		}		
		Element ele = this.getElement(prefix,no);
		if(ele!=null){
			return this.getProperty(ele,postfix,key);
		}
		return null;
	}	
	
	private Element getElement(String prefix,int no) {
		String[] propName = parsePropertyName(prefix);
		int len = propName.length;
		List eleList = null;
			
		Element element = this.getDocument().getRootElement();			
		for (int i = 0; i < len;) {
			//System.out.println(element);
			if(i==len-1){
				//if(element.getChild(propName[i])!=null) 
				eleList = element.getChildren();
				break;
			}
			else{			
				i++;
				element = element.getChild(propName[i]);			
				if (element == null) {
					break;		
				}
			}
		}
		
		if(eleList!=null){
			int eleSize = eleList.size();
			if(no>eleSize-1){
				return null;
			}
			Element tmpEle = (Element)eleList.get(no);
			return tmpEle;		
		}
				
		return null;
	}	
	
	private int getPropertyNum(Element element,String name){
		//System.out.println(element);
		int num = 0;
		String[] propName = this.parsePropertyName(name);
		int len = propName.length;
		for (int i = 0; i < len;) {
			if(i==len-1){
				num = element.getChildren().size();
				break;
			}
			else{	
				i++;
				//System.out.println("# = "+propName[i]);
				element = element.getChild(propName[i]);
				//System.out.println(i+"="+element);
				//System.out.println("==============");
				if (element == null) {						
					break;	
				}
			}
		}
		return num;		
	}	
	
	public void clear(){
		this.propertyCache.clear();
	}
	
	public Map<String,String> getPropertyCache() {
		return propertyCache;
	}

	public void setPropertyCache(Map<String,String> propertyCache) {
		this.propertyCache = propertyCache;
	}

	public String getXmlString() {
		return xmlString;
	}

	public void setXmlString(String xmlString) {
		this.xmlString = xmlString;
	}
		

	public Document getDocument() {
		return document;
	}

	public void setDocument(Document document) {
		this.document = document;
	}

	private Document parseDocument(String xmlString) throws Exception{
		Document doc = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			DataUnformatFilter format = new DataUnformatFilter();
			builder.setXMLFilter(format);
			
			if(StringHelper.isEmpty(this.encoding)){
				doc = builder.build(new java.io.ByteArrayInputStream(xmlString.getBytes()));
			}
			else{
				doc = builder.build(new java.io.ByteArrayInputStream(xmlString.getBytes(this.encoding)));
			}
		}
		catch (Exception ex) {
			log.error(ex);
			throw ex;
		}
		return doc;		
	}	
	
	private Document parseDocument(File xmlFile) throws Exception{
		Document doc = null;
		try {
			SAXBuilder builder = new SAXBuilder();
			DataUnformatFilter format = new DataUnformatFilter();
			builder.setXMLFilter(format);
			doc = builder.build(xmlFile);
		}
		catch (Exception ex) {
			log.error(ex);
			throw ex;
		}
		return doc;		
	}	
	
	/**
	 * Returns an array representation of the given Jive property. Jive
	 * properties are always in the format "prop.name.is.this" which would be
	 * represented as an array of four Strings.
	 * @param name the name of the Jive property.
	 * @return an array representation of the given Jive property.
	 */
	private String[] parsePropertyName(String name) {
		// Figure out the number of parts of the name (this becomes the size
		// of the resulting array).
		int size = 1;
		for (int i = 0; i < name.length(); i++) {
			if (name.charAt(i) == '.') {
				size++;
			}
		}
		String[] propName = new String[size];
		// Use a StringTokenizer to tokenize the property name.
		StringTokenizer tokenizer = new StringTokenizer(name, ".");
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			propName[i] = tokenizer.nextToken();
			i++;
		}
		return propName;
	}

}
