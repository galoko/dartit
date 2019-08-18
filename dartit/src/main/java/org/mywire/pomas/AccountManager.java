package org.mywire.pomas;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.DatatypeConverter;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import oracle.jdbc.OracleConnection;

public final class AccountManager {
	
	private static AccountManager instance;
	
	private static final int MANAGER_PRIVELEGE = 0x1;
	
	private static final String SECRET_KEY_BASE64 = "gtU0YvrT09FEA36/pbGloQjGquHYdDNhLlGuF0dy12s=";
	
	SecretKey tokenKey;
	
	private AccountManager() {
		byte[] keyData = DatatypeConverter.parseBase64Binary(SECRET_KEY_BASE64);
		tokenKey = Keys.hmacShaKeyFor(keyData);
	}
	
	static final String REGISTER_SQL = "INSERT INTO USERS (LOGIN, PASSWORD_HASH, SALT, PRIVELEGES) VALUES (?, ?, ?, ?)";
	
	public String registerAccount(String login, String password, boolean isMananager) {
		OracleConnection con = DatabaseManager.getInstance().getConnection();
		try (PreparedStatement statement = con.prepareStatement(REGISTER_SQL)) {
			
			String salt = generateSalt();
			String hash = hashPassword(password, salt);
			int priveleges = 0;
			if (isMananager) {
				priveleges |= MANAGER_PRIVELEGE;
			}
			
			statement.setString(1, login);
			statement.setString(2, hash);
			statement.setString(3, salt);
			statement.setInt(4, priveleges);
			
			statement.executeUpdate();
			
			return getToken(login);
		} catch(SQLException e) {
			return null;
		}
	}
	
	private static final String GET_PASSWORD_SQL = "SELECT PASSWORD_HASH, SALT FROM USERS WHERE LOGIN = ?";
	
	public String signIn(String login, String password) {
		String password_hash = null;
		String salt = null;
		
		OracleConnection con = DatabaseManager.getInstance().getConnection();
		try (PreparedStatement statement = con.prepareStatement(GET_PASSWORD_SQL)) {
						
			statement.setString(1, login);
			
			ResultSet res = statement.executeQuery();
			
			if (res.next()) {
				password_hash = res.getString("PASSWORD_HASH");
				salt = res.getString("SALT");
			}
		} catch(SQLException e) {
			return null;
		}
		
		if (password_hash == null || salt == null) {
			return null;
		}
		
		String input_password_hash = hashPassword(password, salt);
		if (!input_password_hash.equals(password_hash)) {
			return null;
		}
		
		return getToken(login);
	}
	
	private String getToken(String login) {
		
		String token = Jwts.builder().setSubject(login).signWith(tokenKey).compact();
		
		return token;
	}
	
    private String hashPassword(String password, String salt) {
    	int iterations = 1000;
    	int keyBitLength = 128;
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA512");
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt.getBytes(), iterations, keyBitLength);
            SecretKey key = skf.generateSecret(spec);
            byte[] res = key.getEncoded();
            String str = DatatypeConverter.printBase64Binary(res);
            return str;
        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new RuntimeException(e);
        }
    }
    
    protected String generateSalt() {
    	int saltLength = 32;
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < saltLength) {
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;
    }
    
	public final static class User {
		private String login;
		private boolean isManager;
		
		private User(String login, boolean isManager) {
			this.login = login;
			this.isManager = isManager;
		}
		
		public String getLogin() {
			return login;
		}
		
		public boolean isManager() {
			return isManager;
		}
	}
	
	private static final String GET_PRIVELEGES_SQL = "SELECT PRIVELEGES FROM USERS WHERE LOGIN = ?";
	
	public User getUser(HttpServletRequest req) {
		Cookie[] cookies = req.getCookies();

		String token = null;
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals("token")) {
					token = cookie.getValue();
					break;
				}
			}
		}
		
		if (token == null) {
			return null;
		}
		
		String login = null;
		try {
		    login = Jwts.parser().setSigningKey(tokenKey).
		    		parseClaimsJws(token).getBody().getSubject();
		} catch (JwtException e) {
			return null;
		}
		
		Integer priveleges = null;
		
		OracleConnection con = DatabaseManager.getInstance().getConnection();
		try (PreparedStatement statement = con.prepareStatement(GET_PRIVELEGES_SQL)) {
						
			statement.setString(1, login);
			
			ResultSet res = statement.executeQuery();
			
			if (res.next()) {
				priveleges = res.getInt("PRIVELEGES");
			}
		} catch(SQLException e) {
			return null;
		}
		
		if (priveleges == null) {
			return null;
		}
		
		boolean isManager = (priveleges & MANAGER_PRIVELEGE) == MANAGER_PRIVELEGE;
		
		return new User(login, isManager);
	}
	
	public static AccountManager getInstance() {
		if (instance == null) {
			synchronized (AccountManager.class) {
				if (instance == null) {
					instance = new AccountManager();
				}
			}
		}
		return instance;
	}
}