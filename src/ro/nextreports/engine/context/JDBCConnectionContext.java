package ro.nextreports.engine.context;

import java.sql.Connection;

public class JDBCConnectionContext implements XConnectionContext {
	
	private Connection connection;
	private int queryTimeout = 600; // seconds
	boolean csv;
	
	public Connection getConnection() {
		return connection;
	}
	public void setConnection(Connection connection) {
		this.connection = connection;
	}
	public int getQueryTimeout() {
		return queryTimeout;
	}
	public void setQueryTimeout(int queryTimeout) {
		this.queryTimeout = queryTimeout;
	}
	public boolean isCsv() {
		return csv;
	}
	public void setCsv(boolean csv) {
		this.csv = csv;
	}			

}
