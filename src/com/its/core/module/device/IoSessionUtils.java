/**
 * 
 */
package com.its.core.module.device;

import java.net.InetSocketAddress;

import org.apache.mina.common.IoSession;

/**
 * 创建日期 2012-9-20 下午02:00:58
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class IoSessionUtils {
	/*
	 * 根据IoSession生成设备连接key
	 * @param socket
	 * @return
	 */
	public static String generateKey(IoSession session){
		InetSocketAddress socketAddress = (InetSocketAddress)session.getRemoteAddress();
		return socketAddress.getAddress().getHostAddress();
	}
	
	public static String getRemoteIp(IoSession session){
		return ((InetSocketAddress)session.getRemoteAddress()).getAddress().getHostAddress();
	}
	
	public static int getRemotePort(IoSession session){
		return ((InetSocketAddress)session.getRemoteAddress()).getPort();
	}

}
