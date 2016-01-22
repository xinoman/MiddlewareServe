/**
 * 
 */
package com.its.core.local.dianbai.task;

import java.io.Serializable;

/**
 * 创建日期 2014-11-5 下午12:26:12
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class VehicelRecordBean implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7691887959331515978L;

	// primary key
	private java.lang.Long id;

	// fields
	private java.lang.String plate;
	private java.lang.Long plateColorCode;
	private java.sql.Timestamp catchTime;
	private java.lang.Long roadId;
	private java.lang.String deviceId;
	private java.lang.String directionCode;
	private java.lang.String directionDrive;	
	private java.lang.String drivewayNo;	
	private java.lang.Long speed;
	private java.lang.Long limitSpeed;
	private java.lang.String alarmTypeId;
	private java.lang.Long blacklistTypeId;
	private java.lang.String panoramaImagePath;
	private java.lang.String featureImagePath;
	private java.sql.Timestamp createTime;
	private java.lang.String status;
	
	/**
	 * @return the id
	 */
	public java.lang.Long getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(java.lang.Long id) {
		this.id = id;
	}

	/**
	 * @return the plate
	 */
	public java.lang.String getPlate() {
		return plate;
	}

	/**
	 * @param plate
	 *            the plate to set
	 */
	public void setPlate(java.lang.String plate) {
		this.plate = plate;
	}
	

	/**
	 * @return the plateColorCode
	 */
	public java.lang.Long getPlateColorCode() {
		return plateColorCode;
	}

	/**
	 * @param plateColorCode the plateColorCode to set
	 */
	public void setPlateColorCode(java.lang.Long plateColorCode) {
		this.plateColorCode = plateColorCode;
	}

	/**
	 * @return the catchTime
	 */
	public java.sql.Timestamp getCatchTime() {
		return catchTime;
	}

	/**
	 * @param catchTime
	 *            the catchTime to set
	 */
	public void setCatchTime(java.sql.Timestamp catchTime) {
		this.catchTime = catchTime;
	}

	/**
	 * @return the roadId
	 */
	public java.lang.Long getRoadId() {
		return roadId;
	}

	/**
	 * @param roadId
	 *            the roadId to set
	 */
	public void setRoadId(java.lang.Long roadId) {
		this.roadId = roadId;
	}

	/**
	 * @return the deviceId
	 */
	public java.lang.String getDeviceId() {
		return deviceId;
	}

	/**
	 * @param deviceId
	 *            the deviceId to set
	 */
	public void setDeviceId(java.lang.String deviceId) {
		this.deviceId = deviceId;
	}

	/**
	 * @return the directionCode
	 */
	public java.lang.String getDirectionCode() {
		return directionCode;
	}

	/**
	 * @param directionCode
	 *            the directionCode to set
	 */
	public void setDirectionCode(java.lang.String directionCode) {
		this.directionCode = directionCode;
	}

	/**
	 * @return the directionDrive
	 */
	public java.lang.String getDirectionDrive() {
		return directionDrive;
	}

	/**
	 * @param directionDrive
	 *            the directionDrive to set
	 */
	public void setDirectionDrive(java.lang.String directionDrive) {
		this.directionDrive = directionDrive;
	}

	/**
	 * @return the drivewayNo
	 */
	public java.lang.String getDrivewayNo() {
		return drivewayNo;
	}

	/**
	 * @param drivewayNo
	 *            the drivewayNo to set
	 */
	public void setDrivewayNo(java.lang.String drivewayNo) {
		this.drivewayNo = drivewayNo;
	}

	/**
	 * @return the speed
	 */
	public java.lang.Long getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(java.lang.Long speed) {
		this.speed = speed;
	}

	/**
	 * @return the limitSpeed
	 */
	public java.lang.Long getLimitSpeed() {
		return limitSpeed;
	}

	/**
	 * @param limitSpeed the limitSpeed to set
	 */
	public void setLimitSpeed(java.lang.Long limitSpeed) {
		this.limitSpeed = limitSpeed;
	}

	/**
	 * @return the alarmTypeId
	 */
	public java.lang.String getAlarmTypeId() {
		return alarmTypeId;
	}

	/**
	 * @param alarmTypeId
	 *            the alarmTypeId to set
	 */
	public void setAlarmTypeId(java.lang.String alarmTypeId) {
		this.alarmTypeId = alarmTypeId;
	}

	/**
	 * @return the blacklistTypeId
	 */
	public java.lang.Long getBlacklistTypeId() {
		return blacklistTypeId;
	}

	/**
	 * @param blacklistTypeId
	 *            the blacklistTypeId to set
	 */
	public void setBlacklistTypeId(java.lang.Long blacklistTypeId) {
		this.blacklistTypeId = blacklistTypeId;
	}

	/**
	 * @return the panoramaImagePath
	 */
	public java.lang.String getPanoramaImagePath() {
		return panoramaImagePath;
	}

	/**
	 * @param panoramaImagePath
	 *            the panoramaImagePath to set
	 */
	public void setPanoramaImagePath(java.lang.String panoramaImagePath) {
		this.panoramaImagePath = panoramaImagePath;
	}

	/**
	 * @return the featureImagePath
	 */
	public java.lang.String getFeatureImagePath() {
		return featureImagePath;
	}

	/**
	 * @param featureImagePath
	 *            the featureImagePath to set
	 */
	public void setFeatureImagePath(java.lang.String featureImagePath) {
		this.featureImagePath = featureImagePath;
	}

	/**
	 * @return the createTime
	 */
	public java.sql.Timestamp getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 *            the createTime to set
	 */
	public void setCreateTime(java.sql.Timestamp createTime) {
		this.createTime = createTime;
	}

	/**
	 * @return the status
	 */
	public java.lang.String getStatus() {
		return status;
	}

	/**
	 * @param status
	 *            the status to set
	 */
	public void setStatus(java.lang.String status) {
		this.status = status;
	}

}
