/**
 * 
 */
package com.its.core.module.device.vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.common.sequence.SequenceFactory;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.broadcast.BroadcastProtocolBean;
import com.its.core.module.broadcast.InfoBroadcastModule;
import com.its.core.module.broadcast.ProtocolGeneratorFactory;
import com.its.core.module.broadcast.ProtocolHelper;
import com.its.core.module.device.vehicle.filter.IProcessorFilter;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-9-21 下午02:05:59
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class InitRealtimeVehicleInfoFilter implements IProcessorFilter {
	private static final Log log = LogFactory.getLog(InitRealtimeVehicleInfoFilter.class);
	
	private String sqlInsert = null;	
	private String sqlInsertAlarm = null;	
	private String sqlSelectBlacklistType = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.device.vehicle.filter.IProcessorFilter#configure(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	public void configure(XMLProperties props, String propertiesPrefix, int no)throws Exception {
		this.sqlInsert = props.getProperty(propertiesPrefix,no,"sql_insert");	
		this.sqlInsertAlarm = props.getProperty(propertiesPrefix,no,"sql_insert_alarm");
		this.sqlSelectBlacklistType = props.getProperty(propertiesPrefix,no,"sql_select_blacklist_type");

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.device.vehicle.filter.IProcessorFilter#process(com.its.core.module.device.vehicle.RealtimeVehicleInfoBean)
	 */
	public boolean process(RealtimeVehicleInfoBean realtimeVehicleInfoBean) throws Exception {
		boolean result = true;
		
		//查询系统是否存在该设备编号
		Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
		if(!dibMap.containsKey(realtimeVehicleInfoBean.getDeviceId())){
			log.debug("未知的设备编号:" + realtimeVehicleInfoBean.getDeviceId());
			return result;							
		}
		
		//过滤抓拍时间超过一周的数据
//		if(realtimeVehicleInfoBean.getCatchTime().getTime() < DateHelper.str2date(DateHelper.addDay(DateHelper.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"), -100), "yyyy-MM-dd HH:mm:ss").getTime() || realtimeVehicleInfoBean.getCatchTime().getTime() > DateHelper.str2date(DateHelper.addDay(DateHelper.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss"), 1), "yyyy-MM-dd HH:mm:ss").getTime()) {				
//			log.debug("设备编号[" + realtimeVehicleInfoBean.getDeviceId() + "] GPS未正常校时！");
//			return result;
//		}
		
		Connection conn = null;

		try{
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			long key = (long)SequenceFactory.getInstance().getVehicleRecordSequence();
			realtimeVehicleInfoBean.setRecordId(key);
			
			//第一步：比对黑名单车辆号牌
			this.initBlacklistType(conn,realtimeVehicleInfoBean);
			if(realtimeVehicleInfoBean.isBlacklistVehicle()){
				realtimeVehicleInfoBean.setAlarmTypeId("1");
				
				if(StringHelper.isNotEmpty(this.getSqlInsertAlarm())){
					this.insertVehicleRecordAlarm(conn, key, realtimeVehicleInfoBean);
				}
			} else {
				realtimeVehicleInfoBean.setAlarmTypeId("0");
			}
			
			//第二步：报警信息入库
			this.insertVehicleRecord(conn,key,realtimeVehicleInfoBean);		
			
			//第三步：向消息广播队列中写数据			
			if(InfoBroadcastModule.isBroadcastEnabled()){
				try {					
					//实时车辆
					String protocolMessage = ProtocolGeneratorFactory.getInstance().getProtocolGenerator().generateRealtimeVehicleMessage(realtimeVehicleInfoBean);
					BroadcastProtocolBean beanRealtime = new BroadcastProtocolBean();
					beanRealtime.setRegisterProtocol(ProtocolHelper.PROTOCOL_REGISTER_REALTIME_VEHICLE);
					beanRealtime.setDeviceId(realtimeVehicleInfoBean.getDeviceId());
					beanRealtime.setDirectionCode(realtimeVehicleInfoBean.getDirectionCode());
					beanRealtime.setContent(protocolMessage);
					
					beanRealtime.setOriMessageBean(realtimeVehicleInfoBean.getProtocolBean());
					
					//broadcastInfoQueue.put(beanRealtime);
					InfoBroadcastModule.putMessage(beanRealtime);
					
					//黑名单车辆
					if(realtimeVehicleInfoBean.isBlacklistVehicle()){
						String blacklistMessage = ProtocolGeneratorFactory.getInstance().getProtocolGenerator().generateBlacklistVehicleMessage(realtimeVehicleInfoBean);		
						BroadcastProtocolBean beanBlacklist = new BroadcastProtocolBean();
						beanBlacklist.setRegisterProtocol(ProtocolHelper.PROTOCOL_REGISTER_BLACKLIST_ALARM);
						beanBlacklist.setDeviceId(realtimeVehicleInfoBean.getDeviceId());
						beanBlacklist.setContent(blacklistMessage);
						beanBlacklist.setDirectionCode(realtimeVehicleInfoBean.getDirectionCode());
						
						beanBlacklist.setOriMessageBean(realtimeVehicleInfoBean.getProtocolBean());
						
						//broadcastInfoQueue.put(beanBlacklist);					
						InfoBroadcastModule.putMessage(beanBlacklist);
					}
					
					//超速车辆
					if(realtimeVehicleInfoBean.isOverSpeedVehicle()){
						String overspeedMessage = ProtocolGeneratorFactory.getInstance().getProtocolGenerator().generateOverspeedMessage(realtimeVehicleInfoBean);		
						BroadcastProtocolBean beanOverspeed = new BroadcastProtocolBean();
						beanOverspeed.setRegisterProtocol(ProtocolHelper.PROTOCOL_REGISTER_OVERSPEED);
						beanOverspeed.setDeviceId(realtimeVehicleInfoBean.getDeviceId());
						beanOverspeed.setDirectionCode(realtimeVehicleInfoBean.getDirectionCode());
						beanOverspeed.setContent(overspeedMessage);
						
						beanOverspeed.setOriMessageBean(realtimeVehicleInfoBean.getProtocolBean());
						
						//broadcastInfoQueue.put(beanOverspeed);
						InfoBroadcastModule.putMessage(beanOverspeed);						
					}				
				} catch (Exception e) {
					log.error(e);
				}			
			}
			
		}
		catch(Exception ex){
			result = false;
			log.error(ex.getMessage(), ex);			
		}
		finally{
			try{
				ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
			}catch(Exception ex1){}
		}
		
		return result;
	}	
	
	/**
	 * 往通行告警记录表（T_ITS_VEHICLE_RECORD_ALARM）中插入记录
	 * @param conn
	 * @param key
	 * @param realtimeVehicleInfoBean
	 * @throws Exception
	 */
	protected void insertVehicleRecordAlarm(Connection conn,long key,RealtimeVehicleInfoBean realtimeVehicleInfoBean) throws Exception{
		PreparedStatement preStatement = null;
		try{		
			
			conn.setAutoCommit(true);
			preStatement = conn.prepareStatement(this.getSqlInsertAlarm());
			preStatement.setLong(1,key);
			preStatement.setString(2,realtimeVehicleInfoBean.getPlateNo());
			preStatement.setInt(3,Integer.parseInt(realtimeVehicleInfoBean.getPlateColor()));		
			preStatement.setTimestamp(4,new java.sql.Timestamp(realtimeVehicleInfoBean.getCatchTime().getTime()));
			
			Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
			DeviceInfoBean dib = (DeviceInfoBean)dibMap.get(realtimeVehicleInfoBean.getDeviceId());
			preStatement.setInt(5, Integer.parseInt(dib.getRoadId()));			
			
			preStatement.setString(6,realtimeVehicleInfoBean.getDeviceId());
			preStatement.setString(7,realtimeVehicleInfoBean.getDirectionCode());
			preStatement.setString(8,realtimeVehicleInfoBean.getDirectionDrive());
			
			String drivewayNo = realtimeVehicleInfoBean.getDrivewayNo();
			if(drivewayNo.length()==1) drivewayNo = "0" + drivewayNo;
			preStatement.setString(9,drivewayNo);			
			preStatement.setInt(10,Integer.parseInt(realtimeVehicleInfoBean.getSpeed()));
			preStatement.setInt(11,Integer.parseInt(realtimeVehicleInfoBean.getLimitSpeed()));
			preStatement.setInt(12,Integer.parseInt(realtimeVehicleInfoBean.getAlarmTypeId()));
			preStatement.setInt(13,Integer.parseInt(realtimeVehicleInfoBean.getBlackListTypeId()));			
			preStatement.setString(14,realtimeVehicleInfoBean.getPanoramaImagePath());
			preStatement.setString(15,realtimeVehicleInfoBean.getFeatureImagePath());			
			preStatement.setTimestamp(16,new Timestamp(System.currentTimeMillis()));
			preStatement.executeUpdate();						
		}
		catch(Exception ex){
			log.error("往通行告警记录表（T_ITS_VEHICLE_RECORD_ALARM）中插入记录时出错：" + ex.getMessage(), ex);	
			throw ex;		
		}
		finally{
			DatabaseHelper.close(null, preStatement);			
		}	
	}		
	
	/**
	 * 往通行记录表（T_ITS_VEHICLE_RECORD）中插入记录
	 * @param conn
	 * @param key
	 * @param realtimeVehicleInfoBean
	 * @throws Exception
	 */
	protected void insertVehicleRecord(Connection conn,long key,RealtimeVehicleInfoBean realtimeVehicleInfoBean) throws Exception{
		PreparedStatement preStatement = null;
		long id = -1L;
		
		try{			
			conn.setAutoCommit(true);			

			preStatement = conn.prepareStatement(this.getSqlInsert());
			preStatement.setLong(1,key);
			preStatement.setString(2,realtimeVehicleInfoBean.getPlateNo());
			preStatement.setInt(3,Integer.parseInt(realtimeVehicleInfoBean.getPlateColor()));		
			preStatement.setTimestamp(4,new java.sql.Timestamp(realtimeVehicleInfoBean.getCatchTime().getTime()));
			
			Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
			DeviceInfoBean dib = (DeviceInfoBean)dibMap.get(realtimeVehicleInfoBean.getDeviceId());
			preStatement.setInt(5, Integer.parseInt(dib.getRoadId()));			
			
			preStatement.setString(6,realtimeVehicleInfoBean.getDeviceId());
			preStatement.setString(7,realtimeVehicleInfoBean.getDirectionCode());
			preStatement.setString(8,realtimeVehicleInfoBean.getDirectionDrive());
			
			String drivewayNo = realtimeVehicleInfoBean.getDrivewayNo();
			if(drivewayNo.length()==1) drivewayNo = "0" + drivewayNo;
			preStatement.setString(9,drivewayNo);			
			preStatement.setInt(10,Integer.parseInt(realtimeVehicleInfoBean.getSpeed()));
			preStatement.setInt(11,Integer.parseInt(realtimeVehicleInfoBean.getLimitSpeed()));
			preStatement.setInt(12,Integer.parseInt(realtimeVehicleInfoBean.getAlarmTypeId()));
			preStatement.setInt(13,Integer.parseInt(realtimeVehicleInfoBean.getBlackListTypeId()));			
			preStatement.setString(14,realtimeVehicleInfoBean.getPanoramaImagePath());
			preStatement.setString(15,realtimeVehicleInfoBean.getFeatureImagePath());			
			preStatement.setTimestamp(16,new Timestamp(System.currentTimeMillis()));
			preStatement.executeUpdate();					
			log.debug("入库成功生成的ID为："+key);
								
		}
		catch(Exception ex){
			log.error("往通行记录表（T_ITS_VEHICLE_RECORD）中插入记录时出错：" + ex.getMessage(), ex);	
			throw ex;		
		}
		finally{				
			DatabaseHelper.close(null, preStatement);
		}	
	}	
	
	/**
	 * 获取黑名单类型
	 * @param conn
	 * @param realtimeVehicleInfoBean
	 * @throws Exception
	 */
	private void initBlacklistType(Connection conn,RealtimeVehicleInfoBean realtimeVehicleInfoBean) throws Exception{
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;

		try{
			conn.setAutoCommit(true);
			preStatement = conn.prepareStatement(this.getSqlSelectBlacklistType());
			preStatement.setString(1,realtimeVehicleInfoBean.getPlateNo());
			preStatement.setInt(2,Integer.parseInt(realtimeVehicleInfoBean.getPlateColor()));
			resultSet = preStatement.executeQuery();
					
				
			long blacklistTypeId = -1;
			String blacklistTypeName = "正常车辆";
			if(resultSet.next()){
				blacklistTypeName = resultSet.getString(1);
				blacklistTypeId = resultSet.getLong(2);
				realtimeVehicleInfoBean.setBlacklistVehicle(true);				
			}
			
			realtimeVehicleInfoBean.setBlackListTypeId(String.valueOf(blacklistTypeId));		
			realtimeVehicleInfoBean.setBlackListTypeName(blacklistTypeName);
						
			
		}
		catch(Exception ex){
			log.error("获取黑名单类型时出错：" + ex.getMessage(), ex);	
			throw ex;		
		}
		finally{
			DatabaseHelper.close(resultSet, preStatement);
		}
	}

	/**
	 * @return the sqlInsert
	 */
	public String getSqlInsert() {
		return sqlInsert;
	}

	/**
	 * @param sqlInsert the sqlInsert to set
	 */
	public void setSqlInsert(String sqlInsert) {
		this.sqlInsert = sqlInsert;
	}

	/**
	 * @return the sqlInsertAlarm
	 */
	public String getSqlInsertAlarm() {
		return sqlInsertAlarm;
	}

	/**
	 * @param sqlInsertAlarm the sqlInsertAlarm to set
	 */
	public void setSqlInsertAlarm(String sqlInsertAlarm) {
		this.sqlInsertAlarm = sqlInsertAlarm;
	}

	/**
	 * @return the sqlSelectBlacklistType
	 */
	public String getSqlSelectBlacklistType() {
		return sqlSelectBlacklistType;
	}

	/**
	 * @param sqlSelectBlacklistType the sqlSelectBlacklistType to set
	 */
	public void setSqlSelectBlacklistType(String sqlSelectBlacklistType) {
		this.sqlSelectBlacklistType = sqlSelectBlacklistType;
	}	

}
