/**
 * 
 */
package com.its.core.module.device;

import com.its.core.util.XMLParser;

/**
 * 创建日期 2012-9-19 下午05:32:50
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class MessageBean {
	
	//Message版本，XML版本的通讯协议
	private int version = MessageHelper.VERSION_XML;
	
	//用于XML消息
	private XMLParser xmlParser = null;
	
	//头文字标识
	private String head = null;
	
	//Session Key/
	private String sessionKey = null;	
	
	//消息来源IP/
	private String fromIp = null;
	
	//完整消息
	private String full = null;

	/**
	 * @return the version
	 */
	public int getVersion() {
		return version;
	}

	/**
	 * @param version the version to set
	 */
	public void setVersion(int version) {
		this.version = version;
	}

	/**
	 * @return the xmlParser
	 */
	public XMLParser getXmlParser() {
		return xmlParser;
	}

	/**
	 * @param xmlParser the xmlParser to set
	 */
	public void setXmlParser(XMLParser xmlParser) {
		this.xmlParser = xmlParser;
	}

	/**
	 * @return the head
	 */
	public String getHead() {
		return head;
	}

	/**
	 * @param head the head to set
	 */
	public void setHead(String head) {
		this.head = head;
	}

	/**
	 * @return the sessionKey
	 */
	public String getSessionKey() {
		return sessionKey;
	}

	/**
	 * @param sessionKey the sessionKey to set
	 */
	public void setSessionKey(String sessionKey) {
		this.sessionKey = sessionKey;
	}

	/**
	 * @return the fromIp
	 */
	public String getFromIp() {
		return fromIp;
	}

	/**
	 * @param fromIp the fromIp to set
	 */
	public void setFromIp(String fromIp) {
		this.fromIp = fromIp;
	}

	/**
	 * @return the full
	 */
	public String getFull() {
		return full;
	}

	/**
	 * @param full the full to set
	 */
	public void setFull(String full) {
		this.full = full;
	}	

}
