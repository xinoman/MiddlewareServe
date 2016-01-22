/**
 * 
 */
package com.its.core.module.task.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.task.ATask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-4-17 下午02:29:47
 * @author GuoPing.Wu QQ:365175040
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ClearExpireVehicleDataTask extends ATask {
	private static final Log log = LogFactory.getLog(ClearExpireVehicleDataTask.class);	
	
	private String selectLastHistorySql = null;	
	private String transferVehicleRecordSql = null;	
	private String deleteOvertimeHistorySql = null;	

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {		
		this.selectLastHistorySql = props.getProperty(propertiesPrefix,no,"sql.select_last_history");
		this.transferVehicleRecordSql = props.getProperty(propertiesPrefix,no,"sql.transfer_vehicle_record");		
		this.deleteOvertimeHistorySql = props.getProperty(propertiesPrefix,no,"sql.delete_overtime_history");
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		
		Connection conn = null;
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;
		
		Timestamp catchTime = null;
		String tableName = null;
		Timestamp startTimestamp = null;	
		
		try {
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(false);
			
			//获取最早记录的时间
			preStatement = conn.prepareStatement("select * from ( select row_.*, rownum rownum_ from (select * from T_ITS_VEHICLE_RECORD order by catch_time) row_) where rownum=1");
			resultSet = preStatement.executeQuery();			
			if(resultSet.next()){				
				catchTime = resultSet.getTimestamp("catch_time");
			}
			DatabaseHelper.close(resultSet,preStatement);
			
			//获取历史备份表中最早一条记录
			preStatement = conn.prepareStatement(this.getSelectLastHistorySql());
			log.debug(this.getSelectLastHistorySql());
			resultSet = preStatement.executeQuery();			
			while(resultSet.next()){								
				tableName = resultSet.getString("table_name");
				startTimestamp = resultSet.getTimestamp("start_time");				
			}
			DatabaseHelper.close(resultSet,preStatement);
			
			//开始转移数据
			if(StringHelper.isNotEmpty(catchTime) && StringHelper.isNotEmpty(tableName)) {
				this.traferVehicleRecordToHistory(conn, tableName, catchTime, startTimestamp);
			}			
			
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);		
		}
		finally{
			if(conn!=null){
				try{
					ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
				}
				catch(Exception ex2){}
			}
		}

	}
	
	private void traferVehicleRecordToHistory(Connection conn,String tableName,Timestamp startTime,Timestamp endTime) throws Exception{
		PreparedStatement preStatement = null;
		try{			
				
			String transferSql = StringHelper.replace(this.getTransferVehicleRecordSql(), "${tableName}", tableName);
			log.debug(transferSql);
			
			//转记录
			preStatement = conn.prepareStatement(transferSql);				
			preStatement.setTimestamp(1, startTime);
			preStatement.setTimestamp(2, endTime);
			preStatement.executeUpdate();
			preStatement.close();			
			
			
			//删除原数据（T_ITS_VEHICLE_RECORD）
			log.debug(this.getDeleteOvertimeHistorySql());
			preStatement = conn.prepareStatement(this.getDeleteOvertimeHistorySql());						
			preStatement.setTimestamp(1, startTime);
			preStatement.setTimestamp(2, endTime);
			preStatement.executeUpdate();			
			preStatement.close();
			
			conn.commit();
		}
		catch(Exception ex){
			conn.rollback();
			log.error(ex.getMessage(),ex);
			throw ex;
		}
		finally{
			DatabaseHelper.close(null,preStatement);
		}			
	}

	public String getSelectLastHistorySql() {
		return selectLastHistorySql;
	}

	public void setSelectLastHistorySql(String selectLastHistorySql) {
		this.selectLastHistorySql = selectLastHistorySql;
	}

	public String getTransferVehicleRecordSql() {
		return transferVehicleRecordSql;
	}

	public void setTransferVehicleRecordSql(String transferVehicleRecordSql) {
		this.transferVehicleRecordSql = transferVehicleRecordSql;
	}

	public String getDeleteOvertimeHistorySql() {
		return deleteOvertimeHistorySql;
	}

	public void setDeleteOvertimeHistorySql(String deleteOvertimeHistorySql) {
		this.deleteOvertimeHistorySql = deleteOvertimeHistorySql;
	}

}
