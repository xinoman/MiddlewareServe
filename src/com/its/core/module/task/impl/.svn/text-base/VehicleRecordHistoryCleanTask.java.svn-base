/**
 * 
 */
package com.its.core.module.task.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.task.AFixedHourTask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.PropertiesHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-11-8 下午05:20:17
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class VehicleRecordHistoryCleanTask extends AFixedHourTask {
	
	private static final Log log = LogFactory.getLog(VehicleRecordHistoryCleanTask.class);
	
	private int cleanCycle = 90;

	//选取超过指定时间的历史记录(FROM T_ITS_VEHICLE_RECORD_HISTORY)
	private String selectOvertimeHistorySql = null;
	
	//删除超过指定时间的历史数据（T_ITS_VEHICLE_RECORD_YYYYMMDD)
	private String deleteOvertimeHistoryDataSql = null;
	
	//删除超过指定时间的历史记录（T_ITS_VEHICLE_RECORD_HISTORY)
	private String deleteOvertimeHistorySql = null;	

	/* (non-Javadoc)
	 * @see com.its.core.module.task.AFixedHourTask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.cleanCycle = PropertiesHelper.getInt(propertiesPrefix, no, "clean_cycle", props, this.cleanCycle);
		this.selectOvertimeHistorySql = props.getProperty(propertiesPrefix,no,"sql.select_overtime_history");
		this.deleteOvertimeHistoryDataSql = props.getProperty(propertiesPrefix,no,"sql.delete_overtime_history_data");		
		this.deleteOvertimeHistorySql = props.getProperty(propertiesPrefix,no,"sql.delete_overtime_history");
	}



	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		Connection conn = null;
		try {
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			List<Map<Object,Object>> overtimeHistoryList = this.getOvertimeHistoryList(conn);
			int size = overtimeHistoryList.size();
			conn.setAutoCommit(true);
			for(int i=0;i<size;i++){
				Map<Object,Object> overtimeHistoryMap = (Map<Object,Object>)overtimeHistoryList.get(i);				
				Long id = (Long)overtimeHistoryMap.get("id");
				String tableName = (String)overtimeHistoryMap.get("tableName");					
				try{
					this.truncateTable(conn, tableName);
					this.dropTable(conn, tableName);
					this.deleteHistory(conn, id);
				}catch(Exception ex){
					log.error(ex);
				}
			}
		}
		catch(Exception ex){
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
	
	/**
	 * 获取超过指定时间的历史记录（FROM T_ITS_VEHICLE_RECORD_HISTORY）
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	private List<Map<Object,Object>> getOvertimeHistoryList(Connection conn) throws Exception{
		List<Map<Object,Object>> overtimeHistoryList = new ArrayList<Map<Object,Object>>();		
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;
		try{		
			log.debug("执行："+this.getSelectOvertimeHistorySql());
			preStatement = conn.prepareStatement(this.getSelectOvertimeHistorySql());
			preStatement.setInt(1, this.getCleanCycle());
			resultSet = preStatement.executeQuery();
			
			while(resultSet.next()){	
				Map<Object,Object> overtimeHistoryMap = new HashMap<Object,Object>();
				overtimeHistoryMap.put("id", Long.valueOf(resultSet.getLong("id")));
				overtimeHistoryMap.put("tableName", resultSet.getString("table_name"));
				overtimeHistoryList.add(overtimeHistoryMap);
			}
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}
		finally{
			DatabaseHelper.close(resultSet,preStatement);
		}		
		return overtimeHistoryList;			
	}
	
	/**
	 * 删除表之前先清理表数据，删除超过指定时间的历史数据（T_ITS_VEHICLE_RECORD_YYYYMMDD）
	 * @param conn
	 * @param tableName
	 * @throws Exception
	 */
	private void truncateTable(Connection conn,String tableName) throws Exception{
		PreparedStatement preStatement = null;
		try{		
			StringBuffer truncateSql = new StringBuffer("truncate table ");
			truncateSql.append(tableName);
			truncateSql.append(" drop storage");
			String dropSql = truncateSql.toString();
			log.debug("执行："+dropSql);
			preStatement = conn.prepareStatement(dropSql);
			preStatement.executeUpdate();
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}
		finally{
			DatabaseHelper.close(null,preStatement);
		}			
	}
	
	/**
	 * 直接清除表，删除超过指定时间的历史数据（T_ITS_VEHICLE_RECORD_YYYYMMDD）
	 * @param conn
	 * @param tableName
	 * @throws Exception
	 */
	private void dropTable(Connection conn,String tableName) throws Exception{
		PreparedStatement preStatement = null;
		try{					
			String dropSql = StringHelper.replace(this.getDeleteOvertimeHistoryDataSql(), "${TAB_NAME}", tableName);
			log.debug("执行："+dropSql);
			preStatement = conn.prepareStatement(dropSql);
			preStatement.executeUpdate();
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}
		finally{
			DatabaseHelper.close(null,preStatement);
		}			
	}
	
	/**
	 * 删除超过指定时间的历史记录（T_ITS_VEHICLE_RECORD_HISTORY）
	 * @param conn
	 * @param id
	 * @throws Exception
	 */
	private void deleteHistory(Connection conn,Long id) throws Exception{
		PreparedStatement preStatement = null;
		try{		
			log.debug("执行："+this.getDeleteOvertimeHistorySql());
			preStatement = conn.prepareStatement(this.getDeleteOvertimeHistorySql());
			preStatement.setLong(1, id.longValue());
			preStatement.executeUpdate();
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}
		finally{
			DatabaseHelper.close(null,preStatement);
		}			
	}	

	public int getCleanCycle() {
		return cleanCycle;
	}

	public void setCleanCycle(int cleanCycle) {
		this.cleanCycle = cleanCycle;
	}

	public String getSelectOvertimeHistorySql() {
		return selectOvertimeHistorySql;
	}

	public void setSelectOvertimeHistorySql(String selectOvertimeHistorySql) {
		this.selectOvertimeHistorySql = selectOvertimeHistorySql;
	}

	public String getDeleteOvertimeHistoryDataSql() {
		return deleteOvertimeHistoryDataSql;
	}

	public void setDeleteOvertimeHistoryDataSql(String deleteOvertimeHistoryDataSql) {
		this.deleteOvertimeHistoryDataSql = deleteOvertimeHistoryDataSql;
	}

	public String getDeleteOvertimeHistorySql() {
		return deleteOvertimeHistorySql;
	}

	public void setDeleteOvertimeHistorySql(String deleteOvertimeHistorySql) {
		this.deleteOvertimeHistorySql = deleteOvertimeHistorySql;
	}

}
