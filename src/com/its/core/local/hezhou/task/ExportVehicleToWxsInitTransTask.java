/**
 * 
 */
package com.its.core.local.hezhou.task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.database.ConnectionProviderFactory;
import com.its.core.local.hezhou.tmri.tfc.webservice.TransProxy;
import com.its.core.module.task.ATask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-10-18 上午10:50:15
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ExportVehicleToWxsInitTransTask extends ATask {
	private static final Log log = LogFactory.getLog(ExportVehicleToWxsInitTransTask.class);
	
	private String wsEndPoint = "http://10.151.195.2:9080/rminf/services/Trans";
	
	private String selectDeviceInfoSql = null;	

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.wsEndPoint = props.getProperty(propertiesPrefix, no, "ws_info.ws_endpoint");
		this.selectDeviceInfoSql = props.getProperty(propertiesPrefix, no, "sql.select_device_info_sql");		
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {		
		
		Connection conn = null;	
		
		try {
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			conn.setAutoCommit(true);	
			
			log.debug(this.getWsEndPoint());
			TransProxy proxy = new TransProxy();
			proxy.setEndpoint(wsEndPoint);
			
			this.initTrans(proxy, conn);		
			
			//获取接口调用错误日志
			this.getLastMessage(proxy);
			
		}
		catch(Exception ex){
			log.error("上传失败："+ex.getMessage(),ex);				
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
	
	private void initTrans(TransProxy proxy,Connection conn) throws Exception {		
		PreparedStatement preStatement = null;
		ResultSet rs = null;		
		long initResult = 1;
		try {	
			log.debug("执行："+this.getSelectDeviceInfoSql());
			preStatement = conn.prepareStatement(this.getSelectDeviceInfoSql());
			rs = preStatement.executeQuery();
			
			StringBuffer info = new StringBuffer("<info>")			
			.append("<jkid>62C01</jkid>")			
			.append("<jkxlh>7A1E1D0D06070304091502010002090200060904050817E1B03E6D72692E636E</jkxlh>")
			.append("<time>").append(DateHelper.dateToString(new Date(), "yyyy-MM-dd HH:mm:ss")).append("</time>")
			.append("</info>");					
			while(rs.next()) {	
//				log.info("卡口编号：" + rs.getString("device_id") + " 方向类型：" + rs.getString("direction_id") + " 车道号：" + rs.getString("driveway_no"));				
				String[] line = StringHelper.split(rs.getString("driveway_no"), ",");
				String sbbh = rs.getString("device_id");
				for(int i=0;i<line.length;i++) {
					long cdh = Long.parseLong(line[i]);
					initResult = proxy.initTrans(sbbh, rs.getString("direction_id"), cdh, info.toString());
					log.debug("注册返回结果：" + initResult + " (正数:执行成功；零与负数：错误代码[具体信息参考附录错误代码表]；)");
					if(initResult<1) {
						this.updateDeviceRemark(conn, sbbh, initResult + " (正数:执行成功；零与负数：错误代码[具体信息参考附录错误代码表]；)");
					}
				}								
			}						
		} catch(Exception ex){				
			log.error("设备注册失败："+ex.getMessage(),ex);			
			
		}finally{
			DatabaseHelper.close(rs,preStatement);
		}		
	}	
	
	private void getLastMessage(TransProxy proxy) throws Exception {		
		
        String message = null;
		try {				
			message = proxy.getLastMessage();					
		} catch(Exception ex){				
			log.error("获取消息日志失败："+ex.getMessage(),ex);			

		}		
		log.debug("消息日志：" + message);
	}	
	
	private void updateDeviceRemark(Connection conn,String id,String remark) throws Exception{
		PreparedStatement preStatement = null;
		String sql = "update T_ITS_DEVICE set remark = ? where id = ?";
		try{
//			log.debug("执行："+sql);
			preStatement = conn.prepareStatement(sql);
			preStatement.setString(1, remark);
			preStatement.setString(2, id);
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
	 * @return the wsEndPoint
	 */
	public String getWsEndPoint() {
		return wsEndPoint;
	}

	/**
	 * @param wsEndPoint the wsEndPoint to set
	 */
	public void setWsEndPoint(String wsEndPoint) {
		this.wsEndPoint = wsEndPoint;
	}

	/**
	 * @return the selectDeviceInfoSql
	 */
	public String getSelectDeviceInfoSql() {
		return selectDeviceInfoSql;
	}

	/**
	 * @param selectDeviceInfoSql the selectDeviceInfoSql to set
	 */
	public void setSelectDeviceInfoSql(String selectDeviceInfoSql) {
		this.selectDeviceInfoSql = selectDeviceInfoSql;
	}

}
