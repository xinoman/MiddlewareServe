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
 * 创建日期 2013-10-18 下午12:15:22
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ExportImageFilesReadTaskOld extends ATask {
	private static final Log log = LogFactory.getLog(ExportImageFilesReadTaskOld.class);
	
	private String imgSaveDir = null;
	private String httpPrefix = null;
	
	private String selecImageFileRecordSql = null;
	private String updateImageFileRecordSql = null;
	
	public static Map<String,String> DEVICE_MAP = new HashMap<String,String>();
	public static Map<String,String> DIRECTION_MAP = new HashMap<String,String>();	
	
	static{
		DEVICE_MAP.put("192.168.5.141", "451100100012");
		DEVICE_MAP.put("192.168.5.143", "451100100011");
		DEVICE_MAP.put("192.168.5.145", "451100100010");
		DEVICE_MAP.put("192.168.5.147", "451100100008");
		DEVICE_MAP.put("192.168.5.149", "451100100006");	
		DEVICE_MAP.put("192.168.5.151", "451100100009");
//		DEVICE_MAP.put("192.168.5.153", "");
		DEVICE_MAP.put("192.168.5.155", "451100100013");
		DEVICE_MAP.put("192.168.5.157", "451100100013");
		DEVICE_MAP.put("192.168.5.159", "451100100007");			
		
		DIRECTION_MAP.put("E", "1");
		DIRECTION_MAP.put("W", "2");
		DIRECTION_MAP.put("S", "3");
		DIRECTION_MAP.put("N", "4");		
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
//				log.info("JGSJ = " + record.getJGSJ());
//				log.info(DateHelper.parseDateString(record.getJGSJ(), "yyyy-MM-dd HH:mm:ss"));
//				log.info(DateHelper.dateToString(DateHelper.parseDateString(record.getJGSJ(), "yyyy-MM-dd HH:mm:ss"), "yyyyMMdd"));
				String filePath = this.getImgSaveDir()+"/"+DEVICE_MAP.get(record.getDeviceIp())+"/"+DateHelper.dateToString(DateHelper.parseDateString(record.getJGSJ(), "yyyy-MM-dd HH:mm:ss"), "yyyyMMdd")+"/"+ DateHelper.dateToString(DateHelper.parseDateString(record.getJGSJ(), "yyyy-MM-dd HH:mm:ss"), "HH")+"/";
				FileHelper.createDir(filePath);
				
				StringBuffer fileNameBuffer = new StringBuffer("X03");
									
					fileNameBuffer.append("R").append(DEVICE_MAP.get(record.getDeviceIp()))
					.append("D").append(record.getDirectionCode())
					.append("L").append(record.getDrivewayNo())
					.append("I").append("000")
					.append("V").append(record.getSpeed())
					.append("N").append((int)(Math.random() * (99999 - 10000)) + 10000)
					.append("T").append(DateHelper.dateToString(DateHelper.parseDateString(record.getJGSJ(), "yyyy-MM-dd HH:mm:ss"),"yyyyMMddHHmmssSSS"));
				if(StringHelper.isNotEmpty(record.getPlate().trim()) && record.getPlate().trim().indexOf("无车牌")== -1 && record.getPlate().trim().indexOf("*")== -1 && record.getPlate().trim().indexOf(".")== -1) {
					fileNameBuffer.append("&").append(record.getPlate().trim())
					.append("&").append(record.getPlateColorCode().trim());
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
			log.debug("执行："+this.getSelecImageFileRecordSql());
			preStatement = conn.prepareStatement(this.getSelecImageFileRecordSql());
			rs = preStatement.executeQuery();
			
			while(rs.next()){
				VehicelRecordBean record = new VehicelRecordBean();	
				String fileName = rs.getString("PicPath").substring(rs.getString("PicPath").lastIndexOf("\\")+1, rs.getString("PicPath").length());
				record.setId(rs.getLong("ID"));				
				record.setDeviceIp(rs.getString("crossIP"));
				record.setPlate(rs.getString("HPHM"));
				record.setPlateColorCode(rs.getString("HPYS"));
				record.setDirectionCode(DIRECTION_MAP.get(fileName.substring(24, 25)));
				record.setJGSJ(rs.getString("JGSJ"));
				record.setDrivewayNo("0"+fileName.substring(26, 27));
				String speed = rs.getString("CLSD");
				while(speed.length()<3) speed = "0"+speed;
				record.setSpeed(speed);
				record.setFeatureImagePath(rs.getString("PicPath").substring(rs.getString("PicPath").indexOf("KKPic")+5, rs.getString("PicPath").length()));			
				record.setRoadName(rs.getString("crossName"));
				record.setRoadId(rs.getString("crossNumber"));
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
