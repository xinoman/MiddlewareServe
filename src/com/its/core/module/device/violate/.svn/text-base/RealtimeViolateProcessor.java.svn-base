/**
 * 
 */
package com.its.core.module.device.violate;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.common.sequence.SequenceFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.device.ACommunicateProcessor;
import com.its.core.module.device.MessageBean;
import com.its.core.module.device.MessageHelper;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLParser;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-9-21 下午04:35:34
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class RealtimeViolateProcessor extends ACommunicateProcessor {
	private static final Log log = LogFactory.getLog(RealtimeViolateProcessor.class);
	
	private String insertViolateSql = null;
	
	private String dayStatCheckExistSql = null;	
	private String dayStatInsertSql = null;	
	private String dayStatUpdateSql = null;
	
	private String hourStatCheckExistSql = null;	
	private String hourStatInsertSql = null;	
	private String hourStatUpdateSql = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.device.ACommunicateProcessor#configure(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configure(XMLProperties props, String propertiesPrefix, int no)	throws Exception {
		this.insertViolateSql = props.getProperty(propertiesPrefix, no, "insert_violate_sql");
		
		this.dayStatCheckExistSql = (props.getProperty(propertiesPrefix, no, "day_stat_sql.check_exist"));
		this.dayStatInsertSql = (props.getProperty(propertiesPrefix, no, "day_stat_sql.insert"));
		this.dayStatUpdateSql = (props.getProperty(propertiesPrefix, no, "day_stat_sql.update"));
		
		this.hourStatCheckExistSql = (props.getProperty(propertiesPrefix, no, "hour_stat_sql.check_exist"));
		this.hourStatInsertSql = (props.getProperty(propertiesPrefix, no, "hour_stat_sql.insert"));
		this.hourStatUpdateSql = (props.getProperty(propertiesPrefix, no, "hour_stat_sql.update"));

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.device.ACommunicateProcessor#process(com.its.core.module.device.MessageBean)
	 */
	@Override
	public void process(MessageBean messageBean) throws Exception {
		RealtimeViolateInfoBean realtimeViolateInfoBean = this.parseMessage(messageBean);
		
		Connection conn = null;
		try{
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(true);			
			
			this.insertViolateRecord(conn, realtimeViolateInfoBean);
			
			this.statTrafficFlux(conn, realtimeViolateInfoBean);
			
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);
		}finally{
			try {
				ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
			} catch (Exception ex1) {}
		}

	}
	
	/**
	 * 交通流量统计
	 * @param conn
	 * @param realtimeViolateInfoBean
	 */
	private void statTrafficFlux(Connection conn,RealtimeViolateInfoBean realtimeViolateInfoBean){		
		boolean result = true;
		
		Map<String, DeviceInfoBean> dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();		
		if(!dibMap.containsKey(realtimeViolateInfoBean.getDeviceId())){
			log.debug("未知的设备编号:" + realtimeViolateInfoBean.getDeviceId());			
			result = false;
		}
		
		if(result) {
			PreparedStatement preStatement = null;
			ResultSet resultSet = null;	
			
			try{
				Timestamp catchDay = new Timestamp(DateHelper.parseDateString(DateHelper.dateToString(realtimeViolateInfoBean.getViolateTime(), "yyyyMMdd"),"yyyyMMdd").getTime());
				long statId = -1L;
				String laneNo = realtimeViolateInfoBean.getDrivewayNo();
				if(laneNo.length()==1){
					laneNo = "0"+laneNo;
				}
				
				long roadId = 0;
				
				preStatement = conn.prepareStatement(this.getDayStatCheckExistSql());
				DeviceInfoBean dib = (DeviceInfoBean)dibMap.get(realtimeViolateInfoBean.getDeviceId());
				roadId = Long.valueOf(dib.getRoadId());
				preStatement.setLong(1,roadId);	
				preStatement.setString(2, realtimeViolateInfoBean.getDeviceId());
				preStatement.setString(3, realtimeViolateInfoBean.getDirectionCode());	
				preStatement.setString(4, laneNo);	
				preStatement.setTimestamp(5, catchDay);
				resultSet = preStatement.executeQuery();
				if(resultSet.next()){
					statId = resultSet.getLong("id");					
				}
				
				DatabaseHelper.close(resultSet, preStatement);
				if(statId==-1L){
					
					statId = (long)SequenceFactory.getInstance().getStatTrafficDayStatSequence();
					preStatement = conn.prepareStatement(this.getDayStatInsertSql());
					preStatement.setLong(1,statId);	
					preStatement.setLong(2,roadId);	
					preStatement.setString(3, realtimeViolateInfoBean.getDeviceId());
					preStatement.setString(4, realtimeViolateInfoBean.getDirectionCode());		
					preStatement.setString(5, laneNo);
					preStatement.setTimestamp(6, catchDay);	
					preStatement.setLong(7, 1L);	
//					log.debug("statId = "+statId+" created!");
				}
				else{
					//更新记录:update T_ITS_TRAFFIC_DAY_STAT set IMAGE_SUM=IMAGE_SUM+1 where ID=?
					preStatement = conn.prepareStatement(this.getDayStatUpdateSql());
					preStatement.setLong(1,statId);	
//					log.debug("statId = "+statId+" updated!");
				}
				preStatement.executeUpdate();			
				DatabaseHelper.close(null, preStatement);
				
				String catchHour = DateHelper.dateToString(realtimeViolateInfoBean.getViolateTime(), "yyyy-MM-dd HH");
				long id = -1L;
				preStatement = conn.prepareStatement(this.getHourStatCheckExistSql());				
				preStatement.setLong(1,roadId);
				preStatement.setString(2, realtimeViolateInfoBean.getDeviceId());
				preStatement.setString(3, realtimeViolateInfoBean.getDirectionCode());	
				preStatement.setString(4, laneNo);	
				preStatement.setString(5, catchHour);
				resultSet = preStatement.executeQuery();
				if(resultSet.next()){
					id = resultSet.getLong("id");					
				}
				DatabaseHelper.close(resultSet, preStatement);
				if(id==-1L){
					
					id = (long)SequenceFactory.getInstance().getStatTrafficHourStatSequence();					
					preStatement = conn.prepareStatement(this.getHourStatInsertSql());
					preStatement.setLong(1,id);						
					preStatement.setLong(2,roadId);						
					preStatement.setString(3, realtimeViolateInfoBean.getDeviceId());					
					preStatement.setString(4, realtimeViolateInfoBean.getDirectionCode());					
					preStatement.setString(5, laneNo);					
					preStatement.setString(6, catchHour);	
					preStatement.setLong(7, 1L);	
//					log.debug("id = "+id+" created!");
				}
				else{
					
					preStatement = conn.prepareStatement(this.getHourStatUpdateSql());
					preStatement.setLong(1,id);	
//					log.debug("id = "+id+" updated!");
				}
				preStatement.executeUpdate();
				
			}catch(Exception ex){
				log.error(ex.getMessage(),ex);
			}finally{			
				DatabaseHelper.close(resultSet, preStatement);				
			}
		}		
				
	}
	
	/**
	 * 解析MessageBean
	 * @param messageBean
	 * @return
	 */
	private RealtimeViolateInfoBean parseMessage(MessageBean messageBean) throws Exception{
		XMLParser xmlParser = messageBean.getXmlParser();	
		
		RealtimeViolateInfoBean realtimeViolateInfoBean = new RealtimeViolateInfoBean();	
		realtimeViolateInfoBean.setViolateType(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "zplx"));
		realtimeViolateInfoBean.setDeviceId(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "sbbh"));
		realtimeViolateInfoBean.setDirectionCode(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "fxbh"));
		realtimeViolateInfoBean.setPlateNo(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "hphm"));
		realtimeViolateInfoBean.setPlateColor(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "hpys"));
		String violateTime = xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "wfsj");
		if(violateTime.length()>14){
			violateTime = violateTime.substring(0,14);
		}
		realtimeViolateInfoBean.setViolateTime(DateHelper.parseDateString(violateTime,"yyyyMMddHHmmss"));
		realtimeViolateInfoBean.setDrivewayNo(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "cdbh"));
		realtimeViolateInfoBean.setSpeed(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "clsd"));
		realtimeViolateInfoBean.setLimitSpeed(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "clxs"));
		realtimeViolateInfoBean.setImagePath1(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "zjtp1"));
		realtimeViolateInfoBean.setImagePath2(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "zjtp2"));
		realtimeViolateInfoBean.setImagePath3(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "zjtp3"));
		realtimeViolateInfoBean.setImagePath4(xmlParser.getProperty(MessageHelper.XML_ELE_CONTENT_PREFIX + "zjtp4"));			
		
		return realtimeViolateInfoBean;
	}
	/**
	 * 
	 * @param conn
	 * @param realtimeViolateInfoBean
	 */
	private void insertViolateRecord(Connection conn,RealtimeViolateInfoBean realtimeViolateInfoBean){
		
		boolean result = true;		
		String deviceId = realtimeViolateInfoBean.getDeviceId();
		
   		Map<String, DeviceInfoBean> dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
		if(dibMap.containsKey(deviceId)){
			DeviceInfoBean deviceInfoBean = (DeviceInfoBean)dibMap.get(deviceId);
			realtimeViolateInfoBean.setRoadId(deviceInfoBean.getRoadId());						
		} else {				
			log.debug("未知的设备编号:" + deviceId);		
			result = false;
		}
		
		if(result) {
			PreparedStatement preStatement = null;
			
	       	try{
	       		long key = (long)SequenceFactory.getInstance().getViolateRecordTempSequence();
//	       		log.debug("执行：" + this.getInsertViolateSql());
	       		preStatement = conn.prepareStatement(this.getInsertViolateSql());
				preStatement.setLong(1, key);			
				preStatement.setTimestamp(2, new Timestamp(realtimeViolateInfoBean.getViolateTime().getTime()));		
				preStatement.setInt(3, Integer.parseInt(realtimeViolateInfoBean.getRoadId()));			
				preStatement.setString(4, deviceId);			
				preStatement.setString(5, realtimeViolateInfoBean.getDirectionCode());			
				preStatement.setString(6, realtimeViolateInfoBean.getDrivewayNo());			
				preStatement.setString(7, realtimeViolateInfoBean.getSpeed());			
				preStatement.setString(8, realtimeViolateInfoBean.getLimitSpeed());			
				preStatement.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));				
				preStatement.setString(10, realtimeViolateInfoBean.getPlateNo());
				
				String plateTypeId = SystemConstant.getInstance().getPlateTypeIdByColor(realtimeViolateInfoBean.getPlateColor());
				//缺省：小型汽车 02
				if(StringHelper.isEmpty(plateTypeId)){					
					plateTypeId = SystemConstant.getInstance().PLATE_TYPE_ID_ROADLOUSE;
				}
				else{
					plateTypeId = plateTypeId.trim();
				}	
				
				preStatement.setString(11, plateTypeId);			
				
				if(StringHelper.isNotEmpty(realtimeViolateInfoBean.getImagePath1())) {
					preStatement.setString(12, realtimeViolateInfoBean.getImagePath1());
				} else {
					preStatement.setString(12, null);
				}
				
				if(StringHelper.isNotEmpty(realtimeViolateInfoBean.getImagePath2())) {
					preStatement.setString(13, realtimeViolateInfoBean.getImagePath2());
				} else {
					preStatement.setString(13, null);
				}
				
				if(StringHelper.isNotEmpty(realtimeViolateInfoBean.getImagePath3())) {
					preStatement.setString(14, realtimeViolateInfoBean.getImagePath3());
				} else {
					preStatement.setString(14, null);
				}
				
				if(StringHelper.isNotEmpty(realtimeViolateInfoBean.getImagePath4())) {
					preStatement.setString(15, realtimeViolateInfoBean.getImagePath4());
				} else {
					preStatement.setString(15, null);
				}	
				
				preStatement.setString(16, realtimeViolateInfoBean.getViolateType());

				preStatement.execute();			     		
				log.debug("违法信息入库成功,生成的ID为："+key);
	       	}
	       	catch(Exception ex){
				log.error("违法信息入库失败：" + ex.getMessage(), ex);			
	       	}
	       	finally{			 
				DatabaseHelper.close(null, preStatement);
	       	}
		}
		
	}

	/**
	 * @return the insertViolateSql
	 */
	public String getInsertViolateSql() {
		return insertViolateSql;
	}

	/**
	 * @param insertViolateSql the insertViolateSql to set
	 */
	public void setInsertViolateSql(String insertViolateSql) {
		this.insertViolateSql = insertViolateSql;
	}

	/**
	 * @return the dayStatCheckExistSql
	 */
	public String getDayStatCheckExistSql() {
		return dayStatCheckExistSql;
	}

	/**
	 * @param dayStatCheckExistSql the dayStatCheckExistSql to set
	 */
	public void setDayStatCheckExistSql(String dayStatCheckExistSql) {
		this.dayStatCheckExistSql = dayStatCheckExistSql;
	}

	/**
	 * @return the dayStatInsertSql
	 */
	public String getDayStatInsertSql() {
		return dayStatInsertSql;
	}

	/**
	 * @param dayStatInsertSql the dayStatInsertSql to set
	 */
	public void setDayStatInsertSql(String dayStatInsertSql) {
		this.dayStatInsertSql = dayStatInsertSql;
	}

	/**
	 * @return the dayStatUpdateSql
	 */
	public String getDayStatUpdateSql() {
		return dayStatUpdateSql;
	}

	/**
	 * @param dayStatUpdateSql the dayStatUpdateSql to set
	 */
	public void setDayStatUpdateSql(String dayStatUpdateSql) {
		this.dayStatUpdateSql = dayStatUpdateSql;
	}

	/**
	 * @return the hourStatCheckExistSql
	 */
	public String getHourStatCheckExistSql() {
		return hourStatCheckExistSql;
	}

	/**
	 * @param hourStatCheckExistSql the hourStatCheckExistSql to set
	 */
	public void setHourStatCheckExistSql(String hourStatCheckExistSql) {
		this.hourStatCheckExistSql = hourStatCheckExistSql;
	}

	/**
	 * @return the hourStatInsertSql
	 */
	public String getHourStatInsertSql() {
		return hourStatInsertSql;
	}

	/**
	 * @param hourStatInsertSql the hourStatInsertSql to set
	 */
	public void setHourStatInsertSql(String hourStatInsertSql) {
		this.hourStatInsertSql = hourStatInsertSql;
	}

	/**
	 * @return the hourStatUpdateSql
	 */
	public String getHourStatUpdateSql() {
		return hourStatUpdateSql;
	}

	/**
	 * @param hourStatUpdateSql the hourStatUpdateSql to set
	 */
	public void setHourStatUpdateSql(String hourStatUpdateSql) {
		this.hourStatUpdateSql = hourStatUpdateSql;
	}	

}
