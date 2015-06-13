package com.swap.reviews.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class BaseRepository {

	private static String connectionString = "jdbc:mysql://localhost:3306/reviews";
	private static String userName = "root";
	private static String password = "mysql";

	private static Connection connection = null;

	public static Connection getConnection() {
		if (connection == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				connection = DriverManager.getConnection(connectionString,
						userName, password);
				connection.setAutoCommit(false);
			} catch (final Exception e) {
				throw new RuntimeException(
						"exception while opening connection:", e);
			}

		}

		return connection;
	}

	public static void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (final Exception e) {
				throw new RuntimeException(
						"exception while closing connection:", e);
			}

		}
	}
	
	protected void closeConnection(final Connection conn) {
		if(conn!=null) {
			try {
			conn.close();
			} catch(final SQLException e) {
				throw new RuntimeException("exception while closing connection", e);
			}
			
		}
	}
	
	protected void closeStatment(final Statement stmt) {
		if(stmt!=null) {
			try {
				stmt.close();
			} catch(final SQLException e) {
				throw new RuntimeException("exception while closing statement", e);
			}
			
		}
	}
	
	protected void closeResultset(final ResultSet rs) {
		if(rs!=null) {
			try {
				rs.close();
			} catch(final SQLException e) {
				throw new RuntimeException("exception while closing resultset", e);
			}
			
		}
	}

}
