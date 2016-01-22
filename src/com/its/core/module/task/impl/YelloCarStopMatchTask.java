/**
 * 
 */
package com.its.core.module.task.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.sequence.SequenceFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.task.ATask;
import com.its.core.module.task.bean.YelloCarStopMatchBean;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2014-9-11 上午11:39:43
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class YelloCarStopMatchTask extends ATask {
	private static final Log log = LogFactory.getLog(YelloCarStopMatchTask.class);	
	private String selectYelloCarStopRecordSql = null;
	private String selectVehicleRecordSql = null;
	private String updateVehicleRecordSql = null;
	private String insertViolateTempRecordSql = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.selectYelloCarStopRecordSql = props.getProperty(propertiesPrefix, no, "sql.select_yellocar_stop_record");
		this.selectVehicleRecordSql = props.getProperty(propertiesPrefix, no, "sql.select_vehicle_record");
		this.updateVehicleRecordSql = props.getProperty(propertiesPrefix, no, "sql.update_vehicle_record");
		this.insertViolateTempRecordSql = props.getProperty(propertiesPrefix, no, "sql.insert_violate_temp_record");
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		long startTime = System.currentTimeMillis();
		Connection conn = null;		
		
		try {
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(false);		
			
			List<YelloCarStopMatchBean> recordList = this.getMatchRecordList(conn);
			int size = recordList.size();
			log.debug("共检索到数据："+size+"条！");	
			if(size==0) return;		
			
			short currentExcelRow = 1;
			List<YelloCarStopMatchBean> matchList = this.getMatchConfigRecordList(conn);
			for(int i=0;i<size;i++){			
				YelloCarStopMatchBean listRecord = (YelloCarStopMatchBean)recordList.get(i);
				for(int j=0;j<matchList.size();j++){
					YelloCarStopMatchBean matchRecord = (YelloCarStopMatchBean)recordList.get(j);
					//判断号牌颜色是否为黄色
					if(listRecord.getRoadId().equals(matchRecord.getRoadId()) && listRecord.getPlateColorCode().equals("1")) {
						//禁行路段黄牌车通行，直接导入T_ITS_VIOLATE_RECORD_TEMP违法临时表
						this.insertViolationTemp(conn, listRecord);
					}
				}
				this.updateExportStatus(conn, listRecord.getId());					
				conn.commit();
				currentExcelRow++;
			}
			long currentTime = System.currentTimeMillis();
			log.info("本次共匹配记录：" + (currentExcelRow-1) + "条！ 耗时：" + ((currentTime- startTime) / 1000F) + "秒！");
		} catch(Exception ex){			
			log.error("匹配失败："+ex.getMessage(),ex);				
		} finally{
			if(conn!=null){
				try{
					ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
				}
				catch(Exception ex2){}
			}			
		}

	}
	
	public void insertViolationTemp(Connection conn,YelloCarStopMatchBean listRecord) {        
        PreparedStatement preStatement = null;
       	try{
       		//id,violate_time,road_id,device_id,direction_code,line,speed,limit_speed,create_time,plate,plate_type_id,file_path_1,file_path_2,file_path_3,file_path_4,video_file_path
			preStatement = conn.prepareStatement(this.getInsertViolateTempRecordSql());
			preStatement.setLong(1, (long)SequenceFactory.getInstance().getViolateRecordTempSequence());
			preStatement.setTimestamp(2, new Timestamp(listRecord.getCatchTime().getTime()));
			preStatement.setString(3, listRecord.getRoadId());
			preStatement.setString(4, listRecord.getDeviceId());
			preStatement.setString(5, listRecord.getDirectionCode());
			preStatement.setString(6, listRecord.getDrivewayNo());
			preStatement.setString(7, listRecord.getSpeed());
			preStatement.setString(8, listRecord.getLimitSpeed());
			preStatement.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));
			preStatement.setString(10, listRecord.getPlate());
			
			String plateTypeId = SystemConstant.getInstance().getPlateTypeIdByColor(listRecord.getPlateColorCode());
			if(StringHelper.isEmpty(plateTypeId)){
				//缺省：小型汽车
				plateTypeId = SystemConstant.getInstance().PLATE_TYPE_ID_ROADLOUSE;
			}
			else{
				plateTypeId = plateTypeId.trim();
			}
			
			preStatement.setString(11, plateTypeId);
			
			preStatement.setString(12, listRecord.getFeatureImagePath());		
			if(StringHelper.isNotEmpty(listRecord.getPanoramaImagePath())) {
				preStatement.setString(12, listRecord.getPanoramaImagePath());
			} else {
				preStatement.setString(13, "");
			}

			preStatement.execute();
       	}
       	catch(Exception ex){
			log.error("数据入库失败：" + ex.getMessage(), ex);	
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
	}
	
	private List<YelloCarStopMatchBean> getMatchRecordList(Connection conn) throws Exception{
		List<YelloCarStopMatchBean> recordList = new ArrayList<YelloCarStopMatchBean>();
		PreparedStatement preStatement = null;
		ResultSet rs = null;
		try{		
//			log.debug("执行："+this.getSelectVehicleRecordSql());
			preStatement = conn.prepareStatement(this.getSelectVehicleRecordSql());			
			rs = preStatement.executeQuery();
			
			while(rs.next()){
				YelloCarStopMatchBean record = new YelloCarStopMatchBean();	
				record.setId(rs.getLong("id"));
				record.setRoadId(rs.getString("road_id"));
				record.setPlate(rs.getString("plate"));
				record.setPlateColorCode(rs.getString("plate_color_code"));	
				record.setCatchTime(rs.getTimestamp("catch_time"));
				record.setDeviceId(rs.getString("device_id"));
				record.setDirectionCode(rs.getString("direction_code"));
				record.setDrivewayNo(rs.getString("driveway_no"));
				record.setFeatureImagePath(rs.getString("feature_image_path"));
				record.setPanoramaImagePath(rs.getString("panorama_image_path"));				
				record.setSpeed(rs.getString("speed"));
				record.setLimitSpeed(rs.getString("limit_speed"));						
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
	
	private List<YelloCarStopMatchBean> getMatchConfigRecordList(Connection conn) throws Exception{
		List<YelloCarStopMatchBean> recordList = new ArrayList<YelloCarStopMatchBean>();
		PreparedStatement preStatement = null;
		ResultSet rs = null;
		try{		
//			log.debug("执行："+this.getSelectVehicleRecordSql());
			preStatement = conn.prepareStatement(this.getSelectYelloCarStopRecordSql());			
			rs = preStatement.executeQuery();
			
			while(rs.next()){
				YelloCarStopMatchBean record = new YelloCarStopMatchBean();	
				record.setId(rs.getLong("id"));
				record.setRoadId(rs.getString("road_id"));
				record.setDeviceId(rs.getString("device_id"));
				record.setDirectionCode(rs.getString("direction_code"));
				record.setDrivewayNo(rs.getString("driveway_no"));							
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
	
	private void updateExportStatus(Connection conn,long id) throws Exception{
		PreparedStatement preStatement = null;
		try{
			log.debug("执行："+this.getUpdateVehicleRecordSql());
			preStatement = conn.prepareStatement(this.getUpdateVehicleRecordSql());
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

	public String getSelectVehicleRecordSql() {
		return selectVehicleRecordSql;
	}

	public void setSelectVehicleRecordSql(String selectVehicleRecordSql) {
		this.selectVehicleRecordSql = selectVehicleRecordSql;
	}

	public String getUpdateVehicleRecordSql() {
		return updateVehicleRecordSql;
	}

	public void setUpdateVehicleRecordSql(String updateVehicleRecordSql) {
		this.updateVehicleRecordSql = updateVehicleRecordSql;
	}

	public String getSelectYelloCarStopRecordSql() {
		return selectYelloCarStopRecordSql;
	}

	public void setSelectYelloCarStopRecordSql(String selectYelloCarStopRecordSql) {
		this.selectYelloCarStopRecordSql = selectYelloCarStopRecordSql;
	}

	public String getInsertViolateTempRecordSql() {
		return insertViolateTempRecordSql;
	}

	public void setInsertViolateTempRecordSql(String insertViolateTempRecordSql) {
		this.insertViolateTempRecordSql = insertViolateTempRecordSql;
	}	

}
