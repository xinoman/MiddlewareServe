/**
 * 
 */
package com.its.core.local.suining.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.task.ATask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2015年4月23日 上午9:46:52
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ImportToViolateTempStatTask extends ATask {
	private static final Log log = LogFactory.getLog(ImportToViolateTempStatTask.class);
	
	private String selectViolateTempGdRecordSql = null;
	private String deleteViolateTempGdRecordSql = null;
	private String insertViolateTempRecordSql = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.selectViolateTempGdRecordSql = props.getProperty(propertiesPrefix,no,"sql.select_violate_temp_gd_record_sql");
		this.deleteViolateTempGdRecordSql = props.getProperty(propertiesPrefix,no,"sql.delete_violate_temp_gd_record_sql");
		this.insertViolateTempRecordSql = props.getProperty(propertiesPrefix,no,"sql.insert_violate_temp_ecord_sql");
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		Connection conn = null;
		try {		
			
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			log.debug("conn = "+conn);
			conn.setAutoCommit(false);
			
			List<ImportViolateRecordBean> recordList = this.getImportRecordList(conn);
			ImportViolateRecordBean firstBean = null;
			if(recordList != null && recordList.size()>2){
				firstBean = recordList.get(0);
				recordList.remove(0);
				boolean result = false;
				List<ImportViolateRecordBean> matchlist = new ArrayList<ImportViolateRecordBean>();
				for(int i=0;i<recordList.size();i++){					
					ImportViolateRecordBean record = (ImportViolateRecordBean)recordList.get(i);					
					log.info(record.getDeviceId()+":"+record.getViolateTime().getTime());
					
					if(firstBean.getDeviceId().equals(record.getDeviceId()) && (firstBean.getViolateTime().getTime()-record.getViolateTime().getTime()<=180000)) {
						matchlist.add(record);
					} 
					
					if(matchlist != null &&  matchlist.size()>=2){
						result = true;
					}
					
				}
				this.deleteImportRecord(conn, firstBean.getId());
				if(result) {					
					this.insertViolationRecord(conn, firstBean, matchlist);					
//					for(ImportViolateRecordBean deletebean:matchlist) {
					for(int i=0;i<2;i++){
						this.deleteImportRecord(conn, matchlist.get(i).getId());
					}
					log.info("同一设备匹配三分钟以内记录成功！");
				} else {
					log.info("同一设备匹配三分钟以内记录失败！");
				}
			} else {
				log.debug("记录数据少于三条无法进行匹配操作！");
				return;
			}
			conn.commit();
			
			
		}catch(Exception ex){
			log.error("同一设备匹配三分钟以内记录失败" + ex.getMessage(),ex);		
			
			try {
				conn.rollback();
			} catch (Exception e) {}
			
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
	
	public int insertViolationRecord(Connection conn,ImportViolateRecordBean recordBean,List<ImportViolateRecordBean> reocrdlist) {		
        int result = -1;
        PreparedStatement preStatement = null;
       	try{
       		preStatement = conn.prepareStatement(this.getInsertViolateTempRecordSql());
			preStatement.setLong(1, recordBean.getId());
			preStatement.setTimestamp(2, new Timestamp(recordBean.getViolateTime().getTime()));
			preStatement.setLong(3, recordBean.getRoadId());
			preStatement.setString(4, recordBean.getDeviceId());
			preStatement.setString(5, recordBean.getDirectionCode());
			preStatement.setString(6, recordBean.getLine());
			preStatement.setString(7, "000");
			preStatement.setString(8, "000");
			preStatement.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));
			preStatement.setString(10, "");		
			
			preStatement.setString(11, SystemConstant.getInstance().PLATE_TYPE_ID_ROADLOUSE);
			preStatement.setString(12, recordBean.getImagePath1());
			preStatement.setString(13, reocrdlist.get(0).getImagePath1());
			preStatement.setString(14, reocrdlist.get(1).getImagePath1());

			preStatement.execute();
			result = 0;       		
       	}
       	catch(Exception ex){
			log.error("数据入库失败：" + ex.getMessage(), ex);
			result = -1;		
       	}
       	finally{
			if(preStatement != null){
				try
				{
					preStatement.close();
				}
				catch(Exception ex) { }      
			}       		
       	}
        return result;
	}
	
	private List<ImportViolateRecordBean> getImportRecordList(Connection conn) throws Exception{
		List<ImportViolateRecordBean> recordList = new ArrayList<ImportViolateRecordBean>();
		PreparedStatement preStatement = null;
		ResultSet rs = null;
		try{		
//			log.debug("执行："+this.getSelectViolateTempGdRecordSql());
			preStatement = conn.prepareStatement(this.getSelectViolateTempGdRecordSql());
			rs = preStatement.executeQuery();
			
			while(rs.next()){
				ImportViolateRecordBean record = new ImportViolateRecordBean();	
				record.setId(rs.getLong("id"));
				record.setPlate(rs.getString("plate"));
				record.setPlateTypeId(rs.getString("plate_type_id"));	
				record.setViolateTime(rs.getTimestamp("violate_time"));		
				record.setWfxwCode(rs.getString("wfxw_code"));
				record.setRoadId(rs.getLong("road_id"));
				record.setDeviceId(rs.getString("device_id"));
				record.setDirectionCode(rs.getString("direction_code"));
				record.setLine(rs.getString("line"));				
				record.setImagePath1(rs.getString("image_path_1"));
				
				recordList.add(record);
			}
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}
		finally{
			DatabaseHelper.close(rs,preStatement);
		}		
		return recordList;
	}
	
	private void deleteImportRecord(Connection conn,long id) throws Exception{
		PreparedStatement preStatement = null;
		try{
//			log.debug("执行："+this.getDeleteViolateTempGdRecordSql());
			preStatement = conn.prepareStatement(this.getDeleteViolateTempGdRecordSql());
			preStatement.setLong(1, id);
			preStatement.executeUpdate();		
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}
		finally{
			DatabaseHelper.close(null,preStatement);
		}
	}

	public String getSelectViolateTempGdRecordSql() {
		return selectViolateTempGdRecordSql;
	}

	public void setSelectViolateTempGdRecordSql(String selectViolateTempGdRecordSql) {
		this.selectViolateTempGdRecordSql = selectViolateTempGdRecordSql;
	}

	public String getDeleteViolateTempGdRecordSql() {
		return deleteViolateTempGdRecordSql;
	}

	public void setDeleteViolateTempGdRecordSql(String deleteViolateTempGdRecordSql) {
		this.deleteViolateTempGdRecordSql = deleteViolateTempGdRecordSql;
	}

	public String getInsertViolateTempRecordSql() {
		return insertViolateTempRecordSql;
	}

	public void setInsertViolateTempRecordSql(String insertViolateTempRecordSql) {
		this.insertViolateTempRecordSql = insertViolateTempRecordSql;
	}	

}
