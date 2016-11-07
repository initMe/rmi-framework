package com.dao.jdbc;

import java.sql.Connection;

public class ConnectionBean {
	private Connection conn;
	private boolean isUse = false;
	public Connection getConn() {
		return conn;
	}
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	public boolean isUse() {
		return isUse;
	}
	public void setUse(boolean isUse) {
		this.isUse = isUse;
	}
}
