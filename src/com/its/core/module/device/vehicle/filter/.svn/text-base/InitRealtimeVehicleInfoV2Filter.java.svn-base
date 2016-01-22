/**
 * 
 */
package com.its.core.module.device.vehicle.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
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
import com.its.core.module.device.BlacklistBean;
import com.its.core.module.device.ResourceCache;
import com.its.core.module.device.VehicleRecordAlarmBean;
import com.its.core.module.device.vehicle.InitRealtimeVehicleInfoFilter;
import com.its.core.module.device.vehicle.RealtimeVehicleInfoBean;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-1-30 下午03:47:36
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class InitRealtimeVehicleInfoV2Filter implements IProcessorFilter {
	private static final Log log = LogFactory.getLog(InitRealtimeVehicleInfoFilter.class);
	
	private String sqlInsert = null;	
	private String sqlInsertAlarm = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.device.vehicle.filter.IProcessorFilter#configure(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configure(XMLProperties props, String propertiesPrefix, int no) throws Exception {
		this.sqlInsert = props.getProperty(propertiesPrefix,no,"sql_insert");	
		this.sqlInsertAlarm = props.getProperty(propertiesPrefix,no,"sql_insert_alarm");

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.device.vehicle.filter.IProcessorFilter#process(com.its.core.module.device.vehicle.RealtimeVehicleInfoBean)
	 */
	@Override
	public boolean process(RealtimeVehicleInfoBean realtimeVehicleInfoBean) throws Exception {
		boolean result = true;
		
		//查询系统是否存在该设备编号
		Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
		if(!dibMap.containsKey(realtimeVehicleInfoBean.getDeviceId())){
			log.debug("未知的设备编号:" + realtimeVehicleInfoBean.getDeviceId());
			return result;							
		}
		
		Connection conn = null;
		try{
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			long key = (long)SequenceFactory.getInstance().getVehicleRecordSequence();
			realtimeVehicleInfoBean.setRecordId(key);
			realtimeVehicleInfoBean.setBlackListTypeId("-1");
			
			List<BlacklistBean> blacklistList = ResourceCache.getPlateMonitorMap().match(realtimeVehicleInfoBean.getPlateNo(), realtimeVehicleInfoBean.getPlateColor());
			int blacklistSize  = blacklistList.size();
			List<VehicleRecordAlarmBean> alarmList = null;			
			if(blacklistSize>0){
				//实时车辆信息只取第一个黑名单类型
				realtimeVehicleInfoBean.setBlacklistVehicle(true);
				realtimeVehicleInfoBean.setBlackListTypeId(blacklistList.get(0).getTypeId());
				realtimeVehicleInfoBean.setBlackListTypeName(blacklistList.get(0).getTypeName());
				realtimeVehicleInfoBean.setAlarmTypeId("1");
				
				if(StringHelper.isNotEmpty(this.getSqlInsertAlarm())){
					//报警信息入库
					alarmList = this.insertVehicleRecordAlarm(conn, realtimeVehicleInfoBean,blacklistList);
				}				
			}else {
				realtimeVehicleInfoBean.setAlarmTypeId("0");
			}
			
			
			//第二步：实时车辆信息入库
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
					if(alarmList!=null){
						RealtimeVehicleInfoBean tmpRvi = new RealtimeVehicleInfoBean();
						BeanUtils.copyProperties(tmpRvi, realtimeVehicleInfoBean);
						int alarmSize = alarmList.size();
						log.info("alarmSize = " + alarmSize);
						for(int i=0;i<alarmSize;i++){
							VehicleRecordAlarmBean alarmBean = alarmList.get(i);
							BlacklistBean blacklist = alarmBean.getBlacklist();
//							tmpRvi.setAlarmId(alarmBean.getAlarmId());
//							tmpRvi.setBlacklistId(blacklist.getId());
							tmpRvi.setBlackListTypeId(blacklist.getTypeId());
							tmpRvi.setBlackListTypeName(blacklist.getTypeName());
//							tmpRvi.setBlackListPlateNo(blacklist.getPlate());
//							tmpRvi.setBlacklistPlateColor(blacklist.getPlateColorCode());
//							tmpRvi.setBlacklistPlateMatchBitNum(PlateHelper.getMatchNumber(tmpRvi.getPlateNo(), blacklist.getPlate()));
							
							String blacklistMessage = ProtocolGeneratorFactory.getInstance().getProtocolGenerator().generateBlacklistVehicleMessage(tmpRvi);		
							BroadcastProtocolBean beanBlacklist = new BroadcastProtocolBean();
							beanBlacklist.setRegisterProtocol(ProtocolHelper.PROTOCOL_REGISTER_BLACKLIST_ALARM);
							beanBlacklist.setDeviceId(realtimeVehicleInfoBean.getDeviceId());
							beanBlacklist.setContent(blacklistMessage);
							beanBlacklist.setDirectionCode(realtimeVehicleInfoBean.getDirectionCode());
							
							beanBlacklist.setOriMessageBean(realtimeVehicleInfoBean.getProtocolBean());
							
							//broadcastInfoQueue.put(beanBlacklist);					
							InfoBroadcastModule.putMessage(beanBlacklist);							
						}
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
	protected List<VehicleRecordAlarmBean> insertVehicleRecordAlarm(Connection conn,RealtimeVehicleInfoBean realtimeVehicleInfoBean,List<BlacklistBean> blacklistList) throws Exception{
		PreparedStatement preStatement = null;
		List<VehicleRecordAlarmBean> alarmList = new ArrayList<VehicleRecordAlarmBean>(blacklistList.size());
		try{
			conn.setAutoCommit(true);
			preStatement = conn.prepareStatement(this.getSqlInsertAlarm());
			int size = blacklistList.size();
			for(int i=0;i<size;i++){
				BlacklistBean blacklist = (BlacklistBean)blacklistList.get(i);
				long alarmId = (long)SequenceFactory.getInstance().getVehicleRecordSequence();
				VehicleRecordAlarmBean alarmBean = new VehicleRecordAlarmBean();
				alarmBean.setAlarmId(alarmId);
				alarmBean.setBlacklist(blacklist);
				alarmList.add(alarmBean);
				
				preStatement.setLong(1,alarmId);
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
				preStatement.addBatch();
			}

			preStatement.executeBatch();				
		}
		catch(Exception ex){
			log.error("往通行告警记录表（T_ITS_VEHICLE_RECORD_ALARM）中插入记录时出错：" + ex.getMessage(), ex);	
			throw ex;		
		}
		finally{
			DatabaseHelper.close(null, preStatement);			
		}	
		
		return alarmList;
			
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

	public String getSqlInsert() {
		return sqlInsert;
	}

	public void setSqlInsert(String sqlInsert) {
		this.sqlInsert = sqlInsert;
	}

	public String getSqlInsertAlarm() {
		return sqlInsertAlarm;
	}

	public void setSqlInsertAlarm(String sqlInsertAlarm) {
		this.sqlInsertAlarm = sqlInsertAlarm;
	}	

}
