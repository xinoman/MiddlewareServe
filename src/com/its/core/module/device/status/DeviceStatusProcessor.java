/**
 * 
 */
package com.its.core.module.device.status;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.sequence.SequenceFactory;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.device.ACommunicateProcessor;
import com.its.core.module.device.MessageBean;
import com.its.core.module.device.MessageHelper;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLParser;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-9-21 下午04:17:09
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class DeviceStatusProcessor extends ACommunicateProcessor {
	private static final Log log = LogFactory.getLog(DeviceStatusProcessor.class);
	
	private String selectExistSql = null;
	private String insertStatusSql = null;
	private String updateStatusSql = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.device.ACommunicateProcessor#configure(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configure(XMLProperties props, String propertiesPrefix, int no)	throws Exception {
		this.selectExistSql = props.getProperty(propertiesPrefix, no, "select_exist_sql");
		this.insertStatusSql = props.getProperty(propertiesPrefix, no, "insert_status_sql");
		this.updateStatusSql = props.getProperty(propertiesPrefix, no, "update_status_sql");

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.device.ACommunicateProcessor#process(com.its.core.module.device.MessageBean)
	 */
	@Override
	public void process(MessageBean messageBean) throws Exception {
		String deviceId = null;				
		
		List<DeviceStatusBean> dsbList = this.parseMessage(messageBean);
		Connection conn = null;
		try{
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(true);			
			
			int size = dsbList.size();
			for(int i=0;i<size;i++){
				DeviceStatusBean dsb = dsbList.get(i);
				if(StringHelper.isEmpty(dsb.getStatusInfo())){
					continue;
				}
				deviceId = dsb.getDeviceId();						
				this.insertDeviceStatus(conn, deviceId,dsb);
				
			}
			
		}catch(Exception ex){
			log.error(ex.getMessage(),ex);
		}finally{
			try {
				ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
			} catch (Exception ex1) {
			}
		}

	}
	
	/**
	 * 解析MessageBean
	 * @param messageBean
	 * @return
	 */
	private List<DeviceStatusBean> parseMessage(MessageBean messageBean){
		List<DeviceStatusBean> dsbList = new ArrayList<DeviceStatusBean>();
		
		XMLParser xmlParser = messageBean.getXmlParser();
		String directionEle = MessageHelper.XML_ELE_CONTENT;
		int len = xmlParser.getPropertyNum(directionEle);
		for(int i=0;i<len;i++){
			DeviceStatusBean dsb = new DeviceStatusBean();				
			dsb.setDeviceId(xmlParser.getProperty(directionEle, i, "device.id"));
			dsb.setDirectionCode(xmlParser.getProperty(directionEle, i, "device.direction"));
			dsb.setStatusInfo(xmlParser.getProperty(directionEle,i,"device.status"));
			dsb.setDesc(xmlParser.getProperty(directionEle, i, "device.desc"));
			dsbList.add(dsb);
		}			
		
		return dsbList;
	}
	
	private void insertDeviceStatus(Connection conn,String deviceId,DeviceStatusBean deviceStatusBean){
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;
		try {							

			//不带方向
			preStatement = conn.prepareStatement(this.getSelectExistSql());
			preStatement.setString(1, deviceId);
			resultSet = preStatement.executeQuery();

			boolean exist = false;
			if (resultSet.next()) {
				exist = true;
			}
			DatabaseHelper.close(resultSet, preStatement);
			//log.debug("device status exist = "+exist);
			if (exist) {
				//更新设备状态		
				//log.debug(this.getUpdateStatusSql());
				preStatement = conn.prepareStatement(this.getUpdateStatusSql());
				preStatement.setString(1, deviceStatusBean.getStatusInfo());
				preStatement.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
				preStatement.setString(3, deviceId);
				preStatement.executeUpdate();
			} else {
				//插入设备状态
				//log.debug(this.getInsertStatusSql());
				preStatement = conn.prepareStatement(this.getInsertStatusSql());
				preStatement.setLong(1, (long)SequenceFactory.getInstance().getDeviceStatusSequence());
				preStatement.setString(2, deviceId);
				preStatement.setString(3, deviceStatusBean.getDirectionCode());
				preStatement.setString(4, deviceStatusBean.getStatusInfo());
				preStatement.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
				preStatement.executeUpdate();
			}	
			DatabaseHelper.close(resultSet, preStatement);					
			
		} catch (Exception ex) {
			log.error("处理设备状态信息时出错：" + ex.getMessage(), ex);
		} finally {
			DatabaseHelper.close(resultSet, preStatement);
		}		
	}

	/**
	 * @return the selectExistSql
	 */
	public String getSelectExistSql() {
		return selectExistSql;
	}

	/**
	 * @param selectExistSql the selectExistSql to set
	 */
	public void setSelectExistSql(String selectExistSql) {
		this.selectExistSql = selectExistSql;
	}

	/**
	 * @return the insertStatusSql
	 */
	public String getInsertStatusSql() {
		return insertStatusSql;
	}

	/**
	 * @param insertStatusSql the insertStatusSql to set
	 */
	public void setInsertStatusSql(String insertStatusSql) {
		this.insertStatusSql = insertStatusSql;
	}

	/**
	 * @return the updateStatusSql
	 */
	public String getUpdateStatusSql() {
		return updateStatusSql;
	}

	/**
	 * @param updateStatusSql the updateStatusSql to set
	 */
	public void setUpdateStatusSql(String updateStatusSql) {
		this.updateStatusSql = updateStatusSql;
	}	

}
