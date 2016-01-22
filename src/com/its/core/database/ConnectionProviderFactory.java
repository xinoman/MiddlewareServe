
package com.its.core.database;

import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.constant.Environment;
import com.its.core.constant.SystemConstant;


/**
 * Instantiates a connection provider given either <tt>System</tt> properties or
 * a <tt>java.util.Properties</tt> instance. The <tt>ConnectionProviderFactory</tt>
 * first attempts to find a name of a <tt>ConnectionProvider</tt> subclass in the
 * property <tt>hibernate.connection.provider_class</tt>. If missing, heuristics are used
 * to choose either <tt>DriverManagerConnectionProvider</tt>,
 * <tt>DatasourceConnectionProvider</tt>
 * @see ConnectionProvider
 */

public final class ConnectionProviderFactory {

	private static final Log log = LogFactory.getLog(ConnectionProviderFactory.class);

	private static ConnectionProviderFactory cpf = new ConnectionProviderFactory();
	
	private IConnectionProvider connProvider;
	
	/*
	static{
		cpf = new ConnectionProviderFactory();
	}
	*/
	
	// cannot be instantiated
	//private ConnectionProviderFactory() { throw new UnsupportedOperationException(); }	
	private ConnectionProviderFactory() { }
	
	public static ConnectionProviderFactory getInstance(){
		/*
		if(cpf==null){
			cpf = new ConnectionProviderFactory();
		}
		*/
		return cpf;
	}
	
	public void initConnectionProvider(Properties properties) throws Exception{
		String providerClass = properties.getProperty(Environment.CONNECTION_PROVIDER);
		if ( providerClass!=null ) {
			try {
				log.info("Initializing connection provider: " + providerClass);
				this.connProvider = (IConnectionProvider) Class.forName(providerClass).newInstance();
			}
			catch (Exception e) {
				log.fatal("Could not instantiate connection provider", e);
				throw new Exception("Could not instantiate connection provider: " + providerClass);
			}
		}
		else if ( properties.getProperty(Environment.DATASOURCE)!=null ) {
			this.connProvider = new DatasourceConnectionProvider();
		}	
		else if ( properties.getProperty(Environment.URL)!=null ) {
			this.connProvider = new DriverManagerConnectionProvider();
		}
		else {
			log.error("未找到合适的数据库连接供应器，请检查属性配置文件！");
			this.connProvider = new UserSuppliedConnectionProvider();
		}
		
		this.connProvider.configure(properties);		
	}
	
	public void refreshConnProperties(){
		log.debug("刷新数据库连接属性...");
		try{
			try{
				ConnectionProviderFactory.getInstance().getConnectionProvider().close();
			}
			catch(Exception ex1) { }
			SystemConstant.getInstance().refreshProperties();
			ConnectionProviderFactory.getInstance().initConnectionProvider(SystemConstant.getInstance().getProperties());
		}
		catch(Exception ex)        {
			log.error("刷新数据库连接属性时出错：" + ex.getMessage(), ex);
		}
	}	
		
	/**
	 * Instantiate a <tt>ConnectionProvider</tt> using <tt>System</tt> properties.
	 * @return ConnectionProvider
	 * @throws Exception
	 */
	public IConnectionProvider getConnectionProvider() {
		if(this.connProvider==null){
			log.error("IConnectionProvider为空，请先使用ConnectionProviderFactory初始化连接供应器！");
		}
		return this.connProvider;
	}


	/**
	 * Transform JDBC connection properties.
	 *
	 * Passed in the form <tt>hibernate.connection.*</tt> to the
	 * format accepted by <tt>DriverManager</tt> by triming the leading "<tt>hibernate.connection</tt>".
	 */
	public Properties getConnectionProperties(Properties properties) {
		Properties result = new Properties();
		Iterator iter = properties.keySet().iterator();				
		while ( iter.hasNext() ) {
			String prop = (String) iter.next();			
			if ( prop.indexOf(Environment.CONNECTION_PREFIX) > -1 && !SPECIAL_PROPERTIES.contains(prop) ) {
				result.setProperty(
					prop.substring( Environment.CONNECTION_PREFIX.length()+1 ),
					properties.getProperty(prop)
				);
			}
		}
		
		String userName = properties.getProperty(Environment.USER);
		if (userName!=null) result.setProperty( "user", userName );
		//log.info("password = "+result.getProperty("password"));
		
		String passWord = properties.getProperty(Environment.PASS);
		if (passWord!=null) result.setProperty( "password", passWord );		
		
		return result;
	}

	private static final Set<String> SPECIAL_PROPERTIES;
	static {
		SPECIAL_PROPERTIES = new HashSet<String>();
		SPECIAL_PROPERTIES.add(Environment.DATASOURCE);
		SPECIAL_PROPERTIES.add(Environment.URL);
		SPECIAL_PROPERTIES.add(Environment.CONNECTION_PROVIDER);
		SPECIAL_PROPERTIES.add(Environment.POOL_SIZE);
		SPECIAL_PROPERTIES.add(Environment.ISOLATION);
		SPECIAL_PROPERTIES.add(Environment.DRIVER);
		SPECIAL_PROPERTIES.add(Environment.USER);
	}
	
	public static void main(String[] args){
		InputStream is = null;
		try{
//            Properties property = new Properties();
//            is = Environment.class.getResourceAsStream("/redmap.properties");
//            property.load(is);
            
			IConnectionProvider icp = ConnectionProviderFactory.getInstance().getConnectionProvider();
//			System.out.println(is);
//			System.out.println(icp);
			System.out.println("conn = "+icp.getConnection());
		}catch(Exception ex){
			ex.printStackTrace();
        } finally {
            try {
                is.close();
            } catch(Exception e) {}
        }
	}

}






