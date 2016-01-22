/**
 * 
 */
package com.its.core.local.longchuan.task;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.local.ningxiang.task.ExportViolateRecordBean;
import com.its.core.module.task.ATask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.ImageHelper;
import com.its.core.util.PropertiesHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-9-19 上午07:37:16
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ExportViolateFileTask extends ATask {
	private static final Log log = LogFactory.getLog(ExportViolateFileTask.class);
	
	private String imageExportDir = null;
	private float imageQuality = -1;
	
	private String selectViolateRecordSql = null;
	private String updateViolateRecordSql = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.imageExportDir = props.getProperty(propertiesPrefix,no,"image_export_dir");
		this.imageQuality = PropertiesHelper.getFloat(propertiesPrefix,no,"image_quality",props,this.imageQuality);
		
		this.selectViolateRecordSql = props.getProperty(propertiesPrefix,no,"sql.select_violate_record_sql");
		this.updateViolateRecordSql = props.getProperty(propertiesPrefix,no,"sql.update_violate_record_sql");

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
			
			List<ExportViolateRecordBean> recordList = this.getUnexportRecordList(conn);
			int size = recordList.size();
			log.debug("共找到："+size+"条记录！");
			if(size==0) return;
			
			short currentExcelRow = 1;
			for(int i=0;i<size;i++){
				
				ExportViolateRecordBean record = (ExportViolateRecordBean)recordList.get(i);
				
				//生成三位毫秒随机数
				int rad = (int) (Math.random() * (999 - 100)) + 100;
				
				DeviceInfoBean dib = (DeviceInfoBean)DeviceInfoLoaderFactory.getInstance().getDeviceMap().get(record.getDeviceId());
				String deviceId = dib.getThirdPartyDeviceInfoBean().getDeviceId();
//				String filePath = this.getImageExportDir()+"/"+DateHelper.dateToString(record.getViolateTime(), "yyyyMMdd")+"/"+deviceId+"/";
				String filePath = this.getImageExportDir()+"/";
				String fileName = deviceId+DateHelper.dateToString(record.getViolateTime(), "yyyyMMddHHmmss")+rad;
				String snapId = DateHelper.dateToString(record.getViolateTime(), "yyyyMMddHHmmss")+rad;
				FileHelper.createDir(filePath);				
				
				int snapFrames = 2;
				if(record.getImagePath1().indexOf("X00") != -1 &&StringHelper.isNotEmpty(record.getImagePath1()) && StringHelper.isNotEmpty(record.getImagePath2()) && StringHelper.isNotEmpty(record.getImagePath3())){
					try{	
						snapFrames = 3;
						byte[] fileBytes1 = ImageHelper.getImageBytes(record.getImagePath1().substring(0,record.getImagePath1().lastIndexOf("/")+1) + URLEncoder.encode(record.getImagePath1().substring(record.getImagePath1().lastIndexOf("/")+1),"UTF-8"));	
						if(imageQuality>0) fileBytes1 = ImageHelper.compress(fileBytes1,imageQuality);
						FileHelper.writeFile(fileBytes1, filePath + fileName+"_1.JPG");
						byte[] fileBytes2 = ImageHelper.getImageBytes(record.getImagePath2().substring(0,record.getImagePath2().lastIndexOf("/")+1) + URLEncoder.encode(record.getImagePath2().substring(record.getImagePath2().lastIndexOf("/")+1),"UTF-8"));
						if(imageQuality>0) fileBytes2 = ImageHelper.compress(fileBytes2,imageQuality);
						FileHelper.writeFile(fileBytes2, filePath + fileName+"_2.JPG");
						byte[] fileBytes3 = ImageHelper.getImageBytes(record.getImagePath3().substring(0,record.getImagePath3().lastIndexOf("/")+1) + URLEncoder.encode(record.getImagePath3().substring(record.getImagePath3().lastIndexOf("/")+1),"UTF-8"));
						if(imageQuality>0) fileBytes3 = ImageHelper.compress(fileBytes3,imageQuality);
						FileHelper.writeFile(fileBytes3, filePath + fileName+"_3.JPG");						
						
					}catch(FileNotFoundException fnfe){
						log.error("未找到文件："+record.getImagePath1(),fnfe);
					}
				} else {
					try{	
						byte[] fileBytes1 = ImageHelper.getImageBytes(record.getImagePath1().substring(0,record.getImagePath1().lastIndexOf("/")+1) + URLEncoder.encode(record.getImagePath1().substring(record.getImagePath1().lastIndexOf("/")+1),"UTF-8"));	
						if(imageQuality>0) fileBytes1 = ImageHelper.compress(fileBytes1,imageQuality);
						FileHelper.writeFile(fileBytes1, filePath + fileName+"_1.JPG");
						byte[] fileBytes2 = ImageHelper.getImageBytes(record.getImagePath2().substring(0,record.getImagePath2().lastIndexOf("/")+1) + URLEncoder.encode(record.getImagePath2().substring(record.getImagePath2().lastIndexOf("/")+1),"UTF-8"));
						if(imageQuality>0) fileBytes2 = ImageHelper.compress(fileBytes2,imageQuality);
						FileHelper.writeFile(fileBytes2, filePath + fileName+"_2.JPG");						
					}catch(FileNotFoundException fnfe){
						log.error("未找到文件："+record.getImagePath1(),fnfe);
					}
				}
				
				//创建xml文件
				this.createDatFile(record, filePath + fileName + ".xml",snapId,snapFrames,deviceId);
				
				//更新导出状态
				this.updateExportRecordStatus(conn, record.getId());				
				conn.commit();
				currentExcelRow++;
			}
			log.info("本次共导出记录：" + (currentExcelRow-1) + "条！");
			
		}catch(Exception ex){
			log.error("导出失败" + ex.getMessage(),ex);		
			
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
//			log.debug("执行："+this.getSelectViolateRecordSql());
			preStatement = conn.prepareStatement(this.getSelectViolateRecordSql());
			rs = preStatement.executeQuery();
			
			while(rs.next()){
				ExportViolateRecordBean record = new ExportViolateRecordBean();	
				record.setId(rs.getLong("id"));
				record.setPlate(rs.getString("plate"));
				record.setPlateTypeId(rs.getString("plate_type_id"));	
				record.setViolateTime(rs.getTimestamp("violate_time"));		
//				record.setWfxwCode(rs.getString("wfxw_code"));
				record.setDeptCode(rs.getString("dept_code"));
				record.setRoadCode(rs.getString("road_code"));
				record.setRoadSegment(rs.getString("road_segment"));
				record.setRoadDistance(rs.getString("road_distance"));
				record.setRoadName(rs.getString("road_name"));
				record.setDeviceId(rs.getString("device_id"));
				record.setLine(rs.getString("line"));
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
	
	private void createDatFile(ExportViolateRecordBean record,String fileName,String snapId,int snapFrames,String deviceId) throws Exception{
		log.info("Return datRealName = " + fileName);
		FileWriter fileWriter = new FileWriter(fileName);
		String newLine = "\r\n";
		StringBuffer  xml = new StringBuffer("<?xml version=\"1.0\" encoding=\"GBK\"?>").append(newLine);
		xml.append("<OffenceSnap>").append(newLine);
//			xml.append("<device_uid>").append(snapId).append("</device_uid>").append(newLine);
			xml.append("<device_no>").append(deviceId).append("</device_no>").append(newLine);
			xml.append("<org_code>").append(record.getDeptCode()).append("</org_code>").append(newLine);
			xml.append("<device_type>1</device_type>").append(newLine);
			xml.append("<device_subtype></device_subtype>").append(newLine);
			xml.append("<max_car_speed>0</max_car_speed>").append(newLine);
			xml.append("<max_vehicle_speed>0</max_vehicle_speed>").append(newLine);
			xml.append("<max_motor_speed>0</max_motor_speed>").append(newLine);
			xml.append("<min_speed>0</min_speed>").append(newLine);
			
			xml.append("<snap_id>").append(snapId).append("</snap_id>").append(newLine);	
			xml.append("<snap_frames>").append(snapFrames).append("</snap_frames>").append(newLine);
			xml.append("<snap_memo></snap_memo>").append(newLine);			
			xml.append("<offence_time>").append(DateHelper.dateToString(record.getViolateTime(), "yyyy-MM-dd HH:mm:ss")).append("</offence_time>").append(newLine);
//			xml.append("<snap_type>6</snap_type>").append(newLine);
			xml.append("<road_code>").append(record.getRoadCode()).append("</road_code>").append(newLine);
			if(StringHelper.isNotEmpty(record.getRoadSegment())) {
				xml.append("<road_segment>").append(record.getRoadSegment()).append("</road_segment>").append(newLine);				
			} else {
				xml.append("<road_segment></road_segment>").append(newLine);				
			}
			
			if(StringHelper.isNotEmpty(record.getRoadDistance())) {
				xml.append("<road_distance>").append(record.getRoadDistance()).append("</road_distance>").append(newLine);				
			} else {
				xml.append("<road_distance>000</road_distance>").append(newLine);				
			}
			
			xml.append("<road_name>").append(record.getRoadName()).append("</road_name>").append(newLine);
			xml.append("<drive_lane>").append(Integer.parseInt(record.getLine())).append("</drive_lane>").append(newLine);
			xml.append("<drive_direction></drive_direction>").append(newLine);
			
			xml.append("<offence_code>1625</offence_code>").append(newLine);
			xml.append("<licence_plate>").append(record.getPlate()).append("</licence_plate>").append(newLine);
			xml.append("<plate_type>").append(record.getPlateTypeId()).append("</plate_type>").append(newLine);
//			xml.append("<plate_color_code></plate_color_code>").append(newLine);
			xml.append("<base_speed>0</base_speed>").append(newLine);
			xml.append("<obj_speed>0</obj_speed>").append(newLine);
			xml.append("<duty_police_no></duty_police_no>").append(newLine);
			xml.append("<duty_department></duty_department>").append(newLine);
			xml.append("<duty_department_code></duty_department_code>").append(newLine);
			
			xml.append("<average_speed></average_speed>").append(newLine);			
			xml.append("<upstream_device_no></upstream_device_no>").append(newLine);
			xml.append("<upstream_road_code></upstream_road_code>").append(newLine);
			xml.append("<upstream_obj_speed></upstream_obj_speed>").append(newLine);
			xml.append("<upstream_offence_time></upstream_offence_time>").append(newLine);
			xml.append("<upstream_snap_id></upstream_snap_id>").append(newLine);
			xml.append("<trip_distance></trip_distance>").append(newLine);
			xml.append("<red_start_time></red_start_time>").append(newLine);
		xml.append("</OffenceSnap>").append(newLine);
		
		fileWriter.write(xml.toString());
		fileWriter.close();			
	}
	
	private void updateExportRecordStatus(Connection conn,long id) throws Exception{
		PreparedStatement preStatement = null;
		try{
			log.debug("执行："+this.getUpdateViolateRecordSql());
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

	public float getImageQuality() {
		return imageQuality;
	}

	public void setImageQuality(float imageQuality) {
		this.imageQuality = imageQuality;
	}

	public String getImageExportDir() {
		return imageExportDir;
	}

	public void setImageExportDir(String imageExportDir) {
		this.imageExportDir = imageExportDir;
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

}
