/**
 * 
 */
package com.its.core.local.hezhou.task;

import java.io.Serializable;

/**
 * 创建日期 2013-10-8 下午08:04:47
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class VehicelRecordBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// primary key
	private java.lang.Long id;

	// fields
	private java.lang.String plate;
	private java.lang.String plateColorCode;
	private java.lang.String deviceId;
	private java.lang.String deviceIp;
	private java.sql.Timestamp catchTime;
	private java.lang.String JGSJ;
	private java.lang.String drivewayNo;
	private java.lang.String directionCode;
	private java.lang.String speed;
	private java.lang.String limitSpeed;
	private java.lang.String panoramaImagePath;
	private java.lang.String featureImagePath;
	
	private java.lang.String roadName;
	private java.lang.String roadId;
	
	public java.lang.Long getId() {
		return id;
	}

	public void setId(java.lang.Long id) {
		this.id = id;
	}

	public java.lang.String getPlate() {
		return plate;
	}

	public void setPlate(java.lang.String plate) {
		this.plate = plate;
	}

	public java.lang.String getPlateColorCode() {
		return plateColorCode;
	}

	public void setPlateColorCode(java.lang.String plateColorCode) {
		this.plateColorCode = plateColorCode;
	}

	public java.lang.String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(java.lang.String deviceId) {
		this.deviceId = deviceId;
	}

	public java.sql.Timestamp getCatchTime() {
		return catchTime;
	}

	public void setCatchTime(java.sql.Timestamp catchTime) {
		this.catchTime = catchTime;
	}	

	public java.lang.String getJGSJ() {
		return JGSJ;
	}

	public void setJGSJ(java.lang.String jgsj) {
		JGSJ = jgsj;
	}

	public java.lang.String getDrivewayNo() {
		return drivewayNo;
	}

	public void setDrivewayNo(java.lang.String drivewayNo) {
		this.drivewayNo = drivewayNo;
	}

	public java.lang.String getDirectionCode() {
		return directionCode;
	}

	public void setDirectionCode(java.lang.String directionCode) {
		this.directionCode = directionCode;
	}

	public java.lang.String getSpeed() {
		return speed;
	}

	public void setSpeed(java.lang.String speed) {
		this.speed = speed;
	}

	public java.lang.String getLimitSpeed() {
		return limitSpeed;
	}

	public void setLimitSpeed(java.lang.String limitSpeed) {
		this.limitSpeed = limitSpeed;
	}

	public java.lang.String getPanoramaImagePath() {
		return panoramaImagePath;
	}

	public void setPanoramaImagePath(java.lang.String panoramaImagePath) {
		this.panoramaImagePath = panoramaImagePath;
	}

	public java.lang.String getFeatureImagePath() {
		return featureImagePath;
	}

	public void setFeatureImagePath(java.lang.String featureImagePath) {
		this.featureImagePath = featureImagePath;
	}

	public java.lang.String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(java.lang.String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public java.lang.String getRoadName() {
		return roadName;
	}

	public void setRoadName(java.lang.String roadName) {
		this.roadName = roadName;
	}

	public java.lang.String getRoadId() {
		return roadId;
	}

	public void setRoadId(java.lang.String roadId) {
		this.roadId = roadId;
	}	

}
