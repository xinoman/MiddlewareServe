/**
 * 
 */
package com.its.core.module.task.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.sequence.SequenceFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.task.AFixedHourTask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.PropertiesHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-11-7 下午02:02:52
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class VechileRecordHistoryBackupTask extends AFixedHourTask {
	private static final Log log = LogFactory.getLog(VechileRecordHistoryBackupTask.class);
	
	private int backupCycle = 7;
	private int cacheDay = 1;
	private String selectLastHistory = null;
	private String selectFirstVehicleRecord = null;
	private String insertVehicleRecordHistory = null;
	
	private String checkHistoryTableExist = null;
	private String createHistoryTable = null;
	private String createHistoryIndex = null;
	
	private String selectOvertimeVehicleRecord = null;
	private String transferVehicleRecord = null;
	private String deleteVehicleRecord = null;

	/* (non-Javadoc)
	 * @see com.swy.tiip.tools.module.task.ATask#configureSpecificallyProperties(com.swy.tiip.tools.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		String dbType = SystemConstant.getInstance().getCurrentDbType();
		this.backupCycle = PropertiesHelper.getInt(propertiesPrefix, no, "backup_cycle", props, this.backupCycle);
		this.cacheDay = PropertiesHelper.getInt(propertiesPrefix, no, "cache_day", props, this.cacheDay);
		this.selectLastHistory = props.getProperty(propertiesPrefix,no,"sql."+dbType+".select_last_history");
		this.selectFirstVehicleRecord = props.getProperty(propertiesPrefix,no,"sql."+dbType+".select_first_vehicle_record");
		this.insertVehicleRecordHistory = props.getProperty(propertiesPrefix,no,"sql."+dbType+".insert_vehicle_record_history");
		this.checkHistoryTableExist = props.getProperty(propertiesPrefix,no,"sql."+dbType+".create_history.check_exist");
		this.createHistoryTable = props.getProperty(propertiesPrefix,no,"sql."+dbType+".create_history.table");
		this.createHistoryIndex = props.getProperty(propertiesPrefix,no,"sql."+dbType+".create_history.index");		
		
		this.selectOvertimeVehicleRecord = props.getProperty(propertiesPrefix,no,"sql."+dbType+".transfer.select_overtime_vehicle_record");
		this.transferVehicleRecord = props.getProperty(propertiesPrefix,no,"sql."+dbType+".transfer.transfer_vehicle_record");
		this.deleteVehicleRecord = props.getProperty(propertiesPrefix,no,"sql."+dbType+".transfer.delete_vehicle_record");		
	}

	/* (non-Javadoc)
	 * @see com.swy.tiip.tools.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		Connection conn = null;
		
		try {
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(false);
			List<Map<Object,Object>> lastHistoryList = this.getLastHistoryList(conn);			
			int size = lastHistoryList.size();
			Map<Object, Object> lastHistoryMap = null;
			
			if(size==0){
				Map<Object, Object> firstVehicleRecord = this.getFirstVehicleRecordMap(conn);
				if(firstVehicleRecord.size()==0) return;
				Timestamp catchTime = (Timestamp)firstVehicleRecord.get("catchTime");
				
				//最旧的记录超过了当前的备份周期，则需要备份数据
				if((System.currentTimeMillis()-catchTime.getTime())> this.getBackupCycle()*24*3600000L){
					lastHistoryMap = this.createtVehicleRecordHistory(conn, new Date(catchTime.getTime()));	
				}		
				else{
					log.debug("记录暂未超过备份周期："+this.getBackupCycle()+"天,不需要备份！");
					return;
				}
				
			}else {
				lastHistoryMap = (Map<Object, Object>)lastHistoryList.get(0);
			}
			
			//创建表与索引
			if(lastHistoryMap!=null && lastHistoryMap.size()>0){
				Timestamp endTime = (Timestamp)lastHistoryMap.get("endTime");
				Timestamp startTime = (Timestamp)lastHistoryMap.get("startTime");
				String tableName = (String)lastHistoryMap.get("tableName");
				
				//间隔周期
				float intervalCycle = (System.currentTimeMillis()-endTime.getTime())/(this.getBackupCycle()*24*3600000f);
				while(intervalCycle>1){					
					lastHistoryMap = this.createtVehicleRecordHistory(conn, new Date(endTime.getTime()+24*3600000L));
					endTime = (Timestamp)lastHistoryMap.get("endTime");
					startTime = (Timestamp)lastHistoryMap.get("startTime");
					tableName = (String)lastHistoryMap.get("tableName");
					intervalCycle = (System.currentTimeMillis()-endTime.getTime())/(this.getBackupCycle()*24*3600000f);
				}
				
				//实时车辆信息（T_ITS_VEHICLE_RECORD）默认只保存设定日期天数的数据
				if(intervalCycle < 1 && DateHelper.getDaysBetween(DateHelper.dateToString(endTime.getTime(), "yyyy-MM-dd"), DateHelper.dateToString(System.currentTimeMillis(), "yyyy-MM-dd")) > this.getCacheDay()) {
					
					//判断结束时间与开始时间是否在备份周期内，如果是进行表结束时间更新，否则创建新表。
					if(DateHelper.getDaysBetween(DateHelper.dateToString(startTime.getTime(), "yyyy-MM-dd"), DateHelper.dateToString(endTime.getTime()+24*3600000L, "yyyy-MM-dd")) < this.getBackupCycle()) {
						lastHistoryMap = this.updateVehicleRecordHistory(conn, tableName, startTime);						
					}else {						
						lastHistoryMap = this.createtVehicleRecordHistoryOther(conn, new Date(endTime.getTime()+24*3600000L));
					}
				}
			}
			
			for(int i=0;i<size;i++){
				if(this.getOvertimeVehicleRecordCount(conn)==0) break;
				lastHistoryMap = (Map<Object, Object>)lastHistoryList.get(i);					
				String tableName = (String)lastHistoryMap.get("tableName");	
				Timestamp startTime = (Timestamp)lastHistoryMap.get("startTime");
				Timestamp endTime = (Timestamp)lastHistoryMap.get("endTime");					
				this.traferVehicleRecordToHistory(conn, tableName, startTime, endTime);				
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
	
	private Map<Object,Object> updateVehicleRecordHistory(Connection conn,String tableName,Timestamp startTime) throws Exception{
		log.info("更新表：" + tableName);
		Map<Object,Object> lastHistoryMap = new HashMap<Object,Object>();		
		PreparedStatement preStatement = null;
		try{		
			conn.setAutoCommit(true);
			Timestamp endTimestamp = new Timestamp(DateHelper.parseDateString(DateHelper.dateToString(System.currentTimeMillis()-24*3600000L, "yyyy-MM-dd"+ " 23:59:59"), "yyyy-MM-dd HH:mm:ss").getTime());
			preStatement = conn.prepareStatement("update T_ITS_VEHICLE_RECORD_HISTORY set end_time = ? , create_time=sysdate where table_name = ?");
			preStatement.setTimestamp(1,endTimestamp);							
			preStatement.setString(2,tableName);
			preStatement.executeUpdate();	
			
			lastHistoryMap.put("tableName", tableName);
			lastHistoryMap.put("startTime", startTime);
			lastHistoryMap.put("endTime", endTimestamp);
		}catch(Exception ex){			
			log.error(ex);
			throw ex;
		}finally{
			DatabaseHelper.close(null,preStatement);		
		}
		return lastHistoryMap;
	}
	
	/**
	 * 获取最新的T_ITS_VEHICLE_RECORD_HISTORY表记录
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<Map<Object,Object>> getLastHistoryList(Connection conn) throws Exception{
		List<Map<Object,Object>> lastHistoryList = new ArrayList<Map<Object,Object>>();		
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;
		try{		
			log.debug("执行："+this.getSelectLastHistory());
			preStatement = conn.prepareStatement(this.getSelectLastHistory());
			resultSet = preStatement.executeQuery();
			
			while(resultSet.next()){	
				Map<Object,Object> lastHistoryMap = new HashMap<Object,Object>();				
				lastHistoryMap.put("tableName", resultSet.getString("table_name"));
				lastHistoryMap.put("startTime", resultSet.getTimestamp("start_time"));
				lastHistoryMap.put("endTime", resultSet.getTimestamp("end_time"));
				lastHistoryList.add(lastHistoryMap);
			}
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}
		finally{
			DatabaseHelper.close(resultSet,preStatement);
		}		
		return lastHistoryList;		
	}
	
	/**
	 * 获取最旧的T_ITS_VEHICLE_RECORD记录
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public Map<Object,Object> getFirstVehicleRecordMap(Connection conn) throws Exception{
		Map<Object,Object> lastHistoryMap = new HashMap<Object,Object>();
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;
		try{		
			log.debug("执行："+this.getSelectFirstVehicleRecord());
			preStatement = conn.prepareStatement(this.getSelectFirstVehicleRecord());
			resultSet = preStatement.executeQuery();
			
			if(resultSet.next()){				
				lastHistoryMap.put("catchTime", resultSet.getTimestamp("catch_time"));
			}
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}
		finally{
			DatabaseHelper.close(resultSet,preStatement);
		}		
		return lastHistoryMap;		
	}
	
	private void createtVehicleRecordTableAndIndex(Connection conn,Date startTime) throws Exception{
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;
		try{		
			String yearMonthDay = DateHelper.dateToString(startTime, "yyyyMMdd");
			
			//检查表是否已经存在
			String checkHistoryTableExistSql = StringHelper.replace(this.getCheckHistoryTableExist(), "${YYYYMMDD}", yearMonthDay);
			log.debug("执行："+checkHistoryTableExistSql);
			preStatement = conn.prepareStatement(checkHistoryTableExistSql);
			resultSet = preStatement.executeQuery();
			boolean exist = false;
			if(resultSet.next()){				
				exist = true;
			}			
			DatabaseHelper.close(resultSet,preStatement);
			
			if(!exist){
				//创建表
				String createTableSql = StringHelper.replace(this.getCreateHistoryTable(), "${YYYYMMDD}", yearMonthDay);
				log.debug(createTableSql);
				preStatement = conn.prepareStatement(createTableSql);
				preStatement.executeUpdate();
				DatabaseHelper.close(null,preStatement);
				
				//创建索引
				String[] createIndexSqlArr = StringHelper.replace(this.getCreateHistoryIndex(), "${YYYYMMDD}", yearMonthDay).split("[;]");
				int len = createIndexSqlArr.length;
				for(int i=0;i<len;i++){
					if(StringHelper.isEmpty(createIndexSqlArr[i])) continue;
					log.debug(createIndexSqlArr[i]);
					preStatement = conn.prepareStatement(createIndexSqlArr[i]);
					preStatement.executeUpdate();
					DatabaseHelper.close(null,preStatement);			
				}
			}
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}
		finally{
			DatabaseHelper.close(resultSet,preStatement);
		}
	}
	
	private Map<Object,Object> createtVehicleRecordHistory(Connection conn,Date startTime) throws Exception{
		Map<Object,Object> lastHistoryMap = new HashMap<Object,Object>();
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;
		try{
			//创建表与索引
			this.createtVehicleRecordTableAndIndex(conn, startTime);
			
			String yearMonthDay = DateHelper.dateToString(startTime, "yyyyMMdd");
			String tableName = "T_ITS_VEHICLE_RECORD_"+yearMonthDay;			
			//insert into T_ITS_VEHICLE_RECORD_HISTORY (ID, TABLE_NAME, START_TIME, END_TIME, RECORD_COUNT, CREATE_TIME) values (?,?,?,?,?,?)
			log.debug("执行："+this.getInsertVehicleRecordHistory());
			conn.setAutoCommit(true);
			long id = (long)SequenceFactory.getInstance().getVehicleRecordSequence();
			preStatement = conn.prepareStatement(this.getInsertVehicleRecordHistory());
			preStatement.setLong(1,id);
			preStatement.setString(2, tableName);
			Timestamp startTimestamp = new Timestamp(DateHelper.parseDateString(DateHelper.dateToString(startTime, "yyyy-MM-dd"), "yyyy-MM-dd").getTime());
			Timestamp endTimestamp = new Timestamp(DateHelper.parseDateString(DateHelper.dateToString(startTime, "yyyy-MM-dd")+" 23:59:59", "yyyy-MM-dd HH:mm:ss").getTime()+(this.getBackupCycle()-1)*24*3600000L);
			preStatement.setTimestamp(3, startTimestamp);
			preStatement.setTimestamp(4, endTimestamp);
			preStatement.setLong(5, 0);
			preStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			preStatement.executeUpdate();
			
			//lastHistoryMap.put("id", id);
			lastHistoryMap.put("tableName", tableName);
			lastHistoryMap.put("startTime", startTimestamp);
			lastHistoryMap.put("endTime", endTimestamp);			
			
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}
		finally{
			DatabaseHelper.close(resultSet,preStatement);
		}			
		return lastHistoryMap;
	}
	
	private Map<Object,Object> createtVehicleRecordHistoryOther(Connection conn,Date startTime) throws Exception{
		Map<Object,Object> lastHistoryMap = new HashMap<Object,Object>();
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;
		try{
			//创建表与索引
			this.createtVehicleRecordTableAndIndex(conn, startTime);
			
			String yearMonthDay = DateHelper.dateToString(startTime, "yyyyMMdd");
			String tableName = "T_ITS_VEHICLE_RECORD_"+yearMonthDay;			
			//insert into T_ITS_VEHICLE_RECORD_HISTORY (ID, TABLE_NAME, START_TIME, END_TIME, RECORD_COUNT, CREATE_TIME) values (?,?,?,?,?,?)
			log.debug("执行："+this.getInsertVehicleRecordHistory());
			conn.setAutoCommit(true);
			long id = (long)SequenceFactory.getInstance().getVehicleRecordSequence();
			preStatement = conn.prepareStatement(this.getInsertVehicleRecordHistory());
			preStatement.setLong(1,id);
			preStatement.setString(2, tableName);
			
			Timestamp startTimestamp = new Timestamp(DateHelper.parseDateString(DateHelper.dateToString(startTime, "yyyy-MM-dd"), "yyyy-MM-dd").getTime());
			Timestamp endTimestamp = new Timestamp(DateHelper.parseDateString(DateHelper.dateToString(System.currentTimeMillis()-24*3600000L, "yyyy-MM-dd"+ " 23:59:59"), "yyyy-MM-dd HH:mm:ss").getTime());

			preStatement.setTimestamp(3, startTimestamp);
			preStatement.setTimestamp(4, endTimestamp);
			preStatement.setLong(5, 0);
			preStatement.setTimestamp(6, new Timestamp(System.currentTimeMillis()));
			preStatement.executeUpdate();
			
			//lastHistoryMap.put("id", id);
			lastHistoryMap.put("tableName", tableName);
			lastHistoryMap.put("startTime", startTimestamp);
			lastHistoryMap.put("endTime", endTimestamp);			
			
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}
		finally{
			DatabaseHelper.close(resultSet,preStatement);
		}			
		return lastHistoryMap;
	}
	
	private long getOvertimeVehicleRecordCount(Connection conn) throws Exception{
		long recordCount = 0;
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;
		try{
			log.debug(this.getSelectOvertimeVehicleRecord());
			preStatement = conn.prepareStatement(this.getSelectOvertimeVehicleRecord());				
			preStatement.setInt(1,this.getCacheDay());
			resultSet = preStatement.executeQuery();
			if(resultSet.next()){
				recordCount = resultSet.getLong("recordCount");
			}
		}
		catch(Exception ex){
			conn.rollback();
			log.error(ex.getMessage(),ex);
			throw ex;
		}
		finally{
			DatabaseHelper.close(resultSet,preStatement);			
		}
		return recordCount;
	}
	
	private void traferVehicleRecordToHistory(Connection conn,String tableName,Timestamp startTime,Timestamp endTime) throws Exception{
		PreparedStatement preStatement = null;
		try{
			//创建表与索引
			this.createtVehicleRecordTableAndIndex(conn, new Date(startTime.getTime()));
			
			log.debug("Start Time:"+DateHelper.dateToString(new Date(startTime.getTime()), "yyyy-MM-dd HH:mm:ss"));
			log.debug("End Time:"+DateHelper.dateToString(new Date(endTime.getTime()), "yyyy-MM-dd HH:mm:ss"));
			conn.setAutoCommit(false);			
			String transferSql = StringHelper.replace(this.getTransferVehicleRecord(), "${tableName}", tableName);
			log.debug(transferSql);
			
			//转记录
			preStatement = conn.prepareStatement(transferSql);				
			preStatement.setTimestamp(1, startTime);
			preStatement.setTimestamp(2, endTime);
			preStatement.executeUpdate();
			preStatement.close();			
			
			
			//删除原数据（T_ITS_VEHICLE_RECORD）
			log.debug(this.getDeleteVehicleRecord());
			preStatement = conn.prepareStatement(this.getDeleteVehicleRecord());						
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

	/**
	 * @return the backupCycle
	 */
	public int getBackupCycle() {
		return backupCycle;
	}

	/**
	 * @param backupCycle the backupCycle to set
	 */
	public void setBackupCycle(int backupCycle) {
		this.backupCycle = backupCycle;
	}	

	/**
	 * @return the cacheDay
	 */
	public int getCacheDay() {
		return cacheDay;
	}

	/**
	 * @param cacheDay the cacheDay to set
	 */
	public void setCacheDay(int cacheDay) {
		this.cacheDay = cacheDay;
	}

	/**
	 * @return the selectLastHistory
	 */
	public String getSelectLastHistory() {
		return selectLastHistory;
	}

	/**
	 * @param selectLastHistory the selectLastHistory to set
	 */
	public void setSelectLastHistory(String selectLastHistory) {
		this.selectLastHistory = selectLastHistory;
	}

	/**
	 * @return the selectFirstVehicleRecord
	 */
	public String getSelectFirstVehicleRecord() {
		return selectFirstVehicleRecord;
	}

	/**
	 * @param selectFirstVehicleRecord the selectFirstVehicleRecord to set
	 */
	public void setSelectFirstVehicleRecord(String selectFirstVehicleRecord) {
		this.selectFirstVehicleRecord = selectFirstVehicleRecord;
	}

	/**
	 * @return the insertVehicleRecordHistory
	 */
	public String getInsertVehicleRecordHistory() {
		return insertVehicleRecordHistory;
	}

	/**
	 * @param insertVehicleRecordHistory the insertVehicleRecordHistory to set
	 */
	public void setInsertVehicleRecordHistory(String insertVehicleRecordHistory) {
		this.insertVehicleRecordHistory = insertVehicleRecordHistory;
	}

	/**
	 * @return the checkHistoryTableExist
	 */
	public String getCheckHistoryTableExist() {
		return checkHistoryTableExist;
	}

	/**
	 * @param checkHistoryTableExist the checkHistoryTableExist to set
	 */
	public void setCheckHistoryTableExist(String checkHistoryTableExist) {
		this.checkHistoryTableExist = checkHistoryTableExist;
	}

	/**
	 * @return the createHistoryTable
	 */
	public String getCreateHistoryTable() {
		return createHistoryTable;
	}

	/**
	 * @param createHistoryTable the createHistoryTable to set
	 */
	public void setCreateHistoryTable(String createHistoryTable) {
		this.createHistoryTable = createHistoryTable;
	}

	/**
	 * @return the createHistoryIndex
	 */
	public String getCreateHistoryIndex() {
		return createHistoryIndex;
	}

	/**
	 * @param createHistoryIndex the createHistoryIndex to set
	 */
	public void setCreateHistoryIndex(String createHistoryIndex) {
		this.createHistoryIndex = createHistoryIndex;
	}

	/**
	 * @return the selectOvertimeVehicleRecord
	 */
	public String getSelectOvertimeVehicleRecord() {
		return selectOvertimeVehicleRecord;
	}

	/**
	 * @param selectOvertimeVehicleRecord the selectOvertimeVehicleRecord to set
	 */
	public void setSelectOvertimeVehicleRecord(String selectOvertimeVehicleRecord) {
		this.selectOvertimeVehicleRecord = selectOvertimeVehicleRecord;
	}

	/**
	 * @return the transferVehicleRecord
	 */
	public String getTransferVehicleRecord() {
		return transferVehicleRecord;
	}

	/**
	 * @param transferVehicleRecord the transferVehicleRecord to set
	 */
	public void setTransferVehicleRecord(String transferVehicleRecord) {
		this.transferVehicleRecord = transferVehicleRecord;
	}

	/**
	 * @return the deleteVehicleRecord
	 */
	public String getDeleteVehicleRecord() {
		return deleteVehicleRecord;
	}

	/**
	 * @param deleteVehicleRecord the deleteVehicleRecord to set
	 */
	public void setDeleteVehicleRecord(String deleteVehicleRecord) {
		this.deleteVehicleRecord = deleteVehicleRecord;
	}

}
