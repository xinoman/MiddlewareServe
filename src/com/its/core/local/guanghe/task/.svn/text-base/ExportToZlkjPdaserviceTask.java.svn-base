/**
 * 
 */
package com.its.core.local.guanghe.task;

import java.io.FileNotFoundException;
import java.net.InetAddress;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import sun.misc.BASE64Encoder;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.local.hezheng.task.ViolateRecordBean;
import com.its.core.local.hezheng.ws.AutoServicesPortTypeProxy;
import com.its.core.module.task.ATask;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.ImageHelper;
import com.its.core.util.IpMacAddressHelper;
import com.its.core.util.PropertiesHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLParser;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-12-13 下午06:43:26
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ExportToZlkjPdaserviceTask extends ATask {
	private static final Log log = LogFactory.getLog(ExportToZlkjPdaserviceTask.class);
	
	private String epoEndPoint = null;		
	private String xlh = "xcvfjwqeio34292fnsdfjoasdofjfjif";	
	private String fxjg = null;
	
	private String imageSavePath = null;
	private float imageQuality = -1;
	
	private String selectUnexportRecordSql = null;
	
	//更新过期记录的FINISH_STATUS='E'
	private String updateViolateRecordExpireStatusSql = null;		
	private String updateViolateRecordStatusSql = null;	

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.epoEndPoint = props.getProperty(propertiesPrefix,no,"webservice.epo_endpoint");	
//		this.xlh = props.getProperty(propertiesPrefix,no,"webservice.xlh");	
		this.fxjg = props.getProperty(propertiesPrefix,no,"webservice.fxjg");
		
		this.imageSavePath = props.getProperty(propertiesPrefix,no,"image_save_path");	
		this.imageQuality = PropertiesHelper.getFloat(propertiesPrefix,no,"image_quality",props,this.imageQuality);
		
		this.selectUnexportRecordSql = props.getProperty(propertiesPrefix,no,"sql.select_unexport_record");	
		this.updateViolateRecordExpireStatusSql = props.getProperty(propertiesPrefix,no,"sql.update_violate_record_expire_status");
		this.updateViolateRecordStatusSql = props.getProperty(propertiesPrefix,no,"sql.update_violate_record_status");
	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		Connection conn = null;
		
		try {
			String osName = System.getProperty("os.name").toUpperCase();
			String mac = null, ip = null, address = null;
			
			InetAddress addr = InetAddress.getLocalHost();
			ip=addr.getHostAddress();
			address=addr.getHostName();
			
			if(osName.indexOf(SystemConstant.WINDOWS_2003) != -1 || osName.indexOf(SystemConstant.WINDOWS_XP) != -1) {
				mac = IpMacAddressHelper.getMACAddress();				
			} else {
				mac = IpMacAddressHelper.getWindowsMACAddress();
			}
			if(StringHelper.isEmpty(mac)) IpMacAddressHelper.getLocalMachineInfo();
			log.info("Computer name = " + address + " IP = " + ip + " MAC = " + mac);
		} catch (Exception e) {
			log.debug("Server operating systems are not compatible!");
		}
		
		try {		
			
			conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
			log.debug("conn = "+conn);
			conn.setAutoCommit(false);
			
			List<ViolateRecordBean> recordList = this.getUnexportRecord(conn);
			int size = recordList.size();
			log.debug("共找到："+size+"条记录！");
			if(size==0) return;			
			
			log.debug(this.getEpoEndPoint());
			AutoServicesPortTypeProxy proxy = new AutoServicesPortTypeProxy();			
			proxy.setEndpoint(this.getEpoEndPoint());
			
			short currentExcelRow = 1;
			for(int i=0;i<size;i++){
				ViolateRecordBean record = (ViolateRecordBean)recordList.get(i);		
				
				//修改状态标识
				this.updateRecordStatus(conn, record.getId());						
				
				boolean foundFile = false;		
				byte[] fileBytes = null;
				
				if(StringHelper.isNotEmpty(record.getImagePath1()) && StringHelper.isNotEmpty(record.getImagePath2()) && StringHelper.isNotEmpty(record.getImagePath3())){										
					foundFile = true;
					try{	
						byte[] fileBytes1 = ImageHelper.getImageBytes(record.getImagePath1().substring(0,record.getImagePath1().lastIndexOf("/")+1) + URLEncoder.encode(record.getImagePath1().substring(record.getImagePath1().lastIndexOf("/")+1),"UTF-8"));	
						byte[] fileBytes2 = ImageHelper.getImageBytes(record.getImagePath2().substring(0,record.getImagePath2().lastIndexOf("/")+1) + URLEncoder.encode(record.getImagePath2().substring(record.getImagePath2().lastIndexOf("/")+1),"UTF-8"));
						byte[] fileBytes3 = ImageHelper.getImageBytes(record.getImagePath3().substring(0,record.getImagePath3().lastIndexOf("/")+1) + URLEncoder.encode(record.getImagePath3().substring(record.getImagePath3().lastIndexOf("/")+1),"UTF-8"));
						fileBytes = ImageHelper.compose(ImageHelper.compose(fileBytes1, fileBytes2, true), fileBytes3, true);	
						
						if(StringHelper.isNotEmpty(this.getImageSavePath())) {
							DeviceInfoBean dib = (DeviceInfoBean) DeviceInfoLoaderFactory.getInstance().getDeviceMap().get(record.getDeviceId());
							String filePath = this.getImageSavePath()+this.getFxjg()+"/"+DateHelper.dateToString(record.getViolateTime(), "yyyyMMdd")+"/";
							FileHelper.createDir(filePath);
							FileHelper.writeFile(fileBytes, filePath + dib.getThirdPartyDeviceInfoBean().getDeviceId()+"@"+DateHelper.dateToString(record.getViolateTime(),"yyyy年MM月dd日HH时mm分ss秒") + "@"+record.getPlateTypeId()+"@"+record.getPlate()+"@"+this.getFxjg()+".JPG");
						}
						
					}catch(FileNotFoundException fnfe){
						log.error("未找到文件："+record.getImagePath1(),fnfe);		
						foundFile = false;
					}
				} else {
					foundFile = true;
					try{
						fileBytes = ImageHelper.getImageBytes(record.getImagePath1().substring(0,record.getImagePath1().lastIndexOf("/")+1) + URLEncoder.encode(record.getImagePath1().substring(record.getImagePath1().lastIndexOf("/")+1),"UTF-8"));
						if(imageQuality>0) fileBytes = ImageHelper.compress(fileBytes,imageQuality);
						
						if(StringHelper.isNotEmpty(this.getImageSavePath())) {
							DeviceInfoBean dib = (DeviceInfoBean) DeviceInfoLoaderFactory.getInstance().getDeviceMap().get(record.getDeviceId());
							String filePath = this.getImageSavePath()+this.getFxjg()+"/"+DateHelper.dateToString(record.getViolateTime(), "yyyyMMdd")+"/";
							FileHelper.createDir(filePath);
							FileHelper.writeFile(fileBytes, filePath + dib.getThirdPartyDeviceInfoBean().getDeviceId()+"@"+DateHelper.dateToString(record.getViolateTime(),"yyyy年MM月dd日HH时mm分ss秒") + "@"+record.getPlateTypeId()+"@"+record.getPlate()+"@"+this.getFxjg()+".JPG");
						}
					}catch(FileNotFoundException fnfe){
						log.error("未找到文件："+record.getImagePath1(),fnfe);		
						foundFile = false;
					}
					
				}
				
				//找到文件
				if(foundFile){
					String response = null, xmlDoc = null,returnCode = "0",returnMessage = null;
					try{
						
						xmlDoc = this.getXmlDoc(record,fileBytes);		
						log.info("序列号(xlh):"+ this.getXlh());
						response = proxy.writeSurveil(this.getXlh(), StringHelper.encodeUTF8(xmlDoc));						
						response = StringHelper.decodeUTF8(response);
						log.info("公安交通管理综合应用平台系统,接口返回消息："+response);
						
						if(StringHelper.isNotEmpty(response)){
							XMLParser xmlParser = new XMLParser(response,"GBK");
							returnCode 	= xmlParser.getProperty("root.head.code");
							returnMessage = xmlParser.getProperty("root.head.message");	
						} else{
							returnCode = "0";
							returnMessage = "";
						}
						
					}catch(Exception ex){						
						log.error("上传失败,返回消息：" + ex.getMessage(),ex);						
					}	
					
					if("1".equals(returnCode)){		
						conn.commit();
						log.debug("入库成功！返回消息："+returnMessage);						
					}
					else{
						conn.rollback();						
						log.warn("入库失败！返回代码："+returnCode+",返回消息："+returnMessage);
					}
										
				} 
//				else {
//					conn.commit();
//				}
				
				currentExcelRow++;
			}
			log.info("本次共上传记录：" + (currentExcelRow-1) + "条！");
		}
		catch(Exception ex){
			log.error("上传失败" + ex.getMessage(),ex);		
			
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
	
	private String getXmlDoc(ViolateRecordBean record,byte[] fileBytes) throws Exception {

		DeviceInfoBean dib = (DeviceInfoBean) DeviceInfoLoaderFactory.getInstance().getDeviceMap().get(record.getDeviceId());
		StringBuffer xmlDoc = new StringBuffer();		
		xmlDoc.append("<root>");
			xmlDoc.append("<violation>");		
				xmlDoc.append("<fxjg>").append(this.getFxjg()).append("</fxjg>");		
				xmlDoc.append("<hpzl>").append(record.getPlateTypeId()).append("</hpzl>");
				xmlDoc.append("<hphm>").append(record.getPlate()).append("</hphm>");
				xmlDoc.append("<wfsj>").append(DateHelper.dateToString(record.getViolateTime(), "yyyy-MM-dd HH:mm:ss")).append("</wfsj>");
				xmlDoc.append("<wfxw>").append(record.getWfxwCode()).append("</wfxw>");
				xmlDoc.append("<sbbh>").append(dib.getThirdPartyDeviceInfoBean().getDeviceId()).append("</sbbh>");
				xmlDoc.append("<bzz></bzz>");
				xmlDoc.append("<scz></scz>");	
				log.info(xmlDoc.toString());
				xmlDoc.append("<photo1>").append(new BASE64Encoder().encode(fileBytes)).append("</photo1>");
				xmlDoc.append("<photo2></photo2>");
				xmlDoc.append("<photo3></photo3>");
			xmlDoc.append("</violation>");
		xmlDoc.append("</root>");

		return xmlDoc.toString();
	}	
	
	/**
	 * 获取所有未导出的T_ITS_VEH_VIO_RECORD记录
	 * @param conn
	 * @return
	 * @throws Exception
	 */
	public List<ViolateRecordBean> getUnexportRecord(Connection conn) throws Exception{
		List<ViolateRecordBean> recordList = new ArrayList<ViolateRecordBean>();
		PreparedStatement preStatement = null;
		ResultSet rs = null;
		try{		
			log.debug("执行："+this.getSelectUnexportRecordSql());
			preStatement = conn.prepareStatement(this.getSelectUnexportRecordSql());
			rs = preStatement.executeQuery();
			
			while(rs.next()){
				ViolateRecordBean record = new ViolateRecordBean();
				record.setId(rs.getLong("id"));
				record.setViolateTime(rs.getTimestamp("violate_time"));				
				record.setDeviceId(rs.getString("device_id"));
				record.setPlate(rs.getString("plate"));
				record.setPlateTypeId(rs.getString("plate_type_id"));				
				record.setImagePath1(rs.getString("image_path_1"));
				record.setImagePath2(rs.getString("image_path_2"));
				record.setImagePath3(rs.getString("image_path_3"));					
				record.setWfxwCode(rs.getString("wfxw_code"));							
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
	
	/**
	 * 更新T_ITS_VIOLATE_RECORD:STATUS
	 * @param conn
	 * @param id
	 * @throws Exception
	 */
	private void updateRecordStatus(Connection conn,long id) throws Exception{
		PreparedStatement preStatement = null;
		try{
			log.debug(this.getUpdateViolateRecordStatusSql());
			preStatement = conn.prepareStatement(this.getUpdateViolateRecordStatusSql());
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

	public String getEpoEndPoint() {
		return epoEndPoint;
	}

	public void setEpoEndPoint(String epoEndPoint) {
		this.epoEndPoint = epoEndPoint;
	}

	public String getXlh() {
		return xlh;
	}

	public void setXlh(String xlh) {
		this.xlh = xlh;
	}

	public String getFxjg() {
		return fxjg;
	}

	public void setFxjg(String fxjg) {
		this.fxjg = fxjg;
	}

	public String getSelectUnexportRecordSql() {
		return selectUnexportRecordSql;
	}

	public void setSelectUnexportRecordSql(String selectUnexportRecordSql) {
		this.selectUnexportRecordSql = selectUnexportRecordSql;
	}

	public String getUpdateViolateRecordExpireStatusSql() {
		return updateViolateRecordExpireStatusSql;
	}

	public void setUpdateViolateRecordExpireStatusSql(
			String updateViolateRecordExpireStatusSql) {
		this.updateViolateRecordExpireStatusSql = updateViolateRecordExpireStatusSql;
	}

	public String getUpdateViolateRecordStatusSql() {
		return updateViolateRecordStatusSql;
	}

	public void setUpdateViolateRecordStatusSql(String updateViolateRecordStatusSql) {
		this.updateViolateRecordStatusSql = updateViolateRecordStatusSql;
	}

	public String getImageSavePath() {
		return imageSavePath;
	}

	public void setImageSavePath(String imageSavePath) {
		this.imageSavePath = imageSavePath;
	}

	public float getImageQuality() {
		return imageQuality;
	}

	public void setImageQuality(float imageQuality) {
		this.imageQuality = imageQuality;
	}	

}
