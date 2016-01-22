/**
 * 
 */
package com.its.core.module.broadcast;

import com.its.core.module.device.MessageBean;

/**
 * 创建日期 2012-9-24 上午09:45:51
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class BroadcastProtocolBean {
	
	//广播信息所适用的注册协议/
	private String registerProtocol;
	
	//该信息来自于哪个设备ID/
	private String deviceId;
	
	//该信息来自于哪个设备管理的哪个方向/
	private String directionCode;
	
	//协议内容
	private String content;
	
	//原始协议内容：设备发给服务器的原始信息，未经过加工的数据
	private MessageBean oriMessageBean;

	/**
	 * @return the registerProtocol
	 */
	public String getRegisterProtocol() {
		return registerProtocol;
	}

	/**
	 * @param registerProtocol the registerProtocol to set
	 */
	public void setRegisterProtocol(String registerProtocol) {
		this.registerProtocol = registerProtocol;
	}	

	/**
	 * @return the deviceId
	 */
	public String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId the deviceId to set
	 */
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the directionCode
	 */
	public String getDirectionCode() {
		return directionCode;
	}

	/**
	 * @param directionCode the directionCode to set
	 */
	public void setDirectionCode(String directionCode) {
		this.directionCode = directionCode;
	}

	/**
	 * @return the content
	 */
	public String getContent() {
		return content;
	}

	/**
	 * @param content the content to set
	 */
	public void setContent(String content) {
		this.content = content;
	}

	/**
	 * @return the oriMessageBean
	 */
	public MessageBean getOriMessageBean() {
		return oriMessageBean;
	}

	/**
	 * @param oriMessageBean the oriMessageBean to set
	 */
	public void setOriMessageBean(MessageBean oriMessageBean) {
		this.oriMessageBean = oriMessageBean;
	}		
	
}
