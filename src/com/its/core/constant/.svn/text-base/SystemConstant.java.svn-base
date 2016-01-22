/**
 * 
 */
package com.its.core.constant;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-8-3 上午10:31:12
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class SystemConstant {
	private static final Log log = LogFactory.getLog(SystemConstant.class);
	
	private String propertiesFileName;
	private XMLProperties properties = null;
	
	private static SystemConstant systemConstant = new SystemConstant();
	
	private SystemConstant(){}
	
	public static SystemConstant getInstance() {
		return systemConstant;
	}
	
	public final static String WINDOWS_2003 = "2003";
	public final static String WINDOWS_XP = "XP";	
	public final static String WINDOWS_VISTA = "VISTA";
	public final static String WINDOWS_7 = "7";
	public final static String WINDOWS_SERVER_2008 = "2008";
	
	public final String DB_TYPE_ORACLE = "oracle";
	
	public final String TABLENAME_VIOLATE_RECORD_TEMP = "T_ITS_VIOLATE_RECORD_TEMP";
	public final String TABLENAME_VEHICLE_RECORD = "T_ITS_VEHICLE_RECORD";
	public final String TABLENAME_TRAFFIC_DAY_STAT = "T_ITS_TRAFFIC_DAY_STAT";
	public final String TABLENAME_TRAFFIC_HOUR_STAT = "T_ITS_TRAFFIC_HOUR_STAT";
	public final String TABLENAME_DEVICE_STATUS = "T_ITS_DEVICE_STATUS";
	
	/**
	 * 根据名称获取方向编号
	 * @param name
	 * @return
	 */
	public final String getDirectionNoByName(String name){
		if(name!=null){
			String tmpName = name.trim().toUpperCase();
			if("东".equals(tmpName) || "由东往西".equals(tmpName)|| "东至西".equals(tmpName)|| "东往西".equals(tmpName)) return "1";
			else if("西".equals(tmpName) || "由西往东".equals(tmpName) ||"西至东".equals(tmpName) ||"西往东".equals(tmpName)) return "2";
			else if("南".equals(tmpName) || "由南往北".equals(tmpName) ||"南至北".equals(tmpName) ||"南往北".equals(tmpName)) return "3";
			else if("北".equals(tmpName) || "由北往南".equals(tmpName) ||"北至南".equals(tmpName) ||"北往南".equals(tmpName)) return "4";
		}
		return null;
	}
	
	public final String[] DIRECTION_DEF = new String[]{"","东","西","南","北","由东北往西南","由西南往东北","由东南往西北","由西北往东南"};
	
	public String getDirectionNameByCode(String directionCode){
		String directionName = "";
		try{
			int directionNo = Integer.parseInt(directionCode);
			directionName = DIRECTION_DEF[directionNo];
		} catch(Exception ex){
			directionName = "";
		}
		return directionName;
	}
	
	//小型汽车号牌种类ID
	public final String PLATE_TYPE_ID_ROADLOUSE = "02";
	
	/**
	 * 顺序: 颜色编号,颜色描述,(标准)车辆类型编号,车辆类型描述
	 */
	public final String[][] PLATE_TYPE_COLOR_PAIR = {{"0","白色","23","警用车"},{"1","黄色","01","大型汽车"},{"2","蓝色","02","小型汽车"},{"3","黑色","06","外籍汽车"}};
	
	public void clearProperties() {
		if (properties != null) {
			properties.clearProperties();
		}
	}	
	
	public String getPlateColorByPlateTypeId(String plateTypeId){
		if(StringHelper.isEmpty(plateTypeId)) return null;
		int len = PLATE_TYPE_COLOR_PAIR.length;
		for(int i=0;i<len;i++){
			if(PLATE_TYPE_COLOR_PAIR[i][2].equals(plateTypeId.trim())){
				return PLATE_TYPE_COLOR_PAIR[i][0];
			}
		}
		return null;
	}
	
	public String getPlateTypeIdByColor(String color){
		if(StringHelper.isEmpty(color)) return null;
		int len = PLATE_TYPE_COLOR_PAIR.length;
		for(int i=0;i<len;i++){
			if(PLATE_TYPE_COLOR_PAIR[i][0].equals(color.trim())){
				return PLATE_TYPE_COLOR_PAIR[i][2];
			}
		}
		return null;
	}
	
	public String getPlateTypeIdByColorName(String colorName){
		if(StringHelper.isEmpty(colorName)) return null;
		int len = PLATE_TYPE_COLOR_PAIR.length;
		for(int i=0;i<len;i++){
			if(PLATE_TYPE_COLOR_PAIR[i][1].equals(colorName.trim())){
				return PLATE_TYPE_COLOR_PAIR[i][2];
			}
		}
		return null;
	}

	/**
	 * Loads properties if necessary. Property loading must be done lazily so
	 */
	public void initProperties(String propertiesFileName) {
		//Create a manager with the full path to the xml config file.
		this.propertiesFileName = propertiesFileName;
		this.properties = new XMLProperties(propertiesFileName);
	}
	
	public void refreshProperties(){		
		this.properties.clearProperties();
		this.properties = new XMLProperties(this.propertiesFileName);		
	}
	
	//系统是否使用数据库
	public boolean isUseDbConnection(){
		String connectionValid = this.getProperty(Environment.CONNECTION_PREFIX + ".valid");
		if ("true".equalsIgnoreCase(connectionValid) || "y".equalsIgnoreCase(connectionValid)) {
			return true;
		}
		return false;
	}
	
	public final String getCurrentDbType(){
		String dbType = this.getProperty(Environment.DB_TYPE_NAME);
		if(StringHelper.isEmpty(dbType)){
			dbType = DB_TYPE_ORACLE;
		}
		return dbType;
	}
	
	public String getProperty(String name) {
		if (properties == null) {
			log.warn("property name is null!");
			return "";
		}
		String value = properties.getProperty(name);
		if (value == null) {
			//log.warn("not found property name:"+name+"!");
			if (this.properties != null) {
				this.properties.clearProperties();
			}
			else {
				this.initProperties(this.propertiesFileName);
			}
			value = properties.getProperty(name);
		}
		return value;
	}

	/**
	 * @return the propertiesFileName
	 */
	public String getPropertiesFileName() {
		return propertiesFileName;
	}

	/**
	 * @param propertiesFileName the propertiesFileName to set
	 */
	public void setPropertiesFileName(String propertiesFileName) {
		this.propertiesFileName = propertiesFileName;
	}

	/**
	 * @return the properties
	 */
	public XMLProperties getProperties() {
		return properties;
	}

	/**
	 * @param properties the properties to set
	 */
	public void setProperties(XMLProperties properties) {
		this.properties = properties;
	}
	
	

}
