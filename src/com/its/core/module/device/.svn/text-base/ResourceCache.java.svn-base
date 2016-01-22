/**
 * 
 */
package com.its.core.module.device;

/**
 * 创建日期 2013-1-30 下午03:28:49
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class ResourceCache {
	
	private static boolean PLATE_MATCH_IGNORE_FIRST_CHAR = true;
	
	private static int PLATE_MATCH_LEAST_CHECK_BITNUM = 3;
	
	//车辆监控MAP
	private static PlateMonitorMap plateMonitorMap = null;
	
	public static void init(boolean plateMatchIgnoreFirstChar,int plateMatchLeastCheckBitnum,IPlateMonitorMapInit plateMonitorMapInit){
		PLATE_MATCH_IGNORE_FIRST_CHAR = plateMatchIgnoreFirstChar;
		PLATE_MATCH_LEAST_CHECK_BITNUM = plateMatchLeastCheckBitnum;		
		if(plateMonitorMap==null){
			plateMonitorMap = new PlateMonitorMap(PLATE_MATCH_IGNORE_FIRST_CHAR,PLATE_MATCH_LEAST_CHECK_BITNUM,plateMonitorMapInit);
		}	
		plateMonitorMap.reload();
	}
	
	public static PlateMonitorMap getPlateMonitorMap(){
		return plateMonitorMap;
	}

}
