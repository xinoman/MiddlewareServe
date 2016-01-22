/**
 * 
 */
package com.its.core.module.broadcast;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 创建日期 2012-9-24 上午10:01:30
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ProtocolHelper {
	private static final Log log = LogFactory.getLog(ProtocolHelper.class);
	
	//协议分隔符
	public static final String PROTOCOL_SPLITER 					= "|";
	
	/**
	 * 协议类型：注册实时车辆，取终端设备发送回来的原始数据
	 * 例： register:realtime-vehicle-ori
	 */
	public static final String PROTOCOL_REGISTER_REALTIME_VEHICLE_ORI 	= "register:realtime-vehicle-ori";
	
	/**
	 * 协议头定义
	 */
	public static final String PROTOCOL_HEAD_REALTIME_VEHICLE		= "realtime-vehicle";
	public static final String PROTOCOL_HEAD_BLACKLIST_ALARM		= "blacklist-alarm";
	public static final String PROTOCOL_HEAD_OVERSPEED				= "overspeed";
	public static final String PROTOCOL_HEAD_AREA_OVERSPEED			= "area-overspeed";	
	public static final String PROTOCOL_HEAD_DUP_PLATE_MONITOR		= "dup-plate-monitor";
	
	/**
	 * 协议类型：注册实时车辆
	 * 后面可带设备编号参数,表示只接收该设备编号下的实时车辆信息，如不带参数，则表示接收所有车辆信息
	 * 例： register:realtime-vehicle 10001
	 */
	public static final String PROTOCOL_REGISTER_REALTIME_VEHICLE 	= "register:realtime-vehicle";
	
	/**
	 * 协议类型：注册黑名单车辆告警信息
	 * 后面可带设备编号参数,表示只接收该设备编号下的黑名单告警，如不带参数，则表示接收所有黑名单车辆告警信息
	 * 例： register:blacklist-alarm 10001
	 */
	public static final String PROTOCOL_REGISTER_BLACKLIST_ALARM 	= "register:blacklist-alarm";
	
	/**
	 * 协议类型：注册超速车辆信息
	 * 后面可带设备编号参数,表示只接收该设备编号下的超速车辆信息，如不带参数，则表示接收所有设备抓拍的超速车辆信息
	 */
	public static final String PROTOCOL_REGISTER_OVERSPEED 			= "register:overspeed";	
	
	/**
	 * 发送的心跳包
	 */
	public static final String PROTOCOL_HEART_BEAT_PACKAGE = "1";
	
	/**
	 * 协议类型：注销实时车辆，可以带设备编号参数
	 */
	public static final String PROTOCOL_CANCEL_REALTIME_VEHICLE 	= "cancel:realtime-vehicle";	
	
	/**
	 * 协议类型：注销黑名单车辆告警信息，可以带设备编号参数
	 */
	public static final String PROTOCOL_CANCEL_BLACKLIST_ALARM 		= "cancel:blacklist-alarm";	
	
	/**
	 * 协议类型：注销超速车辆信息，可以带设备编号参数
	 */
	public static final String PROTOCOL_CANCEL_OVERSPEED 			= "cancel:overspeed";	
	
	/**
	 * 协议类型：注销区间超速车辆信息，不带参数
	 */
	public static final String PROTOCOL_CANCEL_AREA_OVERSPEED 		= "cancel:area-overspeed";		
	
	/**
	 * 协议类型：注销套牌车监控，不带参数
	 */
	public static final String PROTOCOL_CANCEL_DUP_PLATE_MONITOR 	= "cancel:dup-plate-monitor";
	
	/**
	 * 协议类型：注册区间超速车辆信息
	 * 无参数
	 */
	public static final String PROTOCOL_REGISTER_AREA_OVERSPEED 	= "register:area-overspeed";		
	
	/**
	 * 协议类型：注册套牌车监控
	 * 套牌车监控可以分为两类：黑名单中的套牌车监控，归入“黑名单车辆监控” 本协议属于自动套牌车监控
	 * 
	 */
	public static final String PROTOCOL_REGISTER_DUP_PLATE_MONITOR 	= "register:dup-plate-monitor";
	
	/**
	 * 协议类型：注销所有已经注册的指令
	 */
	public static final String PROTOCOL_CANCEL_ALL				 	= "cancel:all";
	
	/**
	 * 协议类型：关闭当前SESSION
	 */
	public static final String PROTOCOL_CLOSE				 		= "close";
	
	/**
	 * 根据注销协议头文字获取对应的注册协议
	 * @param cancelProtocolHead
	 * @return
	 */
	public static final String getRegisterProtocol(String cancelProtocolHead){
		String result = null;
		if(cancelProtocolHead.startsWith(PROTOCOL_CANCEL_REALTIME_VEHICLE)){
			result = PROTOCOL_REGISTER_REALTIME_VEHICLE;
		}
		else if(cancelProtocolHead.startsWith(PROTOCOL_CANCEL_BLACKLIST_ALARM)){
			result = PROTOCOL_REGISTER_BLACKLIST_ALARM;
		}	
		else if(cancelProtocolHead.startsWith(PROTOCOL_CANCEL_OVERSPEED)){
			result = PROTOCOL_REGISTER_OVERSPEED;
		}	
		else if(cancelProtocolHead.startsWith(PROTOCOL_CANCEL_AREA_OVERSPEED)){
			result = PROTOCOL_REGISTER_AREA_OVERSPEED;
		}	
		else if(cancelProtocolHead.startsWith(PROTOCOL_CANCEL_DUP_PLATE_MONITOR)){
			result = PROTOCOL_REGISTER_DUP_PLATE_MONITOR;
		}			
		
		return result;
	}

}
