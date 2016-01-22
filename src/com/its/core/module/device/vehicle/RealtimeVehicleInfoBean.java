/**
 * 
 */
package com.its.core.module.device.vehicle;

import java.io.Serializable;
import java.util.Date;

import com.its.core.module.device.MessageBean;

/**
 * 创建日期 2012-9-21 上午10:33:34
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class RealtimeVehicleInfoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8802169932332124300L;
	
	//主键ID
	private long recordId = 0L;
	
	//设备ID
	private String deviceId = null;
	
	//方向代码
	private String directionCode = null;
	
	//行驶编号
	private String directionDrive;
	
	//号牌号码
	private String plateNo = null;
	
	//号牌颜色
	private String plateColor = null;
	
	//抓拍时间
	private Date catchTime = null;
	
	//车道编号
	private String drivewayNo = null;
	
	//车速
	private String speed = null;
	
	//限速
	private String limitSpeed = null;	
	
	//特写图片路径
	private String featureImagePath = null;
	
	//全景图片路径
	private String panoramaImagePath = null;
	
	//是否黑名单车辆
	private boolean blacklistVehicle = false;
	
	//黑名单类型ID
	private String blackListTypeId = null;
	
	//黑名单类型名称（描述）
	private String blackListTypeName = null;
	
	//前端设备告警类型ID (0:正常车辆,1:黑名单车辆)
	private String alarmTypeId = null;
	
	//是否超速车辆
	private boolean overSpeedVehicle = false;
	
	private MessageBean messageBean = null;	

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
	 * @return the directionDrive
	 */
	public String getDirectionDrive() {
		return directionDrive;
	}

	/**
	 * @param directionDrive the directionDrive to set
	 */
	public void setDirectionDrive(String directionDrive) {
		this.directionDrive = directionDrive;
	}

	/**
	 * @return the plateNo
	 */
	public String getPlateNo() {
		return plateNo;
	}

	/**
	 * @param plateNo the plateNo to set
	 */
	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}

	/**
	 * @return the plateColor
	 */
	public String getPlateColor() {
		return plateColor;
	}

	/**
	 * @param plateColor the plateColor to set
	 */
	public void setPlateColor(String plateColor) {
		this.plateColor = plateColor;
	}

	/**
	 * @return the catchTime
	 */
	public Date getCatchTime() {
		return catchTime;
	}

	/**
	 * @param catchTime the catchTime to set
	 */
	public void setCatchTime(Date catchTime) {
		this.catchTime = catchTime;
	}

	/**
	 * @return the drivewayNo
	 */
	public String getDrivewayNo() {
		return drivewayNo;
	}

	/**
	 * @param drivewayNo the drivewayNo to set
	 */
	public void setDrivewayNo(String drivewayNo) {
		this.drivewayNo = drivewayNo;
	}

	/**
	 * @return the speed
	 */
	public String getSpeed() {
		return speed;
	}

	/**
	 * @param speed the speed to set
	 */
	public void setSpeed(String speed) {
		this.speed = speed;
	}

	/**
	 * @return the limitSpeed
	 */
	public String getLimitSpeed() {
		return limitSpeed;
	}

	/**
	 * @param limitSpeed the limitSpeed to set
	 */
	public void setLimitSpeed(String limitSpeed) {
		this.limitSpeed = limitSpeed;
	}

	/**
	 * @return the featureImagePath
	 */
	public String getFeatureImagePath() {
		return featureImagePath;
	}

	/**
	 * @param featureImagePath the featureImagePath to set
	 */
	public void setFeatureImagePath(String featureImagePath) {
		this.featureImagePath = featureImagePath;
	}

	/**
	 * @return the panoramaImagePath
	 */
	public String getPanoramaImagePath() {
		return panoramaImagePath;
	}

	/**
	 * @param panoramaImagePath the panoramaImagePath to set
	 */
	public void setPanoramaImagePath(String panoramaImagePath) {
		this.panoramaImagePath = panoramaImagePath;
	}

	/**
	 * @return the blacklistVehicle
	 */
	public boolean isBlacklistVehicle() {
		return blacklistVehicle;
	}

	/**
	 * @param blacklistVehicle the blacklistVehicle to set
	 */
	public void setBlacklistVehicle(boolean blacklistVehicle) {
		this.blacklistVehicle = blacklistVehicle;
	}

	/**
	 * @return the blackListTypeId
	 */
	public String getBlackListTypeId() {
		return blackListTypeId;
	}

	/**
	 * @param blackListTypeId the blackListTypeId to set
	 */
	public void setBlackListTypeId(String blackListTypeId) {
		this.blackListTypeId = blackListTypeId;
	}

	/**
	 * @return the blackListTypeName
	 */
	public String getBlackListTypeName() {
		return blackListTypeName;
	}

	/**
	 * @param blackListTypeName the blackListTypeName to set
	 */
	public void setBlackListTypeName(String blackListTypeName) {
		this.blackListTypeName = blackListTypeName;
	}

	/**
	 * @return the recordId
	 */
	public long getRecordId() {
		return recordId;
	}

	/**
	 * @param recordId the recordId to set
	 */
	public void setRecordId(long recordId) {
		this.recordId = recordId;
	}

	/**
	 * @return the alarmTypeId
	 */
	public String getAlarmTypeId() {
		return alarmTypeId;
	}

	/**
	 * @param alarmTypeId the alarmTypeId to set
	 */
	public void setAlarmTypeId(String alarmTypeId) {
		this.alarmTypeId = alarmTypeId;
	}
	
	public MessageBean getMessageBean() {
		return messageBean;
	}

	public void setMessageBean(MessageBean messageBean) {
		this.messageBean = messageBean;
	}

	public MessageBean getProtocolBean() {
		return messageBean;
	}

	public void setProtocolBean(MessageBean messageBean) {
		this.messageBean = messageBean;
	}	
	
	public boolean isOverSpeedVehicle() {
		return overSpeedVehicle;
	}

	public void setOverSpeedVehicle(boolean overSpeedVehicle) {
		this.overSpeedVehicle = overSpeedVehicle;
	}
	

}
