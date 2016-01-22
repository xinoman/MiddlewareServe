/**
 * 
 */
package com.its.core.module.broadcast;

import com.its.core.module.device.vehicle.RealtimeVehicleInfoBean;

/**
 * 创建日期 2012-9-24 上午09:50:22
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public interface IProtocolGenerator {
	
	/**
	 * 生成实时车辆信息
	 * @param realtimeVehicleInfoBean
	 * @return
	 */
	public String generateRealtimeVehicleMessage(RealtimeVehicleInfoBean realtimeVehicleInfoBean);
	
	/**
	 * 生成超速车辆信息
	 * @param realtimeVehicleInfoBean
	 * @return
	 */
	public String generateOverspeedMessage(RealtimeVehicleInfoBean realtimeVehicleInfoBean);
	
	/**
	 * 生成黑名单车辆信息
	 * @param realtimeVehicleInfoBean
	 * @return
	 */
	public String generateBlacklistVehicleMessage(RealtimeVehicleInfoBean realtimeVehicleInfoBean);
	
	/**
	 * 生成区间超速车辆信息
	 * @param realtimeVehicleInfoBean
	 * @return
	 */
	public String generateAreaOverspeedMessage(RealtimeVehicleInfoBean realtimeVehicleInfoBean);

}
