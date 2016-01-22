/**
 * 
 */
package com.its.core.module.broadcast;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.mina.common.ByteBuffer;
import org.apache.mina.common.DefaultIoFilterChainBuilder;
import org.apache.mina.common.IoAcceptor;
import org.apache.mina.common.IoAcceptorConfig;
import org.apache.mina.common.SimpleByteBufferAllocator;
import org.apache.mina.filter.LoggingFilter;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.transport.socket.nio.SocketAcceptor;
import org.apache.mina.transport.socket.nio.SocketAcceptorConfig;
import org.apache.mina.transport.socket.nio.SocketSessionConfig;

import com.its.core.module.IModule;
import com.its.core.util.PropertiesHelper;
import com.its.core.util.StringHelper;
import com.its.core.util.XMLProperties;

/**
 * 创建日期 2012-9-24 上午09:25:14
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public class InfoBroadcastModule implements IModule {
	private static final Log log = LogFactory.getLog(InfoBroadcastModule.class);
	
	private static boolean running = false;
	
	/**
	 * 服务器监听端口
	 */
	private int serverPort = 9901;
	
	/**
	 * 保存所有注册客户端的SESSION
	 * key:IP地址
	 * value:ClientSessionBean
	 */
	private static Map<String,ClientSessionBean> sessionsMap = Collections.synchronizedMap(new HashMap<String,ClientSessionBean>());
	
	/**
	 * 信息广播线程
	 */
	private Thread infoBroadcastThread = null;
	
	private IoAcceptor acceptor = null;
	
	/**
	 * 需要广播的信息队列
	 */		
	private static BlockingQueue<BroadcastProtocolBean> broadcastInfoQueue = new LinkedBlockingQueue<BroadcastProtocolBean>(500);

	/* (non-Javadoc)
	 * @see com.its.core.module.IModule#config(com.its.core.util.XMLProperties, java.lang.String, int)
	 */
	public void config(XMLProperties xmlProperties, String propertiesPrefix,int no) throws Exception {
		this.serverPort = PropertiesHelper.getInt(propertiesPrefix,no,"parameters.port",xmlProperties,this.serverPort);
		String protocolGeneratorStr = PropertiesHelper.getString(propertiesPrefix,no,"parameters.protocol_generator",xmlProperties,null);
		if(StringHelper.isNotEmpty(protocolGeneratorStr)){
			try{
				log.debug("Loading IProtocolGenerator : "+protocolGeneratorStr);
				IProtocolGenerator protocolGenerator = (IProtocolGenerator)Class.forName(protocolGeneratorStr).newInstance();
				ProtocolGeneratorFactory.getInstance().setProtocolGenerator(protocolGenerator);
			}catch(Exception ex){
				log.error("装载协议生成器时出错："+ex.getMessage(),ex);
			}
		}

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.IModule#start()
	 */
	public void start() throws Exception {
		ByteBuffer.setUseDirectBuffers(false);
        ByteBuffer.setAllocator(new SimpleByteBufferAllocator());
        
        this.acceptor = new SocketAcceptor();
        IoAcceptorConfig config = new SocketAcceptorConfig();
        SocketSessionConfig ssc = (SocketSessionConfig)config.getSessionConfig();
        ssc.setReuseAddress(true);
        ssc.setTcpNoDelay(true);
        ssc.setReceiveBufferSize(4096);
        ssc.setSendBufferSize(4096);
        ssc.setKeepAlive(true);
        //log.debug("ssc="+ssc);
        
        DefaultIoFilterChainBuilder chain = config.getFilterChain();

        chain.addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory()));
        chain.addLast("logger", new LoggingFilter());

        InfoBroadcastProtocolHandler infoBroadcastProtocolHandler = new InfoBroadcastProtocolHandler(this);
        acceptor.bind(new InetSocketAddress(this.serverPort), infoBroadcastProtocolHandler,config);
        
        InfoBroadcastThread infoBroadcastRunnable = new InfoBroadcastThread();
        this.infoBroadcastThread = new Thread(infoBroadcastRunnable);
        this.infoBroadcastThread.start();
        
        running = true;

        log.info("InfoBroadcastModule Listening on port " + this.serverPort);

	}

	/* (non-Javadoc)
	 * @see com.its.core.module.IModule#stop()
	 */
	public void stop() throws Exception {
		if(this.acceptor!=null){
			this.acceptor.unbindAll();
		}
		if(this.infoBroadcastThread!=null){
			this.infoBroadcastThread.interrupt();
		}
		
		running = false;

	}
	
	public static Map<String, ClientSessionBean> getSessionsMap() {
		return sessionsMap;
	}
	
	public static BroadcastProtocolBean getMessage() throws InterruptedException{
		try {
			return broadcastInfoQueue.take();
		} catch (InterruptedException e) {
			throw e;			
		}
	}
	
	public static void putMessage(BroadcastProtocolBean broadcastProtocolBean){
		if(running){
			try {
				//broadcastInfoQueue.put(broadcastProtocolBean);
				boolean success = broadcastInfoQueue.offer(broadcastProtocolBean, 1, TimeUnit.SECONDS);
				if(!success){
					log.warn("广播消息队列满："+broadcastInfoQueue.size());
					broadcastInfoQueue.clear();
				}
			} catch (InterruptedException e) {
				broadcastInfoQueue.clear();
				log.error(e.getMessage(),e);
			}			
		}
	}
	
	/**
	 * 是否可以广播，只有在广播模块正在运行且客户端session不为空的情况下才有必要广播
	 * @return
	 */
	public static boolean isBroadcastEnabled(){
		if(isRunning() && !InfoBroadcastModule.getSessionsMap().isEmpty()){
			return true;
		}
		return false;
	}

	/**
	 * @return the running
	 */
	public static boolean isRunning() {
		return running;
	}

	/**
	 * @param running the running to set
	 */
	public static void setRunning(boolean running) {
		InfoBroadcastModule.running = running;
	}	

}
