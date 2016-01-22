/**
 * 
 */
package com.its.core.module.device;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建日期 2012-9-20 下午04:11:04
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class IoSessionAttachment {
	
	private List<String> deviceIdList = new ArrayList<String>();
	private String deviceTypeId;

	public String getDeviceTypeId() {
		return deviceTypeId;
	}

	public void setDeviceTypeId(String deviceTypeId) {
		this.deviceTypeId = deviceTypeId;
	}

	public List<String> getDeviceIdList() {
		return deviceIdList;
	}

	public void setDeviceIdList(List<String> deviceIdList) {
		this.deviceIdList = deviceIdList;
	}

}
