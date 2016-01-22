/**
 * 
 */
package com.its.core.constant;

import java.util.HashMap;

/**
 * 创建日期 2012-8-3 上午10:58:16
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public final class Environment {
	
	public static final String VERSION = "its.version.no";

	public static final String VERSION_RELEASE_TIME = "its.version.release_time";
	
	public static final String MODULE_PREFIX = "its.modules.module";
	
	public static final String TASK_PREFIX = "its.timers";
	
	public static final String DEVICE_COMMUNICATE_HANDLE_SERVER_PREFIX = "its.device_communicate_server";
	
	/**
	 * <tt>ConnectionProvider</tt> implementor to use when obtaining connections
	 */
	public static final String CONNECTION_PROVIDER = "its.connection.provider_class";
	/**
	 * JDBC driver class
	 */
	public static final String DRIVER = "its.connection.driver_class";
	/**
	 * JDBC transaction isolation level
	 */
	public static final String ISOLATION = "its.connection.isolation";
	/**
	 * JDBC URL
	 */
	public static final String URL = "its.connection.url";

	/**
	 * JDBC user
	 */
	public static final String USER = "its.connection.username";
	/**
	 * JDBC password
	 */
	public static final String PASS = "its.connection.password";
	/**
	 * JDBC autocommit mode
	 */
	public static final String AUTOCOMMIT = "its.connection.autocommit";
	/**
	 * Maximum number of inactive connections for Tiip's connection pool
	 */
	public static final String POOL_SIZE = "its.connection.pool_size";
	/**
	 * <tt>java.sql.Datasource</tt> JNDI name
	 */
	public static final String DATASOURCE = "its.connection.datasource";
	/**
	 * prefix for arbitrary JDBC connection properties
	 */
	public static final String CONNECTION_PREFIX = "its.connection";

	public static final String CONTROL_PREFIX = "its.control";

	public static final String DB_TYPE_NAME = "its.connection.db.name";
	
	/**
	 * JNDI initial context class, <tt>Context.INITIAL_CONTEXT_FACTORY</tt>
	 */
	public static final String JNDI_CLASS = "its.jndi.class";
	/**
	 * JNDI provider URL, <tt>Context.PROVIDER_URL</tt>
	 */
	public static final String JNDI_URL = "its.jndi.url";
	/**
	 * prefix for arbitrary JNDI <tt>InitialContext</tt> properties
	 */
	public static final String JNDI_PREFIX = "its.jndi";
	
	public static final String DEVICE_INFO_LOADER_PREFIX = "its.common.device_info";
	
	public static final String FILESCANNER_PREFIX = "its.filescan.scanners";
	
	private static final HashMap<Integer, String> ISOLATION_LEVELS = new HashMap<Integer, String>();
	
	/**
	 * Get the name of a JDBC transaction isolation level
	 *
	 * @see java.sql.Connection
	 * @param isolation as defined by <tt>java.sql.Connection</tt>
	 * @return a human-readable name
	 */
	public static String isolationLevelToString(int isolation) {
		return (String) ISOLATION_LEVELS.get(new Integer(isolation));
	}

}
