package com.its.core.common;

import java.io.Serializable;
import java.util.List;

public class DeviceInfoBean implements Serializable {

	private static final long serialVersionUID = 3757953574426905577L;

	private String deviceId;

	private String deviceName;

	private String from;
	private String to;
	private String ip;
	private String areaId;
	private String areaCode;
	private String areaName;
	private String roadId;

	private String roadName;
	private String deptId;
	private String deptName;
	private String typeId;
	private String typeName;
	private String vendorId;
	private String vendorName;	

	private String directionDrive;
	private String directionCode;

	private List<DeviceDirectionBean> directionList = null;

	// 第三方开发商设备信息，与系统设备相对应
	private ThirdPartyDeviceInfoBean thirdPartyDeviceInfoBean = null;

	public String getDeptId() {
		return deptId;
	}

	public void setDeptId(String deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public String getRoadId() {
		return roadId;
	}

	public void setRoadId(String roadId) {
		this.roadId = roadId;
	}

	public String getRoadName() {
		return roadName;
	}

	public void setRoadName(String roadName) {
		this.roadName = roadName;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getVendorId() {
		return vendorId;
	}

	public void setVendorId(String vendorId) {
		this.vendorId = vendorId;
	}

	public String getVendorName() {
		return vendorName;
	}

	public void setVendorName(String vendorName) {
		this.vendorName = vendorName;
	}

	public ThirdPartyDeviceInfoBean getThirdPartyDeviceInfoBean() {
		return thirdPartyDeviceInfoBean;
	}

	public void setThirdPartyDeviceInfoBean(
			ThirdPartyDeviceInfoBean thirdPartyDeviceInfoBean) {
		this.thirdPartyDeviceInfoBean = thirdPartyDeviceInfoBean;
	}

	public String getAreaId() {
		return areaId;
	}

	public void setAreaId(String areaId) {
		this.areaId = areaId;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}

	public String getAreaName() {
		return areaName;
	}

	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}

	public List<DeviceDirectionBean> getDirectionList() {
		return directionList;
	}

	public void setDirectionList(List<DeviceDirectionBean> directionList) {
		this.directionList = directionList;
	}

	public String getDirectionDrive() {
		return directionDrive;
	}

	public void setDirectionDrive(String directionDrive) {
		this.directionDrive = directionDrive;
	}

	public String getDirectionCode() {
		return directionCode;
	}

	public void setDirectionCode(String directionCode) {
		this.directionCode = directionCode;
	}	

}
