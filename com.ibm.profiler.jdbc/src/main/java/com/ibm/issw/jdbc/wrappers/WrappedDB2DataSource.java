package com.ibm.issw.jdbc.wrappers;

/*
 *-----------------------------------------------------------------
 * IBM Confidential
 *
 * OCO Source Materials
 *
 * WebSphere Commerce
 *
 * (C) Copyright IBM Corp. 2013, 2014
 *
 * The source code for this program is not published or otherwise
 * divested of its trade secrets, irrespective of what has
 * been deposited with the U.S. Copyright Office.
 *-----------------------------------------------------------------
 */

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sql.PooledConnection;

import com.ibm.db2.jcc.DB2ConnectionPoolDataSource;
import com.ibm.issw.jdbc.profiler.JdbcProfilerDaemon;

/**
 * 
 * WrappedDB2DataSource
 */
public final class WrappedDB2DataSource extends DB2ConnectionPoolDataSource {
	/**
	 * IBM Copyright notice field.
	 */
	public static final String COPYRIGHT = com.ibm.commerce.copyright.IBMCopyright.SHORT_COPYRIGHT;
	
	private static final long serialVersionUID = -3224567299738986531L;
	private static final Logger LOG = Logger
			.getLogger(WrappedDB2DataSource.class.getName());

	/**
	 * ctor
	 */
	public WrappedDB2DataSource() {
		JdbcProfilerDaemon.initializeDaemon();
		// JdbcEventManager.addJdbcEventListener(JdbcProfilerDaemon.getInstance());
	}

	/*
	 * (non-Javadoc)
	 * @see com.ibm.db2.jcc.DB2ConnectionPoolDataSource#getPooledConnection()
	 */
	@Override
    public PooledConnection getPooledConnection() throws SQLException {
		PooledConnection pooledConn = super.getPooledConnection();
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Real pooled connection " + pooledConn.toString());
		}

		if ((pooledConn instanceof WrappedPooledConnection)) {
			return pooledConn;
		}
		return new WrappedDB2PooledConnection(pooledConn);
	}
	/*
	 * (non-Javadoc)
	 * @see com.ibm.db2.jcc.DB2ConnectionPoolDataSource#getPooledConnection(java.lang.String, java.lang.String)
	 */
	@Override
    public PooledConnection getPooledConnection(String user, String password)
			throws SQLException {
		PooledConnection pooledConn = super.getPooledConnection(user, password);
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("Real pooled connection " + pooledConn.toString());
		}

		if ((pooledConn instanceof WrappedPooledConnection)) {
			return pooledConn;
		}
		return new WrappedDB2PooledConnection(pooledConn);
	}
}
