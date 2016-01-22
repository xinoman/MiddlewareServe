/**
 * 
 */
package com.its.core.module.device.time;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoSession;

import com.its.core.module.device.ACommunicateProcessor;
import com.its.core.module.device.MessageBean;
import com.its.core.module.device.MessageHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.XMLParser;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-9-21 上午11:24:38
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class RequestTimeAdjustmentProcessor extends ACommunicateProcessor {
	private static final Log log = LogFactory.getLog(RequestTimeAdjustmentProcessor.class);

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
		Date currentTime = new Date();
		StringBuffer timeProtocol = new StringBuffer("<curTime>").append(DateHelper.dateToString(currentTime,"yyyyMMddHHmmss")).append("</curTime>");
		String protocol = MessageHelper.createXmlMsg(
				MessageHelper.XML_MSGCODE_SD_TIME_ADJUST, 
				"2.0.0.0", 
				false,
				xmlParser.getProperty(MessageHelper.XML_ELE_MSG_ID), 
				timeProtocol.toString());	
		
		String sessionKey = messageBean.getSessionKey();
		IoSession session = this.getDeviceCommunicateModule().getSessionsMap().get(sessionKey);
		//log.debug("default write timeout = "+session.getWriteTimeout());
		//session.setWriteTimeout(3);
		
		log.debug("调整时钟："+protocol);
		session.write(protocol);

	}

}
