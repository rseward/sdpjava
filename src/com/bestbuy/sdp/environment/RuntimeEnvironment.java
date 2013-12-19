package com.bestbuy.sdp.environment;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import com.bestbuy.sdp.common.util.Logger;

public class RuntimeEnvironment {

	public static final String DEFAULT_DATASOURCE_JNDI_NAME = "som.sdp.datasource";

	public static final String KCB_DATASOURCE_JNDI_NAME = "som.kcb.datasource";

	public static final String CNF_CSM_DATASOURCE_JNDI_NAME = "cnf.csm.datasource";

	private static Object lock;

	private static Context context;

	static {
		lock = new Object();
		context = null;
	}

	public static Connection getConn() {
		return getConn(DEFAULT_DATASOURCE_JNDI_NAME);

	}

	public static Connection getConnection() throws NamingException,
			SQLException {
		return getConn(DEFAULT_DATASOURCE_JNDI_NAME);
	}

	public static Connection getConn(String dataSourceJNDI) {
		if (context == null) {
			synchronized (lock) {
				try {
					context = new InitialContext();
				} catch (NamingException ne) {
					WeblogicConsole
							.error("NamingException acquiring context in Environment: "
									+ ne.getMessage());
					Logger.logStackTrace(ne.fillInStackTrace());
					return null;
				}
			}
		}

		try {
			if (dataSourceJNDI == null) {
				dataSourceJNDI = DEFAULT_DATASOURCE_JNDI_NAME;
			}
			DataSource ds = (DataSource) context.lookup(dataSourceJNDI);
			Connection conn = ds.getConnection();
			return conn;
		} catch (NamingException ne) {
			WeblogicConsole.error("NamingException acquiring datasource '"
					+ dataSourceJNDI + "' in Environment: " + ne.getMessage());
			Logger.logStackTrace(ne.fillInStackTrace());
			return null;
		} catch (SQLException sqle) {
			WeblogicConsole
					.error("SQLException acquiring connection in Environment: "
							+ sqle.getMessage());
			Logger.logStackTrace(sqle.fillInStackTrace());
			return null;
		}
	}

	public static void releaseConn(Connection conn) {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.close();
			}
		} catch (SQLException sqle) {
			WeblogicConsole
					.error("SQLException releasing connection in Environment: "
							+ sqle.getMessage());
			Logger.logStackTrace(sqle.fillInStackTrace());
		}
	}

	public static void rollBack(Connection conn) {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.rollback();
			}
		} catch (SQLException sqle) {
			WeblogicConsole.error("Failed to rollback database changes: "
					+ sqle.getMessage());
			Logger.logStackTrace(sqle.fillInStackTrace());
		}
	}

	public static void setAutoCommit(Connection conn, boolean key) {
		try {
			if (conn != null && !conn.isClosed()) {
				conn.setAutoCommit(key);
			}
		} catch (SQLException sqle) {
			WeblogicConsole.error("Failed to set Auto Commit to \"" + key
					+ " \"" + sqle.getMessage());
			Logger.logStackTrace(sqle.fillInStackTrace());
		}
	}
}
