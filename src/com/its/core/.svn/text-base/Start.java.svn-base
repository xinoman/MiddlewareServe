/**
 * 
 */
package com.its.core;

import java.io.File;
import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.PropertyConfigurator;
import org.apache.log4j.xml.DOMConfigurator;

import com.its.core.common.DeviceInfoLoaderFactory;
import com.its.core.constant.Environment;
import com.its.core.constant.SystemConstant;
import com.its.core.database.ConnectionProviderFactory;
import com.its.core.database.IConnectionProvider;
import com.its.core.module.IModule;
import com.its.core.util.DateHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-8-3 上午10:17:24
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class Start {
	private static final Log log = LogFactory.getLog(Start.class);
	
	private static String CONFIG_DIR = "config/";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		if(args.length>0) {
			CONFIG_DIR = args[0];
		}
		System.out.println("Config Dir :" + CONFIG_DIR);
		
		String logConfigXmlFileName = CONFIG_DIR +"/log4j.xml";
		String logPropertiesFileName = CONFIG_DIR +"/log4j.properties";
		
		if (new File(logConfigXmlFileName).exists()) {
			DOMConfigurator.configure(logConfigXmlFileName);
		} else if (new File(logPropertiesFileName).exists()) {
			PropertyConfigurator.configure(logPropertiesFileName);
		} else {
			System.out.println("未找到log4j配置文件：'" + logConfigXmlFileName + "' 或 '" + logPropertiesFileName + "'");
		}	
		
//		try {
//			String startTime = "2014-06-15 23:59:59";
//			String endTime = "2014-06-16 23:59:59";
//			if(System.currentTimeMillis() >= DateHelper.parseDateString(startTime, "yyyy-MM-dd HH:mm:ss").getTime() && System.currentTimeMillis() <= DateHelper.parseDateString(endTime, "yyyy-MM-dd HH:mm:ss").getTime()) {
//				
//			} else {
//				log.info("软件试用包有效期已过，请联系管理员更换正式程序包！");
//				System.exit(0);
//			}
//		}catch(Exception e) {}		
		
		//装载初始化XML文件
		SystemConstant.getInstance().initProperties(CONFIG_DIR);
		String versionNo = SystemConstant.getInstance().getProperty(Environment.VERSION);
		String releaseTime = SystemConstant.getInstance().getProperty(Environment.VERSION_RELEASE_TIME);
		log.info("Version:" + versionNo + "\tRelease Time:" + releaseTime);	
		
		//测试数据库连接
		if (SystemConstant.getInstance().isUseDbConnection()) {
			IConnectionProvider connProvider = null;
			Connection conn = null;
			try {
				ConnectionProviderFactory.getInstance().initConnectionProvider(SystemConstant.getInstance().getProperties());
				connProvider = ConnectionProviderFactory.getInstance().getConnectionProvider();
				conn = connProvider.getConnection();
				log.info("测试连接 = " + conn + " 成功！");
			} catch (Exception ex) {
				log.error("获取连接失败:" + ex.getMessage(), ex);
			} finally {
				if (conn != null) {
					try {
						connProvider.closeConnection(conn);
					} catch (Exception ex) {
					}
				}
			}
		}
		
		//初始化设备信息
		DeviceInfoLoaderFactory.getInstance().initDeviceInfoLoader(SystemConstant.getInstance().getProperties());
		
		XMLProperties xmlProperties = SystemConstant.getInstance().getProperties();		
		
		//装载相关模块
		String modulePrefix = Environment.MODULE_PREFIX;		
		int size = xmlProperties.getPropertyNum(modulePrefix);		
		for(int i=0;i<size;i++){
			String valid = xmlProperties.getProperty(modulePrefix,i,"run");
			if("true".equalsIgnoreCase(valid) || "y".equalsIgnoreCase(valid)){
				String name = xmlProperties.getProperty(modulePrefix,i,"name");
				if(name!=null && !name.trim().equals("")){
					try{				
						IModule module = (IModule)Class.forName(name).newInstance();
						module.config(xmlProperties,modulePrefix,i);
						module.start();
						log.debug("Loaded module : "+name);
					} 
					catch(java.net.BindException bindException){
						//端口已经被占用，说明MiddlewareServe已经启动
						log.error("装载模块'"+name+"'时出错："+bindException.getMessage(),bindException);
						log.warn("MiddlewareServe正在运行中，不允许重复启动！");
						System.exit(0);
					}
					catch(Exception ex){
						log.error("装载模块'"+name+"'时出错："+ex.getMessage(),ex);
					}
				}					
			}
		}

	}

}
