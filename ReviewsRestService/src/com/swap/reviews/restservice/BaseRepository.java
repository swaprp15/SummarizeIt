package com.swap.reviews.restservice;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class BaseRepository {

	private static String connectionString = "jdbc:mysql://localhost:3306/reviews";
	private static String userName;
	private static String password ;

	private static String configFile = "/home/swapnil/InstalledSoftware/ApacheTomcat/webapps/ReviewsRestService/WEB-INF/config.properties";
	
	private static String mySqlUserConfigName = "mySqlUser";
	private static String mySqlPasswordConfigName = "mySqlPassword";
	
	private static Connection connection = null;

	public BaseRepository()
	{
		Properties properties = new Properties();
		
		InputStream input = null;
		
		try
		{
			input = new FileInputStream(configFile);
			
			properties.load(input);

			userName = properties.getProperty(mySqlUserConfigName);
			password = properties.getProperty(mySqlPasswordConfigName);
			
			System.out.println("Username " + userName);
		}
		catch(IOException e)
		{	
			e.printStackTrace();
		}
		finally
		{
			try {
				input.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
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
