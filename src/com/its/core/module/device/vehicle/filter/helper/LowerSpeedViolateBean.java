/**
 * 
 */
package com.its.core.module.device.vehicle.filter.helper;

/**
 * 创建日期 2012-12-6 下午03:34:57
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class LowerSpeedViolateBean {
	
	private SectionBean sectionBean;
	
	private CarRecordBean fromCarRecordBean;
	
	private CarRecordBean toCarRecordBean;
	
	private String directionCode;
	
	private int speed;
	
	private int limitSpeed;
	
	private int distance;

	public SectionBean getSectionBean() {
		return sectionBean;
	}

	public void setSectionBean(SectionBean sectionBean) {
		this.sectionBean = sectionBean;
	}

	public CarRecordBean getFromCarRecordBean() {
		return fromCarRecordBean;
	}

	public void setFromCarRecordBean(CarRecordBean fromCarRecordBean) {
		this.fromCarRecordBean = fromCarRecordBean;
	}

	public CarRecordBean getToCarRecordBean() {
		return toCarRecordBean;
	}

	public void setToCarRecordBean(CarRecordBean toCarRecordBean) {
		this.toCarRecordBean = toCarRecordBean;
	}

	public String getDirectionCode() {
		return directionCode;
	}

	public void setDirectionCode(String directionCode) {
		this.directionCode = directionCode;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getLimitSpeed() {
		return limitSpeed;
	}

	public void setLimitSpeed(int limitSpeed) {
		this.limitSpeed = limitSpeed;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

}
