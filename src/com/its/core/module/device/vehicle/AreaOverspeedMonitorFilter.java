/**
 * 
 */
package com.its.core.module.device.vehicle;

import java.awt.Color;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.common.DeviceInfoBean;
import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.common.sequence.SequenceFactory;
import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.module.device.vehicle.filter.IProcessorFilter;
import com.its.core.module.device.vehicle.filter.helper.AreaOverspeedHelper;
import com.its.core.module.device.vehicle.filter.helper.AreaOverspeedMonitorLinkedHashMapImpl;
import com.its.core.module.device.vehicle.filter.helper.CarRecordBean;
import com.its.core.module.device.vehicle.filter.helper.CheckPointBean;
import com.its.core.module.device.vehicle.filter.helper.LowerSpeedViolateBean;
import com.its.core.module.device.vehicle.filter.helper.SectionBean;
import com.its.core.module.filescan.violation.ViolationInfoBean;
import com.its.core.util.ColorHelper;
import com.its.core.util.DatabaseHelper;
import com.its.core.util.DateHelper;
import com.its.core.util.FileHelper;
import com.its.core.util.ImageHelper;
import com.its.core.util.PropertiesHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-12-6 下午02:53:07
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class AreaOverspeedMonitorFilter implements IProcessorFilter {
	private static final Log log = LogFactory.getLog(AreaOverspeedMonitorFilter.class);
	
	//号牌号码占位符
	private char PLATE_PLACEHOLDER = '@';
	
	//连续的低于最低限速的行驶车辆统计次数上限
	private int LOWER_SPEED_CONTINUOUS_COUNT_UPPERLIMIT = 3;
	
	private boolean plateFullMatch = true;
	
	//有效的车牌识别底限位数，如果识别出的车牌位数（不包括前两位）小于该值，则不处理
	private int plateMatchBit = 4;
	
	/**
	 * 是否忽略单点超速车辆，如为false，则表示即使单点超速了，仍然检测其是否区间超速。
	 * 单点超速车辆是那些经过单个卡口时即检测出超速的车辆，如果已经将单点超速车辆导入违法处理库，则此处必须设为true，不再重复处罚。
	 */
	private boolean ignoreSinglePointOverspeed = false;
	
	//清理垃圾数据的时间间隔
	private long clearIntervalMillis = 1800000;
	
	//最后一次清理的时间点
	private long lastClearTimeMillis = System.currentTimeMillis();
	
	//区间测速路段列表
	private List<SectionBean> sectionsList = new ArrayList<SectionBean>();	
	
	//区间超速图片本地保存目录
	private String areaOverspeedSaveDir = null;	
	
	//合并前统一单张图片的宽度（合并后的真实宽度等于该宽度乘以2）
	private int imageWidth = 1600;
	
	private int imageHeight = 1200;
	
	//超速限值,当检测到的限速值超出该值时,系统则认为不合理,从而放弃该图片生成,可以设为小于等于0,表示不设限.
	private int overspeedLimitValue = 0;
	
	//水印标志参数
	private int waterMarkBgHeight = 16;
	private int waterMarkFontSize = 14;
	private int waterMarkFontHeight = 16;
	private Color waterMarkFontColor = Color.yellow;
	private Color waterMarkBgColor = Color.black;
	private int waterMarkLeftMargin = 10;
	private int waterMarkTopMargin = 5;
	
	//合并图片前休眠时间，单位：毫秒 ,缺省10秒钟（该参数为等待前端生成图片的休眠时间，可以设为0，表示不等待）
	private long sleepMillisecond = 10000L;
	
	//是否检测低速限制
	private boolean checkLowerSpeed = false;
	
	/**
	 * 检测最低限速时的时限因子，超出时限的记录将被清除。
	 * 如最低限速30KM/H，如检测距离为15KM，则超过时间 (15/30)+(15/30)*0.3F的车辆行驶记录将被清除
	 */
	private float lowerSpeedTimeLimitFactor = 1.0F; 
	
	/**
	 * 超低速违法车辆列表，所有的超低速行驶车辆信息先进入该列表，
	 * 经特定算法确定属于非堵塞、或其它非特殊天气而造成的低速行驶，则会生成违法图片
	 */
	private List<LowerSpeedViolateBean> lowerSpeedVioBeanList = new ArrayList<LowerSpeedViolateBean>();

	/**
	 * 配置区间测速属性
	 */
	public void configure(XMLProperties props, String propertiesPrefix, int no)	throws Exception {
		this.areaOverspeedSaveDir = props.getProperty(propertiesPrefix, no, "parameters.image_save_dir_area_overspeed");
		this.imageWidth = PropertiesHelper.getInt(propertiesPrefix,no,"parameters.image_width",props,this.imageWidth);
		this.imageHeight = PropertiesHelper.getInt(propertiesPrefix,no,"parameters.image_height",props,this.imageHeight);
		this.sleepMillisecond = PropertiesHelper.getLong(propertiesPrefix,no,"parameters.sleep_millisecond",props,this.sleepMillisecond);
		this.overspeedLimitValue = PropertiesHelper.getInt(propertiesPrefix,no,"parameters.overspeed_limit_value",props,this.overspeedLimitValue);
		String strPlateFullMatch = props.getProperty(propertiesPrefix, no, "parameters.plate_full_match");
		if("false".equalsIgnoreCase(strPlateFullMatch) || "n".equalsIgnoreCase(strPlateFullMatch)){
			this.plateFullMatch = false;
		}
		else{
			this.plateFullMatch = true;
		}		
		this.plateMatchBit = PropertiesHelper.getInt(propertiesPrefix,no,"parameters.plate_match_bit",props,this.plateMatchBit);
		this.ignoreSinglePointOverspeed = StringHelper.getBoolean(props.getProperty(propertiesPrefix, no, "parameters.ignore_single_point_overspeed"), this.ignoreSinglePointOverspeed);
		this.lowerSpeedTimeLimitFactor = PropertiesHelper.getFloat(propertiesPrefix,no,"parameters.lower_speed_time_limit_factor",props,this.lowerSpeedTimeLimitFactor);
			
		this.waterMarkBgHeight = PropertiesHelper.getInt(propertiesPrefix,no,"watermark.bg_height",props,this.waterMarkBgHeight);
		this.waterMarkFontSize = PropertiesHelper.getInt(propertiesPrefix,no,"watermark.font_size",props,this.waterMarkFontSize);
		this.waterMarkFontHeight = PropertiesHelper.getInt(propertiesPrefix,no,"watermark.font_height",props,this.waterMarkFontHeight);
		this.waterMarkLeftMargin = PropertiesHelper.getInt(propertiesPrefix,no,"watermark.left_margin",props,this.waterMarkLeftMargin);
		this.waterMarkTopMargin = PropertiesHelper.getInt(propertiesPrefix,no,"watermark.top_margin",props,this.waterMarkTopMargin);
		this.waterMarkFontColor = ColorHelper.getColor(props.getProperty(propertiesPrefix, no, "watermark.font_color"));
		this.waterMarkBgColor = ColorHelper.getColor(props.getProperty(propertiesPrefix, no, "watermark.bg_color"));
		
		try{
			FileHelper.createDir(this.areaOverspeedSaveDir);		
		}catch(Exception ex){
			log.error(ex);
		}
		
		int size = props.getPropertyNum(propertiesPrefix,no,"sections.section");
		for (int i = 0; i < size; i++) {			
			String sectionDirection		= props.getProperty(propertiesPrefix,no,"sections.section",i,"direction");
			String sectionName			= props.getProperty(propertiesPrefix,no,"sections.section",i,"name");	
			String sectionDeviceId		= props.getProperty(propertiesPrefix,no,"sections.section",i,"device_id");
			int checkPointNum 			= props.getPropertyNum(propertiesPrefix,no,"sections.section",i,"check_points.check_point");
			log.debug(sectionDirection+"\t"+sectionName+"\t"+sectionDeviceId + "\t"+ checkPointNum);
			//List<CheckPointBean> checkPointList = new ArrayList<CheckPointBean>();
			CheckPointBean firstCheckPointBean = null;
			CheckPointBean prefiousCheckPointBean = null;
			for(int j=0;j<checkPointNum;j++){
				String deviceIds		= props.getProperty(propertiesPrefix,no,"sections.section",i,"check_points.check_point",j,"device_ids");
				String distance			= props.getProperty(propertiesPrefix,no,"sections.section",i,"check_points.check_point",j,"distance");
				
				String limitSpeed		= props.getProperty(propertiesPrefix,no,"sections.section",i,"check_points.check_point",j,"limit_speed");
				String limitSpeedMap	= props.getProperty(propertiesPrefix,no,"sections.section",i,"check_points.check_point",j,"limit_speed_map");
				
				String lowerLimitSpeed	= props.getProperty(propertiesPrefix,no,"sections.section",i,"check_points.check_point",j,"lower_limit_speed");
				String lowerLimitSpeedMap	= props.getProperty(propertiesPrefix,no,"sections.section",i,"check_points.check_point",j,"lower_limit_speed_map");
				
				String catchPointMap	= props.getProperty(propertiesPrefix,no,"sections.section",i,"check_points.check_point",j,"catch_point_map");
				
				CheckPointBean checkPointBean = new CheckPointBean();				
				Map<String,String> deviceMap = new HashMap<String,String>();
				String[] deviceArr = deviceIds.split("[,]");
				for (String deviceId : deviceArr) {
					if(StringHelper.isNotEmpty(deviceId)){
						deviceMap.put(deviceId,deviceId);
					}
				}
				checkPointBean.setDeviceMap(deviceMap);
				if(StringHelper.isNotEmpty(distance)){
					checkPointBean.setDistance(Integer.parseInt(distance));
				}
				if(StringHelper.isNotEmpty(limitSpeed)){
					checkPointBean.setLimitSpeed(Integer.parseInt(limitSpeed));
				}
				checkPointBean.setLowerSpeedContinuousCountUpperLimit(LOWER_SPEED_CONTINUOUS_COUNT_UPPERLIMIT);
				
				if(StringHelper.isNotEmpty(limitSpeedMap)){
					String[] arrLimitSpeedMap = StringHelper.split(limitSpeedMap, ",");
					Map<String,Integer> limitSpeedMapA = new HashMap<String,Integer>();
					for(int k=0;k<arrLimitSpeedMap.length;k++){
						if(StringHelper.isEmpty(arrLimitSpeedMap[k])) continue;
						String[] arrLimitSpeedMapTmp = StringHelper.split(arrLimitSpeedMap[k], "=");
						log.debug(arrLimitSpeedMapTmp[0]+"="+arrLimitSpeedMapTmp[1]);
						limitSpeedMapA.put(arrLimitSpeedMapTmp[0], new Integer(arrLimitSpeedMapTmp[1]));
					}
					checkPointBean.setLimitSpeedMap(limitSpeedMapA);
				}
				
				
				if(StringHelper.isNotEmpty(lowerLimitSpeed)){
					checkPointBean.setLowerLimitSpeed(Integer.parseInt(lowerLimitSpeed));
					this.setCheckLowerSpeed(true);
				}
				if(StringHelper.isNotEmpty(lowerLimitSpeedMap)){
					String[] arrLowerLimitSpeedMap = StringHelper.split(lowerLimitSpeedMap, ",");
					Map<String,Integer> lowerLimitSpeedMapA = new HashMap<String,Integer>();
					for(int k=0;k<arrLowerLimitSpeedMap.length;k++){
						if(StringHelper.isEmpty(arrLowerLimitSpeedMap[k])) continue;
						String[] arrLowerLimitSpeedMapTmp = StringHelper.split(arrLowerLimitSpeedMap[k], "=");
						log.debug(arrLowerLimitSpeedMapTmp[0]+"="+arrLowerLimitSpeedMapTmp[1]);
						lowerLimitSpeedMapA.put(arrLowerLimitSpeedMapTmp[0], new Integer(arrLowerLimitSpeedMapTmp[1]));
					}
					checkPointBean.setLimitSpeedMap(lowerLimitSpeedMapA);
				}
				
				if(StringHelper.isNotEmpty(catchPointMap)){
					String[] arrCatchPointMap = StringHelper.split(catchPointMap, ",");
					Map<String,Integer> catchPointMapA = new HashMap<String,Integer>();
					for(int k=0;k<arrCatchPointMap.length;k++){
						if(StringHelper.isEmpty(arrCatchPointMap[k])) continue;
						String[] arrCatchPointMapTmp = StringHelper.split(arrCatchPointMap[k], "=");
						log.debug("启拍点["+arrCatchPointMapTmp[0]+"="+arrCatchPointMapTmp[1]+"]");
						catchPointMapA.put(arrCatchPointMapTmp[0], new Integer(arrCatchPointMapTmp[1]));
					}
					checkPointBean.setCatchPointMap(catchPointMapA);
				}				
				
				if(checkPointBean.getDistance()>0){
					if(checkPointBean.getLowerLimitSpeed()>0){
						if(checkPointBean.getLowestspeedTimeLimit()>this.clearIntervalMillis) 
							this.clearIntervalMillis = checkPointBean.getLowestspeedTimeLimit();
					}
					else if(checkPointBean.getOverspeedTimeLimit()<this.clearIntervalMillis){
						this.clearIntervalMillis = checkPointBean.getOverspeedTimeLimit();
					}					
				}

				
				checkPointBean.setPrevious(prefiousCheckPointBean);
				if(prefiousCheckPointBean!=null) prefiousCheckPointBean.setNext(checkPointBean);
				
				prefiousCheckPointBean = checkPointBean;
				if(j==0) firstCheckPointBean = checkPointBean;
				
				log.info("add checkpoint:"+deviceIds+" "+distance+" "+lowerLimitSpeed);
			}			
			
			SectionBean sectionBean = new SectionBean();
			sectionBean.setName(sectionName);
			sectionBean.setDirection(sectionDirection);
			sectionBean.setDeviceId(sectionDeviceId);
			sectionBean.setFirstCheckPointBean(firstCheckPointBean);
			//aosBean.setCheckPointList(checkPointList);
			
			this.sectionsList.add(sectionBean);
			log.info("size = " + this.sectionsList.size());
		}
		
		/**
		 * 如果需要检测低速限制，则在清理间隔时间上加上30%
		 */
		if(this.isCheckLowerSpeed()) this.clearIntervalMillis += this.clearIntervalMillis*this.getLowerSpeedTimeLimitFactor();
		
		log.debug("定时清理时限："+DateHelper.parseMillisecond(this.clearIntervalMillis));
	}
	
	/**
	 * 检测区间超速
	 */
	public boolean process(RealtimeVehicleInfoBean realtimeVehicleInfoBean) throws Exception {
		//如果忽略单点超速，且已属于超速车辆，被前面的filter处理过，可以不用理会
		if(this.isIgnoreSinglePointOverspeed() && Integer.parseInt(realtimeVehicleInfoBean.getSpeed())>Integer.parseInt(realtimeVehicleInfoBean.getLimitSpeed())){
			log.debug("车牌号码："+realtimeVehicleInfoBean.getPlateNo()+"单点超速，不需要区间测速！");
			return true;
		}
		
		//只有车牌的后五位至少识别出指定位数，才有检测的必要
		if(this.getValidPlatePlacerNum(realtimeVehicleInfoBean.getPlateNo())<plateMatchBit){
			log.debug("车牌号码："+realtimeVehicleInfoBean.getPlateNo()+"识别率不够！");
			return true;
		}
		log.debug("size = " + this.sectionsList.size());
		String deviceId = realtimeVehicleInfoBean.getDeviceId();
		CheckPointBean checkPointBean = AreaOverspeedHelper.getCheckPointBean(this.sectionsList, deviceId,realtimeVehicleInfoBean.getDirectionCode());
		
		//不属于区间测速点的设备
		if(checkPointBean==null){
			log.debug("设备："+deviceId+"不属于区间超速设备！");
			return true;
		}
		
		String key = realtimeVehicleInfoBean.getPlateNo()+"_"+realtimeVehicleInfoBean.getPlateColor();
		//log.debug("KEY："+key);
		CarRecordBean crb = new CarRecordBean();
		crb.setRecordId(realtimeVehicleInfoBean.getRecordId());
		crb.setCatchTime(realtimeVehicleInfoBean.getCatchTime().getTime());
		crb.setDeviceId(realtimeVehicleInfoBean.getDeviceId());
		crb.setPlateColor(realtimeVehicleInfoBean.getPlateColor());
		crb.setPlateNo(realtimeVehicleInfoBean.getPlateNo());
		crb.setDeviceIp(realtimeVehicleInfoBean.getProtocolBean().getFromIp());
		crb.setDrivewayNo(realtimeVehicleInfoBean.getDrivewayNo());
		crb.setFeatureImagePath(realtimeVehicleInfoBean.getFeatureImagePath());
		crb.setPanoramaImagePath(realtimeVehicleInfoBean.getPanoramaImagePath());
		
		//不是第一个检测点
		if(checkPointBean.getPrevious()!=null){
			CheckPointBean previousCheckPointBean = checkPointBean.getPrevious();
			AreaOverspeedMonitorLinkedHashMapImpl previousCarRecordBeanMap = previousCheckPointBean.getCarRecordBeanMap();
			CarRecordBean previousCrb = previousCarRecordBeanMap.remove(key);
			//找到前一检测点完全匹配的车辆
			if(previousCrb!=null){
				log.debug("完全匹配成功！");
			}
			//匹配前一检测点相近的车辆
			else if(!this.isPlateFullMatch()){
				try{
					previousCrb = this.getCarRecordBean(previousCarRecordBeanMap, crb.getPlateNo(), crb.getPlateColor());
				}catch(Exception ex){
					log.error("查找匹配车辆时出错："+ex.getMessage(),ex);
				}
			}
			
			if(previousCrb!=null){
				log.debug("与之匹配的前检测点车辆记录-"+previousCrb.getDeviceId()+":"+previousCrb.getPlateNo()+"_"+previousCrb.getPlateColor());
				
				//计算是否超速
				float distance 	= previousCheckPointBean.getDistance()/1000F;					//距离（KM）
				float useTime 	= (crb.getCatchTime()-previousCrb.getCatchTime())/3600000F;		//所用时间（小时）	
//				log.debug("距离（KM）= " + distance + " 所用时间（小时）= " + useTime);
				if(useTime>0F){
					int speed 	= Math.round(distance/useTime);									//KM/H
					log.debug("区间速度："+speed);					
					
					//如果属于区间超速或超低速
					boolean isOverspeed = false, isOverLowSpeed = false;
					int limitSpeed = 0;
					int checkLimitSpeed = previousCheckPointBean.getLimitSpeed();
					int checkLowLimitSpeed = previousCheckPointBean.getLowerLimitSpeed();
					if(previousCheckPointBean.getLimitSpeedMap()!=null && previousCheckPointBean.getLimitSpeedMap().containsKey(previousCrb.getPlateColor())){
						checkLimitSpeed = previousCheckPointBean.getLimitSpeedMap().get(previousCrb.getPlateColor()).intValue();
					}
					if(previousCheckPointBean.getLowerLimitSpeedMap()!=null && previousCheckPointBean.getLowerLimitSpeedMap().containsKey(previousCrb.getPlateColor())){
						checkLowLimitSpeed = previousCheckPointBean.getLowerLimitSpeedMap().get(previousCrb.getPlateColor()).intValue();
					}					

					int catchPoint = 0;
					if(previousCheckPointBean.getCatchPointMap()!=null && previousCheckPointBean.getCatchPointMap().containsKey(previousCrb.getPlateColor())){
						catchPoint = previousCheckPointBean.getCatchPointMap().get(previousCrb.getPlateColor()).intValue();
					}		
					log.info(speed + "|" + catchPoint + "|" +checkLimitSpeed + "|" + previousCrb.getPlateColor());
					if((speed > catchPoint) && (speed > checkLimitSpeed)){
						if(this.getOverspeedLimitValue()>0 && speed>this.getOverspeedLimitValue()){
							log.warn("车辆速度:"+speed+" 大于合理值:"+this.getOverspeedLimitValue()+",舍弃!");
						}
						else{
							log.info("车辆："+realtimeVehicleInfoBean.getPlateNo()+"实际速度："+speed+",大于限速："+checkLimitSpeed+",超速了！");
							limitSpeed = checkLimitSpeed;
							isOverspeed = true;							
							checkPointBean.setLowerSpeedContinuousCount(checkPointBean.getLowerSpeedContinuousCount()-1);
						}
					}
					//如果区间超低速行驶
					else if(speed < checkLowLimitSpeed){
						checkPointBean.setLowerSpeedContinuousCount(checkPointBean.getLowerSpeedContinuousCount()+1);
						log.info("车辆："+realtimeVehicleInfoBean.getPlateNo()+"实际速度："+speed+",小于最低限速："+checkLowLimitSpeed+"！");
						limitSpeed = checkLowLimitSpeed;
						isOverLowSpeed = true;						
					}
					else if(speed > checkLowLimitSpeed){
						//当前车速大于最低限速,属于正常行驶的车辆
						checkPointBean.setLowerSpeedContinuousCount(checkPointBean.getLowerSpeedContinuousCount()-1);						
					}
					
					if(isOverspeed){
						try {			
							SectionBean sectionBean = AreaOverspeedHelper.getSectionBean(this.sectionsList, deviceId,realtimeVehicleInfoBean.getDirectionCode());
							
							//广播消息
							this.broadcast(sectionBean,previousCrb, crb,realtimeVehicleInfoBean.getDirectionCode(),speed, limitSpeed,previousCheckPointBean.getDistance());
							
							//保存区间超速图片
							this.save(sectionBean,previousCrb, crb,realtimeVehicleInfoBean.getDirectionCode(),speed, limitSpeed,previousCheckPointBean.getDistance());
						} catch (Exception e) {
							log.error(e.getMessage(),e);
						}											
					}
					
					//连续超低速行驶的车辆超过了上限，则认为当前为交通堵塞或有其它特殊天气状态
					if(checkPointBean.getLowerSpeedContinuousCount()>=checkPointBean.getLowerSpeedContinuousCountUpperLimit()){
						SectionBean sectionBean = AreaOverspeedHelper.getSectionBean(this.sectionsList, deviceId,realtimeVehicleInfoBean.getDirectionCode());
						log.warn("区间："+sectionBean.getName()+" 疑似塞车！");
						this.getLowerSpeedVioBeanList().clear();
					}
					else{
						if(isOverLowSpeed){
							//当前超低速行驶的车辆记录先记入列表
							LowerSpeedViolateBean lsv = new LowerSpeedViolateBean();
							SectionBean sectionBean = AreaOverspeedHelper.getSectionBean(this.sectionsList, deviceId,realtimeVehicleInfoBean.getDirectionCode());
							lsv.setSectionBean(sectionBean);
							lsv.setFromCarRecordBean(previousCrb);
							lsv.setToCarRecordBean(crb);
							lsv.setDirectionCode(realtimeVehicleInfoBean.getDirectionCode());
							lsv.setDistance(previousCheckPointBean.getDistance());
							lsv.setLimitSpeed(limitSpeed);
							lsv.setSpeed(speed);
							this.getLowerSpeedVioBeanList().add(lsv);
						}
						else if(checkPointBean.getLowerSpeedContinuousCount()<=0){
							int size = this.getLowerSpeedVioBeanList().size();
							while(size>0){
								LowerSpeedViolateBean lsv = this.getLowerSpeedVioBeanList().remove(0);
								this.save(lsv.getSectionBean(), lsv.getFromCarRecordBean(), lsv.getToCarRecordBean(), lsv.getDirectionCode(), lsv.getSpeed(), lsv.getLimitSpeed(), lsv.getDistance());
								size = this.getLowerSpeedVioBeanList().size();
							}
						}
					}					
				}
			}
			else{
				log.debug("未找到与之匹配的前检测点车辆记录！");
			}
		}
		
		//如果还有后续检测点
		if(checkPointBean.getNext()!=null){
			checkPointBean.getCarRecordBeanMap().put(key, crb);
			log.debug("还有后续检测点："+checkPointBean.getNext().getDeviceMap()+"，保存："+crb.getPlateNo()+"_"+crb.getPlateColor());
		}
		
		this.clear();
		
		return true;
	}
	
	/**
	 * 广播消息
	 * @param sectionBean
	 * @param fromCarRecordBean
	 * @param toCarRecordBean
	 * @param direction
	 * @param speed
	 * @param limitSpeed
	 * @param distance
	 */
	protected void broadcast(SectionBean sectionBean,CarRecordBean fromCarRecordBean,CarRecordBean toCarRecordBean,String direction,int speed,int limitSpeed,int distance){
		/*
		String sectionName = "未知路段";
		if(sectionBean!=null){
			sectionName = sectionBean.getName();
		}
		
		if(InfoBroadcastModule.isRunning()){								
			String areaOverSpeedMessage = ProtocolGeneratorFactory.getInstance().getProtocolGenerator().generateAreaOverspeedMessage(
					previousCrb,
					crb,
					-1,
					distance,
					speed,
					limitSpeed,
					sectionName				
			);		
			BroadcastProtocolBean areaOverSpeed = new BroadcastProtocolBean();
			areaOverSpeed.setRegisterProtocol(ProtocolHelper.PROTOCOL_REGISTER_AREA_OVERSPEED);
			areaOverSpeed.setDeviceId(sectionBean.getDeviceId());
			areaOverSpeed.setContent(areaOverSpeedMessage);
			
			InfoBroadcastModule.putMessage(areaOverSpeed);																
		}			
		*/
	}
	
	/**
	 * 清理过期数据
	 */
	protected void clear(){
		long startMillis = System.currentTimeMillis();
		long interval =  startMillis-this.lastClearTimeMillis;
		//log.debug("interval："+interval);
		int clearCount = 0;
		if(interval>=this.clearIntervalMillis){
			for (SectionBean sectionBean : this.sectionsList) {
				CheckPointBean tmpCheckPointBean = sectionBean.getFirstCheckPointBean();
				long timeLimit = tmpCheckPointBean.getOverspeedTimeLimit();
				if(tmpCheckPointBean.getLowerLimitSpeed()>0){
					timeLimit = tmpCheckPointBean.getLowestspeedTimeLimit();
					timeLimit += timeLimit*this.getLowerSpeedTimeLimitFactor();
				}
				do {
					Iterator<CarRecordBean> iterator = tmpCheckPointBean.getCarRecordBeanMap().values().iterator();
					while(iterator.hasNext()){
						CarRecordBean carRecordBean = (CarRecordBean)iterator.next();
						//已经过了(上下)超速的时限，注定不会超速的车辆记录
						if((startMillis-carRecordBean.getCatchTime())>timeLimit){
							iterator.remove();
							log.info("清理"+carRecordBean.getDeviceId()+":"+carRecordBean.getPlateNo()+"_"+carRecordBean.getPlateColor());
							clearCount++;
						}
						else{
							//因为使用LinkedHashMap,iterator()方法将按插入的顺序遍历
							//一旦当前车辆运行记录CarRecordBean的catchTime还未过期，则可认为其后续插入的记录也未过期
							break;	
						}
					}
				} while ((tmpCheckPointBean=tmpCheckPointBean.getNext())!=null);
			}
			
			this.lastClearTimeMillis = startMillis;
			log.info("清理数据"+clearCount+"条，耗时："+ (System.currentTimeMillis()-startMillis)+"毫秒！");				
		}		
	}
	
	/**
	 * 生成图片
	 * @param sectionBean
	 * @param fromCarRecordBean
	 * @param toCarRecordBean
	 * @param direction
	 * @param speed
	 * @param limitSpeed
	 * @param distance
	 */
	protected void save(SectionBean sectionBean,CarRecordBean fromCarRecordBean,CarRecordBean toCarRecordBean,String direction,int speed,int limitSpeed,int distance){
		try{
			//休眠等待前端设备生成全景图和特写图，如果前端设备先生成图片后发送消息，则休眠可以取消
			if(this.getSleepMillisecond()>0L){
				Thread.sleep(this.getSleepMillisecond());
			}
			
			Map<String,DeviceInfoBean> deviceMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
			
			//合并第一个检测点的全景图与特写图，垂直排列
			java.util.Date startCatchTime = new java.util.Date(fromCarRecordBean.getCatchTime());
//			String panoramaImagePath 	= fromCarRecordBean.getPanoramaImagePath().substring(0,fromCarRecordBean.getPanoramaImagePath().lastIndexOf("/")+1) + URLEncoder.encode(fromCarRecordBean.getPanoramaImagePath().substring(fromCarRecordBean.getPanoramaImagePath().lastIndexOf("/")+1),"UTF-8");
			String featureImagePath 	= fromCarRecordBean.getFeatureImagePath().substring(0,fromCarRecordBean.getFeatureImagePath().lastIndexOf("/")+1) + URLEncoder.encode(fromCarRecordBean.getFeatureImagePath().substring(fromCarRecordBean.getFeatureImagePath().lastIndexOf("/")+1),"UTF-8");	
			
//			byte[] imgByteFrom = ImageHelper.compose(ImageHelper.getImageBytes(featureImagePath),ImageHelper.getImageBytes(panoramaImagePath), false);
			byte[] imgByteFrom = ImageHelper.getImageBytes(featureImagePath);
			String startAddress = fromCarRecordBean.getDeviceId();
			if(deviceMap.containsKey(fromCarRecordBean.getDeviceId())){
				DeviceInfoBean dib = (DeviceInfoBean)deviceMap.get(fromCarRecordBean.getDeviceId());
				startAddress = new StringBuilder(dib.getRoadName()).toString();
			}				
//			String[] waterMarkArr = new String[]{
//					"区间路段：" + sectionBean.getName() + " 驶入地点："+address+" 驶入时间："+DateHelper.dateToString(catchTime,"yyyy-MM-dd HH:mm:ss")
//			};	
//			imgByteFrom = ImageHelper.createWaterMark(imgByteFrom,waterMarkArr, this.getWaterMarkFontSize(), this.getWaterMarkFontHeight(), this.getWaterMarkFontColor(), this.getWaterMarkBgColor(), this.getWaterMarkLeftMargin(),this.getWaterMarkTopMargin());
						
			//合并第二个检测点的全景图与特写图，垂直排列
			java.util.Date endCatchTime = new java.util.Date(toCarRecordBean.getCatchTime());
//			panoramaImagePath 	= toCarRecordBean.getPanoramaImagePath().substring(0,toCarRecordBean.getPanoramaImagePath().lastIndexOf("/")+1) + URLEncoder.encode(toCarRecordBean.getPanoramaImagePath().substring(toCarRecordBean.getPanoramaImagePath().lastIndexOf("/")+1),"UTF-8");
			featureImagePath 	= toCarRecordBean.getFeatureImagePath().substring(0,toCarRecordBean.getFeatureImagePath().lastIndexOf("/")+1) + URLEncoder.encode(toCarRecordBean.getFeatureImagePath().substring(toCarRecordBean.getFeatureImagePath().lastIndexOf("/")+1),"UTF-8");
			
//			byte[] imgByteTo = ImageHelper.compose(ImageHelper.getImageBytes(featureImagePath),ImageHelper.getImageBytes(panoramaImagePath), false);
			byte[] imgByteTo = ImageHelper.getImageBytes(featureImagePath);
			String endAddress = toCarRecordBean.getDeviceId();
			if(deviceMap.containsKey(toCarRecordBean.getDeviceId())){
				DeviceInfoBean dib = (DeviceInfoBean)deviceMap.get(toCarRecordBean.getDeviceId());
				endAddress = new StringBuilder(dib.getRoadName()).toString();
			}				
			long useTime = toCarRecordBean.getCatchTime()-fromCarRecordBean.getCatchTime();
			
//			if(speed>limitSpeed){
//				waterMarkArr = new String[]{
//						"驶出地点："+address +" 驶出时间："+DateHelper.dateToString(catchTime,"yyyy-MM-dd HH:mm:ss") + "    行驶距离："+distance+"米    耗时："+DateHelper.parseMillisecond(useTime),
//						"平均车速："+speed+"公里/小时    最高限速："+limitSpeed+"公里/小时"
//				};				
//			}
//			else{
//				waterMarkArr = new String[]{
//						"驶出地点："+address +" 驶出时间："+DateHelper.dateToString(catchTime,"yyyy-MM-dd HH:mm:ss") + "    行驶距离："+distance+"米    耗时："+DateHelper.parseMillisecond(useTime),
//						"平均车速："+speed+"公里/小时    最低限速："+limitSpeed+"公里/小时"
//				};				
//			}				
//			imgByteTo = ImageHelper.createWaterMark(imgByteTo,waterMarkArr, this.getWaterMarkFontSize(), this.getWaterMarkFontHeight(), this.getWaterMarkFontColor(), this.getWaterMarkBgColor(), this.getWaterMarkLeftMargin(),this.getWaterMarkTopMargin());
			
			//合并两个检测点的图片，垂直排列
			byte[] fileBytes = ImageHelper.compose(imgByteFrom, imgByteTo, false);
			int[] imageSize = ImageHelper.getImageSize(fileBytes);
			
			byte[] waterMarkImage = ImageHelper.createColorImage(Color.BLACK, imageSize[0], this.getWaterMarkBgHeight());
			
			String[] waterMarkArr = null;
			if(speed>limitSpeed){
				DecimalFormat decimalFormat=new DecimalFormat("#.0");
				float overSpeedRate = ((speed-limitSpeed)/(float)limitSpeed)*100.0f;
				waterMarkArr = new String[]{
						sectionBean.getName()+ " 距离:"+distance+"米 耗时:"+DateHelper.parseMillisecond(useTime)+" 平均车速:"+speed+"KM/H (超速"+decimalFormat.format(overSpeedRate)+"%) 最高限速:"+limitSpeed+"KM/H"+
						" [起点:"+startAddress +	" "+DateHelper.dateToString(startCatchTime,"yyyy年MM月dd日HH时mm分ss秒]")+" [终点:"+endAddress +" "+DateHelper.dateToString(endCatchTime,"yyyy年MM月dd日HH时mm分ss秒]")
				};
			}else {
				waterMarkArr = new String[]{
						sectionBean.getName()+ " 距离:"+distance+"米 耗时:"+DateHelper.parseMillisecond(useTime)+" 平均车速:"+speed+"公里/小时 最低限速:"+limitSpeed+"公里/小时"+
						" [起点:"+startAddress +	" 时间:"+DateHelper.dateToString(startCatchTime,"yyyy年MM月dd日HH时mm分ss秒]")+" [终点:"+endAddress +" 时间："+DateHelper.dateToString(endCatchTime,"yyyy年MM月dd日HH时mm分ss秒]")
				};
			}
			
			waterMarkImage = ImageHelper.createWaterMark(
					waterMarkImage,
					waterMarkArr,
					this.getWaterMarkFontSize(),
					this.getWaterMarkFontHeight(),
					this.getWaterMarkFontColor(),
					this.getWaterMarkBgColor(),
					this.getWaterMarkLeftMargin(),
					this.getWaterMarkTopMargin());	
			
			//合并第水印与原始图片
			byte[] imageByte = ImageHelper.compose(waterMarkImage, fileBytes, true);
			
			//实速值不满三位前面加0
			String strSpeed = String.valueOf(speed);
			while(strSpeed.length()<3) strSpeed = "0"+strSpeed;
			
			//限速值不满三位前面加0
			String strLimitSpeed = String.valueOf(limitSpeed);
			while(strLimitSpeed.length()<3) strLimitSpeed = "0"+strLimitSpeed;
			
			String laneNo = toCarRecordBean.getDrivewayNo();
			if(laneNo.length()==1) laneNo = "0" + laneNo;
			
			StringBuffer fileNameBuffer = new StringBuffer("X04")
									.append("R").append(sectionBean.getDeviceId())
									.append("D").append(direction)
									.append("L").append("01")
									.append("I").append(strLimitSpeed)
									.append("V").append(strSpeed)
									.append("N").append((int)(Math.random() * (99999 - 10000)) + 10000)
									.append("T").append(DateHelper.dateToString(endCatchTime,"yyyyMMddHHmmssSSS"))//									
									.append("&").append(toCarRecordBean.getPlateNo())
									.append("&").append(toCarRecordBean.getPlateColor())
									.append("S11.JPG");
			String fileName = fileNameBuffer.toString();
			//生成图片文件
//			String filePath = this.getAreaOverspeedSaveDir()+"/04/" + sectionBean.getDeviceId() + "/"+DateHelper.dateToString(endCatchTime, "yyyyMMdd")+"/" + DateHelper.dateToString(endCatchTime, "HH") +"/";	
			String filePath = this.getAreaOverspeedSaveDir() + sectionBean.getDeviceId() + "/"+DateHelper.dateToString(endCatchTime, "yyyyMMdd")+"/"+DateHelper.dateToString(endCatchTime, "HH")+"/";	
			FileHelper.createDir(filePath);
			filePath += fileName;
			log.info("生成图片："+filePath);
			FileHelper.writeFile(imageByte, filePath);	
			
			//导入违法临时记录表
			insertViolationRecordTemp(sectionBean,toCarRecordBean,strSpeed,strLimitSpeed,fileName);
		}catch (Exception e) {
			log.error("保存图片失败："+e.getMessage(),e);
		}	
	}	
	
	public void insertViolationRecordTemp(SectionBean sectionBean,CarRecordBean toCarRecordBean,String speed,String limitSpeed,String fileName) {	
		Connection conn = null;
        PreparedStatement preStatement = null;
       	try{
       		conn = ConnectionProviderFactory.getInstance().getConnectionProvider().getConnection();
       		//id,violate_time,road_id,device_id,direction_code,line,speed,limit_speed,create_time,plate,plate_type_id,file_path_1,file_path_2,file_path_3,file_path_4,video_file_path
			preStatement = conn.prepareStatement("insert into t_its_violate_record_temp(id,violate_time,road_id,device_id,direction_code,line,speed,limit_speed,create_time,status,plate,plate_type_id,image_path_1) values (?,?,?,?,?,?,?,?,?,'N',?,?,?)");
			preStatement.setLong(1, (long)SequenceFactory.getInstance().getViolateRecordTempSequence());
			preStatement.setTimestamp(2, new Timestamp(toCarRecordBean.getCatchTime()));
			Map dibMap = DeviceInfoLoaderFactory.getInstance().getDeviceMap();
			DeviceInfoBean dib = (DeviceInfoBean)dibMap.get(sectionBean.getDeviceId());
			preStatement.setString(3, dib.getRoadId());
			preStatement.setString(4, sectionBean.getDeviceId());
			preStatement.setString(5, dib.getDirectionCode());
			preStatement.setString(6, "01");
			preStatement.setString(7, speed);
			preStatement.setString(8, limitSpeed);
			preStatement.setTimestamp(9, new Timestamp(new java.util.Date().getTime()));
			preStatement.setString(10, toCarRecordBean.getPlateNo());			
			preStatement.setString(11, SystemConstant.getInstance().PLATE_TYPE_ID_ROADLOUSE);
			
			String pathPrefix = "http://192.168.88.100:81/veh/" + sectionBean.getDeviceId() + "/" + DateHelper.dateToString(new Date(toCarRecordBean.getCatchTime()), "yyyyMMdd") + "/"+ DateHelper.dateToString(new Date(toCarRecordBean.getCatchTime()), "HH") +"/";
			preStatement.setString(12, pathPrefix + fileName);		

			preStatement.execute();
       	}
       	catch(Exception ex){
			log.error("数据入库失败：" + ex.getMessage(), ex);
       	}
       	finally{
       		DatabaseHelper.close(null, preStatement);      
       		try{
				ConnectionProviderFactory.getInstance().getConnectionProvider().closeConnection(conn);
			}catch(Exception ex1){}
       	}
	}
	
	/**
	 * 根据车牌号码和颜色获取最匹配的车辆记录
	 * 如粤B12345 则与之匹配的车辆有：　..1234. , ...2345
	 * @param carRecordBeanMap
	 * @param plateNo
	 * @param plateColor
	 * @return
	 */
	protected CarRecordBean getCarRecordBean(Map carRecordBeanMap,String plateNo,String plateColor) throws Exception{
		CarRecordBean carRecordBean = null;
		String plateNoA = plateNo.substring(2);
		int plateNoALen = plateNoA.length();
		Iterator<CarRecordBean> iterator = carRecordBeanMap.values().iterator();
		while(iterator.hasNext()){
			CarRecordBean tmpCarRecordBean = (CarRecordBean)iterator.next();
			if(!tmpCarRecordBean.getPlateColor().equals(plateColor)) continue;
			String tmpPlateNo = tmpCarRecordBean.getPlateNo().substring(2);
			if(tmpPlateNo.startsWith(plateNoA.substring(0,plateMatchBit)) || 
				tmpPlateNo.endsWith(plateNoA.substring(plateNoALen-plateMatchBit))){
				
				iterator.remove();
				carRecordBean = tmpCarRecordBean;
				break;	
			}
		}
		return carRecordBean;
	}
	
	/**
	 * 获取识别的有效车牌号码位数（排除前两位，如粤B12345，只统计12345的识别数）
	 * @param plateNo
	 * @return
	 */
	protected int getValidPlatePlacerNum(String plateNo){
		int count = 0;
		if(StringHelper.isEmpty(plateNo)) return 0;
		int len = plateNo.length();
		for(int i=2;i<len;i++){
			if(plateNo.charAt(i)!=PLATE_PLACEHOLDER){
				count++;
			}
		}
		return count;
	}		

	public String getAreaOverspeedSaveDir() {
		return areaOverspeedSaveDir;
	}

	public void setAreaOverspeedSaveDir(String areaOverspeedSaveDir) {
		this.areaOverspeedSaveDir = areaOverspeedSaveDir;
	}

	public int getImageHeight() {
		return imageHeight;
	}

	public void setImageHeight(int imageHeight) {
		this.imageHeight = imageHeight;
	}

	public int getImageWidth() {
		return imageWidth;
	}

	public void setImageWidth(int imageWidth) {
		this.imageWidth = imageWidth;
	}

	public long getSleepMillisecond() {
		return sleepMillisecond;
	}

	public void setSleepMillisecond(long sleepMillisecond) {
		this.sleepMillisecond = sleepMillisecond;
	}

	public long getClearIntervalMillis() {
		return clearIntervalMillis;
	}

	public void setClearIntervalMillis(long clearIntervalMillis) {
		this.clearIntervalMillis = clearIntervalMillis;
	}

	public long getLastClearTimeMillis() {
		return lastClearTimeMillis;
	}

	public void setLastClearTimeMillis(long lastClearTimeMillis) {
		this.lastClearTimeMillis = lastClearTimeMillis;
	}

	public char getPLATE_PLACEHOLDER() {
		return PLATE_PLACEHOLDER;
	}

	public void setPLATE_PLACEHOLDER(char plate_placeholder) {
		PLATE_PLACEHOLDER = plate_placeholder;
	}

	public List<SectionBean> getSectionsList() {
		return sectionsList;
	}

	public void setSectionsList(List<SectionBean> sectionsList) {
		this.sectionsList = sectionsList;
	}	

	public int getWaterMarkBgHeight() {
		return waterMarkBgHeight;
	}

	public void setWaterMarkBgHeight(int waterMarkBgHeight) {
		this.waterMarkBgHeight = waterMarkBgHeight;
	}

	public Color getWaterMarkBgColor() {
		return waterMarkBgColor;
	}

	public void setWaterMarkBgColor(Color waterMarkBgColor) {
		this.waterMarkBgColor = waterMarkBgColor;
	}

	public Color getWaterMarkFontColor() {
		return waterMarkFontColor;
	}

	public void setWaterMarkFontColor(Color waterMarkFontColor) {
		this.waterMarkFontColor = waterMarkFontColor;
	}

	public int getWaterMarkFontHeight() {
		return waterMarkFontHeight;
	}

	public void setWaterMarkFontHeight(int waterMarkFontHeight) {
		this.waterMarkFontHeight = waterMarkFontHeight;
	}

	public int getWaterMarkFontSize() {
		return waterMarkFontSize;
	}

	public void setWaterMarkFontSize(int waterMarkFontSize) {
		this.waterMarkFontSize = waterMarkFontSize;
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

	public int getOverspeedLimitValue() {
		return overspeedLimitValue;
	}

	public void setOverspeedLimitValue(int overspeedLimitValue) {
		this.overspeedLimitValue = overspeedLimitValue;
	}

	public boolean isPlateFullMatch() {
		return plateFullMatch;
	}

	public void setPlateFullMatch(boolean plateFullMatch) {
		this.plateFullMatch = plateFullMatch;
	}

	public int getPlateMatchBit() {
		return plateMatchBit;
	}

	public void setPlateMatchBit(int plateMatchBit) {
		this.plateMatchBit = plateMatchBit;
	}

	public boolean isCheckLowerSpeed() {
		return checkLowerSpeed;
	}

	public void setCheckLowerSpeed(boolean checkLowerSpeed) {
		this.checkLowerSpeed = checkLowerSpeed;
	}

	public float getLowerSpeedTimeLimitFactor() {
		return lowerSpeedTimeLimitFactor;
	}

	public void setLowerSpeedTimeLimitFactor(float lowerSpeedTimeLimitFactor) {
		this.lowerSpeedTimeLimitFactor = lowerSpeedTimeLimitFactor;
	}

	public boolean isIgnoreSinglePointOverspeed() {
		return ignoreSinglePointOverspeed;
	}

	public void setIgnoreSinglePointOverspeed(boolean ignoreSinglePointOverspeed) {
		this.ignoreSinglePointOverspeed = ignoreSinglePointOverspeed;
	}

	public List<LowerSpeedViolateBean> getLowerSpeedVioBeanList() {
		return lowerSpeedVioBeanList;
	}

	public void setLowerSpeedVioBeanList(List<LowerSpeedViolateBean> lowerSpeedVioBeanList) {
		this.lowerSpeedVioBeanList = lowerSpeedVioBeanList;
	}	

}
