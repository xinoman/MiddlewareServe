/**
 * 
 */
package com.its.core.module.device.as;

import org.apache.mina.common.IoSession;

import com.its.core.module.device.ACommunicateProcessor;
import com.its.core.module.device.BlacklistBean;
import com.its.core.module.device.MessageBean;
import com.its.core.module.device.MessageHelper;
import com.its.core.module.device.ResourceCache;
import com.its.core.util.DateHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLParser;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-2-1 下午02:10:56
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 * * AS1001：接收来自应用系统（客户端）的黑名单布控消息
 * <?xml version="1.0" encoding="UTF-8"?>
	<message>
		<head>
			<code>AS1001</code>	
			<version>2.0.0.0</version>
			<feedback>true</feedback>
		</head>
		<body>
			<id>5654</id>
			<content>
				<control>
					<blacklistId>黑名单编号（2941）</blacklistId>
					<plateNo>号牌号码（粤B12345）</plateNo>
					<plateColor>号牌颜色</plateColor>
					<blacklistTypeId>黑名单类型编号</blacklistTypeId>
					<blacklistTypeName>黑名单类型名称（盗抢）</blacklistTypeName>
					<startTime>布控开始时间（yyyyMMddHHmmss）</startTime>
					<endTime>布控结束时间（yyyyMMddHHmmss）</endTime>
				</control>	
				<control>
					<blacklistId></blacklistId>
					<plateNo></plateNo>
					<plateColor></plateColor>
					<blacklistTypeId></blacklistTypeId>
					<blacklistTypeName></blacklistTypeName>
					<startTime>布控开始时间（yyyyMMddHHmmss）</startTime>
					<endTime>布控结束时间（yyyyMMddHHmmss）</endTime>
				</control>	
				......
			</content>
		</body>
	</message>
 *
 */
public class ControlProcessor extends ACommunicateProcessor {

	/* (non-Javadoc)
	 * @see com.its.core.module.device.ACommunicateProcessor#configure(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configure(XMLProperties props, String propertiesPrefix, int no)	throws Exception {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.device.ACommunicateProcessor#process(com.its.core.module.device.MessageBean)
	 */
	@Override
	public void process(MessageBean messageBean) throws Exception {
		XMLParser xmlParser = messageBean.getXmlParser();
		String feedback = xmlParser.getProperty(MessageHelper.XML_ELE_MSG_FEEDBACK);
		String instructionId = xmlParser.getProperty(MessageHelper.XML_ELE_MSG_ID);
		int controlNum = xmlParser.getPropertyNum(MessageHelper.XML_ELE_CONTENT);
		for(int i=0;i<controlNum;i++){
			String blacklistId 			= xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT, i, "control.blacklistId");
			String plateNo				= xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT, i, "control.plateNo");
			String plateColor			= xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT, i, "control.plateColor");
			String blacklistTypeId		= xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT, i, "control.blacklistTypeId");
			String blacklistTypeName	= xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT, i, "control.blacklistTypeName");
			String startTime			= xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT, i, "control.startTime");
			String endTime				= xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT, i, "control.endTime");
			BlacklistBean blacklist = new BlacklistBean();
			blacklist.setId(blacklistId);
			blacklist.setPlate(plateNo);
			blacklist.setPlateColorCode(plateColor);
			blacklist.setTypeId(blacklistTypeId);
			blacklist.setTypeName(blacklistTypeName);
			if(StringHelper.isNotEmpty(startTime)){
				blacklist.setStartTime(new Long(DateHelper.parseDateString(startTime, "yyyyMMddHHmmss").getTime()));
			}			
			if(StringHelper.isNotEmpty(endTime)){
				blacklist.setEndTime(new Long(DateHelper.parseDateString(endTime, "yyyyMMddHHmmss").getTime()));
			}
			ResourceCache.getPlateMonitorMap().put(blacklist);
		}

		//反馈消息
		if(StringHelper.getBoolean(feedback)){
			String returnInstruction = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><message><head><code>SA1000</code><version>2.0.0.0</version></head><body><id>"+instructionId+"</id><content><returnCode>0</returnCode><returnMsg></returnMsg></content></body></message>";
			IoSession session = this.getDeviceCommunicateModule().getSessionsMap().get(messageBean.getSessionKey());
			session.write(returnInstruction);
		}
	}
}
