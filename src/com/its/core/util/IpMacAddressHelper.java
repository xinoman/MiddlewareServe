/**
 * 
 */
package com.its.core.util;

import java.io.BufferedReader;   
import java.io.IOException;   
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.local.huixian.task.ExportToZlkjPdaserviceTask;

/**
 * 创建日期 2013-5-6 上午10:49:40
 * @author GuoPing.Wu
 * Copyright: Xinoman Technologies CO.,LTD.
 */
public class IpMacAddressHelper {
	private static final Log log = LogFactory.getLog(IpMacAddressHelper.class);
	
	public static String hexByte(byte b) {
        String s = "000000" + Integer.toHexString(b);
        return s.substring(s.length() - 2);
    }
	
	public static String getMACAddress() {
        Enumeration<NetworkInterface> el;
        String mac_s = "";
        try {
            el = NetworkInterface.getNetworkInterfaces();
            while (el.hasMoreElements()) {
                byte[] mac = el.nextElement().getHardwareAddress();
                if (mac == null)
                    continue;
                mac_s = hexByte(mac[0]) + "-" + hexByte(mac[1]) + "-"
                        + hexByte(mac[2]) + "-" + hexByte(mac[3]) + "-"
                        + hexByte(mac[4]) + "-" + hexByte(mac[5]);
 
            }
        } catch (SocketException e1) {
            e1.printStackTrace();
        }
        return mac_s.toUpperCase();
    }
	
	public static String getWindowsMACAddress() {
		String mac = null;
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("ipconfig /all");
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			int index = -1;
			while ((line = bufferedReader.readLine()) != null) {
				log.info(line);
				index = line.toLowerCase().indexOf("physical address");
				if (index != -1) {
					index = line.indexOf(":");
					if (index != -1) {
						mac = line.substring(index + 1).trim();
					}
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}

		return mac;
	}
	
	public static void getLocalMachineInfo(){  
		BufferedReader bufferedReader = null;
		Process process = null;
		try {
			process = Runtime.getRuntime().exec("ipconfig /all");
			bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line = null;
			while ((line = bufferedReader.readLine()) != null) {
				log.info(line);				
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (bufferedReader != null) {
					bufferedReader.close();
				}
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			bufferedReader = null;
			process = null;
		}
    } 

}
