/**
 * 
 */
package com.its.core.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

/**
 * 创建日期 2012-8-3 上午11:03:13
 * @author GuoPing.Wu
 * Copyright: ITS Technologies CO.,LTD.
 */
public interface IConnectionProvider {
	
	/**
	 * Initialize the connection provider from given properties.
	 * @param props <tt>SessionFactory</tt> properties
	 */
	public void configure(Properties props) throws Exception;
	
	/**
	 * Grab a connection
	 * @return a JDBC connection
	 * @throws SQLException
	 */
	public Connection getConnection() throws SQLException;
	
	/**
	 * Dispose of a used connection.
	 * @param conn a JDBC connection
	 * @throws SQLException
	 */
	public void closeConnection(Connection conn) throws SQLException;

	/**
	 * Release all resources held by this provider. JavaDoc requires a second sentence.
	 * @throws HibernateException
	 */
	public void close() throws Exception;

}
