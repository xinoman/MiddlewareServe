/**
 * 
 */
package com.its.core.local.dianbai.task;

/**
 * 创建日期 2014-7-23 下午08:19:01
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ExportVehicelPassRecordBean {
	
	// primary key
	private java.lang.Long id;

	// fields
	private java.lang.String plate;
	private java.lang.String plateColorCode;
	private java.lang.String deviceId;
	private java.sql.Timestamp catchTime;
	private java.lang.String drivewayNo;
	private java.lang.String directionCode;
	private java.lang.String speed;
	private java.lang.String limitSpeed;
	private java.lang.String panoramaImagePath;
	private java.lang.String featureImagePath;

	public java.lang.Long getId() {
		return id;
	}

	public void setId(java.lang.Long id) {
		this.id = id;
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

}
