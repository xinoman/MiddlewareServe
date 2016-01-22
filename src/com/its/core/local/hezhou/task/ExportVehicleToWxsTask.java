/**
 * 
 */
package com.its.core.local.hezhou.task;

import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.local.hezhou.tmri.tfc.webservice.TransProxy;
import com.its.core.module.task.ATask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-10-8 下午07:56:36
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ExportVehicleToWxsTask extends ATask {
	private static final Log log = LogFactory.getLog(ExportVehicleToWxsTask.class);
	
	private String wsEndPoint = "http://10.151.195.2:9080/rminf/services/Trans";
	private String httpImagePrefix = null;
	
	private String selectVehicleRecordSql = null;
	private String updateVehicleRecordSql = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {		
		this.wsEndPoint = props.getProperty(propertiesPrefix, no, "ws_info.ws_endpoint");				
		this.httpImagePrefix = props.getProperty(propertiesPrefix, no, "ws_info.http_image_prefix");
		
		this.selectVehicleRecordSql = props.getProperty(propertiesPrefix, no, "sql.select_vehicle_record");
		this.updateVehicleRecordSql = props.getProperty(propertiesPrefix, no, "sql.update_vehicle_record");

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
			
			log.debug(this.getWsEndPoint());
			TransProxy proxy = new TransProxy();
			proxy.setEndpoint(wsEndPoint);			
			
			short currentExcelRow = 1;
			for(int i=0;i<size;i++){
				
				long result = 0;
				
				VehicelRecordBean record = (VehicelRecordBean)recordList.get(i);	
				DeviceInfoBean dib = (DeviceInfoBean)DeviceInfoLoaderFactory.getInstance().getDeviceMap().get(record.getDeviceId());
				
				String drivewayNo = record.getDrivewayNo();
				if(drivewayNo.length()==1) drivewayNo = "0" + drivewayNo;				
				
				String featureImageUrl = "";
				if(StringHelper.isNotEmpty(record.getFeatureImagePath())) {
					featureImageUrl = record.getFeatureImagePath().substring(0,record.getFeatureImagePath().lastIndexOf("/")+1) + URLEncoder.encode(record.getFeatureImagePath().substring(record.getFeatureImagePath().lastIndexOf("/")+1),"UTF-8");					
				} 
//				log.debug(featureImageUrl);
				String plateType = SystemConstant.getInstance().getPlateTypeIdByColor(record.getPlateColorCode());
				if(StringHelper.isEmpty(plateType)) 
					plateType = "02";	
				String plateColor = record.getPlateColorCode();
				if(StringHelper.isEmpty(plateColor)) 
					plateColor = "9";
				if(!plateColor.equals("0") && !plateColor.equals("1") && !plateColor.equals("2") && !plateColor.equals("3") && !plateColor.equals("4"))
					plateColor = "9";
				String plate = record.getPlate();
				if(StringHelper.isEmpty(plate)) {
					plate = "-";
					plateType = "41";
				}				
	
				result = proxy.writeVehicleInfo(
						record.getDeviceId(),//卡口编号
						dib.getThirdPartyDeviceInfoBean().getDirectId(),//方向类型					
						Long.parseLong(drivewayNo),//车道编号
						plate,//号牌号码
						plateType,//号牌种类
						DateHelper.dateToString(record.getCatchTime(), "yyyy-MM-dd HH:mm:ss"),//经过时间
						Long.parseLong(record.getSpeed()),//车辆速度(整数，最长3位)
						Long.parseLong(record.getLimitSpeed()),//车辆限速(整数，最长3位)
						"",//违章行为编码
						(long)300,//车外廓长
						plateColor,//号牌颜色0-白色，1-黄色，2-蓝色，3-黑色，4-绿色，9-其它颜色，不能为空
						"",//车辆类型
						"",//辅助号牌种类
						"",//辅助号牌号码
						"",//辅助号牌颜色
						"",//车辆品牌
						"",//车辆外形
						"",//车身颜色
						this.getHttpImagePrefix(),//通行图片路径
						featureImageUrl.substring(featureImageUrl.indexOf("veh")+3, featureImageUrl.length()),//通行图片1
						"",//通行图片2
						"",
						""
						);
//				log.debug(this.getHttpImagePrefix() + featureImageUrl.substring(featureImageUrl.indexOf("veh")+3, featureImageUrl.length()));
				log.debug("全国机动车缉查布控系统调用结果：" + result + "(正数:执行成功；零与负数：错误代码[具体信息参考附录错误代码表]；)");		
				
				//获取接口调用错误日志
				this.getLastMessage(proxy);
			
				this.updateExportStatus(conn, record.getId());					
				conn.commit();
				
				currentExcelRow++;
			}
			long currentTime = System.currentTimeMillis();
			log.info("本次共上传记录：" + (currentExcelRow-1) + "条！ 耗时：" + ((currentTime- startTime) / 1000F) + "秒！");
			
		}
		catch(Exception ex){
			try {
				conn.commit();
			} catch (SQLException e) {
				e.printStackTrace();
			}
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
	
	private void getLastMessage(TransProxy proxy) throws Exception {		
		
        String message = null;
		try {				
			message = proxy.getLastMessage();					
		} catch(Exception ex){				
			log.error("获取消息日志失败："+ex.getMessage(),ex);			

		}		
		log.debug("消息日志：" + message);
	}
	
	private List<VehicelRecordBean> getExportRecordList(Connection conn) throws Exception{
		List<VehicelRecordBean> recordList = new ArrayList<VehicelRecordBean>();
		PreparedStatement preStatement = null;
		ResultSet rs = null;
		try{		
//			log.debug("执行："+this.getSelectVehicleRecordSql());
			preStatement = conn.prepareStatement(this.getSelectVehicleRecordSql());			
			rs = preStatement.executeQuery();
			
			while(rs.next()){
				VehicelRecordBean record = new VehicelRecordBean();	
				record.setId(rs.getLong("id"));
				record.setPlate(rs.getString("plate"));
				record.setPlateColorCode(rs.getString("plate_color_code"));	
				record.setCatchTime(rs.getTimestamp("catch_time"));
				record.setDeviceId(rs.getString("device_id"));
				record.setDirectionCode(rs.getString("direction_code"));
				record.setDrivewayNo(rs.getString("driveway_no"));
				record.setFeatureImagePath(rs.getString("feature_image_path"));
//				record.setPanoramaImagePath(rs.getString("panorama_image_path"));				
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
	
	private void updateExportStatus(Connection conn,long id) throws Exception{
		PreparedStatement preStatement = null;
		try{
//			log.debug("执行："+this.getUpdateVehicleRecordSql());
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
	 * @return the httpImagePrefix
	 */
	public String getHttpImagePrefix() {
		return httpImagePrefix;
	}

	/**
	 * @param httpImagePrefix the httpImagePrefix to set
	 */
	public void setHttpImagePrefix(String httpImagePrefix) {
		this.httpImagePrefix = httpImagePrefix;
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
	 * @return the updateVehicleRecordSql
	 */
	public String getUpdateVehicleRecordSql() {
		return updateVehicleRecordSql;
	}

	/**
	 * @param updateVehicleRecordSql the updateVehicleRecordSql to set
	 */
	public void setUpdateVehicleRecordSql(String updateVehicleRecordSql) {
		this.updateVehicleRecordSql = updateVehicleRecordSql;
	}	

}
