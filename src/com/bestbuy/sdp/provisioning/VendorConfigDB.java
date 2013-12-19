package com.bestbuy.sdp.provisioning;

import java.sql.Types;
import java.sql.Timestamp;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.CallableStatement;

import com.bestbuy.sdp.environment.RuntimeEnvironment;
import com.bestbuy.sdp.environment.WeblogicConsole;

public class VendorConfigDB {
	public static int getAggregateMax(String vendorID) {
		int aggMax = 1;
		Connection conn = null;
		CallableStatement proc = null;

		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn
					.prepareCall("{call vendorConfig.getAggregateMax(?,?) }");

			proc.setString(1, vendorID);
			proc.registerOutParameter(2, Types.INTEGER);
			proc.executeQuery();

			aggMax = proc.getInt(2);

		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of vendorConfig.getAggregateMax:");
			sqlex.printStackTrace();
			return aggMax;
		} finally {
			// Clean-up
			try {
				if (proc != null)
					proc.close();
			} catch (Exception ex) {
			}

			try {
				if (conn != null)
					RuntimeEnvironment.releaseConn(conn);
			} catch (Exception ex) {
			}
		}

		return aggMax;
	}

	public static Timestamp getLastAggregationTS(String vendorID) {
		Timestamp lastAggTS = null;
		Connection conn = null;
		CallableStatement proc = null;

		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn
					.prepareCall("{call vendorConfig.getLastAggregationTS(?,?) }");

			proc.setString(1, vendorID);
			proc.registerOutParameter(2, Types.TIMESTAMP);
			proc.executeQuery();

			lastAggTS = proc.getTimestamp(2);

		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of vendorConfig.getLastAggregationTS:");
			sqlex.printStackTrace();
			return lastAggTS;
		} finally {
			// Clean-up
			try {
				if (proc != null)
					proc.close();
			} catch (Exception ex) {
			}

			try {
				if (conn != null)
					RuntimeEnvironment.releaseConn(conn);
			} catch (Exception ex) {
			}
		}

		return lastAggTS;
	}

	public static int updateLastAggregationTS(String vendorID,
			Timestamp updatedTimestamp) {
		Connection conn = null;
		int updateStatus = 0;
		CallableStatement proc = null;

		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn
					.prepareCall("{call vendorConfig.updateLastAggregationTS(?,?,?) }");

			proc.setString(1, vendorID);
			proc.setTimestamp(2, updatedTimestamp);
			proc.registerOutParameter(3, Types.INTEGER);
			proc.executeQuery();

			updateStatus = proc.getInt(3);

		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of vendorConfig.updateLastAggregationTS:");
			sqlex.printStackTrace();
			return updateStatus;
		} finally {
			// Clean-up
			try {
				if (proc != null)
					proc.close();
			} catch (Exception ex) {
			}

			try {
				if (conn != null)
					RuntimeEnvironment.releaseConn(conn);
			} catch (Exception ex) {
			}
		}

		return updateStatus;
	}

	public static int getAggregateFreq(String vendorID) {
		int aggFreq = -1;
		Connection conn = null;
		CallableStatement proc = null;

		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn
					.prepareCall("{call vendorConfig.getAggregateFreq(?,?) }");

			proc.setString(1, vendorID);
			proc.registerOutParameter(2, Types.INTEGER);
			proc.executeQuery();

			aggFreq = proc.getInt(2);

		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of vendorConfig.getAggregateFreq:");
			sqlex.printStackTrace();
			return aggFreq;
		} finally {
			// Clean-up
			try {
				if (proc != null)
					proc.close();
			} catch (Exception ex) {
			}

			try {
				if (conn != null)
					RuntimeEnvironment.releaseConn(conn);
			} catch (Exception ex) {
			}
		}

		return aggFreq;
	}

	public static int getRetryMax(String vendorID) {
		int retryMax = 1;
		Connection conn = null;
		CallableStatement proc = null;

		try {
			conn = RuntimeEnvironment.getConn();
			proc = conn.prepareCall("{call vendorConfig.getRetryMax(?,?) }");

			proc.setString(1, vendorID);
			proc.registerOutParameter(2, Types.INTEGER);
			proc.executeQuery();

			retryMax = proc.getInt(2);

		} catch (SQLException sqlex) {
			WeblogicConsole
					.error("Caught SQLException during execution of vendorConfig.getRetryMax:");
			sqlex.printStackTrace();
			return retryMax;
		} finally {
			// Clean-up
			try {
				if (proc != null)
					proc.close();
			} catch (Exception ex) {
			}

			try {
				if (conn != null)
					RuntimeEnvironment.releaseConn(conn);
			} catch (Exception ex) {
			}
		}

		return retryMax;
	}
}
