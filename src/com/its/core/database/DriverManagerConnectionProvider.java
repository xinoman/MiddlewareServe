
package com.its.core.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.constant.Environment;
import com.its.core.util.PropertiesHelper;

/**
 * A connection provider that uses <tt>java.sql.DriverManager</tt>. This provider
 * also implements a very rudimentary connection pool.
 * @see ConnectionProvider
 * @author Gavin King
 */
public class DriverManagerConnectionProvider implements IConnectionProvider {

	private String url;
	private Properties connectionProps;
	private Integer isolation;
	private final ArrayList<Connection> pool = new ArrayList<Connection>();
	private int poolSize;
	private int checkedOut = 0;
	private boolean autocommit = true;

	private static final Log log = LogFactory.getLog(DriverManagerConnectionProvider.class);

	public void configure(Properties props) throws Exception {

		String driverClass = props.getProperty(Environment.DRIVER);

		poolSize = PropertiesHelper.getInt(Environment.POOL_SIZE, props, 10); //default pool size 10
		
		log.debug("Connection pool size: " + poolSize);
		
		autocommit = PropertiesHelper.getBoolean(Environment.AUTOCOMMIT, props);
		log.debug("autocommit mode: " + autocommit);

		isolation = PropertiesHelper.getInteger(Environment.ISOLATION, props);
		if (isolation!=null)
		log.debug( "JDBC isolation level: " + Environment.isolationLevelToString( isolation.intValue() ) );

		if (driverClass==null) {
			log.warn("no JDBC Driver class was specified by property " + Environment.DRIVER);
		}
		else {
			try {
				// trying via forName() first to be as close to DriverManager's semantics
				Class.forName(driverClass);
			}
			catch (ClassNotFoundException cnfe) {				
				String msg = "JDBC Driver class not found: " + driverClass;
				log.fatal(msg, cnfe);
				throw new Exception(msg, cnfe);				
			}
		}

		url = props.getProperty(Environment.URL);
		if (url==null) {
			String msg = "JDBC URL was not specified by property " + Environment.URL;
			log.fatal(msg);
			throw new Exception(msg);
		}

		connectionProps = ConnectionProviderFactory.getInstance().getConnectionProperties(props);

		log.info( "using driver: " + driverClass + " at URL: " + url );
		// if debug level is enabled, then log the password, otherwise mask it
		/*
		if ( log.isDebugEnabled() ) {
			log.debug( "connection properties: " + connectionProps );
		} 
		else if ( log.isInfoEnabled() ) {
			log.info( "connection properties: " + PropertiesHelper.maskOut(connectionProps, "password") );
		}
		*/
		this.close();
	}

	public Connection getConnection() throws SQLException {

		//if ( log.isTraceEnabled() ) log.trace( "total checked-out connections: " + checkedOut );
		synchronized (pool) {
			if (!pool.isEmpty()) {
				int last = pool.size() - 1;
				if ( log.isTraceEnabled() ) {
					//log.trace("using pooled JDBC connection, pool size: " + last);
					checkedOut++;
				}
				
				try{
					Connection pooled = (Connection) pool.remove(last);
					if (isolation!=null) pooled.setTransactionIsolation( isolation.intValue());
					
					//if ( pooled.getAutoCommit()!=autocommit )
					
					//执行该语句，可以检测连接是否有效，如果失效（比如网络断开）会抛出SQLException，则关闭池中所有连接
					pooled.setAutoCommit(autocommit);
					return pooled;			
				}
				catch(SQLException sqlExce){
					this.close();
				}
			}
		}

		log.debug("opening new JDBC connection");
		Connection conn = DriverManager.getConnection(url, connectionProps);
		if (isolation!=null) conn.setTransactionIsolation( isolation.intValue() );
		conn.setAutoCommit(autocommit);

		if ( log.isDebugEnabled() ) {
			log.debug( "created connection to: " + url + ", Isolation Level: " + conn.getTransactionIsolation() );
		}
		if ( log.isTraceEnabled() ) checkedOut++;

		return conn;
	}

	public void closeConnection(Connection conn) throws SQLException {		
		if(conn==null) return;
		if ( log.isDebugEnabled() ) checkedOut--;

		synchronized (pool) {
			int currentSize = pool.size();
			if ( currentSize < poolSize ) {
				//if ( log.isTraceEnabled() ) log.trace("returning connection to pool, pool size: " + (currentSize + 1) );
				pool.add(conn);
				return;
			}
		}

		//log.debug("closing JDBC connection");
		conn.close();

	}
	
    protected void finalize(){
        close();
    }	

	public void close() {		
		//log.debug("close all connection!");
		Iterator iter = pool.iterator();
		while ( iter.hasNext() ) {
			try {
				Connection conn = (Connection) iter.next();
				conn.close();
				conn = null;
			}
			catch (SQLException sqle) {
				log.warn("problem closing pooled connection:"+sqle.getMessage(), sqle);
			}
		}
		pool.clear();		
	}

}







