/**
 * 
 */
package com.its.core.module.filescan.violation;

import java.awt.Color;
import java.io.Serializable;

/**
 * 创建日期 2012-12-13 上午08:31:29
 * @author GuoPing.Wu QQ:365175040
 * Copyright: ITS Technologies CO.,LTD.
 */
public class ScannerParamBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8705390959022791192L;
	
	//图片文件扩展名
	private String imgFileExtension = null;

	//视频文件扩展名
	private String videoFileExtension = null;
	
	//最小图片文件Size,单位：字节
	private long minImageFileSize = 51200;

	//最小视频文件Size,单位：字节
	private long minVideoFileSize = 51200;
	
	//延迟扫描时间，单位：秒，缺省：（180秒）三分钟，在扫描图片之前，会先检查图片的lastModified时间，如果间隔时间小于定义的秒数，则不扫描该文件，主要为防止扫描正在传输中的图片
	private long delayScanSecond = 180L;

	//如果文件大小小于配置值，最长允许文件存在时间,单位：秒
	private long maxNoModifyTimeAllow = 1800;
	
	private String defaultWfxwCode = null;
	
	//检查T_TEMP_PIC中有无重复记录的SQL
	private String checkRepeatedViolateRecordTempSql = null;
	
	//检查T_FXC_RECORD中有无重复记录的SQL
	private String checkRepeatedViolateRecordSql = null;	
	
	//扫描完成后源文件备份目录
	private String backupDir = null;

	private long delayDeleteDay = -1;

	private String deleteFileDir = null;

	private String invalidFileDir = null;
	
    private String backupDataDir = null;
    private String backupDataEncodingFrom = null;
    private String backupDataEncodingTo = null;	
    
    /**
	 * 视频文件是否是必需的，有些项目中闯红灯图片没有对应的视频文件也是合法的．
	 * 对于这种情况，请将扫描程序中的require_video属性设为false
	 * 缺省为true：表示闯红灯图片中必需有相对应的视频匹配．
	 */
	private boolean requireVideo = true;
	
	/**
	 * 是否直接删除视频文件，如果该参数设为true，则requireVideo必须设为false才有效.
	 * 设为true时，扫描程序每次启动运行时首先将视频文件清除．
	 */
	private boolean directDeleteVideo = false;
	
	//是否为图片添加水印 缺省为false
	private boolean requireAddWaterMark = false;
	
	//经校验无效的文件，是否移到invalidFileDir文件目录中
	private boolean requireMoveInvalidFile = true;
	
	//图片压缩，-1表示不压缩
	private float imageCompressQuality = -1;
	
	//当图片大于该项值（单位：字节）时，启用压缩，-1或0无意义
	private long imageCompressGeSize = -1;
	
	//目标图片宽度（如果小于0，表示不改变）
	private int imageWidth = -1;

	//目标图片高度（如果小于0，表示不改变）
	private int imageHeight = -1;	
	
	//是否添加MD5校验码
	private boolean addMd5Verify = false;
	
	
	//水印标志参数(只有当require_add_watermark参数为true时这些参数才有意义)
	private int waterMarkFontSize = 14;
	private int waterMarkFontHeight = 18;
	private Color waterMarkFontColor = Color.BLACK;
	private Color waterMarkBgColor = Color.WHITE;
	private int waterMarkBgHeight = 0;
	private int waterMarkLeftMargin = 0;
	private int waterMarkTopMargin = 0;	
	/**
	 * 水印位置
	 * top:图片上方单条
	 */
	private String waterMarkPosition = null;
	
	//按日、24小时统计T_ITS_VIOLATE_RECORD_TEMP违法信息到表t_its_traffic_day_stat和t_its_traffic_hour_stat 细化到车道一级
	private String statTrafficDayCheckExistSql = null;
	private String statTrafficDayInsertSql = null;
	private String statTrafficDayUpdateSql = null;
	
	private String statTrafficHourCheckExistSql = null;	
	private String statTrafficHourInsertSql = null;	
	private String statTrafficHourUpdateSql = null;
	
	public String getImgFileExtension() {
		return imgFileExtension;
	}

	public void setImgFileExtension(String imgFileExtension) {
		this.imgFileExtension = imgFileExtension;
	}

	public String getVideoFileExtension() {
		return videoFileExtension;
	}

	public void setVideoFileExtension(String videoFileExtension) {
		this.videoFileExtension = videoFileExtension;
	}

	public long getMinImageFileSize() {
		return minImageFileSize;
	}

	public void setMinImageFileSize(long minImageFileSize) {
		this.minImageFileSize = minImageFileSize;
	}

	public long getMinVideoFileSize() {
		return minVideoFileSize;
	}

	public void setMinVideoFileSize(long minVideoFileSize) {
		this.minVideoFileSize = minVideoFileSize;
	}

	public long getDelayScanSecond() {
		return delayScanSecond;
	}

	public void setDelayScanSecond(long delayScanSecond) {
		this.delayScanSecond = delayScanSecond;
	}

	public long getMaxNoModifyTimeAllow() {
		return maxNoModifyTimeAllow;
	}

	public void setMaxNoModifyTimeAllow(long maxNoModifyTimeAllow) {
		this.maxNoModifyTimeAllow = maxNoModifyTimeAllow;
	}

	public String getDefaultWfxwCode() {
		return defaultWfxwCode;
	}

	public void setDefaultWfxwCode(String defaultWfxwCode) {
		this.defaultWfxwCode = defaultWfxwCode;
	}

	public String getCheckRepeatedViolateRecordTempSql() {
		return checkRepeatedViolateRecordTempSql;
	}

	public void setCheckRepeatedViolateRecordTempSql(
			String checkRepeatedViolateRecordTempSql) {
		this.checkRepeatedViolateRecordTempSql = checkRepeatedViolateRecordTempSql;
	}

	public String getCheckRepeatedViolateRecordSql() {
		return checkRepeatedViolateRecordSql;
	}

	public void setCheckRepeatedViolateRecordSql(
			String checkRepeatedViolateRecordSql) {
		this.checkRepeatedViolateRecordSql = checkRepeatedViolateRecordSql;
	}

	public String getBackupDir() {
		return backupDir;
	}

	public void setBackupDir(String backupDir) {
		this.backupDir = backupDir;
	}

	public long getDelayDeleteDay() {
		return delayDeleteDay;
	}

	public void setDelayDeleteDay(long delayDeleteDay) {
		this.delayDeleteDay = delayDeleteDay;
	}

	public String getDeleteFileDir() {
		return deleteFileDir;
	}

	public void setDeleteFileDir(String deleteFileDir) {
		this.deleteFileDir = deleteFileDir;
	}

	public String getInvalidFileDir() {
		return invalidFileDir;
	}

	public void setInvalidFileDir(String invalidFileDir) {
		this.invalidFileDir = invalidFileDir;
	}

	public String getBackupDataDir() {
		return backupDataDir;
	}

	public void setBackupDataDir(String backupDataDir) {
		this.backupDataDir = backupDataDir;
	}

	public String getBackupDataEncodingFrom() {
		return backupDataEncodingFrom;
	}

	public void setBackupDataEncodingFrom(String backupDataEncodingFrom) {
		this.backupDataEncodingFrom = backupDataEncodingFrom;
	}

	public String getBackupDataEncodingTo() {
		return backupDataEncodingTo;
	}

	public void setBackupDataEncodingTo(String backupDataEncodingTo) {
		this.backupDataEncodingTo = backupDataEncodingTo;
	}

	public boolean isRequireVideo() {
		return requireVideo;
	}

	public void setRequireVideo(boolean requireVideo) {
		this.requireVideo = requireVideo;
	}

	public boolean isDirectDeleteVideo() {
		return directDeleteVideo;
	}

	public void setDirectDeleteVideo(boolean directDeleteVideo) {
		this.directDeleteVideo = directDeleteVideo;
	}

	public boolean isRequireAddWaterMark() {
		return requireAddWaterMark;
	}

	public void setRequireAddWaterMark(boolean requireAddWaterMark) {
		this.requireAddWaterMark = requireAddWaterMark;
	}

	public boolean isRequireMoveInvalidFile() {
		return requireMoveInvalidFile;
	}

	public void setRequireMoveInvalidFile(boolean requireMoveInvalidFile) {
		this.requireMoveInvalidFile = requireMoveInvalidFile;
	}

	public float getImageCompressQuality() {
		return imageCompressQuality;
	}

	public void setImageCompressQuality(float imageCompressQuality) {
		this.imageCompressQuality = imageCompressQuality;
	}

	public long getImageCompressGeSize() {
		return imageCompressGeSize;
	}

	public void setImageCompressGeSize(long imageCompressGeSize) {
		this.imageCompressGeSize = imageCompressGeSize;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public boolean isAddMd5Verify() {
		return addMd5Verify;
	}

	public void setAddMd5Verify(boolean addMd5Verify) {
		this.addMd5Verify = addMd5Verify;
	}

	public int getWaterMarkFontSize() {
		return waterMarkFontSize;
	}

	public void setWaterMarkFontSize(int waterMarkFontSize) {
		this.waterMarkFontSize = waterMarkFontSize;
	}

	public int getWaterMarkFontHeight() {
		return waterMarkFontHeight;
	}

	public void setWaterMarkFontHeight(int waterMarkFontHeight) {
		this.waterMarkFontHeight = waterMarkFontHeight;
	}

	public Color getWaterMarkFontColor() {
		return waterMarkFontColor;
	}

	public void setWaterMarkFontColor(Color waterMarkFontColor) {
		this.waterMarkFontColor = waterMarkFontColor;
	}

	public Color getWaterMarkBgColor() {
		return waterMarkBgColor;
	}

	public void setWaterMarkBgColor(Color waterMarkBgColor) {
		this.waterMarkBgColor = waterMarkBgColor;
	}	

	public int getWaterMarkBgHeight() {
		return waterMarkBgHeight;
	}

	public void setWaterMarkBgHeight(int waterMarkBgHeight) {
		this.waterMarkBgHeight = waterMarkBgHeight;
	}

	public int getWaterMarkLeftMargin() {
		return waterMarkLeftMargin;
	}

	public void setWaterMarkLeftMargin(int waterMarkLeftMargin) {
		this.waterMarkLeftMargin = waterMarkLeftMargin;
	}

	public int getWaterMarkTopMargin() {
		return waterMarkTopMargin;
	}

	public void setWaterMarkTopMargin(int waterMarkTopMargin) {
		this.waterMarkTopMargin = waterMarkTopMargin;
	}

	public String getWaterMarkPosition() {
		return waterMarkPosition;
	}

	public void setWaterMarkPosition(String waterMarkPosition) {
		this.waterMarkPosition = waterMarkPosition;
	}

	public String getStatTrafficDayCheckExistSql() {
		return statTrafficDayCheckExistSql;
	}

	public void setStatTrafficDayCheckExistSql(String statTrafficDayCheckExistSql) {
		this.statTrafficDayCheckExistSql = statTrafficDayCheckExistSql;
	}

	public String getStatTrafficDayInsertSql() {
		return statTrafficDayInsertSql;
	}

	public void setStatTrafficDayInsertSql(String statTrafficDayInsertSql) {
		this.statTrafficDayInsertSql = statTrafficDayInsertSql;
	}

	public String getStatTrafficDayUpdateSql() {
		return statTrafficDayUpdateSql;
	}

	public void setStatTrafficDayUpdateSql(String statTrafficDayUpdateSql) {
		this.statTrafficDayUpdateSql = statTrafficDayUpdateSql;
	}

	public String getStatTrafficHourCheckExistSql() {
		return statTrafficHourCheckExistSql;
	}

	public void setStatTrafficHourCheckExistSql(String statTrafficHourCheckExistSql) {
		this.statTrafficHourCheckExistSql = statTrafficHourCheckExistSql;
	}

	public String getStatTrafficHourInsertSql() {
		return statTrafficHourInsertSql;
	}

	public void setStatTrafficHourInsertSql(String statTrafficHourInsertSql) {
		this.statTrafficHourInsertSql = statTrafficHourInsertSql;
	}

	public String getStatTrafficHourUpdateSql() {
		return statTrafficHourUpdateSql;
	}

	public void setStatTrafficHourUpdateSql(String statTrafficHourUpdateSql) {
		this.statTrafficHourUpdateSql = statTrafficHourUpdateSql;
	}	

}
