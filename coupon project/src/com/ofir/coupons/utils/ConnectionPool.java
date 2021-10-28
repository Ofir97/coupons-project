package com.ofir.coupons.utils;

import com.ofir.coupons.custom_exceptions.DBErrorException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class ConnectionPool {
	private static ConnectionPool instance = new ConnectionPool(); //eager

	private Set<Connection> connections;
	private static final int MAX_CONNECTIONS = 10;
	private static final String JDBC_URL = "jdbc:mysql://localhost:3306/coupon_system";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "1";

	/**
	 * this method initializes the connection pool and adds 10 connections to the pool to be ready for use.
	 */
	private ConnectionPool()  {
		connections = new HashSet<Connection>();
		for (int i = 0; i < MAX_CONNECTIONS; i++) {
			try {
				Connection connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
				connections.add(connection);
			} catch (SQLException e) {
				System.out.println(e.getMessage());
			}
		}
	}

	public static ConnectionPool getInstance() {
		return instance;
	}

	/**
	 * this method takes a connection out of the pool and returns it.
	 * if connection pool is empty because all connections are being used - wait until a connection is restored to the pool.
	 */
	public synchronized Connection getConnection() {
		while (connections.isEmpty()) {
			try {
				wait();
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
			}
		}
		Iterator<Connection> cursor = connections.iterator();
		Connection connection = cursor.next();
		cursor.remove(); //removes connection from the pool
		return connection;
	}

	/**
	 * @param connection is the connection to be restored to the pool
	 */
	public synchronized void restoreConnection(Connection connection) {
		connections.add(connection);
		notifyAll();
	}

	/**
	 * this method closes all connections in the pool and removes them from the pool.
	 * if connection pool is empty because all connections are being used - wait until a connection is restored to the pool.
	 */
	public synchronized void closeAllConnections() throws DBErrorException {
		int closedConnectionsCounter = 0;
		while (closedConnectionsCounter < MAX_CONNECTIONS) {
			while (connections.isEmpty()) {
				try {
					wait();
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
				}
			}
			Iterator<Connection> cursor = connections.iterator();
			while (cursor.hasNext()) {
				Connection currentConnection = cursor.next();
				try {
					currentConnection.close();
					cursor.remove(); //removes connection from the pool
					closedConnectionsCounter++;
				} catch (SQLException e) {
					Date currentTime = Calendar.getInstance().getTime();
					String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
					throw new DBErrorException(currentTime, getClass().getName(), methodName, e.getMessage());
				}
			}
		}
	}

	public void closeResources(PreparedStatement preparedStatement) throws DBErrorException {
		try {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		} catch (SQLException e) {
			Date currentTime = Calendar.getInstance().getTime();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			throw new DBErrorException(currentTime, getClass().getName(), methodName, e.getMessage());
		}
	}

	public void closeResources(PreparedStatement preparedStatement, ResultSet resultSet) throws DBErrorException {
		closeResources(preparedStatement);
		try {
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (SQLException e) {
			Date currentTime = Calendar.getInstance().getTime();
			String methodName = Thread.currentThread().getStackTrace()[1].getMethodName();
			throw new DBErrorException(currentTime, getClass().getName(), methodName, e.getMessage());
		}

	}
}
