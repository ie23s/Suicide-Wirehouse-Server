package com.ie23s.java.suicidewarehouseserver.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

public abstract class MySQLUtil {
	public Connection connection = null;
	private String host;
	private short port;
	private String database;
	private String user;
	private String password;

	public MySQLUtil(String host, short port, String database, String user, String password) throws
			ClassNotFoundException, IllegalAccessException, InstantiationException {
		this.host = host;
		this.port = port;
		this.database = database;
		this.user = user;
		this.password = password;
		Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
	}

	public static String strip(String str) {
		str = str.replaceAll("<[^>]*>", "");
		str = str.replace("\\", "\\\\");
		str = str.trim();
		return str;
	}

	public boolean connect() throws SQLException {
		connection = DriverManager.getConnection(
				String.format("jdbc:mysql://%s:%d/%s?useUnicode=true&characterEncoding=UTF-8&user=%s&password=%s&serverTimezone=UTC&useSSL=false",
						host, port, database, user, password));
		return !connection.isClosed();
	}

	public void close() {
		try {
			connection.close();
		} catch (SQLException ignore) {
		}
	}

	public boolean hasConnected() {
		try {
			return !connection.isClosed();
		} catch (SQLException ignore) {
			return false;
		}
	}

	public void execute(String query) throws SQLException {
		connection.createStatement().execute(strip(query));
	}

	public ResultSet executeQuery(String query) throws SQLException {
		return connection.createStatement().executeQuery(strip(query));
	}

	public abstract void init() throws SQLException;
}
