/**
 * 
 */
package com.its.core.module.device.violate;

import java.io.Serializable;
import java.util.Date;

/**
 * 创建日期 2012-9-21 下午04:51:52
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class RealtimeViolateInfoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6765793256553862812L;
	
	//违法类型
	private String violateType = null;
	
	//设备ID
	private String deviceId = null;
	
	//方向代码
	private String directionCode = null;
	
	//号牌号码
	private String plateNo = null;
	
	//号牌颜色
	private String plateColor = null;
	
	//违法时间
	private Date violateTime = null;
	
	//车道编号
	private String drivewayNo = null;
	
	//车速
	private String speed = null;
	
	//限速
	private String limitSpeed = null;	
	
	//图片路径一
	private String imagePath1 = null;
	
	//图片路径二
	private String imagePath2 = null;
	
	//图片路径三
	private String imagePath3 = null;
	
	//图片路径四
	private String imagePath4 = null;
	
	//路口编号
	private String roadId;
	
	//图片类型编号
	private String frameTypeId = null;

	/**
	 * @return the violateType
	 */
	public String getViolateType() {
		return violateType;
	}

	/**
	 * @param violateType the violateType to set
	 */
	public void setViolateType(String violateType) {
		this.violateType = violateType;
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
	 * @return the violateTime
	 */
	public Date getViolateTime() {
		return violateTime;
	}

	/**
	 * @param violateTime the violateTime to set
	 */
	public void setViolateTime(Date violateTime) {
		this.violateTime = violateTime;
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
	 * @return the imagePath1
	 */
	public String getImagePath1() {
		return imagePath1;
	}

	/**
	 * @param imagePath1 the imagePath1 to set
	 */
	public void setImagePath1(String imagePath1) {
		this.imagePath1 = imagePath1;
	}

	/**
	 * @return the imagePath2
	 */
	public String getImagePath2() {
		return imagePath2;
	}

	/**
	 * @param imagePath2 the imagePath2 to set
	 */
	public void setImagePath2(String imagePath2) {
		this.imagePath2 = imagePath2;
	}

	/**
	 * @return the imagePath3
	 */
	public String getImagePath3() {
		return imagePath3;
	}

	/**
	 * @param imagePath3 the imagePath3 to set
	 */
	public void setImagePath3(String imagePath3) {
		this.imagePath3 = imagePath3;
	}

	/**
	 * @return the imagePath4
	 */
	public String getImagePath4() {
		return imagePath4;
	}

	/**
	 * @param imagePath4 the imagePath4 to set
	 */
	public void setImagePath4(String imagePath4) {
		this.imagePath4 = imagePath4;
	}

	/**
	 * @return the roadId
	 */
	public String getRoadId() {
		return roadId;
	}

	/**
	 * @param roadId the roadId to set
	 */
	public void setRoadId(String roadId) {
		this.roadId = roadId;
	}

	/**
	 * @return the frameTypeId
	 */
	public String getFrameTypeId() {
		return frameTypeId;
	}

	/**
	 * @param frameTypeId the frameTypeId to set
	 */
	public void setFrameTypeId(String frameTypeId) {
		this.frameTypeId = frameTypeId;
	}		

}
