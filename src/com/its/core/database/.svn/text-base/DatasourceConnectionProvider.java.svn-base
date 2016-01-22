
package com.its.core.database;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.its.core.constant.Environment;
import com.its.core.util.NamingHelper;

public class DatasourceConnectionProvider implements IConnectionProvider {
	private DataSource ds;
	private String user;
	private String pass;

	private static final Log log = LogFactory.getLog(DatasourceConnectionProvider.class);

	public void configure(Properties props) throws Exception {

		String jndiName = props.getProperty(Environment.DATASOURCE);
		if (jndiName==null) {
			String msg = "datasource JNDI name was not specified by property " + Environment.DATASOURCE;
			log.fatal(msg);
			throw new Exception(msg);
		}

		user = props.getProperty(Environment.USER);
		pass = props.getProperty(Environment.PASS);

		try {
			ds = (DataSource) NamingHelper.getInitialContext(props).lookup(jndiName);
		}
		catch (Exception e) {
			log.fatal( "Could not find datasource: " + jndiName, e );
			throw new Exception( "Could not find datasource", e );
		}
		if (ds==null) throw new Exception( "Could not find datasource: " + jndiName );
		log.info( "Using datasource: " + jndiName );
	}

	public Connection getConnection() throws SQLException {
		if (user != null || pass != null) {
			return ds.getConnection(user, pass);
		}
		else {
			return ds.getConnection();
		}
	}

	public void closeConnection(Connection conn) throws SQLException {
		conn.close();
	}

	public void close() {}

}
