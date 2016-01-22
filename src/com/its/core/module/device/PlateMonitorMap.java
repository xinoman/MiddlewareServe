/**
 * 
 */
package com.its.core.module.device;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.util.PlateHelper;
import com.its.core.util.StringHelper;

/**
 * 创建日期 2013-1-30 下午03:29:55
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class PlateMonitorMap {
	private static final Log log = LogFactory.getLog(PlateMonitorMap.class);
	
	//是否忽略第一个字符，如果为true,则使用占位符替换
	private boolean ignoreFirstChar = true;
	
	//模糊匹配时最少检测位数
	private int leastCheckBitNum = 3; 
	
	/**
	 * 黑名单Map
	 * Key:号牌号码_号牌颜色 (plate_plateColorCode)
	 * Value:BlacklistBean
	 */
	private Map<String,List<BlacklistBean>> blacklistMap = Collections.synchronizedMap(new HashMap<String,List<BlacklistBean>>());

	private IPlateMonitorMapInit plateMonitorMapInit = null;
	
	public PlateMonitorMap(boolean ignoreFirstChar,int leastCheckBitNum,IPlateMonitorMapInit plateMonitorMapInit){
		this.ignoreFirstChar = ignoreFirstChar;
		this.leastCheckBitNum = leastCheckBitNum;
		this.plateMonitorMapInit = plateMonitorMapInit;
	}
	
	/**
	 * 添加黑名单
	 * @param blacklist
	 */
	public void put(BlacklistBean blacklist){
		String matchPlate = blacklist.getMatchPlate();
		if(StringHelper.isEmpty(matchPlate)){
			matchPlate = PlateHelper.getMatchPlate(blacklist.getPlate(), this.ignoreFirstChar,0);
			blacklist.setMatchPlate(matchPlate);
		}
		
		String plateColorCode = blacklist.getPlateColorCode();
		if(StringHelper.isEmpty(plateColorCode)){
			plateColorCode = "";
		}
		String key1 = blacklist.getPlate()+"_"+plateColorCode;		
		List<BlacklistBean> blacklistList1 = blacklistMap.get(key1);
		if(blacklistList1==null){
			blacklistList1 = new ArrayList<BlacklistBean>();
			log.debug("put blacklist = "+key1);
			blacklistMap.put(key1, blacklistList1);
		}
		else{
			//从blacklistList中清除原来的历史记录
			Iterator<BlacklistBean> blacklistIterator = blacklistList1.iterator();
			while(blacklistIterator.hasNext()){
				BlacklistBean tmp = blacklistIterator.next();
				if(tmp.getId().equals(blacklist.getId())){
					blacklistIterator.remove();
				}
			}			
		}
		blacklistList1.add(blacklist);

		if(matchPlate!=null && !matchPlate.equals(blacklist.getPlate())){
			String key2 = matchPlate+"_"+plateColorCode;
			List<BlacklistBean> blacklistList2 = blacklistMap.get(key2);
			if(blacklistList2==null){
				blacklistList2 = new ArrayList<BlacklistBean>();
				log.debug("put blacklist = "+key2);
				blacklistMap.put(key2, blacklistList2);
			}
			else{
				//从blacklistList中清除原来的历史记录
				Iterator<BlacklistBean> blacklistIterator = blacklistList2.iterator();
				while(blacklistIterator.hasNext()){
					BlacklistBean tmp = blacklistIterator.next();
					if(tmp.getId().equals(blacklist.getId())){
						blacklistIterator.remove();
					}
				}				
			}
			blacklistList2.add(blacklist);
		}
	}
	
	/**
	 * 黑名单撤控
	 * @param blacklistId
	 */
	public void remove(String blacklistId){
		Iterator iterator = this.blacklistMap.values().iterator();
		while(iterator.hasNext()){
			List<BlacklistBean> blacklistList = (List<BlacklistBean>)iterator.next();
			Iterator blacklistIterator = blacklistList.iterator();
			while(blacklistIterator.hasNext()){
				BlacklistBean blacklist = (BlacklistBean)blacklistIterator.next();
				if(blacklist.getId().equals(blacklistId)){
					log.debug("remove "+blacklist.getId());
					blacklistIterator.remove();
				}
			}
		}
	}
	
	/**
	 * 移出所有的黑名单
	 */
	public void removeAll(){
		Iterator iterator = this.blacklistMap.values().iterator();
		while(iterator.hasNext()){
			List<BlacklistBean> blacklistList = (List<BlacklistBean>)iterator.next();
			blacklistList.clear();
			blacklistList = null;
		}
		this.blacklistMap.clear();
	}
	
	/**
	 * 重新装载黑名单信息
	 */
	public void reload(){
		this.removeAll();
		try {
			List<BlacklistBean> blacklistList = this.getPlateMonitorMapInit().load();
			int size = blacklistList.size();
			for(int i=0;i<size;i++){
				this.put(blacklistList.get(i));
			}
		} catch (Exception e) {
			log.error("重新装载黑名单时出错："+e.getMessage(),e);
		}
	}
	
	/**
	 * 匹配黑名单，返回所有符合匹配条件的黑名单记录
	 * @param plate
	 * @param plateColorCode
	 * @return
	 */
	public List<BlacklistBean> match(String plate,String plateColorCode){
		List<BlacklistBean> matchList = new ArrayList<BlacklistBean>();
		String matchPlate = PlateHelper.getMatchPlate(plate, this.ignoreFirstChar,0);
		boolean compareMatchPlate = !(StringHelper.isEmpty(matchPlate) || plate.equals(matchPlate));
		int validNumber = PlateHelper.getValidNumber(plate);
			
		compareMatchPlate = compareMatchPlate&(validNumber>=this.leastCheckBitNum);
		
		String key = plate+"_";
		List<BlacklistBean> blacklistList1 = this.blacklistMap.get(key);
		if(blacklistList1!=null){
			this.addNew(plate,matchList, blacklistList1);
			if(blacklistList1.size()==0){
				this.blacklistMap.remove(key);
			}
		}
		
		if(compareMatchPlate){
			key = matchPlate+"_";
			List<BlacklistBean> blacklistList2 = this.blacklistMap.get(key);
			if(blacklistList2!=null){
				this.addNew(plate,matchList, blacklistList2);
				if(blacklistList2.size()==0){
					this.blacklistMap.remove(key);
				}
			}
		}
		
		if(StringHelper.isNotEmpty(plateColorCode)){
			key = plate+"_"+plateColorCode;
			List<BlacklistBean> blacklistList3 = this.blacklistMap.get(key);
			if(blacklistList3!=null){
				this.addNew(plate,matchList, blacklistList3);
				if(blacklistList3.size()==0){
					this.blacklistMap.remove(key);
				}
			}
			
			if(compareMatchPlate){
				key = matchPlate+"_"+plateColorCode;
				List<BlacklistBean> blacklistList4 = this.blacklistMap.get(key);
				if(blacklistList4!=null){
					this.addNew(plate,matchList, blacklistList4);
					if(blacklistList4.size()==0){
						this.blacklistMap.remove(key);
					}
				}
			}
		}
		return matchList;
	}
	
	/**
	 * 将newList中未过期且不存于oriList的黑名单信息添加到oriList中
	 * @param oriList
	 * @param newList
	 */
	private void addNew(String plate,List<BlacklistBean> oriList,List<BlacklistBean> newList){
		int oriSize = oriList.size();
		Iterator<BlacklistBean> newIterator = newList.iterator();
		while(newIterator.hasNext()){
			BlacklistBean blacklist = newIterator.next();
			if(blacklist.getStartTime()!=null && System.currentTimeMillis()<blacklist.getStartTime()){
				//newIterator.remove();
				continue;
			}
			
			//已过期，则删除黑名单
			if(blacklist.getEndTime()!=null && System.currentTimeMillis()>blacklist.getEndTime()){
				newIterator.remove();
				continue;
			}
			
			//号牌匹配位数小于最少匹配位数则不添加
			int currentMatchNumber = PlateHelper.getMatchNumber(plate, blacklist.getPlate());	
			if(currentMatchNumber<this.leastCheckBitNum){
				continue;
			}
			
			String blacklistId = blacklist.getId();
			
			boolean found = false;
			for(int j=0;j<oriSize;j++){
				if(blacklistId.equals(oriList.get(j).getId())){
					found = true;
					break;
				}
			}
			if(!found){
				oriList.add(blacklist);
			}			
		}		
	}

	public IPlateMonitorMapInit getPlateMonitorMapInit() {
		return plateMonitorMapInit;
	}

	public void setPlateMonitorMapInit(IPlateMonitorMapInit plateMonitorMapInit) {
		this.plateMonitorMapInit = plateMonitorMapInit;
	}

}
