/**
 * 
 */
package com.its.core.local.dfa.task;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.task.ATask;
import com.its.core.util.ColorHelper;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.ImageHelper;
import com.its.core.util.PropertiesHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2013-4-15 上午11:54:21
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ExportViolateToTestTask extends ATask {
	private static final Log log = LogFactory.getLog(ExportViolateToTestTask.class);
	
	/**
	 * 水印标志参数
	 */
	private int waterMarkBgHeight = 80;
	private int waterMarkFontSize = 80;
	private int waterMarkFontHeight = 80;
	private Color waterMarkFontColor = Color.white;
	private Color waterMarkBgColor = Color.black;
	private int waterMarkLeftMargin = 10;
	private int waterMarkTopMargin = 10;
	
	private String selectUnexportRecordSql = null;
	private String updateViolateStatusSql = null;

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		this.waterMarkBgHeight = PropertiesHelper.getInt(propertiesPrefix,no,"watermark.bg_height",props,this.waterMarkFontSize);
		this.waterMarkFontSize = PropertiesHelper.getInt(propertiesPrefix,no,"watermark.font_size",props,this.waterMarkFontSize);
		this.waterMarkFontHeight = PropertiesHelper.getInt(propertiesPrefix,no,"watermark.font_height",props,this.waterMarkFontHeight);
		this.waterMarkLeftMargin = PropertiesHelper.getInt(propertiesPrefix,no,"watermark.left_margin",props,this.waterMarkLeftMargin);
		this.waterMarkTopMargin = PropertiesHelper.getInt(propertiesPrefix,no,"watermark.top_margin",props,this.waterMarkTopMargin);
		this.waterMarkFontColor = ColorHelper.getColor(props.getProperty(propertiesPrefix, no, "watermark.font_color"));
		this.waterMarkBgColor = ColorHelper.getColor(props.getProperty(propertiesPrefix, no, "watermark.bg_color"));
		
		this.selectUnexportRecordSql = props.getProperty(propertiesPrefix,no,"sql.select_unviolate_record");	
		this.updateViolateStatusSql = props.getProperty(propertiesPrefix,no,"sql.update_violate_status");

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
			log.debug("conn = "+conn);
			List<ViolateRecordBean> recordList = this.getUnexportRecord(conn);
			int size = recordList.size();
			log.debug("共找到："+size+"条记录！");
			conn.setAutoCommit(false);
			
			short currentExcelRow = 1;
			for(int i=0;i<size;i++){
				ViolateRecordBean record = (ViolateRecordBean)recordList.get(i);
				
				byte[] fileBytes1 = ImageHelper.getImageBytes(record.getImagePath1());
				byte[] fileBytes2 = ImageHelper.getImageBytes(record.getImagePath2());
				byte[] fileBytes3 = ImageHelper.getImageBytes(record.getImagePath3());
				
				byte[] fileBytes4 = null;
				byte[] fileBytes = null;
				
				//判断用户有没有剪切车牌特写图
				if(StringHelper.isNotEmpty(record.getPlatePosition())) {
					String[] Imageindex = StringHelper.split(record.getPlatePosition(), ",");	
					if(Imageindex[0].equals("1")) {
						BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes1));
						fileBytes4 = ImageHelper.cut(image, Integer.parseInt(Imageindex[1])-200, Integer.parseInt(Imageindex[2])-200, 500, 370);					
					} else if(Imageindex[0].equals("2")) {
						BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes2));
						fileBytes4 = ImageHelper.cut(image, Integer.parseInt(Imageindex[1])-200, Integer.parseInt(Imageindex[2])-200, 500, 370);				
					} else if(Imageindex[0].equals("3")) {
						BufferedImage image = ImageIO.read(new ByteArrayInputStream(fileBytes3));
						fileBytes4 = ImageHelper.cut(image, Integer.parseInt(Imageindex[1])-200, Integer.parseInt(Imageindex[2])-200, 500, 370);			
					}
					fileBytes4 = ImageHelper.changeSize(fileBytes4, 2592, 1920);
					fileBytes = ImageHelper.compose(ImageHelper.compose(ImageHelper.compose(fileBytes1, fileBytes2, false), fileBytes3, false), fileBytes4, false);
				} else {
					fileBytes = ImageHelper.compose(ImageHelper.compose(fileBytes1, fileBytes2, false), fileBytes3, false);
				}				
				FileHelper.writeBuffered(fileBytes4, "D:/特写.JPG");				
				
				
				int[] imageSize = ImageHelper.getImageSize(fileBytes);
				
				Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
				DeviceInfoBean dib = (DeviceInfoBean)dibMap.get(record.getDeviceId());
				
				//文字水印
				String[] waterMarkArr = new String[]{		
						"时间:"+DateHelper.dateToString(record.getViolateTime(),"yyyy年MM月dd日HH时mm分ss秒")+		
						" 地点:"+dib.getRoadName()+
						" "+SystemConstant.getInstance().getDirectionNameByCode(record.getDirectionCode())+ "方向" +
						" 车道 :"+record.getLine() +	
						" 限速:"+record.getSpeed()+"KM/H"+
						" 实速:"+record.getLimitSpeed()+"KM/H"
											
				};
				byte[] waterMarkImage = ImageHelper.createColorImage(Color.BLACK, imageSize[0], this.getWaterMarkBgHeight());
				
				waterMarkImage = ImageHelper.createWaterMark(
						waterMarkImage,
						waterMarkArr,
						this.getWaterMarkFontSize(),
						this.getWaterMarkFontHeight(),
						this.getWaterMarkFontColor(),
						null,
						this.getWaterMarkLeftMargin(),
						this.getWaterMarkTopMargin());	
				
				//合并第水印与原始图片
				byte[] imageByte = ImageHelper.compose(waterMarkImage, fileBytes, true);	
				
				FileHelper.writeBuffered(imageByte, "D:/示例.JPG");
				
//				this.updateRecordStatus(conn, record.getId());
//				conn.commit();
				currentExcelRow++;
			}
			long currentTime = System.currentTimeMillis();
			log.info("本次共上传记录：" + (currentExcelRow-1) + "条 耗时：" + ((currentTime- startTime) / 1000F) + "秒！");
		}
		catch(Exception ex){
			log.error(ex.getMessage(),ex);		
			if(conn!=null){
				try {
					conn.rollback();
				} catch (Exception e) {
					log.error(e);
				}
			}
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
	
	/**
	 * 获取所有未导出的T_ITS_VIOLATE_RECORD记录
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
				record.setDirectionCode(rs.getString("direction_code"));
				record.setLine(rs.getString("line"));
				record.setPlatePosition(rs.getString("plate_position"));
				record.setSpeed(rs.getString("speed"));
				record.setLimitSpeed(rs.getString("limit_speed"));
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
			log.debug(this.getUpdateViolateStatusSql());
			preStatement = conn.prepareStatement(this.getUpdateViolateStatusSql());
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

	public String getSelectUnexportRecordSql() {
		return selectUnexportRecordSql;
	}

	public void setSelectUnexportRecordSql(String selectUnexportRecordSql) {
		this.selectUnexportRecordSql = selectUnexportRecordSql;
	}

	public String getUpdateViolateStatusSql() {
		return updateViolateStatusSql;
	}

	public void setUpdateViolateStatusSql(String updateViolateStatusSql) {
		this.updateViolateStatusSql = updateViolateStatusSql;
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

	public int getWaterMarkBgHeight() {
		return waterMarkBgHeight;
	}

	public void setWaterMarkBgHeight(int waterMarkBgHeight) {
		this.waterMarkBgHeight = waterMarkBgHeight;
	}		

}
