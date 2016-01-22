/**
 * 
 */
package com.its.core.module.device.as;

import org.apache.mina.common.IoSession;

import com.its.core.module.device.ACommunicateProcessor;
import com.its.core.module.device.MessageBean;
import com.its.core.module.device.MessageHelper;
import com.its.core.module.device.ResourceCache;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLParser;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-2-1 下午02:16:14
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 * AS1003：重读黑名单消息
	<?xml version="1.0" encoding="UTF-8"?>
	<message>
		<head>
			<code>AS1003</code>	
			<version>2.0.0.0</version>
			<feedback>true</feedback>
		</head>
		<body>
			<id>5657</id>
		</body>
	</message>
 */
public class BlacklistReloadProcessor extends ACommunicateProcessor {

	/* (non-Javadoc)
	 * @see com.its.core.module.device.ACommunicateProcessor#configure(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configure(XMLProperties props, String propertiesPrefix, int no)
			throws Exception {
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
		
		ResourceCache.getPlateMonitorMap().reload();
		
		//反馈消息
		if(StringHelper.getBoolean(feedback)){
			String returnInstruction = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><message><head><code>SA1000</code><version>2.0.0.0</version></head><body><id>"+instructionId+"</id><content><returnCode>0</returnCode><returnMsg></returnMsg></content></body></message>";
			IoSession session = this.getDeviceCommunicateModule().getSessionsMap().get(messageBean.getSessionKey());
			session.write(returnInstruction);
		}

	}

}
