/**
 * 
 */
package com.its.core.module.broadcast;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.module.device.MessageBean;
import com.its.core.module.device.MessageHelper;

/**
 * 创建日期 2012-9-24 上午09:54:49
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class InfoBroadcastThread implements Runnable {
	private static final Log log = LogFactory.getLog(InfoBroadcastThread.class);

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		while(true){
			try {
				BroadcastProtocolBean broadcastProtocolBean = InfoBroadcastModule.getMessage();
				if(broadcastProtocolBean==null) continue;
				Map<String, ClientSessionBean> sessionsMap = InfoBroadcastModule.getSessionsMap();
				Iterator<ClientSessionBean> iterator = sessionsMap.values().iterator();
				while(iterator.hasNext()){
					ClientSessionBean clientSessionBean = iterator.next();
					Map registerTypeMap = clientSessionBean.getRegisterTypeMap();
					log.debug("registerProtocol="+broadcastProtocolBean.getRegisterProtocol()+"\tdeviceId="+broadcastProtocolBean.getDeviceId());
					if(registerTypeMap.containsKey(ProtocolHelper.PROTOCOL_REGISTER_REALTIME_VEHICLE_ORI)){
						MessageBean msgBean = broadcastProtocolBean.getOriMessageBean();
						if(msgBean==null){
							log.warn("broadcastProtocolBean.getOriMessageBean() is null!");
						}
						else{
							String broadcastContent = msgBean.getFull();
							if(MessageHelper.VERSION_XML==msgBean.getVersion()){
								broadcastContent = msgBean.getXmlParser().getXmlString();
							}
							log.debug("广播数据："+broadcastContent+" 到 "+clientSessionBean.getSession());
							clientSessionBean.getSession().write(broadcastContent);
						}
					}
					else if(registerTypeMap.containsKey(broadcastProtocolBean.getRegisterProtocol()) ||
							registerTypeMap.containsKey(broadcastProtocolBean.getRegisterProtocol() + " " + broadcastProtocolBean.getDeviceId()) ||
							registerTypeMap.containsKey(broadcastProtocolBean.getRegisterProtocol() + " " + broadcastProtocolBean.getDeviceId() + " "+broadcastProtocolBean.getDirectionCode())
							){
							
							log.debug("广播数据："+broadcastProtocolBean.getContent()+" 到 "+clientSessionBean.getSession());
							clientSessionBean.getSession().write(broadcastProtocolBean.getContent());
					}			
				}
			} catch (Exception ex){
				log.error("广播数据时出错："+ex.getMessage(),ex);
			}
		}

	}

}
