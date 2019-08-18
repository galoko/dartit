package org.mywire.pomas;

import java.sql.SQLException;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.pool.OracleDataSource;

public final class DatabaseManager {
	
	private static DatabaseManager instance;
	
	private static final String userName = "system";
	private static final String password = "D9YVFzXB";
	private static final String url = "jdbc:oracle:thin:@localhost:1522:orcl";
	private static final String databaseName = "ORCL";

	private DatabaseManager() {
	}
	
	public OracleConnection getConnection() {
		OracleConnection con;
		
	    OracleDataSource ods;
		try {
			ods = new OracleDataSource();

			ods.setDatabaseName(databaseName);
		    ods.setUser(userName);
		    ods.setPassword(password);
		    ods.setURL(url);

			con = (OracleConnection)ods.getConnection();
		} catch (SQLException e) {
			con = null;
		}
		
		return con;
	}
	
	public static DatabaseManager getInstance(){
		if(instance == null) {
			synchronized (DatabaseManager.class) {
				if(instance == null) {
					instance = new DatabaseManager();
				}
			}
		}
		return instance;
	}
}