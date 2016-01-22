/**
 * 
 */
package com.its.core.module.filescan.violation;

import java.io.Serializable;
import java.util.Date;

import com.its.core.common.DeviceInfoBean;

/**
 * 创建日期 2012-12-13 上午08:32:16
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ViolationInfoBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6532948733863830412L;
	
	//设备编号
	private String deviceId; 
	
	//设备IP
	private String deviceIp;
	
	private String md5;
	
	//图片和视频文件所在目录	 */
	private String fileDir = null;
	
	//违法时间
	private Date violateTime; 
	
	//违法毫秒数，违法时间加上这个违法毫秒数，即可精确时间
	private int violateMilliSecond;
	
	//图片路径数组
	private String[] imageFiles; 
	
	//视频路径
	private String videoFile;
	
	//车速
	private String speed;
	
	//限速
	private String limitSpeed;
	
	//车道号
	private String line;
	
	//违法类型
	private String violateType = null;
	
	//红灯时间
	private String redLightTime = null;
	
	//设备信息Bean
	private DeviceInfoBean deviceInfoBean = null;
	
	//车牌号码
	private String plateNo = null;
	private String plateType = null;
	
	/**
	 * 车牌颜色,号牌颜色分类：
	 * 	0---蓝牌		
	 * 	1---黄牌		
	 * 	2---白牌		
	 * 	3---黑牌		
	 * 	4---其他
	 */
	private String plateColor = null;
	
	//设备方向编号
	private String directionNo = null;
	
	//违法行为代码
	private String wfxwCode = null;
	
	private String imagePath1 = null;
	private String imagePath2 = null;
	private String imagePath3 = null;
	private String imagePath4 = null;
	
	private String roadName = null;
	
	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getFileDir() {
		return fileDir;
	}

	public void setFileDir(String fileDir) {
		this.fileDir = fileDir;
	}

	public Date getViolateTime() {
		return violateTime;
	}

	public void setViolateTime(Date violateTime) {
		this.violateTime = violateTime;
	}

	public int getViolateMilliSecond() {
		return violateMilliSecond;
	}

	public void setViolateMilliSecond(int violateMilliSecond) {
		this.violateMilliSecond = violateMilliSecond;
	}

	public String[] getImageFiles() {
		return imageFiles;
	}

	public void setImageFiles(String[] imageFiles) {
		this.imageFiles = imageFiles;
	}

	public String getVideoFile() {
		return videoFile;
	}

	public void setVideoFile(String videoFile) {
		this.videoFile = videoFile;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}

	public String getLimitSpeed() {
		return limitSpeed;
	}

	public void setLimitSpeed(String limitSpeed) {
		this.limitSpeed = limitSpeed;
	}

	public String getLine() {
		return line;
	}

	public void setLine(String line) {
		this.line = line;
	}

	public DeviceInfoBean getDeviceInfoBean() {
		return deviceInfoBean;
	}

	public void setDeviceInfoBean(DeviceInfoBean deviceInfoBean) {
		this.deviceInfoBean = deviceInfoBean;
	}

	public String getPlateNo() {
		return plateNo;
	}

	public void setPlateNo(String plateNo) {
		this.plateNo = plateNo;
	}public String getPlateType() {
		return plateType;
	}

	public void setPlateType(String plateType) {
		this.plateType = plateType;
	}

	public String getPlateColor() {
		return plateColor;
	}

	public void setPlateColor(String plateColor) {
		this.plateColor = plateColor;
	}

	public String getDirectionNo() {
		return directionNo;
	}

	public void setDirectionNo(String directionNo) {
		this.directionNo = directionNo;
	}

	public String getWfxwCode() {
		return wfxwCode;
	}

	public void setWfxwCode(String wfxwCode) {
		this.wfxwCode = wfxwCode;
	}	

	public String getViolateType() {
		return violateType;
	}

	public void setViolateType(String violateType) {
		this.violateType = violateType;
	}

	public String getRedLightTime() {
		return redLightTime;
	}

	public void setRedLightTime(String redLightTime) {
		this.redLightTime = redLightTime;
	}

	public String getImagePath1() {
		return imagePath1;
	}

	public void setImagePath1(String imagePath1) {
		this.imagePath1 = imagePath1;
	}

	public String getImagePath2() {
		return imagePath2;
	}

	public void setImagePath2(String imagePath2) {
		this.imagePath2 = imagePath2;
	}

	public String getImagePath3() {
		return imagePath3;
	}

	public void setImagePath3(String imagePath3) {
		this.imagePath3 = imagePath3;
	}

	public String getImagePath4() {
		return imagePath4;
	}

	public void setImagePath4(String imagePath4) {
		this.imagePath4 = imagePath4;
	}

	public String getRoadName() {
		return roadName;
	}

	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}

	public String getDeviceIp() {
		return deviceIp;
	}

	public void setDeviceIp(String deviceIp) {
		this.deviceIp = deviceIp;
	}

	public String getMd5() {
		return md5;
	}

	public void setMd5(String md5) {
		this.md5 = md5;
	}	

}
