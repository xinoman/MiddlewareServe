/**
 * 
 */
package com.its.core.module.broadcast;

import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

import com.its.core.module.device.IoSessionUtils;

/**
 * 创建日期 2012-9-24 上午09:33:00
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class InfoBroadcastProtocolHandler extends IoHandlerAdapter {
	private static final Log log = LogFactory.getLog(InfoBroadcastProtocolHandler.class);
	
	private InfoBroadcastModule infoBroadcastModule = null;	

	/**
	 * @param infoBroadcastModule
	 */
	public InfoBroadcastProtocolHandler(InfoBroadcastModule infoBroadcastModule) {		
		this.infoBroadcastModule = infoBroadcastModule;
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#sessionOpened(org.apache.mina.common.IoSession)
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		String sessionKey = IoSessionUtils.getRemoteIp(session)+":"+IoSessionUtils.getRemotePort(session);
		ClientSessionBean clientSessionBean = null;
		if(this.getInfoBroadcastModule().getSessionsMap().containsKey(sessionKey)){
			clientSessionBean = this.getInfoBroadcastModule().getSessionsMap().get(sessionKey);
		}
		else{
			clientSessionBean = new ClientSessionBean();
			clientSessionBean.setSession(session);
			this.getInfoBroadcastModule().getSessionsMap().put(sessionKey, clientSessionBean);
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#messageReceived(org.apache.mina.common.IoSession, java.lang.Object)
	 */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String protocolMessage = ((String) message).trim();
		log.info("从"+session+"接收消息："+protocolMessage);
		
		//心跳包应答
		if(ProtocolHelper.PROTOCOL_HEART_BEAT_PACKAGE.equals(protocolMessage)){
			session.write(ProtocolHelper.PROTOCOL_HEART_BEAT_PACKAGE);
			return;
		}
		
		String sessionKey = IoSessionUtils.getRemoteIp(session)+":"+IoSessionUtils.getRemotePort(session);
		ClientSessionBean clientSessionBean = this.getInfoBroadcastModule().getSessionsMap().get(sessionKey);
		if(clientSessionBean==null){
			clientSessionBean = new ClientSessionBean();
			clientSessionBean.setSession(session);
			this.getInfoBroadcastModule().getSessionsMap().put(sessionKey, clientSessionBean);			
		}
		
		Map<String, String> registerTypeMap = clientSessionBean.getRegisterTypeMap();
		//注册
		if(protocolMessage.startsWith("register:")){
			registerTypeMap.put(protocolMessage, protocolMessage);
		}
		//取消所有注册
		else if(ProtocolHelper.PROTOCOL_CANCEL_ALL.equalsIgnoreCase(protocolMessage)){
			log.debug("清除所有注册！");
			registerTypeMap.clear();
		}
		//关闭当前SESSION
		else if(ProtocolHelper.PROTOCOL_CLOSE.equalsIgnoreCase(protocolMessage)){
			log.debug("关闭当前SESSION！");
			session.close();
		}		
		//取消注册
		else if(protocolMessage.startsWith("cancel:")){
			String registerProtocolHead = ProtocolHelper.getRegisterProtocol(protocolMessage);
			//带参数（设备编号）的注销指令,直接移出，例: cancel:realtime-vehicle 10001
			int indexOf = protocolMessage.indexOf(" "); 
			if(indexOf!=-1){
				String tmpKey = registerProtocolHead+" "+ protocolMessage.substring(indexOf+1);
				registerTypeMap.remove(tmpKey);
				log.debug("取消注册："+tmpKey);
			}
			else{
				//不带参数（设备编号），则表示将所有相关注册都注销，例：cancel:realtime-vehicle				
				synchronized(registerTypeMap){
					Iterator iterator = registerTypeMap.keySet().iterator();
					while(iterator.hasNext()){
						String key = (String)iterator.next();
						if(key.startsWith(registerProtocolHead)){
							iterator.remove();
							log.debug("取消注册："+key);
						}
					}
				}
			}		
		}
		else{
			log.warn("非法连接："+session);
			session.close();
		}
	}
		
	
	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#exceptionCaught(org.apache.mina.common.IoSession, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)	throws Exception {
		log.error("exceptionCaught:"+session+" : "+cause);
		if(session!=null) session.close();
	}
	
	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#sessionClosed(org.apache.mina.common.IoSession)
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		String sessionKey = IoSessionUtils.getRemoteIp(session)+":"+IoSessionUtils.getRemotePort(session);
		Map<String,ClientSessionBean> sessionsMap = this.getInfoBroadcastModule().getSessionsMap();
		if(sessionsMap.containsKey(sessionKey)){
			ClientSessionBean clientSessionBean = sessionsMap.remove(sessionKey);
			clientSessionBean.getRegisterTypeMap().clear();
			if(clientSessionBean.getSession()!=null){
				clientSessionBean.getSession().close();
			}			
		}
	}
	
	

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#messageSent(org.apache.mina.common.IoSession, java.lang.Object)
	 */
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		log.info("发送信息："+message+" 到 "+session);
	}

	/**
	 * @return the infoBroadcastModule
	 */
	public InfoBroadcastModule getInfoBroadcastModule() {
		return infoBroadcastModule;
	}

	/**
	 * @param infoBroadcastModule the infoBroadcastModule to set
	 */
	public void setInfoBroadcastModule(InfoBroadcastModule infoBroadcastModule) {
		this.infoBroadcastModule = infoBroadcastModule;
	}	

}
