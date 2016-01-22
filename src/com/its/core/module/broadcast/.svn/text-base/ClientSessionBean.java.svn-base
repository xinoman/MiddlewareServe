/**
 * 
 */
package com.its.core.module.broadcast;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.mina.common.IoSession;

/**
 * 创建日期 2012-9-24 上午09:41:16
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ClientSessionBean {
	
	//注册类型－－对哪些信息感兴趣，信息广播服务器将根据注册类型向客户端发送对应信息
	private Map<String,String> registerTypeMap = Collections.synchronizedMap(new HashMap<String,String>());
	
	//连接SESSION
	private IoSession session;

	/**
	 * @return the registerTypeMap
	 */
	public Map<String, String> getRegisterTypeMap() {
		return registerTypeMap;
	}

	/**
	 * @param registerTypeMap the registerTypeMap to set
	 */
	public void setRegisterTypeMap(Map<String, String> registerTypeMap) {
		this.registerTypeMap = registerTypeMap;
	}

	/**
	 * @return the session
	 */
	public IoSession getSession() {
		return session;
	}

	/**
	 * @param session the session to set
	 */
	public void setSession(IoSession session) {
		this.session = session;
	}

}
