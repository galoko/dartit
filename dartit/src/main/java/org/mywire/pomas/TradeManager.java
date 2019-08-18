package org.mywire.pomas;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import oracle.jdbc.OracleConnection;

public final class TradeManager {
		
	private static TradeManager instance;
	
	public final static class Product {
		
		private int id;
		private int amount;
		
		public Product(int id, int amount) {
			this.id = id;
			this.amount = amount;
		}
		
		public int getId() {
			return id;
		}
		
		public int getAmount() {
			return amount;
		}
	}
	
	static final String UPDATE_PRODUCT_SQL = "UPDATE PRODUCTS SET AMOUNT = AMOUNT - ? WHERE ID=?";
	static final String INSERT_HISTORY_SQL = "INSERT INTO HISTORY (PRODUCT_ID, USER_LOGIN, AMOUNT, EVENT_DATE) VALUES (?, ?, ?, CAST(sys_extract_utc(SYSTIMESTAMP) AS DATE))";
	
	public boolean trade(AccountManager.User user, List<Product> products) {
		if (user == null) {
			return false;
		}
		
		OracleConnection con = DatabaseManager.getInstance().getConnection();
		
		try (PreparedStatement updateProductStatement = con.prepareStatement(UPDATE_PRODUCT_SQL);
				PreparedStatement insertHistoryStatement = con.prepareStatement(INSERT_HISTORY_SQL)) {
			
			con.setAutoCommit(false);
				
			for (Product product : products) {
				updateProductStatement.setInt(1, product.amount);
				updateProductStatement.setInt(2, product.id);
				updateProductStatement.executeUpdate();
				
				insertHistoryStatement.setInt(1, product.id);
				insertHistoryStatement.setString(2, user.getLogin());
				insertHistoryStatement.setInt(3, product.amount);
				insertHistoryStatement.executeUpdate();
			}
			
			con.commit();
			
			return true;
		} catch(SQLException e) {
			return false;
		}
	}
	
	public static TradeManager getInstance() {
		if (instance == null) {
			synchronized (TradeManager.class) {
				if (instance == null) {
					instance = new TradeManager();
				}
			}
		}
		return instance;
	}
}