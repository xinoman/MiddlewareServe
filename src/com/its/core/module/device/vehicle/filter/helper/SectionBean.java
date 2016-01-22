/**
 * 
 */
package com.its.core.module.device.vehicle.filter.helper;

/**
 * 创建日期 2012-12-6 下午03:23:55
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class SectionBean {
	
	//方向编号
	private String direction = null;
	
	//区间段名称
	private String name = null;
	
	// 区间段虚拟设备ID
	private String deviceId = null;
	
	private CheckPointBean firstCheckPointBean = null;

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public CheckPointBean getFirstCheckPointBean() {
		return firstCheckPointBean;
	}
	public void setFirstCheckPointBean(CheckPointBean firstCheckPointBean) {
		this.firstCheckPointBean = firstCheckPointBean;
	}
	public String getDirection() {
		return direction;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

}
