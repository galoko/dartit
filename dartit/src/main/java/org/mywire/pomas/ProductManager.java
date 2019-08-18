package org.mywire.pomas;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import oracle.jdbc.OracleConnection;

public final class ProductManager {
		
	private static ProductManager instance;
	
	public final static class Product {
		private int id;
		private String name;
		private int amount;
		private double price;
		
		public int getId() {
			return id;
		}
		
		public String getName() {
			return name;
		}
		
		public int getAmount() {
			return amount;
		}
		
		public double getPrice() {
			return price;
		}
	}
	

	static final String ADD_PRODUCT_SQL = "INSERT INTO PRODUCTS (NAME, AMOUNT, PRICE) VALUES (?, ?, ?)";
	
	public boolean addProduct(String name, int amount, double price) {
		OracleConnection con = DatabaseManager.getInstance().getConnection();
		try (PreparedStatement statement = con.prepareStatement(ADD_PRODUCT_SQL)) {
						
			statement.setString(1, name);
			statement.setInt(2, amount);
			statement.setDouble(3, price);
			
			statement.executeUpdate();
			
			return true;
		} catch(SQLException e) {
			return false;
		}
	}

	static final String EDIT_PRODUCT_SQL = "UPDATE PRODUCTS SET NAME=?, AMOUNT=?, PRICE=? WHERE ID=?";

	public boolean editProduct(int id, String name, int amount, double price) {
		OracleConnection con = DatabaseManager.getInstance().getConnection();
		try (PreparedStatement statement = con.prepareStatement(EDIT_PRODUCT_SQL)) {
				
			statement.setString(1, name);
			statement.setInt(2, amount);
			statement.setDouble(3, price);
			statement.setInt(4, id);
			
			statement.executeUpdate();
			
			return true;
		} catch(SQLException e) {
			return false;
		}
	}
	
	static final String DELETE_PRODUCT_SQL = "DELETE FROM PRODUCTS WHERE ID=?";
	
	public boolean deleteProduct(int id) {
		OracleConnection con = DatabaseManager.getInstance().getConnection();
		try (PreparedStatement statement = con.prepareStatement(DELETE_PRODUCT_SQL)) {
				
			statement.setInt(1, id);
			
			statement.executeUpdate();
			
			return true;
		} catch(SQLException e) {
			return false;
		}
	}

	static final String SELECT_PRODUCTS_SQL = "SELECT ID, NAME, AMOUNT, PRICE FROM PRODUCTS WHERE AMOUNT > 0";
	
	public List<Product> getProducts() {
		OracleConnection con = DatabaseManager.getInstance().getConnection();
		try (Statement statement = con.createStatement()) {
			
			ResultSet result = statement.executeQuery(SELECT_PRODUCTS_SQL);
			
			List<Product> products = new ArrayList<>();
			
			while (result.next()) {
				Product product = new Product();
				product.id = result.getInt("ID");
				product.name = result.getString("NAME");
				product.amount = result.getInt("AMOUNT");
				product.price = result.getDouble("PRICE");
				
				products.add(product);
			}
			
			return products;
		} catch(SQLException e) {
			return null;
		}
	}
	
	static final String SELECT_PRODUCT_SQL = "SELECT NAME, AMOUNT, PRICE FROM PRODUCTS WHERE ID = ? AND AMOUNT > 0";
	
	public Product getProduct(int id) {
		OracleConnection con = DatabaseManager.getInstance().getConnection();
		try (PreparedStatement statement = con.prepareStatement(SELECT_PRODUCT_SQL)) {
			
			statement.setInt(1, id);
			
			ResultSet result = statement.executeQuery();
			
			Product product = null;
			
			if (result.next()) {
				product = new Product();
				product.id = id;
				product.name = result.getString("NAME");
				product.amount = result.getInt("AMOUNT");
				product.price = result.getDouble("PRICE");
			}
			
			return product;
		} catch(SQLException e) {
			return null;
		}
	}
	
	public static ProductManager getInstance() {
		if (instance == null) {
			synchronized (ProductManager.class) {
				if (instance == null) {
					instance = new ProductManager();
				}
			}
		}
		return instance;
	}
}