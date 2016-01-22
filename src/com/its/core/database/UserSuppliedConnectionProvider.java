
package com.its.core.database;

import java.sql.Connection;
import java.util.Properties;

import org.apache.commons.logging.LogFactory;

/**
 * An implementation of the <literal>ConnectionProvider</literal> interface that
 * simply throws an exception when a connection is requested. This implementation
 * indicates that the user is expected to supply a JDBC connection.
 * @see ConnectionProvider
 * @author Gavin King
 */
public class UserSuppliedConnectionProvider implements IConnectionProvider {

	/**
	 * @see com.unihz.tiip.tools.database.conn.IConnectionProvider#configure(Properties)
	 */
	public void configure(Properties props) throws Exception {
		LogFactory.getLog(UserSuppliedConnectionProvider.class).warn("No connection properties specified - the user must supply JDBC connections");
	}

	/**
	 * @see com.unihz.tiip.tools.database.conn.IConnectionProvider#getConnection()
	 */
	public Connection getConnection() {
		throw new UnsupportedOperationException("The user must supply a JDBC connection");
	}
	
	/**
	 * @see com.unihz.tiip.tools.database.conn.IConnectionProvider#closeConnection(Connection)
	 */
	public void closeConnection(Connection conn) {
		throw new UnsupportedOperationException("The user must supply a JDBC connection");
	}

	public void close() {
	}

}






