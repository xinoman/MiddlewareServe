/**
 * 
 */
package com.its.core.module.device.status;

import java.io.Serializable;

/**
 * 创建日期 2012-9-21 下午04:20:33
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class DeviceStatusBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2442292381646375775L;
	
	private String deviceId;
	
	private String statusInfo;
	
	private String directionCode;
	
	private String desc;

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
	 * @return the statusInfo
	 */
	public String getStatusInfo() {
		return statusInfo;
	}

	/**
	 * @param statusInfo the statusInfo to set
	 */
	public void setStatusInfo(String statusInfo) {
		this.statusInfo = statusInfo;
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
	 * @return the desc
	 */
	public String getDesc() {
		return desc;
	}

	/**
	 * @param desc the desc to set
	 */
	public void setDesc(String desc) {
		this.desc = desc;
	}

}
