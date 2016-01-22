/**
 * 
 */
package com.its.core.module.broadcast.impl;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.module.broadcast.IProtocolGenerator;
import com.its.core.module.broadcast.ProtocolHelper;
import com.its.core.module.device.vehicle.RealtimeVehicleInfoBean;
import com.its.core.util.DateHelper;

/**
 * 创建日期 2012-9-24 下午01:56:28
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ProtocolGenerator implements IProtocolGenerator {
	private static final Log log = LogFactory.getLog(ProtocolGenerator.class);
	
	//当前协议版本号
	public static final String CURRENT_VERSION	= "1.0.0.1";
	
	/* (non-Javadoc)
	 * @see com.its.core.module.broadcast.IProtocolGenerator#generateRealtimeVehicleMessage(com.its.core.module.device.vehicle.RealtimeVehicleInfoBean)
	 */
	public String generateRealtimeVehicleMessage(RealtimeVehicleInfoBean realtimeVehicleInfoBean) {
		return this.generateMessage(ProtocolHelper.PROTOCOL_HEAD_REALTIME_VEHICLE, realtimeVehicleInfoBean);
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.broadcast.IProtocolGenerator#generateBlacklistVehicleMessage(com.its.core.module.device.vehicle.RealtimeVehicleInfoBean)
	 */
	public String generateBlacklistVehicleMessage(RealtimeVehicleInfoBean realtimeVehicleInfoBean) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.broadcast.IProtocolGenerator#generateOverspeedMessage(com.its.core.module.device.vehicle.RealtimeVehicleInfoBean)
	 */
	public String generateOverspeedMessage(RealtimeVehicleInfoBean realtimeVehicleInfoBean) {
		// TODO Auto-generated method stub
		return null;
	}
	
	/* (non-Javadoc)
	 * @see com.its.core.module.broadcast.IProtocolGenerator#generateAreaOverspeedMessage(com.its.core.module.device.vehicle.RealtimeVehicleInfoBean)
	 */
	public String generateAreaOverspeedMessage(RealtimeVehicleInfoBean realtimeVehicleInfoBean) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private String generateMessage(String head,RealtimeVehicleInfoBean realtimeVehicleInfoBean) {
		StringBuilder broadcastContent = new StringBuilder();
		broadcastContent.append(head).append(ProtocolHelper.PROTOCOL_SPLITER);
		broadcastContent.append(CURRENT_VERSION).append(ProtocolHelper.PROTOCOL_SPLITER);
		broadcastContent.append(realtimeVehicleInfoBean.getRecordId()).append(ProtocolHelper.PROTOCOL_SPLITER);	
		broadcastContent.append(realtimeVehicleInfoBean.getPlateNo()).append(ProtocolHelper.PROTOCOL_SPLITER);
		broadcastContent.append(realtimeVehicleInfoBean.getPlateColor()).append(ProtocolHelper.PROTOCOL_SPLITER);
		broadcastContent.append(DateHelper.dateToString(realtimeVehicleInfoBean.getCatchTime(), "yyyy-MM-dd HH:mm:ss")).append(ProtocolHelper.PROTOCOL_SPLITER);
		
		String deviceId = realtimeVehicleInfoBean.getDeviceId();
		Map deviceMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
		DeviceInfoBean dib = null;
		if(deviceMap.containsKey(deviceId)){
			dib = (DeviceInfoBean)deviceMap.get(deviceId);
			broadcastContent.append(dib.getRoadId()).append(ProtocolHelper.PROTOCOL_SPLITER);
			broadcastContent.append(dib.getRoadName()).append(ProtocolHelper.PROTOCOL_SPLITER);
		}		
		else{
			broadcastContent.append("undefined").append(ProtocolHelper.PROTOCOL_SPLITER);
			broadcastContent.append("undefined").append(ProtocolHelper.PROTOCOL_SPLITER);
			log.warn("未找到设备ID：" + deviceId + "对应的设备信息！");
			//需要重新装载设备信息
			DeviceInfoLoaderFactory.getInstance().setRequireReload(true);	
		}
		
		broadcastContent.append(realtimeVehicleInfoBean.getDeviceId()).append(ProtocolHelper.PROTOCOL_SPLITER);
		broadcastContent.append(realtimeVehicleInfoBean.getDirectionCode()).append(ProtocolHelper.PROTOCOL_SPLITER);		
		broadcastContent.append(realtimeVehicleInfoBean.getDirectionDrive()).append(ProtocolHelper.PROTOCOL_SPLITER);
		broadcastContent.append(realtimeVehicleInfoBean.getDrivewayNo()).append(ProtocolHelper.PROTOCOL_SPLITER);
		broadcastContent.append(Integer.parseInt(realtimeVehicleInfoBean.getSpeed())).append(ProtocolHelper.PROTOCOL_SPLITER);
		broadcastContent.append(Integer.parseInt(realtimeVehicleInfoBean.getLimitSpeed())).append(ProtocolHelper.PROTOCOL_SPLITER);			
		
		broadcastContent.append(realtimeVehicleInfoBean.getBlackListTypeId()).append(ProtocolHelper.PROTOCOL_SPLITER);
		broadcastContent.append(realtimeVehicleInfoBean.getBlackListTypeName()).append(ProtocolHelper.PROTOCOL_SPLITER);
				
		broadcastContent.append(realtimeVehicleInfoBean.getFeatureImagePath()).append(ProtocolHelper.PROTOCOL_SPLITER);
		broadcastContent.append(realtimeVehicleInfoBean.getPanoramaImagePath());						
		
		return broadcastContent.toString();		
	}
	

}
