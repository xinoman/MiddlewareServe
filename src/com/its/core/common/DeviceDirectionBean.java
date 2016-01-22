package com.its.core.common;

import java.io.Serializable;

/**
 * 设备方向
 * 创建日期 2012-08-03 上午09:06:43
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class DeviceDirectionBean implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7070283849920059976L;
	
	private String directionId;
	private String directionCode;
	public String getDirectionId() {
		return directionId;
	}
	public void setDirectionId(String directionId) {
		this.directionId = directionId;
	}
	public String getDirectionCode() {
		return directionCode;
	}
	public void setDirectionCode(String directionCode) {
		this.directionCode = directionCode;
	}	
}
