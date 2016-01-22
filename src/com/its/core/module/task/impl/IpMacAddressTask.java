/**
 * 
 */
package com.its.core.module.task.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.constant.SystemConstant;
import com.its.core.module.task.ATask;
import com.its.core.util.IpMacAddressHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;


/**
 * 创建日期 2014-5-31 下午03:19:20
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class IpMacAddressTask extends ATask {
	private static final Log log = LogFactory.getLog(IpMacAddressTask.class);

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#configureSpecificallyProperties(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	@Override
	public void configureSpecificallyProperties(XMLProperties props,String propertiesPrefix, int no) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.task.ATask#execute()
	 */
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
		try {
			String osName = System.getProperty("os.name").toUpperCase();
			String mac = null, ip = null, address = null;
			
			InetAddress addr = InetAddress.getLocalHost();
			ip=addr.getHostAddress();
			address=addr.getHostName();			
			
			if(osName.indexOf(SystemConstant.WINDOWS_2003) != -1 || osName.indexOf(SystemConstant.WINDOWS_XP) != -1) {
				mac = IpMacAddressHelper.getMACAddress();				
			} else {
				mac = IpMacAddressHelper.getWindowsMACAddress();
			}
			
			if(StringHelper.isEmpty(mac)) IpMacAddressHelper.getLocalMachineInfo();
			log.info("Computer name = " + address + " IP = " + ip + " MAC = " + mac);
		} catch (Exception e) {
			log.debug("Server operating systems are not compatible!");
		}

	}

}
