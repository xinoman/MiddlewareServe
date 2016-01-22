/**
 * 
 */
package com.its.core.module.device.vehicle.filter.helper;

import java.util.Map;

/**
 * 创建日期 2012-12-6 下午03:29:43
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class CheckPointBean {
	
	//该检测点的所有设备编号
	private Map deviceMap = null;
	
	//与当前检测点到下一检测点之间的距离，单位：米
	private int distance = 0;
	
	//从当前设备到下一设备路段之间的上限速，单位：KM/H
	private int limitSpeed = 0;
	
	//从当前设备到下一设备路段之间的下限速，单位：KM/H
	private int lowerLimitSpeed = 0;	
	
	//根据车牌颜色设置的最高限速启拍点
	private Map<String,Integer> catchPointMap = null;	
	
	//根据车牌颜色区分不同的最高限速
	private Map<String,Integer> limitSpeedMap = null;
	
	//根据车牌颜色区分不同的最低限速
	private Map<String,Integer> lowerLimitSpeedMap = null;
	
//	
//	/**
//	 * 低速检测开关，该开关的值随路面的实际情况变化，当发生堵车等情形时，开关自动关闭
//	 */
//	private boolean lowerSpeedCheckSwitcher = false;
	
	//连续的低于最低限速的行驶车辆统计
	private int lowerSpeedContinuousCount = 0;	
	
	//连续的低于最低限速的行驶车辆统计次数上限
	private int lowerSpeedContinuousCountUpperLimit = 3;
	
	//前一个检测点
	private CheckPointBean previous = null;
	
	//后一个检测点
	private CheckPointBean next = null;
	
	//记录通过该checkpoing的车辆信息
	private AreaOverspeedMonitorLinkedHashMapImpl carRecordBeanMap = null;
	
	public CheckPointBean(){
		carRecordBeanMap = new AreaOverspeedMonitorLinkedHashMapImpl(64);
	}
	
	public CheckPointBean(Map deviceMap,int distance,int limitSpeed){
		/**
		 * 初始容量：假定每两秒钟一套设备检测一辆车
		 */
		int initialCapacity = (int)((distance/1000.f/limitSpeed)*3600f*deviceMap.size()/2);
		carRecordBeanMap = new AreaOverspeedMonitorLinkedHashMapImpl(initialCapacity);
	}
	
	/**
	 * @return 从当前检测点到下一检测点的超速时限，即超过这个时限值即表示未超速，单位：millisecond
	 */
	public long getOverspeedTimeLimit(){
		float timeLimit = ((float)this.getDistance()/(float)this.getLimitSpeed())*3600F;
		return (long)timeLimit;
	}
	
	/**
	 * @return 从当前检测点到下一检测点的最低速度时限，即小于这个时限值即表示超低速行驶，单位：millisecond
	 */
	public long getLowestspeedTimeLimit(){
		float timeLimit = ((float)this.getDistance()/(float)this.getLowerLimitSpeed())*3600F;
		return (long)timeLimit;
	}	
	
	public Map getDeviceMap() {
		return deviceMap;
	}
	public void setDeviceMap(Map deviceMap) {
		this.deviceMap = deviceMap;
	}
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getLimitSpeed() {
		return limitSpeed;
	}
	public void setLimitSpeed(int limitSpeed) {
		this.limitSpeed = limitSpeed;
	}
	public AreaOverspeedMonitorLinkedHashMapImpl getCarRecordBeanMap() {
		return carRecordBeanMap;
	}
	public void setCarRecordBeanMap(
			AreaOverspeedMonitorLinkedHashMapImpl carRecordBeanMap) {
		this.carRecordBeanMap = carRecordBeanMap;
	}
	public CheckPointBean getNext() {
		return next;
	}
	public void setNext(CheckPointBean next) {
		this.next = next;
	}
	
	public CheckPointBean getPrevious() {
		return previous;
	}
	public void setPrevious(CheckPointBean previous) {
		this.previous = previous;
	}

	public int getLowerLimitSpeed() {
		return lowerLimitSpeed;
	}

	public void setLowerLimitSpeed(int lowerLimitSpeed) {
		this.lowerLimitSpeed = lowerLimitSpeed;
	}

	public int getLowerSpeedContinuousCount() {
		return lowerSpeedContinuousCount;
	}

	public void setLowerSpeedContinuousCount(int lowerSpeedContinuousCount) {
		if(lowerSpeedContinuousCount>this.lowerSpeedContinuousCountUpperLimit){
			this.lowerSpeedContinuousCount = this.lowerSpeedContinuousCountUpperLimit;
		}
		else if(lowerSpeedContinuousCount<0){
			this.lowerSpeedContinuousCount = 0;
		}
		else{
			this.lowerSpeedContinuousCount = lowerSpeedContinuousCount;
		}
	}

	public int getLowerSpeedContinuousCountUpperLimit() {
		return lowerSpeedContinuousCountUpperLimit;
	}

	public void setLowerSpeedContinuousCountUpperLimit(int lowerSpeedContinuousCountUpperLimit) {
		this.lowerSpeedContinuousCountUpperLimit = lowerSpeedContinuousCountUpperLimit;
	}

	public Map<String, Integer> getLimitSpeedMap() {
		return limitSpeedMap;
	}

	public void setLimitSpeedMap(Map<String, Integer> limitSpeedMap) {
		this.limitSpeedMap = limitSpeedMap;
	}

	public Map<String, Integer> getLowerLimitSpeedMap() {
		return lowerLimitSpeedMap;
	}

	public void setLowerLimitSpeedMap(Map<String, Integer> lowerLimitSpeedMap) {
		this.lowerLimitSpeedMap = lowerLimitSpeedMap;
	}

	public Map<String, Integer> getCatchPointMap() {
		return catchPointMap;
	}

	public void setCatchPointMap(Map<String, Integer> catchPointMap) {
		this.catchPointMap = catchPointMap;
	}

}
