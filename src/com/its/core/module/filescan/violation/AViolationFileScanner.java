/**
 * 
 */
package com.its.core.module.filescan.violation;

import java.awt.Color;
import java.io.File;
import java.io.RandomAccessFile;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceDirectionBean;
import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.common.sequence.SequenceFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.filescan.AFileScanner;
import com.its.core.util.ColorHelper;
import com.its.core.util.CryptoHelper;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.FilenameFilterByPostfix;
import com.its.core.util.FilenameFilterByPostfixAndSize;
import com.its.core.util.ImageHelper;
import com.its.core.util.PropertiesHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-12-13 上午08:21:18
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public abstract class AViolationFileScanner extends AFileScanner {
	private static final Log log = LogFactory.getLog(AViolationFileScanner.class);
	
	//扫描器参数
	private ScannerParamBean scannerParam = new ScannerParamBean();
	
	//配置各地项目的本地属性
	public abstract void configureLocalProperties(XMLProperties props, String propertiesPrefix, int no);

	//处理违法信息 return 0:表示成功，其它值：表示失败
	public abstract int processViolationInfoBean(Connection conn, ViolationInfoBean violationInfoBean);

	//处理违法信息成功后的后期处理
	public abstract boolean postProcessViolationInfoBean(ViolationInfoBean violationInfoBean);
	
	//从备份文件中恢复违法信息成功后的后期处理
	public abstract boolean postRestoreViolationInfoBean(ViolationInfoBean violationInfoBean);

	/* (non-Javadoc)
	 * @see com.its.core.module.filescan.AFileScanner#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		scannerParam.setBackupDir(props.getProperty(propertiesPrefix, no, "violation_param.backup_dir"));
		scannerParam.setInvalidFileDir(props.getProperty(propertiesPrefix, no, "violation_param.invalid_file_dir"));	
		scannerParam.setDeleteFileDir(props.getProperty(propertiesPrefix, no, "violation_param.delete_file_dir"));
		scannerParam.setBackupDataDir(props.getProperty(propertiesPrefix,no,"violation_param.backup_data_dir"));
		scannerParam.setBackupDataEncodingFrom(props.getProperty(propertiesPrefix,no,"violation_param.backup_data_encoding.from"));
		scannerParam.setBackupDataEncodingTo(props.getProperty(propertiesPrefix,no,"violation_param.backup_data_encoding.to"));		
		scannerParam.setDelayDeleteDay(PropertiesHelper.getLong(propertiesPrefix, no, "violation_param.delay_delete_day", props, -1));
		scannerParam.setMaxNoModifyTimeAllow(PropertiesHelper.getLong(propertiesPrefix, no, "violation_param.max_no_modify_time_allow", props, scannerParam.getMaxNoModifyTimeAllow()));
		scannerParam.setDefaultWfxwCode(props.getProperty(propertiesPrefix, no, "violation_param.default_wfxw_code"));
		
		String strRequireMoveInvalidFile = props.getProperty(propertiesPrefix, no, "violation_param.require_move_invalid_file");
		if(StringHelper.isEmpty(strRequireMoveInvalidFile) || "true".equalsIgnoreCase(strRequireMoveInvalidFile.trim())){
			scannerParam.setRequireMoveInvalidFile(true);
		}
		else{
			scannerParam.setRequireMoveInvalidFile(false);
		}

		//violation_param.image
		scannerParam.setImgFileExtension(props.getProperty(propertiesPrefix, no, "violation_param.image.img_file_extension"));
		
		scannerParam.setDelayScanSecond(PropertiesHelper.getLong(propertiesPrefix, no, "violation_param.image.delay_scan_second", props, scannerParam.getDelayScanSecond()));
		scannerParam.setMinImageFileSize(PropertiesHelper.getLong(propertiesPrefix, no, "violation_param.image.min_image_file_size", props, scannerParam.getMinImageFileSize()));
		String strRequireAddWaterMark = props.getProperty(propertiesPrefix, no, "violation_param.image.require_add_watermark");
		if(StringHelper.isEmpty(strRequireAddWaterMark) || "false".equalsIgnoreCase(strRequireAddWaterMark.trim())){
			scannerParam.setRequireAddWaterMark(false);
		}
		else{
			scannerParam.setRequireAddWaterMark(true);
		}
		scannerParam.setImageCompressQuality(PropertiesHelper.getFloat(propertiesPrefix, no, "violation_param.image.image_compress_quality", props, scannerParam.getImageCompressQuality()));
		scannerParam.setImageCompressGeSize(PropertiesHelper.getLong(propertiesPrefix, no, "violation_param.image.image_compress_ge_size", props, scannerParam.getImageCompressGeSize()));
		
		//图片目标宽度（如果为空或小于等于０，表示不改变）
		String strImageWidth = props.getProperty(propertiesPrefix, no, "violation_param.image.img_width");		
		if(StringHelper.isNotEmpty(strImageWidth)){
			scannerParam.setImageWidth(Integer.parseInt(strImageWidth));
		}
		else{
			scannerParam.setImageWidth(-1);
		}
		
		//图片目标高度（如果为空或小于等于０，表示不改变）
		String strImageHeight = props.getProperty(propertiesPrefix, no, "violation_param.image.img_height");		
		if(StringHelper.isNotEmpty(strImageHeight)){
			scannerParam.setImageHeight(Integer.parseInt(strImageHeight));
		}
		else{
			scannerParam.setImageHeight(-1);
		}
		
		//是否添加MD5数字水印证码
		String strMd5Verify = props.getProperty(propertiesPrefix, no, "violation_param.image.add_md5_verify");		
		scannerParam.setAddMd5Verify(StringHelper.getBoolean(strMd5Verify));
		
		//水印标志
		scannerParam.setWaterMarkPosition(props.getProperty(propertiesPrefix, no, "violation_param.watermark.position"));
		scannerParam.setWaterMarkFontSize(PropertiesHelper.getInt(propertiesPrefix,no,"violation_param.watermark.font_size",props,scannerParam.getWaterMarkFontSize()));
		scannerParam.setWaterMarkFontHeight(PropertiesHelper.getInt(propertiesPrefix,no,"violation_param.watermark.font_height",props,scannerParam.getWaterMarkFontHeight()));
		scannerParam.setWaterMarkLeftMargin(PropertiesHelper.getInt(propertiesPrefix,no,"violation_param.watermark.left_margin",props,scannerParam.getWaterMarkLeftMargin()));
		scannerParam.setWaterMarkTopMargin(PropertiesHelper.getInt(propertiesPrefix,no,"violation_param.watermark.top_margin",props,scannerParam.getWaterMarkTopMargin()));
		scannerParam.setWaterMarkBgHeight(PropertiesHelper.getInt(propertiesPrefix,no,"violation_param.watermark.bg_height",props,scannerParam.getWaterMarkBgHeight()));
		Color fontColor = ColorHelper.getColor(props.getProperty(propertiesPrefix, no, "violation_param.watermark.font_color"));
		if(fontColor!=null) scannerParam.setWaterMarkFontColor(fontColor);		
		Color bgColor = ColorHelper.getColor(props.getProperty(propertiesPrefix, no, "violation_param.watermark.bg_color"));
		if(bgColor!=null) scannerParam.setWaterMarkBgColor(bgColor);
		
		//violation_param.video
		String strRequireVideo = props.getProperty(propertiesPrefix, no, "violation_param.video.require_video");
		if(StringHelper.isEmpty(strRequireVideo) || "true".equalsIgnoreCase(strRequireVideo.trim())){
			scannerParam.setRequireVideo(true);
		}
		else{
			scannerParam.setRequireVideo(false);
		}	
		
		String strDirectDeleteVideo = props.getProperty(propertiesPrefix, no, "violation_param.video.direct_delete_video");
		if(StringHelper.isEmpty(strDirectDeleteVideo) || "false".equalsIgnoreCase(strDirectDeleteVideo.trim())){
			scannerParam.setDirectDeleteVideo(false);
		}
		else{
			scannerParam.setDirectDeleteVideo(true);
		}			
		
		scannerParam.setVideoFileExtension(props.getProperty(propertiesPrefix, no, "violation_param.video.video_file_extension"));
		
		scannerParam.setMinVideoFileSize(PropertiesHelper.getLong(propertiesPrefix, no, "violation_param.video.min_video_file_size", props, scannerParam.getMinVideoFileSize()));
		
		scannerParam.setCheckRepeatedViolateRecordTempSql(props.getProperty(propertiesPrefix, no, "violation_param.check_repeated.violate_record_temp"));
		scannerParam.setCheckRepeatedViolateRecordSql(props.getProperty(propertiesPrefix, no, "violation_param.check_repeated.violate_record"));

		//按日统计T_ITS_VIOLATE_RECORD_TEMP违法信息到表t_its_traffic_day_stat
		scannerParam.setStatTrafficDayCheckExistSql(props.getProperty(propertiesPrefix, no, "violation_param.day_stat_sql.check_exist"));
		scannerParam.setStatTrafficDayInsertSql(props.getProperty(propertiesPrefix, no, "violation_param.day_stat_sql.insert"));
		scannerParam.setStatTrafficDayUpdateSql(props.getProperty(propertiesPrefix, no, "violation_param.day_stat_sql.update"));
		
		//按24小时统计T_ITS_VIOLATE_RECORD_TEMP违法信息到表t_its_traffic_hour_stat
		scannerParam.setStatTrafficHourCheckExistSql(props.getProperty(propertiesPrefix, no, "violation_param.hour_stat_sql.check_exist"));
		scannerParam.setStatTrafficHourInsertSql(props.getProperty(propertiesPrefix, no, "violation_param.hour_stat_sql.insert"));
		scannerParam.setStatTrafficHourUpdateSql(props.getProperty(propertiesPrefix, no, "violation_param.hour_stat_sql.update"));
		
		try {
			FileHelper.createDir(scannerParam.getBackupDir());
			FileHelper.createDir(scannerParam.getInvalidFileDir());
			FileHelper.createDir(scannerParam.getDeleteFileDir());
		} catch (Exception ex) {
			log.error(ex);
		}
		
		this.configureLocalProperties(props, propertiesPrefix, no);

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.filescan.AFileScanner#execute()
	 */
	@Override
	public void execute() {
		if(scannerParam.isDirectDeleteVideo() && !scannerParam.isRequireVideo()){
			int cleanNum = this.cleanVideoFile();
			log.debug("共清理视频文件："+cleanNum+"个！");
		}
		
		//清理旧文件
		if (scannerParam.getDelayDeleteDay() > 0L) {			
			this.cleanOldFile();
		}			
		
		Connection conn = null;
		boolean isUseDbConn = SystemConstant.getInstance().isUseDbConnection();
		boolean hasRefreshed = false;
		try {
			boolean hasConn = false;
			if(isUseDbConn){
				try {
					conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
					hasConn = true;
				} catch (Exception ex) {
					log.error("连接失败：" + ex.getMessage(), ex);
					hasRefreshed = true;
					ConnectionProviderFactory.getInstance().refreshConnProperties();
				}
			}
			
			long startTime = System.currentTimeMillis();
			
			int dirNum = this.getScanDirs().length;
			int scanFileCount = 0;
			for(int i=0;i<dirNum && scanFileCount<this.getMaxScanFileNum();i++){
				String strScanDir = this.getScanDirs()[i];
				log.debug("开始扫描闯红灯文件目录：" + strScanDir);
				File scanDir = new File(strScanDir);
				if (!scanDir.exists() || !scanDir.isDirectory()) {
					log.error("闯红灯文件目录：" + scanDir + " 不存在！");
					continue;
				}	
				
				this.cleanSmallFile(scanDir);
				
				scanFileCount = this.processorDir(scanDir,scanFileCount,conn,hasConn,isUseDbConn);
			}
			
			long currentTime = System.currentTimeMillis();
			log.info("本次扫描共耗时：" + ((currentTime - startTime) / 1000F) + "秒，处理违法记录：" + scanFileCount + "条！");
			
			//将先前入库失败的备份数据再做一次入库操作！
			try {
				if(!isUseDbConn){
					this.restoreBackupData(conn,isUseDbConn);
				}
				else if(hasConn){
					this.restoreBackupData(conn,isUseDbConn);
				}				
			} catch (Exception ex) {}				
		} catch (SQLException sqlEx) {
			ConnectionProviderFactory.getInstance().refreshConnProperties();
			hasRefreshed = true;
			log.error(sqlEx);
		} catch (Exception ex) {
			log.error(ex.getMessage(), ex);
		} finally {
			//this.setRunning(false);
			if (conn != null) {
				if (hasRefreshed) {
					try {
						conn.close();
					} catch (Exception ex1) {
					}
				} else {
					try {
						ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
					} catch (Exception ex1) {
					}
				}
			}
		}

	}
	
	/**
	 * 处理单个扫描目录，递归扫描子目录
	 * @param scanDir
	 * @param scanFileCount
	 * @param conn
	 * @param hasConn
	 * @return
	 * @throws Exception
	 */
	private int processorDir(File scanDir,int scanFileCount,Connection conn,boolean hasConn,boolean isUseDbConn) throws Exception{
		log.debug("开始扫描文件目录：" + scanDir.getAbsolutePath());
		if (!scanDir.exists() || !scanDir.isDirectory()) {
			log.error("文件目录：" + scanDir + " 不存在！");
			return scanFileCount;
		}

		File[] jpgFileArr = scanDir.listFiles(new FilenameFilterByPostfixAndSize(scannerParam.getImgFileExtension(), true, true, scannerParam.getMinImageFileSize()));
		int fileNum = jpgFileArr.length;
		for (int j = 0; j < fileNum && scanFileCount<this.getMaxScanFileNum(); j++) {
			File jpgFile = jpgFileArr[j];
			if(jpgFile.isDirectory()){
				scanFileCount = this.processorDir(jpgFile, scanFileCount,conn,hasConn,isUseDbConn);
			}else{
				log.debug("扫描文件："+jpgFile.getAbsolutePath());		
				long lastModified = jpgFile.lastModified();
				if ((System.currentTimeMillis() - lastModified) < (scannerParam.getDelayScanSecond()*1000)){
					log.debug("图片最后修改时间小于规定时限："+scannerParam.getDelayScanSecond()+"秒，需要等待！");	
					continue;
				} 
//				else {
//					String targetFileDir = scannerParam.getInvalidFileDir() + DateHelper.dateToString(new Date(), "yyyyMMdd");
//					FileHelper.createDir(targetFileDir);
//							
//					String targetFileName = targetFileDir + "/" + jpgFile.getName();
//					boolean moveSuccess = FileHelper.moveFile(jpgFile, new File(targetFileName));
//					if (moveSuccess)
//						log.info("超过规定时间未找到匹配的图片，文件：" + jpgFile.getName() + " 转移成功！");
//					else
//						log.info("超过规定时间未找到匹配的图片，文件：" + jpgFile.getName() + " 转移失败，文件可能正在使用中！");
//				}
				
				//通过文件名解析违法信息
				ViolationInfoBean violationInfoBean = null;
				try {
					violationInfoBean = this.parseViolationInfo(scanDir.getAbsolutePath(), jpgFile);
				} catch (VideoFileNotFoundException vfnfExce) {
					log.warn("文件：" + jpgFile.getName() + " 未找到对应的视频文件，未入库！");
					
					if ((System.currentTimeMillis() - lastModified) > scannerParam.getMaxNoModifyTimeAllow() * 1000) {
						String targetFileDir = scannerParam.getInvalidFileDir() + DateHelper.dateToString(new Date(), "yyyyMMdd");
						FileHelper.createDir(targetFileDir);
								
						String targetFileName = targetFileDir + "/" + jpgFile.getName();
						boolean moveSuccess = FileHelper.moveFile(jpgFile, new File(targetFileName));
						if (moveSuccess)
							log.warn("文件：" + jpgFile.getName() + " 转移成功！");
						else
							log.warn("文件：" + jpgFile.getName() + " 转移失败，文件可能正在使用中！");						
					}			
					continue;
				} catch (Exception ex) {
					log.error("解析文件名'"+jpgFile.getName()+"'失败："+ex.getMessage(),ex);
					if(scannerParam.isRequireMoveInvalidFile()){
						try {
							String targetFileDir = scannerParam.getInvalidFileDir() + DateHelper.dateToString(new Date(), "yyyyMMdd");
							FileHelper.createDir(targetFileDir);
							String targetFileName = targetFileDir + "/" + jpgFile.getName();
							log.warn("将该文件转移到：" + targetFileName, ex);
							boolean moveSuccess = FileHelper.moveFile(jpgFile, new File(targetFileName));
							if (moveSuccess)
								log.warn("文件：" + jpgFile.getName() + " 转移成功！");
							else
								log.warn("文件：" + jpgFile.getName() + " 转移失败，文件可能正在使用中！");
						} catch (Exception ex2) {
							log.error(ex2);
						}
					}
					continue;
				}
				
				log.debug("violationInfoBean="+violationInfoBean);
				if(violationInfoBean==null) continue;
				
				//设备ID转换
//				String targetId = DeviceInfoConvertFactory.getInstance().getTargetDeviceId(violationInfoBean.getDeviceId(), violationInfoBean.getDirectionNo());
//				if(StringHelper.isNotEmpty(targetId)) violationInfoBean.setDeviceId(targetId);
				
				violationInfoBean.setFileDir(scanDir.getAbsolutePath()+"/");
				
				if(!this.verifyVideoFile(violationInfoBean)){
					continue;
				}	
				
				Object obj = DeviceInfoLoaderFactory.getInstance().getDeviceMap().get(violationInfoBean.getDeviceId());
				if (obj == null) {
					log.warn("未找到设备ID：" + violationInfoBean.getDeviceId() + "对应的设备信息！");
					
					//需要重新装载设备信息
					DeviceInfoLoaderFactory.getInstance().setRequireReload(true);					
					
					//对于无效文件，有可能是因为设备信息尚未配置而造成的，故设备信息配置完成后，需要将无效文件COPY至上传目录重新扫描
					if((!isUseDbConn || hasConn) && scannerParam.isRequireMoveInvalidFile()){
						//转移无效（设备编号不正确）文件到无效文件目录
						try {
							String targetFileDir = scannerParam.getInvalidFileDir() + DateHelper.dateToString(new Date(), "yyyyMMdd");
							FileHelper.createDir(targetFileDir);
							String sourceFileName,targetFileName;
							for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
								String imageFileName = violationInfoBean.getImageFiles()[i];
								if(StringHelper.isNotEmpty(imageFileName)){
									sourceFileName = violationInfoBean.getFileDir() + "/" + imageFileName;
									targetFileName = targetFileDir + "/" + imageFileName;
									log.debug("开始移动文件：" + sourceFileName + " 到 " + targetFileName);
									FileHelper.moveFile(sourceFileName, targetFileName);					
								}
							}
							
							if(StringHelper.isNotEmpty(violationInfoBean.getVideoFile())){
								sourceFileName = violationInfoBean.getFileDir() + "/" + violationInfoBean.getVideoFile();
								targetFileName = targetFileDir + "/" + violationInfoBean.getVideoFile();		
								log.debug("开始移动文件：" + sourceFileName + " 到 " + targetFileName);
								FileHelper.moveFile(sourceFileName, targetFileName);
							}
						} catch (Exception ex2) {
							log.error(ex2);
						}
					}		
					continue;
				}
				
				DeviceInfoBean dib = (DeviceInfoBean) obj;
				violationInfoBean.setDeviceInfoBean(dib);
				
				if(scannerParam.getImageCompressQuality()>0){
					this.compress(violationInfoBean);
				}
				
				if(scannerParam.getImageWidth()>0 && scannerParam.getImageHeight()>0){
					this.changeSize(violationInfoBean);
					//ImageHelper.changeSize(jpgFile, scannerParam.getImageWidth(), scannerParam.getImageHeight());
				}
				
				if(scannerParam.isRequireAddWaterMark()){
					this.createMark(violationInfoBean);
				}
				
				//添加MD5校验码
				if(scannerParam.isAddMd5Verify()){
					for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
						String imageFileName = violationInfoBean.getImageFiles()[i];
						if(StringHelper.isNotEmpty(imageFileName)){
							String sourceFileName = violationInfoBean.getFileDir() + "/" + imageFileName;
							byte[] resultFileByte = FileHelper.getBytes(sourceFileName);
							byte[] md5 = CryptoHelper.getUniHzImageEncryptString(resultFileByte).getBytes();
							List<byte[]> byteList = new ArrayList<byte[]>();
							byteList.add(resultFileByte);
							byteList.add(md5);
							FileHelper.writeFile(sourceFileName,byteList);
						}
					}
										
				}
				
				if(isUseDbConn) this.process(conn, hasConn, violationInfoBean);
				else this.process(violationInfoBean);
				
				//log.debug("返回码："+returnCode);
				scanFileCount++;
				
				try {
					Thread.sleep(10);
				} catch (Exception ex) {}
			}
		}				
			
		return scanFileCount;	
	}	
	
	/**
	 * 清理不符合最小尺寸的文件
	 */
	protected void cleanSmallFile(File scanDir){
		if (!scanDir.exists() || !scanDir.isDirectory()) {
			return;
		}		
		log.debug("开始清理目录'"+scanDir.getAbsolutePath()+"'下不符合大小的文件！");
		File[] fileArr = scanDir.listFiles(new FilenameFilterByPostfixAndSize(scannerParam.getImgFileExtension(), true, true, 0L, scannerParam.getMinImageFileSize()));
		int fileNum = fileArr.length;
		for (int i = 0; i < fileNum; i++) {
			File file = fileArr[i];
			if(file.isDirectory()){
				this.cleanSmallFile(file);
			}else{
				//log.debug("扫描文件："+jpgFile.getAbsolutePath());		
				
				//通过文件名解析违法信息
				ViolationInfoBean violationInfoBean = null;
				try {
					violationInfoBean = this.parseViolationInfo(scanDir.getAbsolutePath(), file);
					if(violationInfoBean!=null) violationInfoBean.setFileDir(scanDir.getAbsolutePath()+"/");
				} catch (Exception ex) {
					log.error(ex.getMessage(),ex);
				}
				
				//最小文件超过时限
				if ((System.currentTimeMillis() - file.lastModified()) > scannerParam.getMaxNoModifyTimeAllow() * 1000) {
					if(violationInfoBean!=null){
						for(int j=0;j<violationInfoBean.getImageFiles().length;j++){
							String imageFileName = violationInfoBean.getImageFiles()[j];
							if(StringHelper.isNotEmpty(imageFileName)){
								imageFileName = violationInfoBean.getFileDir() + imageFileName;								
								log.debug("删除文件：" + imageFileName);
								try {
									FileHelper.delFile(new File(imageFileName));
								} catch (Exception e) {
									log.error(e);
								}					
							}
							if(StringHelper.isNotEmpty(violationInfoBean.getVideoFile())){
								String videoFileName = violationInfoBean.getFileDir() + violationInfoBean.getVideoFile();
								log.debug("删除文件：" + videoFileName);
								try {
									FileHelper.delFile(new File(videoFileName));
								} catch (Exception e) {
									log.error(e);
								}	
							}					
						}					
					}
					else{
						try {
							log.debug("删除文件：" + file.getAbsolutePath());
							FileHelper.delFile(file);
						} catch (Exception e) {
							log.error(e);
						}						
					}
				}
			}
		}
	}
	
	/**
	 * 检测当前图片是否为重复记录（已存在于T_ITS_VIOLATE_RECORD_TEMP或T_ITS_VIOLATE_RECORD中）
	 * @param violationInfoBean
	 * @return
	 */
	protected boolean isRepeatedData(Connection conn,ViolationInfoBean violationInfoBean) throws Exception{
		boolean repeated = this.isRepeatedDataInTempPic(conn, violationInfoBean);
		
		if(!repeated){
			repeated = this.isRepeatedDataInFxcRecord(conn, violationInfoBean);
		}
		
		return repeated;
	}
	
	/**
	 * 按日统计T_ITS_VIOLATE_RECORD违法信息到表T_ITS_TRAFFIC_DAY_STAT
	 * @param conn
	 * @param violationInfoBean
	 * @return
	 */
	protected boolean statTrafficDay(Connection conn,ViolationInfoBean violationInfoBean){
		boolean success = true;
		PreparedStatement preStatement = null;
		ResultSet resultSet = null;		
		String checkExistSql = this.getScannerParam().getStatTrafficDayCheckExistSql();
		if(StringHelper.isNotEmpty(checkExistSql)){
			try{
//				log.debug(checkExistSql);
				
				//是否有车道参数
				boolean hasLane = false;
				if(checkExistSql.toUpperCase().indexOf("LANE_NO")!=-1){
					hasLane = true;
				}
				
				Timestamp violateDay = new Timestamp(DateHelper.parseDateString(DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd"),"yyyyMMdd").getTime());
				long statId = -1L;
				long roadId = Long.valueOf(violationInfoBean.getDeviceInfoBean().getRoadId());
				String directionCode = violationInfoBean.getDirectionNo();
				if(StringHelper.isEmpty(directionCode)){
					List<DeviceDirectionBean> ddbList = violationInfoBean.getDeviceInfoBean().getDirectionList();
					if(ddbList!=null && ddbList.size()>0){
						DeviceDirectionBean ddb = (DeviceDirectionBean)ddbList.get(0);
						directionCode = ddb.getDirectionCode();
					}
				}
				//select id from T_ITS_TRAFFIC_DAY_STAT where ROAD_ID=? and DEVICE_ID=? and DIRECTION_CODE=? and VIOLATE_DAY=?
				preStatement = conn.prepareStatement(checkExistSql);
				preStatement.setLong(1,roadId);	
				preStatement.setString(2, violationInfoBean.getDeviceId());
				preStatement.setString(3, directionCode);		
				if(hasLane){
					preStatement.setString(4, violationInfoBean.getLine());
					preStatement.setTimestamp(5, violateDay);
				}
				else{
					preStatement.setTimestamp(4, violateDay);
				}
				
				resultSet = preStatement.executeQuery();
				if(resultSet.next()){
					statId = resultSet.getLong("id");					
				}
				DatabaseHelper.close(resultSet, preStatement);
				if(statId==-1L){
					//新增一条记录:insert into T_ITS_TRAFFIC_DAY_STAT (ID,ROAD_ID,DEVICE_ID,DIRECTION_CODE,VIOLATE_DAY,VIOLATE_SUM) values (?,?,?,?,?,?)
					statId = (long)SequenceFactory.getInstance().getStatTrafficDayStatSequence();
					preStatement = conn.prepareStatement(this.getScannerParam().getStatTrafficDayInsertSql());
					preStatement.setLong(1,statId);	
					preStatement.setLong(2,roadId);	
					preStatement.setString(3, violationInfoBean.getDeviceId());
					preStatement.setString(4, directionCode);	
					if(hasLane){
						preStatement.setString(5, violationInfoBean.getLine());
						preStatement.setTimestamp(6, violateDay);	
						preStatement.setLong(7, 1L);							
					}
					else{
						preStatement.setTimestamp(5, violateDay);	
						preStatement.setLong(6, 1L);	
					}

//					log.debug("statId = "+statId+" created!");
				}
				else{
					//更新记录:update T_ITS_TRAFFIC_DAY_STAT set VIOLATE_SUM=VIOLATE_SUM+1 where ID=?
					preStatement = conn.prepareStatement(this.getScannerParam().getStatTrafficDayUpdateSql());
					preStatement.setLong(1,statId);	
//					log.debug("statId = "+statId+" updated!");
				}
				preStatement.executeUpdate();
			}catch(Exception ex){
				success = false;
				log.error(ex.getMessage(),ex);
			}finally{
				DatabaseHelper.close(resultSet, preStatement);
			}
		}
		
		if(StringHelper.isNotEmpty(this.getScannerParam().getStatTrafficHourCheckExistSql())){
			try{
//				log.debug(this.getScannerParam().getStatTrafficHourCheckExistSql());				
				
				String violateHour = DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyy-MM-dd hh");
				long id = -1L;
				long roadId = Long.valueOf(violationInfoBean.getDeviceInfoBean().getRoadId());
				String directionCode = violationInfoBean.getDirectionNo();
				if(StringHelper.isEmpty(directionCode)){
					List<DeviceDirectionBean> ddbList = violationInfoBean.getDeviceInfoBean().getDirectionList();
					if(ddbList!=null && ddbList.size()>0){
						DeviceDirectionBean ddb = (DeviceDirectionBean)ddbList.get(0);
						directionCode = ddb.getDirectionCode();
					}
				}
				//select id from T_ITS_TRAFFIC_HOUR_STAT where ROAD_ID=? and DEVICE_ID=? and DIRECTION_CODE=? and LANE_NO=? and RELEASE_TIME=?
				preStatement = conn.prepareStatement(this.getScannerParam().getStatTrafficHourCheckExistSql());
				preStatement.setLong(1,roadId);	
				preStatement.setString(2, violationInfoBean.getDeviceId());
				preStatement.setString(3, directionCode);						
				preStatement.setString(4, violationInfoBean.getLine());
				preStatement.setString(5, violateHour);				
				
				resultSet = preStatement.executeQuery();
				if(resultSet.next()){
					id = resultSet.getLong("id");					
				}
				DatabaseHelper.close(resultSet, preStatement);
				if(id==-1L){
					//新增一条记录:insert into T_ITS_TRAFFIC_HOUR_STAT (ID,ROAD_ID,DEVICE_ID,DIRECTION_CODE,LANE_NO,RELEASE_TIME,FLUX) values (?,?,?,?,?,?,?)
					id = (long)SequenceFactory.getInstance().getStatTrafficHourStatSequence();
					preStatement = conn.prepareStatement(this.getScannerParam().getStatTrafficHourInsertSql());
					preStatement.setLong(1,id);	
					preStatement.setLong(2,roadId);	
					preStatement.setString(3, violationInfoBean.getDeviceId());
					preStatement.setString(4, directionCode);	
					preStatement.setString(5, violationInfoBean.getLine());
					preStatement.setString(6, violateHour);	
					preStatement.setLong(7, 1L);						
					

//					log.debug("id = "+id+" created!");
				}
				else{
					//更新记录:update T_ITS_TRAFFIC_HOUR_STAT set FLUX=FLUX+1 where ID=?
					preStatement = conn.prepareStatement(this.getScannerParam().getStatTrafficHourUpdateSql());
					preStatement.setLong(1,id);	
//					log.debug("id = "+id+" updated!");
				}
				preStatement.executeUpdate();
			}catch(Exception ex){
				success = false;
				log.error(ex.getMessage(),ex);
			}finally{
				DatabaseHelper.close(resultSet, preStatement);
			}
		}
		return success;
	}
	
	/**
	 * 是否已存在于T_ITS_VIOLATE_RECORD_TEMP表中
	 * @param conn
	 * @param violationInfoBean
	 * @return
	 * @throws Exception
	 */
	protected boolean isRepeatedDataInTempPic(Connection conn,ViolationInfoBean violationInfoBean) throws Exception{
		boolean repeated = false;
		String checkRepeatedTempPicSql = this.getScannerParam().getCheckRepeatedViolateRecordTempSql();
		if(StringHelper.isNotEmpty(checkRepeatedTempPicSql)){
			log.debug(checkRepeatedTempPicSql);
			PreparedStatement preStatement = null;
			ResultSet resultSet = null;
			try{
				preStatement = conn.prepareStatement(checkRepeatedTempPicSql);
				preStatement.setString(1, "%/"+violationInfoBean.getImageFiles()[0]);				
				resultSet = preStatement.executeQuery();
				if(resultSet.next()){
					String id = resultSet.getString("id");
					log.info(violationInfoBean.getImageFiles()[0]+" 重复（T_ITS_VIOLATE_RECORD_TEMP:ID="+id+"）！");
					repeated = true;
				}
				DatabaseHelper.close(resultSet, preStatement);
			}catch(Exception ex){
				log.error(ex.getMessage(),ex);
				throw ex;
			}finally{
				DatabaseHelper.close(resultSet, preStatement);
			}
		}		
		
		return repeated;
	}	
	
	/**
	 * 是否已存在于T_ITS_VIOLATE_RECORD表中
	 * @param conn
	 * @param violationInfoBean
	 * @return
	 * @throws Exception
	 */
	protected boolean isRepeatedDataInFxcRecord(Connection conn,ViolationInfoBean violationInfoBean) throws Exception{
		boolean repeated = false;
		String checkRepeatedFxcRecordSql = this.getScannerParam().getCheckRepeatedViolateRecordSql();
		if(StringHelper.isNotEmpty(checkRepeatedFxcRecordSql)){
			log.debug(checkRepeatedFxcRecordSql);
			PreparedStatement preStatement = null;
			ResultSet resultSet = null;
			try{
				preStatement = conn.prepareStatement(checkRepeatedFxcRecordSql);
				preStatement.setString(1, "%/"+violationInfoBean.getImageFiles()[0]);				
				resultSet = preStatement.executeQuery();
				if(resultSet.next()){
					String id = resultSet.getString("id");
					log.info(violationInfoBean.getImageFiles()[0]+" 重复（T_ITS_VIOLATE_RECORD:ID="+id+"）！");
					repeated = true;
				}
				DatabaseHelper.close(resultSet, preStatement);
			}catch(Exception ex){
				log.error(ex.getMessage(),ex);
				throw ex;
			}finally{
				DatabaseHelper.close(resultSet, preStatement);
			}				
		}
		return repeated;
	}		
	
	/**
	 * 检查重复
	 * @param violationInfoBean
	 * @return
	 * @throws Exception
	 */
	protected boolean isRepeatedData(ViolationInfoBean violationInfoBean) throws Exception{
		return false;
	}
	
	/**
	 * 处理重复数据 (缺省：删除)
	 * @param violationInfoBean
	 */
	protected void processRepeatedData(ViolationInfoBean violationInfoBean){
		String fileName = null;
		for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
			String imageFileName = violationInfoBean.getImageFiles()[i];
			if(StringHelper.isNotEmpty(imageFileName)){
				fileName = violationInfoBean.getFileDir() + "/" + imageFileName;
				log.debug("删除重复文件：" + fileName);
				try {
					FileHelper.delFile(new File(fileName));
				} catch (Exception e) {
					log.error(e);
				}					
			}
		}
		
		if(StringHelper.isNotEmpty(violationInfoBean.getVideoFile())){
			fileName = violationInfoBean.getFileDir() + "/" + violationInfoBean.getVideoFile();
			log.debug("删除重复文件：" + fileName);
			try {
				FileHelper.delFile(new File(fileName));
			} catch (Exception e) {
				log.error(e);
			}		
		}		
	}
	
	/**
	 * 获取文件名中ID字段的值
	 * @param fileName
	 * @return
	 */
	protected String getFileNameIdStr(String fileName) {
		int idIndex = fileName.indexOf("ID");
		String idStr = fileName.substring(idIndex + 2, idIndex + 6);
		return idStr;
	}	
	
	/**
	 * 根据图片文件，分析图片文件名称的格式，获取对应的视频文件；
	 * @param imageFile
	 * @return
	 */
	protected String getVideoFileName(String jpgOriFileName,String scanDir) {
		File dirScan = new File(scanDir);
		String prefix = jpgOriFileName;
		String postfix = scannerParam.getVideoFileExtension();
		log.debug(prefix+"\t"+postfix);
		File videoFileArr[] = dirScan.listFiles(new FilenameFilterByPostfix(prefix,postfix, true));
		if (videoFileArr != null) {
			int len = videoFileArr.length;
			if(len>0) return videoFileArr[0].getName();
		}
		return null;
	}
	
	/**
	 * 根据文件名解析违法信息
	 * @param scanDir
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	protected ViolationInfoBean parseViolationInfo(String scanDir,File imageFile) throws Exception{
		String oriFileName = imageFile.getName();
		String fileName = oriFileName.trim().toUpperCase();
		
		//卡口全景图片不扫描，通过特写图片来完成匹配 (需要把区间测速图片排除)
		if((fileName.endsWith(".P.JPG") || fileName.endsWith(".P.K.JPG")) && !fileName.endsWith(".F.P.K.JPG")){
			return null;
		}
		
		ViolationInfoBean bean = new ViolationInfoBean();
		
		int deviceIndex = fileName.indexOf("R") + 1;
		String deviceId = fileName.substring(deviceIndex, deviceIndex + 5);
		bean.setDeviceId(deviceId);
		
		int timeIndex = fileName.indexOf("T") + 1;
		String timeStr = fileName.substring(timeIndex, timeIndex + 14);
		Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(timeStr);
		bean.setViolateTime(date);
		
		String line = "00";
		if(fileName.indexOf("L")!=-1){
			int lineIndex = fileName.indexOf("L") + 1;
			line = fileName.substring(lineIndex, lineIndex + 2);			
		}
		bean.setLine(line);	
		
		String[] picFiles = null;
		int idIndexOf = fileName.indexOf("ID");		
		if (idIndexOf != -1) {			
			if(!(scannerParam.isDirectDeleteVideo() && !scannerParam.isRequireVideo())){		
				String fileId = this.getFileNameIdStr(fileName);
				if (fileId != null && !fileId.trim().equals("")) {
					String yearMonthDayTime = timeStr.substring(0,12);
					String videoFileName = this.getVideoFileName(oriFileName,scanDir);
					if (scannerParam.isRequireVideo() && (videoFileName == null || videoFileName.trim().equals("")))
						throw new VideoFileNotFoundException(fileId, "未找到视频文件！");
					bean.setVideoFile(videoFileName);
				}				
			}
		} else {            
			int limitSpeedIndex = fileName.indexOf("I");
			int realSpeedIndex = fileName.indexOf("V");
			if (limitSpeedIndex != -1 && realSpeedIndex != -1 && limitSpeedIndex<realSpeedIndex) {
				String limitSpeed = fileName.substring(limitSpeedIndex + 1, limitSpeedIndex + 4);
				bean.setLimitSpeed(limitSpeed);
				String realSpeed = fileName.substring(realSpeedIndex + 1, realSpeedIndex + 4);
				bean.setSpeed(realSpeed);
				
				//如果是卡口超速图片，则使用两张图片：全景图和特写图
				if(fileName.endsWith(".F.JPG")){
					String panoramaPic = oriFileName.substring(0,oriFileName.indexOf("."))+".P.JPG";
					if((new File(scanDir+"/"+panoramaPic)).exists()){
						picFiles = new String[2];
						picFiles[0] = oriFileName;
						picFiles[1] = panoramaPic;
					}
				}
				else if(fileName.endsWith(".F.K.JPG")){
					String panoramaPic = oriFileName.substring(0,oriFileName.indexOf("."))+".P.K.JPG";
					if((new File(scanDir+"/"+panoramaPic)).exists()){
						picFiles = new String[2];
						picFiles[0] = oriFileName;
						picFiles[1] = panoramaPic;
					}					
				}
			}
		}
		
		//红灯时间
		int redLightTime = fileName.indexOf("S");
		//if(redLightTime!=-1 && (idIndexOf==-1 || redLightTime<idIndexOf)){
		if(redLightTime!=-1 && redLightTime<idIndexOf){
			String strRedLightTime = fileName.substring(redLightTime+1,redLightTime+4).toUpperCase();
			strRedLightTime = StringHelper.replace(strRedLightTime, "I", "");
			strRedLightTime = StringHelper.replace(strRedLightTime, ".", "");
			bean.setRedLightTime(strRedLightTime);
		}	
		
		//有车牌信息
		int plateFirstIndexOf = fileName.indexOf("&");
		int plateColorLastIndexOf = fileName.lastIndexOf("&");
		if (plateFirstIndexOf != -1 && plateColorLastIndexOf != -1 && plateFirstIndexOf != plateColorLastIndexOf) {
			String plateNo = fileName.substring(plateFirstIndexOf + 1, plateColorLastIndexOf);
			if (plateNo.indexOf("-") != -1)
				plateNo = plateNo.replaceAll("[-]", "");
			if (plateNo.indexOf("@") != -1)
				plateNo = plateNo.replaceAll("[@]", "");
			bean.setPlateNo(plateNo);
			bean.setPlateColor(fileName.substring(plateColorLastIndexOf + 1, plateColorLastIndexOf + 2));
		}
		
		//有方向信息
		boolean hasDirection = false;
		int directionIndexOf = fileName.indexOf("D");
		if(directionIndexOf!=-1){		 
			 if(idIndexOf==-1 || idIndexOf>directionIndexOf) hasDirection = true;
			 if(hasDirection){
				 String directionNo = fileName.substring(directionIndexOf+1,directionIndexOf+2);
				 bean.setDirectionNo(directionNo);
			 }				 
		}
		
		//如果没有方向信息，则使用设备中的方向信息
		if(!hasDirection){
			Object obj = DeviceInfoLoaderFactory.getInstance().getDeviceMap().get(deviceId);
			if (obj != null) {
				DeviceInfoBean dib = (DeviceInfoBean)obj;
				String directionNo = SystemConstant.getInstance().getDirectionNoByName(dib.getFrom());
				if(StringHelper.isNotEmpty(directionNo)){
					bean.setDirectionNo(directionNo);
				}
				else{
					//为兼容旧的设备，直接从方向表中取第一个方向代码（一般此类设备只有一个方向）
					List<DeviceDirectionBean> directionList = dib.getDirectionList();
					if(directionList!=null && directionList.size()>0){
						DeviceDirectionBean ddb = (DeviceDirectionBean)directionList.get(0);
						bean.setDirectionNo(ddb.getDirectionCode());
					}
				}	
			}			
		}
				
        //解析违法类型
		int violateTypeIndex = fileName.indexOf("A");		
		if(violateTypeIndex!=-1){
			boolean hasViolateType = false;
			if(plateFirstIndexOf!=-1 && violateTypeIndex<plateFirstIndexOf){
				hasViolateType = true;
			}
			else{			
				if(idIndexOf!=-1){
					if(violateTypeIndex<idIndexOf){
						hasViolateType = true;
					}
				}
				//没有ID段
				else{
					int dotIndexOf = fileName.indexOf(".");
					if(dotIndexOf!=-1 && violateTypeIndex<dotIndexOf){
						hasViolateType = true;
					}					
				}				
			}
			if(hasViolateType){
				String violateType = fileName.substring(violateTypeIndex+1, violateTypeIndex+2);			
				bean.setViolateType(violateType);			
			}
		}
		
		//log.debug("violate type = "+bean.getViolateType());
		if(picFiles==null){
			picFiles = new String[1];
			picFiles[0] = oriFileName;
		}		
		bean.setImageFiles(picFiles);	
		
		//填充违法行为代码
		this.fillWfxwCode(bean);
		
		return bean;		
	}
	
	/**
	 * 根据配置填充违法行为代码
	 * @param bean
	 */
	protected void fillWfxwCode(ViolationInfoBean bean) {
		String defaultWfxwCode = scannerParam.getDefaultWfxwCode();
		if(StringHelper.isNotEmpty(defaultWfxwCode)){
			//定义了不同的超速比率
			if(defaultWfxwCode.indexOf(",")!=-1 && defaultWfxwCode.indexOf("=")!=-1){
				if(StringHelper.isNotEmpty(bean.getSpeed()) && StringHelper.isNotEmpty(bean.getLimitSpeed())){
					int speed = Integer.parseInt(bean.getSpeed());
					int limitSpeed = Integer.parseInt(bean.getLimitSpeed());
					if(limitSpeed>0 && speed>limitSpeed){
						float overSpeedRate = ((speed-limitSpeed)/(float)limitSpeed)*100.0f;						
						String[] itemArr = StringHelper.splitExcludeEmpty(defaultWfxwCode, ",");
						int len = itemArr.length;
						String[] previousWfxwCodeArr = null;
						
						for(int i=0;i<len;i++){
							String[] wfxwCodeArr = StringHelper.splitExcludeEmpty(itemArr[i], "=");
							if(i==0){
								if(overSpeedRate<=Float.parseFloat(wfxwCodeArr[0])){
									bean.setWfxwCode(wfxwCodeArr[1]);
									break;
								}
							}
							else if(i==len-1){
								if(overSpeedRate>Float.parseFloat(wfxwCodeArr[0])){
									bean.setWfxwCode(wfxwCodeArr[1]);
									break;
								}
							}
							else{
								if(overSpeedRate>Float.parseFloat(previousWfxwCodeArr[0]) && overSpeedRate<=Float.parseFloat(wfxwCodeArr[0])){
									bean.setWfxwCode(wfxwCodeArr[1]);
									break;
								}
							}
							previousWfxwCodeArr = wfxwCodeArr;
						}		
					}
				}
			}
			else{
				bean.setWfxwCode(defaultWfxwCode);
			}
		}		
	}
	
	/**
	 * 校验视频文件是否合格
	 * @param scanDir
	 * @param violationInfoBean
	 * @return
	 */
	protected boolean verifyVideoFile(ViolationInfoBean violationInfoBean){
		boolean result = true;
		String scanDir = violationInfoBean.getFileDir();
		if (StringHelper.isNotEmpty(violationInfoBean.getVideoFile())) {

			File videoFile = new File(scanDir + "/" + violationInfoBean.getVideoFile());

			long videoFileSize = videoFile.length();
			if (videoFileSize < scannerParam.getMinVideoFileSize()) {
				log.debug("文件:" + violationInfoBean.getVideoFile() + "太小（" + videoFileSize + "）！");

				//如果视频文件太小，则对文件进行日期判断，如果文件的最后修改日期与当前时间之差超过配置值，
				//则转移图片文件和视频文件到不合法文件目录；
				if ((System.currentTimeMillis() - videoFile.lastModified()) > scannerParam.getMaxNoModifyTimeAllow() * 1000) {
					try {
						String targetFileDir = scannerParam.getInvalidFileDir() + DateHelper.dateToString(new Date(), "yyyyMMdd");
						FileHelper.createDir(targetFileDir);
						
						for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
							String imageFileName = violationInfoBean.getImageFiles()[i];
							if(StringHelper.isNotEmpty(imageFileName)){
								File imageFile = new File(scanDir + "/" + imageFileName);
								
								String targetFileName = targetFileDir + "/" + imageFileName;
								log.warn("视频文件大小小于设置值，将文件：" + imageFileName + " 转移到：" + targetFileName);
								boolean moveSuccess = FileHelper.moveFile(imageFile, new File(targetFileName));
								if (moveSuccess)
									log.warn("文件：" + imageFileName + " 转移成功！");
								else
									log.warn("文件：" + imageFileName + " 转移失败，文件可能正在使用中！");								
							}
						}
						
						String targetFileName = targetFileDir + "/" + videoFile.getName();
						log.warn("视频文件大小小于设置值已经一段时间，将文件：" + videoFile.getName() + " 转移到：" + targetFileName);
						boolean moveSuccess = FileHelper.moveFile(videoFile, new File(targetFileName));
						if (moveSuccess)
							log.warn("文件：" + videoFile.getName() + " 转移成功！");
						else
							log.warn("文件：" + videoFile.getName() + " 转移失败，文件可能正在使用中！");
					} catch (Exception ex2) {
					}
				}
				result = false;
			}
		}
		return result;
	}
	
	/**
	 * 压缩图片
	 * @param violationInfoBean
	 */
	protected void compress(ViolationInfoBean violationInfoBean){
		String scanDir = violationInfoBean.getFileDir();
		for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
			String imageFileName = violationInfoBean.getImageFiles()[i];
			if(StringHelper.isNotEmpty(imageFileName)){
				File imageFile = new File(scanDir + "/" + imageFileName);						
				if(scannerParam.getImageCompressGeSize()>0){
					if(imageFile.length()>=scannerParam.getImageCompressGeSize()){
						log.debug("压缩图片："+imageFile.getAbsolutePath());		
						ImageHelper.compress(imageFile,true,null,scannerParam.getImageCompressQuality());
					}
				}
				else{
					log.debug("压缩图片："+imageFile.getAbsolutePath());		
					ImageHelper.compress(imageFile,true,null,scannerParam.getImageCompressQuality());
				}				
			}
		}		
	}
	
	/**
	 * 改变图片尺寸（分辨率）
	 * @param violationInfoBean
	 */
	protected void changeSize(ViolationInfoBean violationInfoBean){
		String scanDir = violationInfoBean.getFileDir();
		for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
			String imageFileName = violationInfoBean.getImageFiles()[i];
			if(StringHelper.isNotEmpty(imageFileName)){
				File imageFile = new File(scanDir + "/" + imageFileName);
				try {
					ImageHelper.changeSize(imageFile, scannerParam.getImageWidth(), scannerParam.getImageHeight());
				} catch (Exception e) {
					log.error(e.getMessage(),e);
				}
			}
		}		
	}	
	
	
	
	/**
	 * 在图片文件上打上标记，子类可以覆盖该方法提供自身打水印（标志）的方法
	 * @param violationInfoBean
	 * @param scanDir
	 */
	protected void createMark(ViolationInfoBean violationInfoBean){
		String[] waterMarkArr = null;
		String scanDir = violationInfoBean.getFileDir();
		String line = violationInfoBean.getLine();		
		
		if(StringHelper.isNotEmpty(violationInfoBean.getSpeed()) && StringHelper.isNotEmpty(violationInfoBean.getLimitSpeed())){
			if("top".equalsIgnoreCase(this.getScannerParam().getWaterMarkPosition())){
				waterMarkArr = new String[]{
						"地点："+violationInfoBean.getDeviceInfoBean().getRoadName()+
						" 时间："+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss")+
						" 限度："+Integer.parseInt(violationInfoBean.getLimitSpeed())+"KM/H"+
						" 实速："+Integer.parseInt(violationInfoBean.getSpeed())+"KM/H"+
						" 车 道 ："+line						
				};				
			}
			else{
				waterMarkArr = new String[]{
						"地点："+violationInfoBean.getDeviceInfoBean().getRoadName(),
						"时间："+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss"),
						"限速："+Integer.parseInt(violationInfoBean.getLimitSpeed())+"KM/H",
						"实速："+Integer.parseInt(violationInfoBean.getSpeed())+"KM/H",
						"车 道 ："+line						
				};					
			}
		}
		else{
			if(StringHelper.isNotEmpty(violationInfoBean.getRedLightTime()) && Integer.parseInt(violationInfoBean.getRedLightTime())>0){
				if("top".equalsIgnoreCase(this.getScannerParam().getWaterMarkPosition())){
					waterMarkArr = new String[]{
							"地点："+violationInfoBean.getDeviceInfoBean().getRoadName()+
							" 时间："+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss")+
							" 红灯持续时间："+Integer.parseInt(violationInfoBean.getRedLightTime())+"秒"+
							" 车 道 ："+line							
					};		
				}
				else{
					waterMarkArr = new String[]{
							"地点："+violationInfoBean.getDeviceInfoBean().getRoadName(),
							"时间："+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss"),
							"红灯持续时间："+Integer.parseInt(violationInfoBean.getRedLightTime())+"秒",
							"车 道 ："+line							
					};							
				}
			}
			else{
				if("top".equalsIgnoreCase(this.getScannerParam().getWaterMarkPosition())){
					waterMarkArr = new String[]{
							"地点："+violationInfoBean.getDeviceInfoBean().getRoadName()+
							" 时间："+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss")+
							" 车 道 ："+line							
					};	
				}
				else{
					waterMarkArr = new String[]{
							"地点："+violationInfoBean.getDeviceInfoBean().getRoadName(),
							"时间："+DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss"),
							"车 道 ："+line							
					};						
				}
			}			
		}		
		
		for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
			String imageFileName = violationInfoBean.getImageFiles()[i];
			if(StringHelper.isNotEmpty(imageFileName)){
				String fullPicName = scanDir + "/" + imageFileName;
				File imageFile = new File(fullPicName);
				//ImageHelper.createWaterMark(imageFile,waterMarkArr,14,18,Color.BLACK,Color.WHITE,0);	
				if("top".equalsIgnoreCase(this.getScannerParam().getWaterMarkPosition())){
					int[] size = new int[]{2288,2288};
					try {
						byte[] fileByte = FileHelper.getBytes(imageFile);
						size = ImageHelper.getImageSize(fileByte);
						byte[] waterMarkPic = ImageHelper.createColorImage(this.getScannerParam().getWaterMarkBgColor(), size[0], this.getScannerParam().getWaterMarkFontHeight()+10);
						waterMarkPic = ImageHelper.createWaterMark(
								waterMarkPic,
								waterMarkArr,
								this.getScannerParam().getWaterMarkFontSize(),
								this.getScannerParam().getWaterMarkFontHeight(),
								this.getScannerParam().getWaterMarkFontColor(),
								null,
								this.getScannerParam().getWaterMarkLeftMargin(),
								this.getScannerParam().getWaterMarkTopMargin());			
						//合并水印与原始图片
						fileByte = ImageHelper.compose(waterMarkPic, fileByte, true);	
						FileHelper.writeFile(fileByte, fullPicName);
					} catch (Exception e) {
						log.error(e.getMessage(),e);
					}					
				}
				else{
					ImageHelper.createWaterMark(
							imageFile,
							waterMarkArr,
							scannerParam.getWaterMarkFontSize(),
							scannerParam.getWaterMarkFontHeight(),
							scannerParam.getWaterMarkFontColor(),
							scannerParam.getWaterMarkBgColor(),
							scannerParam.getWaterMarkLeftMargin(),
							scannerParam.getWaterMarkTopMargin());
				}
			}
		}
	}
	
	/**
	 * 备份违法图片和视频文件
	 * @param violationInfoBean
	 * @throws Exception
	 */
	public boolean backupViolationInfoFile(ViolationInfoBean violationInfoBean) {
		boolean result = true;
		try {		
			String backDir = scannerParam.getBackupDir() + "/" + violationInfoBean.getDeviceId() + "/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd") +"/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "HH") +"/";
			
			for(int i=0;i<violationInfoBean.getImageFiles().length;i++){
				String imageFileName = violationInfoBean.getImageFiles()[i];
				if(StringHelper.isNotEmpty(imageFileName)){				
					File imageFile = new File(violationInfoBean.getFileDir() + "/" + imageFileName);
					
					FileHelper.createDir(backDir);				
					File targetFile = new File(backDir + "/" + imageFileName);
					
					log.debug("开始移动文件：" + imageFile.getAbsolutePath() + " 到 " + targetFile.getAbsolutePath());
					boolean backupSuccess = FileHelper.moveFile(imageFile, targetFile);
					if (backupSuccess)
						log.debug("移动文件成功！");
					else
						log.debug("移动文件失败,请检查该文件是否被其它程序使用！");				
				}
			}		
			
			if (StringHelper.isNotEmpty(violationInfoBean.getVideoFile())) {
				String sourceFileName = violationInfoBean.getFileDir() + "/" + violationInfoBean.getVideoFile();
				String targetFileName = backDir + "/" + violationInfoBean.getVideoFile();
				log.debug("开始移动文件：" + sourceFileName + " 到 " + targetFileName);
				
				boolean backupSuccess = FileHelper.moveFile(sourceFileName, targetFileName);
				
				if (backupSuccess) {
					log.debug("移动文件成功！");
				} else {
					log.debug("移动文件失败,请检查该文件是否被其它程序使用！");	
				}
			}		
		} catch (Exception ex) {
			result = false;
			log.error(ex.getMessage(), ex);
		}		
		
		return result;
	}	
	
	/**
	 * 生成备份清单
	 * @param violationInfoBean
	 * @return
	 */
	public boolean buildBackupData(ViolationInfoBean violationInfoBean) {
		boolean buildResult = true;
		RandomAccessFile raf = null;
		try{
			FileHelper.createDir(scannerParam.getBackupDataDir());
			String backupFileName = scannerParam.getBackupDataDir() + DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyyMMdd") + ".txt";
			File backupFile = new File(backupFileName);
			if(!backupFile.exists()){
				backupFile.createNewFile();
			}
			StringBuffer backupDataBuff = new StringBuffer();
			backupDataBuff.append(violationInfoBean.getDeviceId());
			backupDataBuff.append(",");
			backupDataBuff.append(DateHelper.dateToString(violationInfoBean.getViolateTime(),"yyyy-MM-dd HH:mm:ss"));
			backupDataBuff.append(",");
			
			int i=0;
			for(;i<violationInfoBean.getImageFiles().length;i++){
				String imageFileName = violationInfoBean.getImageFiles()[i];
				backupDataBuff.append(imageFileName);
				backupDataBuff.append(",");												
			}
			for(;i<4;i++){
				backupDataBuff.append("null,");					
			}

			backupDataBuff.append(violationInfoBean.getVideoFile());
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getSpeed());
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getLimitSpeed());
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getLine());			
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getPlateNo());		
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getPlateColor());		
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getDirectionNo());				
			backupDataBuff.append(",");
			backupDataBuff.append(violationInfoBean.getViolateType());				
			backupDataBuff.append("\n");
			
			String backupData = backupDataBuff.toString();
			if(scannerParam.getBackupDataEncodingFrom()!=null && !scannerParam.getBackupDataEncodingFrom().trim().equals("")){			
				backupData = new String(backupData.getBytes(scannerParam.getBackupDataEncodingFrom()),scannerParam.getBackupDataEncodingTo());
			}
			raf = new RandomAccessFile(backupFileName,"rw");
			raf.seek(raf.length());
			raf.writeBytes(backupData);
		}
		catch(Exception ex){
			buildResult = false;
			log.error("生成备份数据时出错："+ex.getMessage(),ex);
		}
		finally{
			if(raf!=null){
				try{
					raf.close();
				}
				catch(Exception ioEx){}				 
			}
		}
		
		return buildResult;		
	}
		
		
	/* 
	 * 恢复备份数据
	 */
	public void restoreBackupData(Connection conn,boolean isUseDbConn) throws Exception{
		File backupDir = new File(scannerParam.getBackupDataDir());
		File backupFileArr[] = backupDir.listFiles(new FilenameFilterByPostfix(".txt", true));
		if(backupFileArr==null) return;
		int len = backupFileArr.length;
		for(int i = 0; i < len; i++){
			if(conn!=null) conn.setAutoCommit(false);
			boolean restoreSuccess = true;
			File backupFile = backupFileArr[i];
			log.debug("开始恢复文件：" + backupFile.getAbsolutePath() + "中的数据！");
			try{
				int processResult = this.restoreBackupFile(conn, isUseDbConn, backupFile);
				if(processResult == 0)
					restoreSuccess = backupFile.delete();
				else
					restoreSuccess = false;
			}
			catch(Exception otherExce){
				restoreSuccess = false;
			}
			if(restoreSuccess){
				if(conn!=null) conn.commit();
				log.debug("恢复文件：" + backupFile.getAbsolutePath() + "中的数据成功！");
			} 
			else {
				if(conn!=null) conn.rollback();
				log.debug("恢复文件：" + backupFile.getAbsolutePath() + "中的数据失败！");
			}
		}
	}
	
	/**
	 * 恢复单个备份文件
	 * @param conn
	 * @param backupFile
	 * @return
	 * @throws Exception
	 */
	public int restoreBackupFile(Connection conn,boolean isUseDbConn,File backupFile) throws Exception{
		int processResult = 0;
		RandomAccessFile raf = null;
		try{
			raf = new RandomAccessFile(backupFile, "r");	
			String content = null;
			while(true){
				content = raf.readLine();
				if(content == null) break;
				content = content.trim();
				if("".equals(content)) break;
				
				if(scannerParam.getBackupDataEncodingFrom()!=null && !scannerParam.getBackupDataEncodingFrom().trim().equals("")){			
					content = new String(content.getBytes(scannerParam.getBackupDataEncodingTo()),scannerParam.getBackupDataEncodingFrom());
				}
				
				String arrContent[] = content.split("[,]");
				if(arrContent.length < 13){
					log.warn("备份数据：'" + content + "'不符合规范！");
					continue;
				}
				String deviceId = arrContent[0].trim();
				if(!DeviceInfoLoaderFactory.getInstance().getDeviceMap().containsKey(deviceId)){
					log.warn("备份数据：'" + content + "',未找到相关设备－" + deviceId);
					continue;
				}
				String violateTime = arrContent[1].trim();
				String picFileName1 = arrContent[2].trim();
				String picFileName2 = arrContent[3].trim();
				String picFileName3 = arrContent[4].trim();
				String picFileName4 = arrContent[5].trim();
				
				String videoFileName = arrContent[6].trim();
				String speed = arrContent[7].trim();
				String limitSpeed = arrContent[8].trim();
				String line = arrContent[9].trim();
				String plateNo = arrContent[10].trim();
				String plateColor = arrContent[11].trim();
				String directionNo = arrContent[12].trim();
				String violateType = arrContent[13].trim();
				
				ViolationInfoBean violationInfoBean = new ViolationInfoBean();
				violationInfoBean.setDeviceId(deviceId);
				
				violationInfoBean.setImageFiles(new String[] {
					picFileName1,picFileName2,picFileName3,picFileName4
				});
				violationInfoBean.setViolateTime(DateHelper.parseDateString(violateTime, "yyyy-MM-dd HH:mm:ss"));
				
				if(StringHelper.isEmpty(videoFileName))
					violationInfoBean.setVideoFile(null);
				else
					violationInfoBean.setVideoFile(videoFileName);
				
				if(StringHelper.isEmpty(speed))
					violationInfoBean.setSpeed(null);
				else
					violationInfoBean.setSpeed(speed);
				
				if(StringHelper.isEmpty(limitSpeed))
					violationInfoBean.setLimitSpeed(null);
				else
					violationInfoBean.setLimitSpeed(limitSpeed);
				
				if(StringHelper.isEmpty(line))
					violationInfoBean.setLine(null);
				else
					violationInfoBean.setLine(line);
				
				if(StringHelper.isEmpty(plateNo))
					violationInfoBean.setPlateNo(null);
				else
					violationInfoBean.setPlateNo(plateNo);
				
				if(StringHelper.isEmpty(plateColor))
					violationInfoBean.setPlateColor(null);
				else
					violationInfoBean.setPlateColor(plateColor);
				
				if(StringHelper.isEmpty(directionNo))
					violationInfoBean.setDirectionNo(null);
				else
					violationInfoBean.setDirectionNo(directionNo);		
				
				if(StringHelper.isEmpty(violateType))
					violationInfoBean.setViolateType(null);
				else
					violationInfoBean.setViolateType(violateType);
				
				violationInfoBean.setDeviceInfoBean((DeviceInfoBean)DeviceInfoLoaderFactory.getInstance().getDeviceMap().get(deviceId));
				String fileDir = scannerParam.getBackupDir() + "/" + deviceId + "/" + DateHelper.dateToString(violationInfoBean.getViolateTime(), "yyyyMMdd") + "/"+ DateHelper.dateToString(violationInfoBean.getViolateTime(), "HH") + "/";
				violationInfoBean.setFileDir(fileDir);
				
				//检测重复数据
				boolean isRepeated = false;
				if(isUseDbConn){
					isRepeated = this.isRepeatedData(conn,violationInfoBean);
				}
				else{
					isRepeated = this.isRepeatedData(violationInfoBean);
				}
				
				//不重复则恢复数据，
				if(!isRepeated){
					log.debug("开始恢复备份文件："+backupFile.getAbsolutePath()+"中的［"+content+"］数据！");	            
					processResult = this.processViolationInfoBean(conn, violationInfoBean);
					if(processResult != 0){
						log.warn("处理备份文件："+backupFile.getAbsolutePath()+"中的［"+content+"］时出错，返回码为："+processResult);
						break;
					}
					else{
						this.statTrafficDay(conn, violationInfoBean);
						boolean afterProcessSuccess = this.postRestoreViolationInfoBean(violationInfoBean);
						if (!afterProcessSuccess) {
							processResult = -1;
							log.debug("后期处理失败！");
							break;
						}
					}				
				}
				//重复则直接进行后期处理
				else{
					this.postRestoreViolationInfoBean(violationInfoBean);
				}
			}
		}
		catch(Exception ex){
			processResult = -1;
			log.error("恢复备份数据时出错：" + ex.getMessage(), ex);
			throw ex;
		}
		finally{
			if(raf != null){
				try{
					raf.close();
				}
				catch(Exception ioEx) { }
			}
		}
    
		return processResult;
	}				
	
	/**
	 * 数据入库
	 * @param conn
	 * @param hasConn
	 * @param violationInfoBean
	 * @param scanDir
	 * @return
	 */
	protected int process(Connection conn,boolean hasConn,ViolationInfoBean violationInfoBean){
		int returnCode = -1;
		try {
			if (hasConn) {				
				conn.setAutoCommit(false);
				
				boolean isRepeated = this.isRepeatedData(conn,violationInfoBean);
				if(isRepeated){
					this.processRepeatedData(violationInfoBean);
					return -1;
				}
				
				returnCode = this.processViolationInfoBean(conn, violationInfoBean);
				if (returnCode == 0) {
					boolean afterProcessSuccess = this.postProcessViolationInfoBean(violationInfoBean);
					if (afterProcessSuccess) {
						this.statTrafficDay(conn, violationInfoBean);
						conn.commit();
						log.debug("已提交入库！");
					} else {
						returnCode = -1;
						conn.rollback();
						log.debug("提交失败，数据回滚！");
					}
				}
			}
			if (!hasConn || returnCode != 0) {
				boolean backupSuccess = this.backupViolationInfoFile(violationInfoBean);
				if (backupSuccess)
					backupSuccess = this.buildBackupData(violationInfoBean);
			}
		} catch (SQLException sqlEx) {
			if (hasConn) {
				try {
					conn.rollback();
				} catch (Exception ex1) {
					log.error(ex1);
				}
			}
		} catch (Exception ex) {
			if (hasConn) {
				try {
					conn.rollback();
				} catch (Exception ex1) {
					log.error(ex1);
				}
			}
			log.error("插入数据失败：" + ex.getMessage(), ex);
		}		
		return returnCode;
	}
	
	/**
	 * 数据入库
	 * @param conn
	 * @param hasConn
	 * @param violationInfoBean
	 * @param scanDir
	 * @return
	 */
	protected int process(ViolationInfoBean violationInfoBean){
		int returnCode = -1;
		try {
			boolean isRepeated = this.isRepeatedData(violationInfoBean);
			if(isRepeated){
				this.processRepeatedData(violationInfoBean);
				return -1;
			}
			
			returnCode = this.processViolationInfoBean(null, violationInfoBean);
			if (returnCode == 0) {
				boolean afterProcessSuccess = this.postProcessViolationInfoBean(violationInfoBean);
				if (afterProcessSuccess) {
					log.debug("处理成功！");
				} else {
					returnCode = -1;
					log.debug("处理失败！");
				}
			}
			if (returnCode != 0) {
				boolean backupSuccess = this.backupViolationInfoFile(violationInfoBean);
				if (backupSuccess)
					backupSuccess = this.buildBackupData(violationInfoBean);
			}
		} catch (Exception ex) {
			log.error("处理失败：" + ex.getMessage(), ex);
		}		
		return returnCode;
	}	
	
	/**
	 * 清理视频文件
	 * @return
	 */
	private int cleanVideoFile(){
		int cleanNum = 0;
		int len = this.getScanDirs().length;
		for(int i=0;i<len;i++){
			String strScanDir = this.getScanDirs()[i];
			File scanDir = new File(strScanDir);
			cleanNum += this.cleanVideoFile(scanDir);
		}
		return cleanNum;
	}
	
	/**
	 * 递归清理子目录
	 * @param scanDir
	 * @return
	 */
	private int cleanVideoFile(File scanDir){
		int cleanNum = 0;
		if (!scanDir.exists() || !scanDir.isDirectory()) {
			return 0;
		}
		
		File[] fileArr = scanDir.listFiles(new FilenameFilterByPostfix(scannerParam.getVideoFileExtension(),true,true));
		int videoNum = fileArr.length;
		for(int j=0;j<videoNum;j++){
			if(fileArr[j].isDirectory()){
				cleanNum += this.cleanVideoFile(fileArr[j]);
			}
			else{
				log.debug("删除视频文件："+fileArr[j].getAbsolutePath());
				if(fileArr[j].delete()) cleanNum++;
			}
		}		
		return cleanNum;
	}
	
	/**
	 * 将旧文件转到旧文件目录中
	 * @return
	 */
	protected int cleanOldFile() {
		log.debug("开始清理旧文件...");
		int cleanFileNum = 0;
		int dirNum = this.getScanDirs().length;
		log.debug("延时天数：" + scannerParam.getDelayDeleteDay());
		long interval = scannerParam.getDelayDeleteDay() * 24L * 60L * 60L * 1000L;
		log.debug("延时毫秒数：" + interval);
		
		if (interval <= 0) {
			log.warn("延时移除时间设置有误！");
			return cleanFileNum;
		}
		
		for(int i=0;i<dirNum;i++){
			String strScanDir = this.getScanDirs()[i];
			File scanDir = new File(strScanDir);
			if(!scanDir.exists()){
				log.warn(strScanDir+"不存在！");
				continue;
			}
			
			if(scanDir.isDirectory()){
				cleanFileNum += this.cleanOldFile(scanDir,interval);
			}
			else{
				log.warn("'"+strScanDir+"'不是目录！");
			}
		}
		
		log.debug("共成功清理旧文件：" + cleanFileNum + "个,这些过期文件已转移到目录：" + scannerParam.getDeleteFileDir());		
		return cleanFileNum;
	}	

	/**
	 * 将旧文件转到旧文件目录中，递归搜索子目录
	 * @return
	 */
	protected int cleanOldFile(File scanDir,long interval) {
		int cleanFileNum = 0;
		log.debug("开始清理目录："+scanDir.getAbsolutePath());
		
		File files[] = scanDir.listFiles();
		int len = files.length;
		
		//删除空目录
		if(len==0 && (System.currentTimeMillis() - scanDir.lastModified()) > interval){
			log.debug("删除空目录："+scanDir.getAbsolutePath());
			scanDir.delete();
			return cleanFileNum;
		}
		
		for (int j = 0; j < len; j++) {
			if (files[j].isDirectory()){
				cleanFileNum += this.cleanOldFile(files[j], interval);
			}
			else{
				String fileName = files[j].getName().toUpperCase().trim();
				/*
				if(fileName.endsWith(".INI")){
					continue;
				}
				*/
				int timeIndex = fileName.indexOf("T") + 1;
				if (timeIndex == -1){
					log.debug("删除文件："+files[j].getAbsolutePath());
					files[j].delete();
					continue;
				}
				
				String timeStr = null;
				Date date = null;
				try {
					timeStr = fileName.substring(timeIndex, timeIndex + 14);
//					String[] fileInfo = StringHelper.split(fileName, "_");
//					timeStr = fileInfo[8];
					date = new SimpleDateFormat("yyyyMMddHHmmss").parse(timeStr);
				} catch (Exception ex) {
					log.warn("文件：" + files[j].getAbsolutePath() + "无效，(" + ex.getMessage() + "),删除它！");
					files[j].delete();
					continue;
				}

				if ((System.currentTimeMillis() - date.getTime()) > interval) {
					String targetFileDir = scannerParam.getDeleteFileDir() + DateHelper.dateToString(new Date(), "yyyyMMdd");
					try {
						FileHelper.createDir(targetFileDir);
					} catch (Exception ex) {
						log.warn("创建旧文件存放目录：" + targetFileDir + "时出错！ -- " + ex.getMessage());
						continue;
					}
					boolean moveSuccess = true;
					try {
						moveSuccess = FileHelper.moveFile(files[j], new File(targetFileDir + "/" + files[j].getName()));
					} catch (Exception moveExce) {
						moveSuccess = false;
					}
					if (moveSuccess)
						cleanFileNum++;
					else
						log.debug("清理文件：" + files[j].getAbsolutePath() + "时出错，该文件可能正在使用中！");
				}
			}
		}			
		return cleanFileNum;
	}	
	
	public ScannerParamBean getScannerParam() {
		return scannerParam;
	}

	public void setScannerParam(ScannerParamBean scannerParam) {
		this.scannerParam = scannerParam;
	}

}
