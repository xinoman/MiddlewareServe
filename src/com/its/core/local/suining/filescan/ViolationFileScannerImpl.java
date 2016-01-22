/**
 * 
 */
package com.its.core.local.suining.filescan;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.common.sequence.SequenceFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.module.filescan.violation.ViolationFileScannerTestImpl;
import com.its.core.module.filescan.violation.ViolationInfoBean;
import com.its.core.util.CryptoHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.ImageHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2015年4月22日 下午8:48:23
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ViolationFileScannerImpl extends AViolationFileScanner {
	private static final Log log = LogFactory.getLog(ViolationFileScannerTestImpl.class);
	
    private String insertSql = null;
    private String filePathPrefix = null;
    
    //图片是否合并
	private boolean merge = false;
	private boolean orientation = false;

	/* (non-Javadoc)
	 * @see com.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#configureLocalProperties(com.swy.tiip.tools.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureLocalProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.filePathPrefix = props.getProperty(propertiesPrefix,no,"standard_version.file_path_prefix");
		this.merge = StringHelper.getBoolean(props.getProperty(propertiesPrefix,no,"standard_version.merge"));
		this.orientation = StringHelper.getBoolean(props.getProperty(propertiesPrefix,no,"standard_version.orientation"));
		this.insertSql = props.getProperty(propertiesPrefix,no,"standard_version.insert_sql");
	}

	@Override
	protected ViolationInfoBean parseViolationInfo(String scanDir, File imageFile) throws Exception {
		
		String oriFileName = imageFile.getName();
		String fileName = oriFileName.trim().toUpperCase();	
		String[] fileNameSplit = StringHelper.split(fileName, "_");
		
		ViolationInfoBean bean = new ViolationInfoBean();
		
		bean.setViolateType("00");
		
		String deviceId = StringHelper.replace(fileNameSplit[1], ".", "");
		bean.setDeviceIp(fileNameSplit[1]);
		bean.setDeviceId(deviceId);
		
		Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
		DeviceInfoBean dib = (DeviceInfoBean) dibMap.get(bean.getDeviceId());		
		
		//行驶方向		
		bean.setDirectionNo(dib.getDirectionCode());
		
		bean.setLine("01");	
		
		bean.setLimitSpeed("000");
		bean.setSpeed("000");
		
		String timeStr = fileNameSplit[3]+fileNameSplit[4];
		Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(timeStr);
		bean.setViolateTime(date);
		
		String[] imageFiles = new String[1];
		imageFiles[0] = oriFileName;
		bean.setImageFiles(imageFiles);		
		
		String fullImageName = scanDir + "/" + oriFileName;		
		byte[] fileByte = FileHelper.getBytes(new File(fullImageName));
		String md5 = CryptoHelper.getUniHzImageEncryptString(fileByte);	
		bean.setMd5(md5);
		return bean;
	}

	/* (non-Javadoc)
	 * @see com.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#processViolationInfoBean(java.sql.Connection, com.swy.tiip.tools.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public int processViolationInfoBean(Connection conn,ViolationInfoBean violationInfoBean) {
		// TODO Auto-generated method stub
		if(StringHelper.isEmpty(this.getInsertSql())){
			log.warn("未定义'insert_sql'参数！");
			return 0;
		}
        int result = -1;
        PreparedStatement preStatement = null;
       	try{
       		//id,violate_time,road_id,device_id,direction_code,line,speed,limit_speed,create_time,plate,plate_type_id,file_path_1,file_path_2,file_path_3,file_path_4,video_file_path
			preStatement = conn.prepareStatement(this.getInsertSql());
			preStatement.setLong(1, (long)SequenceFactory.getInstance().getViolateRecordTempSequence());
			preStatement.setTimestamp(2, new Timestamp(violationInfoBean.getViolateTime().getTime()));
			preStatement.setString(3, violationInfoBean.getDeviceInfoBean().getRoadId());
			preStatement.setString(4, violationInfoBean.getDeviceId());
			preStatement.setString(5, violationInfoBean.getDirectionNo());
			preStatement.setString(6, violationInfoBean.getLine());
			preStatement.setString(7, violationInfoBean.getSpeed());
			preStatement.setString(8, violationInfoBean.getLimitSpeed());
			preStatement.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));
			preStatement.setString(10, violationInfoBean.getPlateNo());
			
			String plateTypeId = SystemConstant.getInstance().getPlateTypeIdByColor(violationInfoBean.getPlateColor());
			if(StringHelper.isEmpty(plateTypeId)){
				//缺省：小型汽车
				plateTypeId = SystemConstant.getInstance().PLATE_TYPE_ID_ROADLOUSE;
			}
			else{
				plateTypeId = plateTypeId.trim();
			}
			
			preStatement.setString(11, plateTypeId);
			
			String pathPrefix = this.getFilePathPrefix() + violationInfoBean.getDeviceId() + "/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd") + "/"+ DateHelper.dateToString(violationInfoBean.getViolateTime(), "HH") +"/";
			int i=0;
			for(;i<violationInfoBean.getImageFiles().length;i++){
				String imageFileName = violationInfoBean.getImageFiles()[i];
				if(StringHelper.isNotEmpty(imageFileName)){
					preStatement.setString(12+i, pathPrefix + imageFileName);
				}
				else{
					preStatement.setString(12+i, null);
				}
			}
			for(;i<4;i++){
				preStatement.setString(12+i, null);
			}				
			
			if(StringHelper.isNotEmpty(violationInfoBean.getViolateType()))
				preStatement.setString(16, violationInfoBean.getViolateType());
			else
				preStatement.setString(16, "00");			

			preStatement.execute();
			result = 0;       		
       	}
       	catch(Exception ex){
			log.error("数据入库失败：" + ex.getMessage(), ex);
			result = -1;		
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
        return result;
	}
	
	/* (non-Javadoc)
	 * @see com.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#postProcessViolationInfoBean(com.swy.tiip.tools.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public boolean postProcessViolationInfoBean(ViolationInfoBean violationInfoBean) {
		
		boolean backupSuccess = true;
		try {
			String backDir = this.getScannerParam().getBackupDir() + "/" + violationInfoBean.getDeviceId() + "/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd")+"/"+DateHelper.dateToString(violationInfoBean.getViolateTime(), "HH") +"/";
			FileHelper.createDir(backDir);
			
			for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
				String imageFileName = violationInfoBean.getImageFiles()[i];
				if(StringHelper.isNotEmpty(imageFileName)){
					String sourceFileName = violationInfoBean.getFileDir() + "/" + imageFileName;
					String targetFileName = backDir + "/" + imageFileName;
					log.debug("开始移动文件：" + sourceFileName + " 到 " + targetFileName);
					FileHelper.moveFile(sourceFileName, targetFileName);					
				}
			}
			
			if(StringHelper.isNotEmpty(violationInfoBean.getVideoFile())){
				String sourceFileName = violationInfoBean.getFileDir() + "/"  + violationInfoBean.getVideoFile();
				String targetFileName = backDir + "/" + violationInfoBean.getVideoFile();		
				FileHelper.moveFile(sourceFileName, targetFileName);
			}
		} catch (Exception ex) {
			backupSuccess = false;
			log.error(ex.getMessage(), ex);
		}
		return backupSuccess;
	}	

	@Override
	protected void createMark(ViolationInfoBean violationInfoBean) {		
		String scanDir = violationInfoBean.getFileDir();
		
		
		try {
			String imageFileName1 = violationInfoBean.getImageFiles()[0];		
			String fullImageName1 = scanDir + "/" + imageFileName1;
			
			byte[] fileByte = FileHelper.getBytes(new File(fullImageName1));
			Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
			DeviceInfoBean dib = (DeviceInfoBean) dibMap.get(violationInfoBean.getDeviceId());	
			String sbbh = dib.getThirdPartyDeviceInfoBean().getDeviceId();
			if(StringHelper.isEmpty(sbbh)) {
				sbbh = violationInfoBean.getDeviceId();
			}
			String[] waterMarkArr = new String[]{
					"交通违章代码:10391" ,
					"交通违章行为:机动车违反规定停放",
					"设备编号:"+sbbh,
					"防伪码:"+violationInfoBean.getMd5()
																	
			};
			fileByte = ImageHelper.createWaterMark(
					fileByte,
			waterMarkArr,
			this.getScannerParam().getWaterMarkFontSize(),
			this.getScannerParam().getWaterMarkFontHeight(),
			this.getScannerParam().getWaterMarkFontColor(),
			null,
			this.getScannerParam().getWaterMarkLeftMargin(),
			this.getScannerParam().getWaterMarkTopMargin());
			
			FileHelper.writeFile(fileByte, violationInfoBean.getFileDir()+"/"+violationInfoBean.getImageFiles()[0]);
		
		
		} catch (Exception e) {
			log.error(e.getMessage(),e);
		}
		System.gc();
	}

	/* (non-Javadoc)
	 * @see com.swy.tiip.tools.module.filescan.violation.AViolationFileScanner#postRestoreViolationInfoBean(com.swy.tiip.tools.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public boolean postRestoreViolationInfoBean(ViolationInfoBean violationInfoBean) {
		return true;
	}

	public String getInsertSql() {
		return insertSql;
	}

	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}

	public String getFilePathPrefix() {
		return filePathPrefix;
	}

	public void setFilePathPrefix(String filePathPrefix) {
		this.filePathPrefix = filePathPrefix;
	}

	public boolean isMerge() {
		return merge;
	}

	public void setMerge(boolean merge) {
		this.merge = merge;
	}

	public boolean isOrientation() {
		return orientation;
	}

	public void setOrientation(boolean orientation) {
		this.orientation = orientation;
	}

}
