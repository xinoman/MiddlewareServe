/**
 * 
 */
package com.its.core.module.device;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.util.StringHelper;
import com.its.core.util.XMLParser;

/**
 * 创建日期 2012-9-20 下午01:48:18
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class MessageHelper {
	private static final Log log = LogFactory.getLog(MessageHelper.class);
	
	//XML版本的通讯协议
	public static final int VERSION_XML = 2;	
	
	//XML协议元素定义
	public static final String XML_ELE_MSG_CODE 							= "message.head.code";		
	public static final String XML_ELE_MSG_FEEDBACK							= "message.head.feedback";
	public static final String XML_ELE_CONTENT								= "message.body.content";
	public static final String XML_ELE_MSG_ID 								= "message.body.id";
	public static final String XML_ELE_CONTENT_PREFIX						= XML_ELE_CONTENT+".";
	public static final String XML_MSG_HEAD									= "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	
	public static final String XML_MSGCODE_SD_TIME_ADJUST					= "SD1001";
	
	public static final List<MessageBean> parse(String info){
		List<MessageBean> protocolList = new ArrayList<MessageBean>();
		
		if(StringHelper.isEmpty(info)) {
			return protocolList; 
		}
		
		//如果是XML版本的协议
		//log.debug("startsWith = "+info.startsWith("<?xml"));
		if(info.startsWith("<?xml")){
			MessageBean messageBean = new MessageBean();
			messageBean.setVersion(MessageHelper.VERSION_XML);
			try {
				XMLParser xmlParser = new XMLParser(info);
				messageBean.setXmlParser(xmlParser);
				String msgCode = xmlParser.getProperty(MessageHelper.XML_ELE_MSG_CODE);
				messageBean.setHead(msgCode);
				//messageBean.setFull(info);
				protocolList.add(messageBean);
			} catch (Exception e) {				
				log.error(e.getMessage(),e);
				
			}
		} else {
			log.error("内容:"+info+"不符合协议规范！");
		}		
		return protocolList;
	}
	
	/**
	 * 创建标准的XML消息
	 * @param msgCode 消息代码
	 * @param version 消息版本号
	 * @param requiredFeedback 是否需要反馈
	 * @param msgId 消息ID，如果requiredFeedback为false,则msgId可以为空
	 * @param content 消息内容
	 * @return
	 */
	public static String createXmlMsg(String msgCode,String version,boolean requiredFeedback,String msgId,String content){
		StringBuffer msg = new StringBuffer(MessageHelper.XML_MSG_HEAD);
		msg.append("<message>");
		msg.append("<head>");
			msg.append("<code>").append(msgCode).append("</code>");
			msg.append("<version>").append(version).append("</version>");
			if(requiredFeedback) msg.append("<feedback>true</feedback>");
		msg.append("</head>");
		msg.append("<body>");
		//if(requiredFeedback) msg.append("<id>").append(msgId).append("</id>");
		if(StringHelper.isNotEmpty(msgId)) msg.append("<id>").append(msgId).append("</id>");
		msg.append("<content>").append(content).append("</content>");
		msg.append("</body>");
		msg.append("</message>");
		return msg.toString();
	}
	
	public static String createDS1005Xml(String msgCode,String version,String content){
		StringBuffer msg = new StringBuffer(MessageHelper.XML_MSG_HEAD);
		msg.append("<message>");
		msg.append("<head>");
			msg.append("<code>").append(msgCode).append("</code>");
			msg.append("<version>").append(version).append("</version>");
		msg.append("</head>");
		msg.append("<body>");		
			msg.append("<content>").append(content).append("</content>");
		msg.append("</body>");
		msg.append("</message>");
		return msg.toString();
	}

}
