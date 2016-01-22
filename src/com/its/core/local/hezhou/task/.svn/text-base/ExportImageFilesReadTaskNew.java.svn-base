/**
 * 
 */
package com.its.core.local.hezhou.task;

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
import com.its.core.module.task.ATask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.ImageHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-10-18 上午09:55:12
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ExportImageFilesReadTaskNew extends ATask {
	private static final Log log = LogFactory.getLog(ExportImageFilesReadTaskNew.class);
	
	private String imgSaveDir = null;
	private String httpPrefix = null;
	
	private String selecImageFileRecordSql = null;
	private String updateImageFileRecordSql = null;
	
	public static Map<String,String> DEVICE_MAP = new HashMap<String,String>();
	public static Map<String,String> DIRECTION_MAP = new HashMap<String,String>();
	public static Map<String,String> COLOR_MAP = new HashMap<String,String>();
	
	static{
		DEVICE_MAP.put("192.168.5.241", "451100100014");
		DEVICE_MAP.put("192.168.5.242", "451100100014");
		DEVICE_MAP.put("192.168.5.243", "451100100014");
		DEVICE_MAP.put("192.168.5.244", "451100100014");	
		DEVICE_MAP.put("192.168.5.245", "451100100004");
		DEVICE_MAP.put("192.168.5.246", "451100100004");
		DEVICE_MAP.put("192.168.5.247", "451100100016");
		DEVICE_MAP.put("192.168.5.248", "451100100016");
		DEVICE_MAP.put("192.168.5.249", "451100100017");
		DEVICE_MAP.put("192.168.5.240", "451100100017");	
		
		DIRECTION_MAP.put("东向西", "1");
		DIRECTION_MAP.put("西向东", "2");
		DIRECTION_MAP.put("南向北", "3");
		DIRECTION_MAP.put("北向南", "4");
		
		COLOR_MAP.put("白", "0");
		COLOR_MAP.put("黄", "1");
		COLOR_MAP.put("蓝", "2");
		COLOR_MAP.put("黑", "3");
		COLOR_MAP.put("无", "4");
		COLOR_MAP.put("绿", "4");
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.imgSaveDir = props.getProperty(propertiesPrefix,no,"img_save_dir");
		this.httpPrefix = props.getProperty(propertiesPrefix,no,"http_prefix");		
		
		this.selecImageFileRecordSql = props.getProperty(propertiesPrefix, no, "sql.select_image_file_record");
		this.updateImageFileRecordSql = props.getProperty(propertiesPrefix, no, "sql.update_image_file_record");
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
			
			List<VehicelRecordBean> recordList = this.getExportRecordList(conn);
			int size = recordList.size();
			log.debug("共检索到数据："+size+"条！");	
			if(size==0) return;	
			
			short currentExcelRow = 1;
			for(int i=0;i<size;i++){
				VehicelRecordBean record = (VehicelRecordBean)recordList.get(i);
				
				//删除数据
				this.updateRecordStatus(conn, record.getId());
				
				String filePath = this.getImgSaveDir()+"/"+DEVICE_MAP.get(record.getDeviceIp())+"/"+DateHelper.dateToString(record.getCatchTime(), "yyyyMMdd")+"/"+ DateHelper.dateToString(record.getCatchTime(), "HH")+"/";
				FileHelper.createDir(filePath);
				
				StringBuffer fileNameBuffer = new StringBuffer("X03");
									
					fileNameBuffer.append("R").append(DEVICE_MAP.get(record.getDeviceIp()))
					.append("D").append(DIRECTION_MAP.get(record.getDirectionCode()))
					.append("L").append(record.getDrivewayNo())
					.append("I").append("000")
					.append("V").append("000")
					.append("N").append((int)(Math.random() * (99999 - 10000)) + 10000)
					.append("T").append(DateHelper.dateToString(record.getCatchTime(),"yyyyMMddHHmmssSSS"));
				if(StringHelper.isNotEmpty(record.getPlate()) && !record.getPlate().equals("无车牌")) {
					fileNameBuffer.append("&").append(record.getPlate())
					.append("&").append(COLOR_MAP.get(record.getPlateColorCode()));
				}
				fileNameBuffer.append("S11.JPG");
				
				
				String fileName = fileNameBuffer.toString();
				
				filePath += fileName;
				log.info("图片路径为：" + this.getHttpPrefix()+record.getFeatureImagePath());
				byte[] imageByte = ImageHelper.getImageBytes(this.getHttpPrefix()+record.getFeatureImagePath());
				log.info("导出图片路径："+filePath);
				FileHelper.writeFile(imageByte, filePath);
				
				conn.commit();
				currentExcelRow++;
			}
			long currentTime = System.currentTimeMillis();
			log.info("本次共导出图片：" + (currentExcelRow-1) + "张 耗时：" + ((currentTime- startTime) / 1000F) + "秒！");
		}catch(Exception ex){			
			log.error("导出失败："+ex.getMessage(),ex);	
			try {
				conn.commit();
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
	
	private List<VehicelRecordBean> getExportRecordList(Connection conn) throws Exception{
		List<VehicelRecordBean> recordList = new ArrayList<VehicelRecordBean>();
		PreparedStatement preStatement = null;
		ResultSet rs = null;
		try{		
//			log.debug("执行："+this.getSelecImageFileRecordSql());
			preStatement = conn.prepareStatement(this.getSelecImageFileRecordSql());
			rs = preStatement.executeQuery();
			
			while(rs.next()){
				VehicelRecordBean record = new VehicelRecordBean();	
				record.setId(rs.getLong("ID"));				
				record.setDeviceIp(rs.getString("DeviceIP"));
				record.setPlate(rs.getString("LicenseNumber"));
				record.setPlateColorCode(rs.getString("LicenseColor"));
				record.setDirectionCode(rs.getString("Direction"));
				record.setCatchTime(rs.getTimestamp("CatchDate"));
				record.setDrivewayNo(rs.getString("RoadId"));
				record.setSpeed(rs.getString("Speed"));
				record.setFeatureImagePath(rs.getString("ImagePath"));				
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
	
	private void updateRecordStatus(Connection conn,long id) throws Exception{
		PreparedStatement preStatement = null;
		try{
//			log.debug(this.getUpdateImageFileRecordSql());
			preStatement = conn.prepareStatement(this.getUpdateImageFileRecordSql());
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

	/**
	 * @return the httpPrefix
	 */
	public String getHttpPrefix() {
		return httpPrefix;
	}

	/**
	 * @param httpPrefix the httpPrefix to set
	 */
	public void setHttpPrefix(String httpPrefix) {
		this.httpPrefix = httpPrefix;
	}

	/**
	 * @return the imgSaveDir
	 */
	public String getImgSaveDir() {
		return imgSaveDir;
	}

	/**
	 * @param imgSaveDir the imgSaveDir to set
	 */
	public void setImgSaveDir(String imgSaveDir) {
		this.imgSaveDir = imgSaveDir;
	}

	/**
	 * @return the selecImageFileRecordSql
	 */
	public String getSelecImageFileRecordSql() {
		return selecImageFileRecordSql;
	}

	/**
	 * @param selecImageFileRecordSql the selecImageFileRecordSql to set
	 */
	public void setSelecImageFileRecordSql(String selecImageFileRecordSql) {
		this.selecImageFileRecordSql = selecImageFileRecordSql;
	}

	/**
	 * @return the updateImageFileRecordSql
	 */
	public String getUpdateImageFileRecordSql() {
		return updateImageFileRecordSql;
	}

	/**
	 * @param updateImageFileRecordSql the updateImageFileRecordSql to set
	 */
	public void setUpdateImageFileRecordSql(String updateImageFileRecordSql) {
		this.updateImageFileRecordSql = updateImageFileRecordSql;
	}

}
