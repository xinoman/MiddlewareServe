/**
 * 
 */
package com.its.core.common.sequence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.util.DatabaseHelper;

/**
 * 创建日期 2012-8-3 上午11:49:23
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ItsSequenceFactory extends SequenceFactory {
	private static final Log log = LogFactory.getLog(ItsSequenceFactory.class);
	
	private static Map<String,SequenceBean> sequenceMap = new HashMap<String,SequenceBean>();
	
	/**
	 * 缺省缓存SEQUENCE数目
	 */
	private static double DEFAULT_CACHE_NUMBER = 50;	
	
	@Override
	public void config() {
	}		
	
	public double getViolateRecordTempSequence() throws Exception{
		return this.getValue(SystemConstant.getInstance().TABLENAME_VIOLATE_RECORD_TEMP);
	}
	
	public double getVehicleRecordSequence() throws Exception{
		return this.getValue(SystemConstant.getInstance().TABLENAME_VEHICLE_RECORD);
	}		
	
	public double getStatTrafficHourStatSequence() throws Exception{
		return this.getValue(SystemConstant.getInstance().TABLENAME_TRAFFIC_HOUR_STAT);
	}
	
	public double getStatTrafficDayStatSequence() throws Exception{
		return this.getValue(SystemConstant.getInstance().TABLENAME_TRAFFIC_DAY_STAT);
	}
	
	public double getDeviceStatusSequence() throws Exception{
		return this.getValue(SystemConstant.getInstance().TABLENAME_DEVICE_STATUS);
	}

	private SequenceBean getCacheSequenceBean(String tableName) throws Exception{
		SequenceBean sequenceBean = null;
		Connection conn = null;
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;
		try {
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(true);
			preStatement = conn.prepareStatement("select MIN_VALUE,MAX_VALUE,CACHE_NUMBER,LAST_VALUE as LAST_VALUE from T_ITS_SEQUENCE where TABLE_NAME='"+tableName+"'");
			resultSet = preStatement.executeQuery();
			double minValue=0,maxValue=0,cacheNumber=0,lastValue = 0;
			if(resultSet.next()){
				minValue 	= resultSet.getDouble("min_value");
				maxValue 	= resultSet.getDouble("max_value");
				cacheNumber = resultSet.getDouble("cache_number");
				lastValue 	= resultSet.getDouble("last_value");
			}
			else{
				DatabaseHelper.close(resultSet, preStatement);
				preStatement = conn.prepareStatement("select MIN_VALUE,MAX_VALUE,CACHE_NUMBER,LAST_VALUE as LAST_VALUE from T_ITS_SEQUENCE where TABLE_NAME='DEFAULT'");
				resultSet = preStatement.executeQuery();
				if(resultSet.next()){
					minValue 	= resultSet.getDouble("min_value");
					maxValue 	= resultSet.getDouble("max_value");	
					cacheNumber = resultSet.getDouble("cache_number");
					lastValue 	= resultSet.getDouble("last_value");
					tableName = "DEFAULT";
				}				
			}
			if(cacheNumber<=0) cacheNumber = DEFAULT_CACHE_NUMBER;
			if(lastValue>=maxValue) lastValue = minValue;
			
			double limitValue = lastValue+cacheNumber;
			if(limitValue>maxValue){
				limitValue = maxValue;
			}				
			sequenceBean = new SequenceBean();
			sequenceBean.setMinValue(minValue);
			sequenceBean.setMaxValue(maxValue);
			sequenceBean.setLastValue(lastValue);
			sequenceBean.setCacheNumber(cacheNumber);
			sequenceBean.setLimitValue(limitValue);
			
			DatabaseHelper.close(resultSet, preStatement);
			preStatement = conn.prepareStatement("update T_ITS_SEQUENCE set LAST_VALUE=? where TABLE_NAME='"+tableName+"'");
			preStatement.setDouble(1, limitValue);
			preStatement.executeUpdate();
		} catch (Exception ex) {
			log.error("获取表'"+tableName+"' SEQUENCE信息时出错：" + ex.getMessage(), ex);
			throw ex;
		} finally {
			DatabaseHelper.close(resultSet, preStatement);
			if (conn != null) {
				try {
					ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
				} catch (Exception ex1) {
				}
			}
		}
		return sequenceBean;
	}
	
	/**
	 * 根据表名获取缓存中的SEQUENCE值
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	private double getValue(String tableName) throws Exception {
		double returnValue = 0;
		synchronized(tableName){
			SequenceBean sequenceBean = (SequenceBean)sequenceMap.get(tableName);
			if(sequenceBean!=null){
				double lastValue = sequenceBean.getLastValue();
				if(lastValue>=sequenceBean.getLimitValue()){
					sequenceBean = this.getCacheSequenceBean(tableName);
					sequenceMap.put(tableName, sequenceBean);
				}			
			}
			else{
				sequenceBean = this.getCacheSequenceBean(tableName);
				if(sequenceBean!=null) sequenceMap.put(tableName, sequenceBean);
			}			
			
			if(sequenceBean!=null){
				double lastValue = sequenceBean.getLastValue();
				lastValue++;
				sequenceBean.setLastValue(lastValue);
				returnValue = lastValue;
				//log.debug("从缓存中获取SEQUENCE值："+returnValue);
			}
			else{
				returnValue = this.getValueDirect(tableName);
			}	
		}
		return returnValue;
	}
	
	/**
	 * 直接从SEQUENCE表中获取值
	 * @param tableName
	 * @return
	 * @throws Exception
	 */
	private double getValueDirect(String tableName) throws Exception {
		double sequence = 0;
		Connection conn = null;
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;
		try {
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(true);
			preStatement = conn.prepareStatement("select MIN_VALUE,MAX_VALUE,LAST_VALUE+1 as LAST_VALUE from T_SEQUENCE where TABLE_NAME='"+tableName+"'");
			resultSet = preStatement.executeQuery();
			if(resultSet.next()){
				double minValue = resultSet.getLong("min_value");
				double maxValue = resultSet.getLong("max_value");
				sequence = resultSet.getDouble("last_value");
				if(sequence>=maxValue) sequence = minValue;
			}
			else{
				DatabaseHelper.close(resultSet, preStatement);
				preStatement = conn.prepareStatement("select MIN_VALUE,MAX_VALUE,LAST_VALUE+1 as LAST_VALUE from T_SEQUENCE where TABLE_NAME='DEFAULT'");
				resultSet = preStatement.executeQuery();
				if(resultSet.next()){
					double minValue = resultSet.getDouble("min_value");
					double maxValue = resultSet.getDouble("max_value");					
					sequence = resultSet.getDouble("last_value");
					if(sequence>=maxValue) sequence = minValue;
					tableName = "DEFAULT";
				}				
			}			
			DatabaseHelper.close(resultSet, preStatement);
			preStatement = conn.prepareStatement("update T_SEQUENCE set LAST_VALUE=? where TABLE_NAME='"+tableName+"'");
			preStatement.setDouble(1, sequence);
			preStatement.executeUpdate();
		} catch (Exception ex) {
			log.error("获取表'"+tableName+"' SEQUENCE信息时出错：" + ex.getMessage(), ex);
			throw ex;
		} finally {
			DatabaseHelper.close(resultSet, preStatement);
			if (conn != null) {
				try {
					ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
				} catch (Exception ex1) {
				}
			}
		}
		return sequence;
	}

}
