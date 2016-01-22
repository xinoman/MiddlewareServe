/**
 * 
 */
package com.its.core.module.device.vehicle;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.common.sequence.SequenceFactory;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.device.vehicle.filter.IProcessorFilter;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-10-26 上午09:51:52
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class TrafficFluxStatFilter implements IProcessorFilter {
	private static final Log log = LogFactory.getLog(TrafficFluxStatFilter.class);
	
	private String dayStatCheckExistSql = null;	
	private String dayStatInsertSql = null;	
	private String dayStatUpdateSql = null;
	
	private String hourStatCheckExistSql = null;	
	private String hourStatInsertSql = null;	
	private String hourStatUpdateSql = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.device.vehicle.filter.IProcessorFilter#configure(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	public void configure(XMLProperties props, String propertiesPrefix, int no) throws Exception {
		this.dayStatCheckExistSql = (props.getProperty(propertiesPrefix, no, "day_stat_sql.check_exist"));
		this.dayStatInsertSql = (props.getProperty(propertiesPrefix, no, "day_stat_sql.insert"));
		this.dayStatUpdateSql = (props.getProperty(propertiesPrefix, no, "day_stat_sql.update"));
		
		this.hourStatCheckExistSql = (props.getProperty(propertiesPrefix, no, "hour_stat_sql.check_exist"));
		this.hourStatInsertSql = (props.getProperty(propertiesPrefix, no, "hour_stat_sql.insert"));
		this.hourStatUpdateSql = (props.getProperty(propertiesPrefix, no, "hour_stat_sql.update"));

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.device.vehicle.filter.IProcessorFilter#process(com.its.core.module.device.vehicle.RealtimeVehicleInfoBean)
	 */
	public boolean process(RealtimeVehicleInfoBean realtimeVehicleInfoBean) throws Exception {
		
		Map<String, DeviceInfoBean> dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();		
		if(!dibMap.containsKey(realtimeVehicleInfoBean.getDeviceId())){
			log.debug("未知的设备编号:" + realtimeVehicleInfoBean.getDeviceId());			
			return true;
		}		
		
		Connection conn = null;
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;		
		
		try{
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(true);
			
			Timestamp catchDay = new Timestamp(DateHelper.parseDateString(DateHelper.dateToString(realtimeVehicleInfoBean.getCatchTime(), "yyyyMMdd"),"yyyyMMdd").getTime());
			long statId = -1L;
			String laneNo = realtimeVehicleInfoBean.getDrivewayNo();
			if(laneNo.length()==1){
				laneNo = "0"+laneNo;
			}
			
			long roadId = 0;
			
			preStatement = conn.prepareStatement(this.getDayStatCheckExistSql());
			DeviceInfoBean dib = (DeviceInfoBean)dibMap.get(realtimeVehicleInfoBean.getDeviceId());
			roadId = Long.valueOf(dib.getRoadId());
			preStatement.setLong(1,roadId);	
			preStatement.setString(2, realtimeVehicleInfoBean.getDeviceId());
			preStatement.setString(3, realtimeVehicleInfoBean.getDirectionCode());	
			preStatement.setString(4, laneNo);	
			preStatement.setTimestamp(5, catchDay);
			resultSet = preStatement.executeQuery();
			if(resultSet.next()){
				statId = resultSet.getLong("id");					
			}
			
			DatabaseHelper.close(resultSet, preStatement);
			if(statId==-1L){
				
				statId = (long)SequenceFactory.getInstance().getStatTrafficDayStatSequence();
				preStatement = conn.prepareStatement(this.getDayStatInsertSql());
				preStatement.setLong(1,statId);	
				preStatement.setLong(2,roadId);	
				preStatement.setString(3, realtimeVehicleInfoBean.getDeviceId());
				preStatement.setString(4, realtimeVehicleInfoBean.getDirectionCode());		
				preStatement.setString(5, laneNo);
				preStatement.setTimestamp(6, catchDay);	
				preStatement.setLong(7, 1L);	
//				log.debug("statId = "+statId+" created!");
			}
			else{
				//更新记录:update T_ITS_TRAFFIC_DAY_STAT set IMAGE_SUM=IMAGE_SUM+1 where ID=?
				preStatement = conn.prepareStatement(this.getDayStatUpdateSql());
				preStatement.setLong(1,statId);	
//				log.debug("statId = "+statId+" updated!");
			}
			preStatement.executeUpdate();	
			
			DatabaseHelper.close(null, preStatement);			
			if(StringHelper.isNotEmpty(this.getHourStatCheckExistSql())) {				

				String catchHour = DateHelper.dateToString(realtimeVehicleInfoBean.getCatchTime(), "yyyy-MM-dd HH");
				long id = -1L;
				preStatement = conn.prepareStatement(this.getHourStatCheckExistSql());				
				preStatement.setLong(1,roadId);
				preStatement.setString(2, realtimeVehicleInfoBean.getDeviceId());
				preStatement.setString(3, realtimeVehicleInfoBean.getDirectionCode());	
				preStatement.setString(4, laneNo);	
				preStatement.setString(5, catchHour);
				resultSet = preStatement.executeQuery();
				if(resultSet.next()){
					id = resultSet.getLong("id");					
				}
				DatabaseHelper.close(resultSet, preStatement);
				if(id==-1L){
					
					id = (long)SequenceFactory.getInstance().getStatTrafficHourStatSequence();					
					preStatement = conn.prepareStatement(this.getHourStatInsertSql());
					preStatement.setLong(1,id);						
					preStatement.setLong(2,roadId);						
					preStatement.setString(3, realtimeVehicleInfoBean.getDeviceId());					
					preStatement.setString(4, realtimeVehicleInfoBean.getDirectionCode());					
					preStatement.setString(5, laneNo);					
					preStatement.setString(6, catchHour);	
					preStatement.setLong(7, 1L);	
//					log.debug("id = "+id+" created!");
				}
				else{
					
					preStatement = conn.prepareStatement(this.getHourStatUpdateSql());
					preStatement.setLong(1,id);	
//					log.debug("id = "+id+" updated!");
				}
				preStatement.executeUpdate();
				DatabaseHelper.close(null, preStatement);
			}
			
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);
		}finally{
			
			DatabaseHelper.close(resultSet, preStatement);
			
			try{
				ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
			}catch(Exception ex1){}				
		}	

		return true;
	}

	public String getDayStatCheckExistSql() {
		return dayStatCheckExistSql;
	}

	public void setDayStatCheckExistSql(String dayStatCheckExistSql) {
		this.dayStatCheckExistSql = dayStatCheckExistSql;
	}

	public String getDayStatInsertSql() {
		return dayStatInsertSql;
	}

	public void setDayStatInsertSql(String dayStatInsertSql) {
		this.dayStatInsertSql = dayStatInsertSql;
	}

	public String getDayStatUpdateSql() {
		return dayStatUpdateSql;
	}

	public void setDayStatUpdateSql(String dayStatUpdateSql) {
		this.dayStatUpdateSql = dayStatUpdateSql;
	}

	public String getHourStatCheckExistSql() {
		return hourStatCheckExistSql;
	}

	public void setHourStatCheckExistSql(String hourStatCheckExistSql) {
		this.hourStatCheckExistSql = hourStatCheckExistSql;
	}

	public String getHourStatInsertSql() {
		return hourStatInsertSql;
	}

	public void setHourStatInsertSql(String hourStatInsertSql) {
		this.hourStatInsertSql = hourStatInsertSql;
	}

	public String getHourStatUpdateSql() {
		return hourStatUpdateSql;
	}

	public void setHourStatUpdateSql(String hourStatUpdateSql) {
		this.hourStatUpdateSql = hourStatUpdateSql;
	}
	

}
