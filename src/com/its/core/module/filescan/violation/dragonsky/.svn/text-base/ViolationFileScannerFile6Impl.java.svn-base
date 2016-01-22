/**
 * 
 */
package com.its.core.module.filescan.violation.dragonsky;

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
import com.its.core.module.filescan.violation.ViolationInfoBean;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2014-5-31 下午02:35:29
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ViolationFileScannerFile6Impl extends AViolationFile6FileScanner {

	private static final Log log = LogFactory.getLog(ViolationFileScannerFile5Impl.class);

	private String insertSql = null;
	private String httpUrlPrefix = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.its.core.module.filescan.violation.AViolationFileScanner#
	 * configureLocalProperties(com.its.core.util.XMLProperties,
	 * java.lang.String, int)
	 */
	@Override
	public void configureLocalProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.httpUrlPrefix = props.getProperty(propertiesPrefix, no,"standard_version.http_url_prefix");
		this.insertSql = props.getProperty(propertiesPrefix, no,"standard_version.insert_sql");
	}	

	@Override
	protected ViolationInfoBean parseViolationInfo(String scanDir,File imageFile) throws Exception {
		String oriFileName = imageFile.getName();
		String fileName = oriFileName.trim().toUpperCase();
		ViolationInfoBean bean = new ViolationInfoBean();
		
		String[] fileNameSplit = StringHelper.split(oriFileName, "_");
		if(fileNameSplit[3].indexOf("未知") == -1) {
			bean.setPlateNo(fileNameSplit[3]);
		}
		
//		if(fileNameSplit[5].indexOf("未知") == -1) {
//			bean.setPlateType(SystemConstant.getInstance().getPlateTypeIdByColorName(fileNameSplit[5]));
//		}

		// 设备编号
		bean.setDeviceId(fileNameSplit[2]);
		
		Map<String, DeviceInfoBean> dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();		
		if(!dibMap.containsKey(fileNameSplit[2])){
			log.debug("未知的设备编号:" + fileNameSplit[2]);			
			return null;
		}

		// 行驶方向
//		Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
		DeviceInfoBean dib = (DeviceInfoBean) dibMap.get(bean.getDeviceId());
		bean.setDirectionNo(dib.getDirectionCode());

		bean.setLine("01");

		// 违法时间
		String timeStr = fileName.substring(1, 15);
		log.info("timeStr = " + timeStr);
		Date time = new SimpleDateFormat("yyyyMMddHHmmss").parse(timeStr);
		bean.setViolateTime(time);
		
		String[] imageFiles = null;

		if (imageFiles == null) {
			imageFiles = new String[1];
			imageFiles[0] = oriFileName;
		}
		bean.setImageFiles(imageFiles);

		return bean;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.its.core.module.filescan.violation.AViolationFileScanner#
	 * processViolationInfoBean(java.sql.Connection,
	 * com.its.core.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public int processViolationInfoBean(Connection conn,ViolationInfoBean violationInfoBean) {
		if (StringHelper.isEmpty(this.getInsertSql())) {
			log.warn("未定义'insert_sql'参数！");
			return 0;
		}
		int result = -1;
		PreparedStatement preStatement = null;
		try {
			// id,violate_time,road_id,device_id,direction_code,line,speed,limit_speed,create_time,status,plate,plate_type_id,image_path_1,image_path_2,image_path_3,image_path_4,violate_type
			preStatement = conn.prepareStatement(this.getInsertSql());
			preStatement.setLong(1, (long) SequenceFactory.getInstance().getViolateRecordTempSequence());
			preStatement.setTimestamp(2, new Timestamp(violationInfoBean.getViolateTime().getTime()));
			preStatement.setString(3, violationInfoBean.getDeviceInfoBean().getRoadId());
			preStatement.setString(4, violationInfoBean.getDeviceId());
			preStatement.setString(5, violationInfoBean.getDirectionNo());
			preStatement.setString(6, violationInfoBean.getLine());
			preStatement.setString(7, "000");
			preStatement.setString(8, "000");
			preStatement.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));
			preStatement.setString(10, violationInfoBean.getPlateNo());

			String plateTypeId = SystemConstant.getInstance().getPlateTypeIdByColor(violationInfoBean.getPlateColor());
			if (StringHelper.isEmpty(plateTypeId)) {
				// 缺省：小型汽车
				plateTypeId = SystemConstant.getInstance().PLATE_TYPE_ID_ROADLOUSE;
			} else {
				plateTypeId = plateTypeId.trim();
			}

			preStatement.setString(11, plateTypeId);

			String pathPrefix = this.getHttpUrlPrefix() + violationInfoBean.getDeviceId()+ "/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd") + "/";
			int i = 0;
			for (; i < violationInfoBean.getImageFiles().length; i++) {
				String imageFileName = violationInfoBean.getImageFiles()[i];
				if (StringHelper.isNotEmpty(imageFileName)) {
					preStatement.setString(12 + i, pathPrefix + imageFileName);
				} else {
					preStatement.setString(12 + i, null);
				}
			}
			for (; i < 4; i++) {
				preStatement.setString(12 + i, null);
			}

			if (StringHelper.isNotEmpty(violationInfoBean.getViolateType()))preStatement.setString(16, pathPrefix + violationInfoBean.getViolateType());
			else
				preStatement.setString(16, "00");

			preStatement.execute();
			result = 0;
		} catch (Exception ex) {
			log.error("数据入库失败：" + ex.getMessage(), ex);
			result = -1;
		} finally {
			if (preStatement != null) {
				try {
					preStatement.close();
				} catch (Exception ex) {
				}
			}
		}
		return result;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.its.core.module.filescan.violation.AViolationFileScanner#
	 * postProcessViolationInfoBean
	 * (com.its.core.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public boolean postProcessViolationInfoBean(ViolationInfoBean violationInfoBean) {
		boolean backupSuccess = true;
		try {
			String backDir = this.getScannerParam().getBackupDir()+ "/"	+ violationInfoBean.getDeviceId()+ "/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd");
			FileHelper.createDir(backDir);

			for (int i = 0; i < violationInfoBean.getImageFiles().length; i++) {
				String imageFileName = violationInfoBean.getImageFiles()[i];
				if (StringHelper.isNotEmpty(imageFileName)) {
					String sourceFileName = violationInfoBean.getFileDir()+ "/" + imageFileName;
					String targetFileName = backDir + "/" + imageFileName;
					log.debug("开始移动文件：" + sourceFileName + " 到 "	+ targetFileName);
					FileHelper.moveFile(sourceFileName, targetFileName);
				}
			}

			if (StringHelper.isNotEmpty(violationInfoBean.getVideoFile())) {
				String sourceFileName = violationInfoBean.getFileDir() + "/"+ violationInfoBean.getVideoFile();
				String targetFileName = backDir + "/"+ violationInfoBean.getVideoFile();
				FileHelper.moveFile(sourceFileName, targetFileName);
			}
		} catch (Exception ex) {
			backupSuccess = false;
			log.error(ex.getMessage(), ex);
		}
		return backupSuccess;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seecom.its.core.module.filescan.violation.AViolationFileScanner#
	 * postRestoreViolationInfoBean
	 * (com.its.core.module.filescan.violation.ViolationInfoBean)
	 */
	@Override
	public boolean postRestoreViolationInfoBean(ViolationInfoBean violationInfoBean) {
		// TODO Auto-generated method stub
		return true;
	}
	
	public String getInsertSql() {
		return insertSql;
	}

	public void setInsertSql(String insertSql) {
		this.insertSql = insertSql;
	}

	public String getHttpUrlPrefix() {
		return httpUrlPrefix;
	}

	public void setHttpUrlPrefix(String httpUrlPrefix) {
		this.httpUrlPrefix = httpUrlPrefix;
	}

}
