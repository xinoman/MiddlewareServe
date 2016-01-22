/**
 * 
 */
package com.its.core.local.dianbai.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.task.ATask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2014-11-5 下午12:16:58
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class MoveVehicleTask extends ATask {
	private static final Log log = LogFactory.getLog(MoveVehicleTask.class);
	
	private String selectVehicleRecordSql = null;
	private String insertVehicleRecordSql = null;
	private String deleteVehicleRecordSql = null;


	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		// TODO Auto-generated method stub
		this.selectVehicleRecordSql = props.getProperty(propertiesPrefix, no, "sql.select_vehicle_record");
		this.insertVehicleRecordSql = props.getProperty(propertiesPrefix, no, "sql.insert_vehicle_record");
		this.deleteVehicleRecordSql = props.getProperty(propertiesPrefix, no, "sql.delete_vehicle_record");
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		long startTime = System.currentTimeMillis();
		Connection conn = null;	
		try {
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(false);		
			
			List<VehicelRecordBean> recordList = this.getVehicleRecordList(conn);
			int size = recordList.size();
			log.debug("共检索到数据："+size+"条！");	
			if(size==0) return;	
			
			short currentExcelRow = 1;
			for(int i=0;i<size;i++){				
				VehicelRecordBean record = (VehicelRecordBean)recordList.get(i);
				this.insertVehicleRecord(conn, record);	
				this.deleteVehicleRecord(conn, record.getId());					
				conn.commit();
				currentExcelRow++;
			}
			
			long currentTime = System.currentTimeMillis();
			log.info("本次共转移记录：" + (currentExcelRow-1) + "条！ 耗时：" + ((currentTime- startTime) / 1000F) + "秒！");
			
		    }catch(Exception ex){
				log.error("转移失败："+ex.getMessage(),ex);				
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
	
	private void deleteVehicleRecord(Connection conn,long id) throws Exception{
		PreparedStatement preStatement = null;
		try{
//			log.debug("执行："+this.getDeleteVehicleRecordSql());
			preStatement = conn.prepareStatement(this.getDeleteVehicleRecordSql());
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
	
	private void insertVehicleRecord(Connection conn,VehicelRecordBean vehicelRecordBean) throws Exception{
		PreparedStatement preStatement = null;		
		
		try{			
			preStatement = conn.prepareStatement(this.getInsertVehicleRecordSql());
			preStatement.setLong(1,vehicelRecordBean.getId());
			preStatement.setString(2,vehicelRecordBean.getPlate());
			preStatement.setLong(3,vehicelRecordBean.getPlateColorCode());		
			preStatement.setTimestamp(4,new java.sql.Timestamp(vehicelRecordBean.getCatchTime().getTime()));
			preStatement.setLong(5, vehicelRecordBean.getRoadId());			
			
			preStatement.setString(6,vehicelRecordBean.getDeviceId());
			preStatement.setString(7,vehicelRecordBean.getDirectionCode());
			preStatement.setString(8,vehicelRecordBean.getDirectionDrive());
			
			preStatement.setString(9,vehicelRecordBean.getDrivewayNo());			
			preStatement.setLong(10,vehicelRecordBean.getSpeed());
			preStatement.setLong(11,vehicelRecordBean.getLimitSpeed());
			preStatement.setString(12,vehicelRecordBean.getAlarmTypeId());
			preStatement.setLong(13,vehicelRecordBean.getBlacklistTypeId());			
			preStatement.setString(14,vehicelRecordBean.getPanoramaImagePath());
			preStatement.setString(15,vehicelRecordBean.getFeatureImagePath());			
			preStatement.setTimestamp(16,new Timestamp(System.currentTimeMillis()));
			preStatement.setString(17,vehicelRecordBean.getStatus());	
			preStatement.executeUpdate();
								
		}
		catch(Exception ex){
			log.error("插入记录时出错：" + ex.getMessage(), ex);	
			throw ex;		
		}
		finally{				
			DatabaseHelper.close(null, preStatement);
		}	
	}	
		
	private List<VehicelRecordBean> getVehicleRecordList(Connection conn) throws Exception{
		List<VehicelRecordBean> recordList = new ArrayList<VehicelRecordBean>();
		PreparedStatement preStatement = null;
		ResultSet rs = null;
		try{		
			log.debug("执行："+this.getSelectVehicleRecordSql());
			preStatement = conn.prepareStatement(this.getSelectVehicleRecordSql());			
			rs = preStatement.executeQuery();
			
			while(rs.next()){
				VehicelRecordBean record = new VehicelRecordBean();	
				record.setId(rs.getLong("id"));
				record.setPlate(rs.getString("plate"));
				record.setPlateColorCode(rs.getLong("plate_color_code"));	
				record.setCatchTime(rs.getTimestamp("catch_time"));
				record.setRoadId(rs.getLong("road_id"));
				record.setDeviceId(rs.getString("device_id"));
				record.setDirectionCode(rs.getString("direction_code"));
				record.setDirectionDrive(rs.getString("direction_drive"));
				record.setDrivewayNo(rs.getString("driveway_no"));
				record.setAlarmTypeId(rs.getString("alarm_type_id"));
				record.setBlacklistTypeId(rs.getLong("blacklist_type_id"));
				record.setFeatureImagePath(rs.getString("feature_image_path"));
				record.setPanoramaImagePath(rs.getString("panorama_image_path"));				
				record.setSpeed(rs.getLong("speed"));
				record.setLimitSpeed(rs.getLong("limit_speed"));	
				record.setCreateTime(rs.getTimestamp("create_time"));
				record.setStatus(rs.getString("status"));	
				recordList.add(record);						
			}
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);	
			throw ex;
		}finally{
			DatabaseHelper.close(rs,preStatement);
		}		
		return recordList;
	}

	/**
	 * @return the selectVehicleRecordSql
	 */
	public String getSelectVehicleRecordSql() {
		return selectVehicleRecordSql;
	}

	/**
	 * @param selectVehicleRecordSql the selectVehicleRecordSql to set
	 */
	public void setSelectVehicleRecordSql(String selectVehicleRecordSql) {
		this.selectVehicleRecordSql = selectVehicleRecordSql;
	}

	/**
	 * @return the deleteVehicleRecordSql
	 */
	public String getDeleteVehicleRecordSql() {
		return deleteVehicleRecordSql;
	}

	/**
	 * @param deleteVehicleRecordSql the deleteVehicleRecordSql to set
	 */
	public void setDeleteVehicleRecordSql(String deleteVehicleRecordSql) {
		this.deleteVehicleRecordSql = deleteVehicleRecordSql;
	}

	/**
	 * @return the insertVehicleRecordSql
	 */
	public String getInsertVehicleRecordSql() {
		return insertVehicleRecordSql;
	}

	/**
	 * @param insertVehicleRecordSql the insertVehicleRecordSql to set
	 */
	public void setInsertVehicleRecordSql(String insertVehicleRecordSql) {
		this.insertVehicleRecordSql = insertVehicleRecordSql;
	}	

}
