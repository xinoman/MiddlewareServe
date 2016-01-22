/**
 * 
 */
package com.its.core.module.device;

import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.IdleStatus;
import org.apache.mina.common.IoHandlerAdapter;
import org.apache.mina.common.IoSession;

/**
 * 创建日期 2012-9-19 下午05:53:28
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class DeviceProtocolHandler extends IoHandlerAdapter {
	private static final Log log = LogFactory.getLog(DeviceProtocolHandler.class);
	
	private DeviceCommunicateModule deviceCommunicateModule = null;
	
	public DeviceCommunicateModule getDeviceCommunicateModule() {
		return deviceCommunicateModule;
	}

	public void setDeviceCommunicateModule(DeviceCommunicateModule deviceCommunicateModule) {
		this.deviceCommunicateModule = deviceCommunicateModule;
	}

	public DeviceProtocolHandler(DeviceCommunicateModule deviceCommunicateModule){
		this.deviceCommunicateModule = deviceCommunicateModule;
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#sessionOpened(org.apache.mina.common.IoSession)
	 */
	@Override
	public void sessionOpened(IoSession session) throws Exception {
		log.info("新连接:"+session.getRemoteAddress());
		String sessionKey = IoSessionUtils.generateKey(session);		
		Map<String, IoSession> sessionsMap = this.getDeviceCommunicateModule().getSessionsMap(); 
		if(sessionsMap.containsKey(sessionKey)){
			log.info("已经存在该连接："+sessionsMap.get(sessionKey).getRemoteAddress()+",将其移出！");
			IoSession existSession = sessionsMap.remove(sessionKey);
			existSession.close();
		}
		
		log.debug("保存新连接:"+session.getRemoteAddress());
		sessionsMap.put(sessionKey, session);		
		
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#messageReceived(org.apache.mina.common.IoSession, java.lang.Object)
	 */
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception {
		String theMessage = ((String) message).trim();
		log.info("接收信息："+theMessage);		
		
		Iterator<MessageBean> iterator = MessageHelper.parse(theMessage).iterator();
		while(iterator.hasNext()){
			MessageBean messageBean = iterator.next();
			messageBean.setSessionKey(IoSessionUtils.generateKey(session));
			messageBean.setFromIp(IoSessionUtils.getRemoteIp(session));
			
			String headWord = messageBean.getHead();			
			
//			BlockingQueue<MessageBean> queue = null;

//			if(headWord.startsWith("DS")){
				//来自前端设备的消息
			BlockingQueue<MessageBean> queue = this.getDeviceCommunicateModule().getInfoQueue(headWord);				
				if(queue==null){					
					queue = this.getDeviceCommunicateModule().getInfoQueue("other");
				}				
//			}
			
			if(queue==null){
				log.warn("对于头文字为：'"+headWord+"'的信息没有找到相应的处理程序！");				
			}
			else{
//				queue.put(messageBean);				
				try {
					boolean success = queue.offer(messageBean, 1, TimeUnit.SECONDS);					
					if(!success){
						log.warn("队列满（"+headWord+"），当前队列长度:"+queue.size());
						queue.clear();
					}	
					log.debug("当前队列长度:"+queue.size());
				} catch (InterruptedException e) {
					queue.clear();
					log.error(e.getMessage(),e);
				}
			}
			
			iterator.remove();
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#messageSent(org.apache.mina.common.IoSession, java.lang.Object)
	 */
	@Override
	public void messageSent(IoSession session, Object message) throws Exception {
		log.info("已经发送："+message);
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#sessionClosed(org.apache.mina.common.IoSession)
	 */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		log.debug("sessionClosed:"+session.getRemoteAddress());
		int remotePort = IoSessionUtils.getRemotePort(session);
		String key = IoSessionUtils.generateKey(session);
		Map<String, IoSession> sessionsMap = this.getDeviceCommunicateModule().getSessionsMap(); 
		if(sessionsMap.containsKey(key)){
			IoSession tempSession = sessionsMap.get(key);
			if(remotePort==IoSessionUtils.getRemotePort(tempSession)){
				log.debug("关闭连接："+sessionsMap.get(key).getRemoteAddress());
				tempSession = sessionsMap.remove(key);
				tempSession.close();				
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#sessionCreated(org.apache.mina.common.IoSession)
	 */
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		// TODO Auto-generated method stub
		super.sessionCreated(session);
	}

	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#sessionIdle(org.apache.mina.common.IoSession, org.apache.mina.common.IdleStatus)
	 */
	@Override
	public void sessionIdle(IoSession session, IdleStatus status) throws Exception {
		log.debug("sessionIdle:"+status.toString());
	}	
	
	/* (non-Javadoc)
	 * @see org.apache.mina.common.IoHandlerAdapter#exceptionCaught(org.apache.mina.common.IoSession, java.lang.Throwable)
	 */
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)	throws Exception {
		log.error("exceptionCaught:"+session+" : "+cause);
		if(session!=null) session.close();
	}

}
