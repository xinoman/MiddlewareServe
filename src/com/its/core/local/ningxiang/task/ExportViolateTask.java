/**
 * 
 */
package com.its.core.local.ningxiang.task;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.task.ATask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-9-18 下午08:24:24
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ExportViolateTask extends ATask {
	private static final Log log = LogFactory.getLog(ExportViolateTask.class);
	
	private String selectViolateRecordSql = null;
	private String updateViolateRecordSql = null;	
	private String insertFxcWfdataRecordSql = null;
	
	private String lrdwdm = null;
	private String lrmjdm = null;
	private String zqdwdm = null;
	private String zqmjdm = null;
	
	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.selectViolateRecordSql = props.getProperty(propertiesPrefix,no,"sql.select_violate_record_sql");
		this.updateViolateRecordSql = props.getProperty(propertiesPrefix,no,"sql.update_violate_record_sql");
		this.insertFxcWfdataRecordSql = props.getProperty(propertiesPrefix,no,"sql.insert_fxc_wfdata_record_sql");
		
		this.lrdwdm = props.getProperty(propertiesPrefix,no,"unitInfo.lrdwdm");
		this.lrmjdm = props.getProperty(propertiesPrefix,no,"unitInfo.lrmjdm");
		this.zqdwdm = props.getProperty(propertiesPrefix,no,"unitInfo.zqdwdm");
		this.zqmjdm = props.getProperty(propertiesPrefix,no,"unitInfo.zqmjdm");

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		
		Connection conn = null;
		
		try {
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(false);
			
			List<ExportViolateRecordBean> recordList = this.getUnexportRecordList(conn);
			int size = recordList.size();
			log.debug("共检索到违法数据："+size+"条！");	
			if(size==0) return;	
			
			for(int i=0;i<size;i++){
				ExportViolateRecordBean record = (ExportViolateRecordBean)recordList.get(i);
				
				this.insertFxcWfdataTable(conn, record);
				this.updateExportRecord(conn, record.getId());
				
				conn.commit();
			}
			
		}catch(Exception ex){			
			log.error("上传失败："+ex.getMessage(),ex);			
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
	
	private List<ExportViolateRecordBean> getUnexportRecordList(Connection conn) throws Exception{
		List<ExportViolateRecordBean> recordList = new ArrayList<ExportViolateRecordBean>();
		PreparedStatement preStatement = null;
		ResultSet rs = null;
		try{		
			log.debug("执行："+this.getSelectViolateRecordSql());
			preStatement = conn.prepareStatement(this.getSelectViolateRecordSql());
			rs = preStatement.executeQuery();
			
			while(rs.next()){
				ExportViolateRecordBean record = new ExportViolateRecordBean();	
				record.setId(rs.getLong("id"));
				record.setPlate(rs.getString("plate"));
				record.setPlateTypeId(rs.getString("plate_type_id"));	
				record.setViolateTime(rs.getTimestamp("violate_time"));		
				record.setWfxwCode(rs.getString("wfxw_code"));
				record.setRoadCode(rs.getString("road_code"));
				record.setRoadName(rs.getString("road_name"));
				record.setSpeed(rs.getString("speed"));
				record.setLimitSpeed(rs.getString("limit_speed"));
				record.setImagePath1(rs.getString("image_path_1"));
				record.setImagePath2(rs.getString("image_path_2"));
				record.setImagePath3(rs.getString("image_path_3"));
				
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
	
	protected void insertFxcWfdataTable(Connection conn,ExportViolateRecordBean record) throws Exception{
		
		PreparedStatement preStatement = null;		
		
		try {
//			log.debug("执行：" + this.getInsertFxcWfdataRecordSql());
			preStatement = conn.prepareStatement(this.getInsertFxcWfdataRecordSql());	
			
			preStatement.setLong(1, record.getId());
			
			if(StringHelper.isEmpty(record.getPlate())) {
				preStatement.setString(2, "");
			} else {
				preStatement.setString(2, record.getPlate());
			}
			preStatement.setString(3, record.getPlateTypeId());
			preStatement.setString(4, record.getWfxwCode());
			preStatement.setString(5, record.getRoadCode());
			preStatement.setString(6, record.getRoadName());
			preStatement.setTimestamp(7,new java.sql.Timestamp(record.getViolateTime().getTime()));
			preStatement.setString(8, record.getSpeed());
			preStatement.setString(9, record.getLimitSpeed());
			
			String imagePath1 = record.getImagePath1().substring(0,record.getImagePath1().lastIndexOf("/")+1) + URLEncoder.encode(record.getImagePath1().substring(record.getImagePath1().lastIndexOf("/")+1),"UTF-8");
			preStatement.setString(10, imagePath1);
			
			if(StringHelper.isNotEmpty(record.getImagePath2())) {
				String imagePath2 = record.getImagePath2().substring(0,record.getImagePath2().lastIndexOf("/")+1) + URLEncoder.encode(record.getImagePath2().substring(record.getImagePath2().lastIndexOf("/")+1),"UTF-8");
				
				preStatement.setString(11, imagePath2);
			} else {
				preStatement.setString(11, "");
			}
			
			if(StringHelper.isNotEmpty(record.getImagePath3())) {
				String imagePath3 = record.getImagePath3().substring(0,record.getImagePath3().lastIndexOf("/")+1) + URLEncoder.encode(record.getImagePath3().substring(record.getImagePath3().lastIndexOf("/")+1),"UTF-8");
				preStatement.setString(12, imagePath3);
			} else {
				preStatement.setString(12, "");
			}
			preStatement.setString(13, this.getLrdwdm());
			preStatement.setString(14, this.getLrmjdm());
			preStatement.setString(15, this.getZqdwdm());
			preStatement.setString(16, this.getZqmjdm());
			
			preStatement.setString(17, "");
			if(Integer.parseInt(record.getSpeed())>Integer.parseInt(record.getLimitSpeed())){
				preStatement.setString(18, "2");
			} else {
				preStatement.setString(18, "1");
			}
			
			preStatement.executeUpdate();											
			conn.commit();				
		} catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		} finally{				
			DatabaseHelper.close(null, preStatement);
		}
	}
	
	private void updateExportRecord(Connection conn,long id) throws Exception{
		PreparedStatement preStatement = null;
		try{
//			log.debug("执行："+this.getUpdateViolateRecordSql());
			preStatement = conn.prepareStatement(this.getUpdateViolateRecordSql());
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

	public String getSelectViolateRecordSql() {
		return selectViolateRecordSql;
	}

	public void setSelectViolateRecordSql(String selectViolateRecordSql) {
		this.selectViolateRecordSql = selectViolateRecordSql;
	}

	public String getUpdateViolateRecordSql() {
		return updateViolateRecordSql;
	}

	public void setUpdateViolateRecordSql(String updateViolateRecordSql) {
		this.updateViolateRecordSql = updateViolateRecordSql;
	}

	public String getInsertFxcWfdataRecordSql() {
		return insertFxcWfdataRecordSql;
	}

	public void setInsertFxcWfdataRecordSql(String insertFxcWfdataRecordSql) {
		this.insertFxcWfdataRecordSql = insertFxcWfdataRecordSql;
	}

	public String getLrdwdm() {
		return lrdwdm;
	}

	public void setLrdwdm(String lrdwdm) {
		this.lrdwdm = lrdwdm;
	}

	public String getLrmjdm() {
		return lrmjdm;
	}

	public void setLrmjdm(String lrmjdm) {
		this.lrmjdm = lrmjdm;
	}

	public String getZqdwdm() {
		return zqdwdm;
	}

	public void setZqdwdm(String zqdwdm) {
		this.zqdwdm = zqdwdm;
	}

	public String getZqmjdm() {
		return zqmjdm;
	}

	public void setZqmjdm(String zqmjdm) {
		this.zqmjdm = zqmjdm;
	}

}
