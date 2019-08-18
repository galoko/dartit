package org.mywire.pomas;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/users/*")
public class Users extends HttpServlet {
	private static final long serialVersionUID = 5L;
       
    public Users() {
        super();
    }
    
    private String extractLogin(String path) {    	
    	if (path == null || !path.startsWith("/")) 
    		return null;
    	
    	try {
    		return path.substring(1);
    	} catch (NumberFormatException e) {
    		return null;
    	}
    }
    
    private void sendToken(String token, HttpServletResponse resp) throws ServletException, IOException {
		JsonObjectBuilder registerResult = Json.createObjectBuilder();
		
    	if (token != null) {
			Cookie tokenCookie = new Cookie("token", token);
			tokenCookie.setHttpOnly(true);
			tokenCookie.setPath("/");
			
	        resp.setStatus(HttpServletResponse.SC_OK);
	        resp.addCookie(tokenCookie);
	        registerResult.add("result", "success");
		} else {
	        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        registerResult.add("result", "fail");
		}
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try (JsonWriter writer = Json.createWriter(resp.getWriter())) {
        	writer.writeObject(registerResult.build());
        }
    }
    
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		
		String token = null;
	
		if (path == null) {		
			// POST /users - register new user
			String login, password;
			boolean isManager;
	        		
	        try (JsonReader reader = Json.createReader(req.getReader())) {
		        JsonObject registerData = reader.readObject();
		        login = registerData.getString("login");
		        password = registerData.getString("password");
		        isManager = registerData.getBoolean("isManager");
	        }
	        
	        token = AccountManager.getInstance().registerAccount(login, password, isManager);
	    } else {
			String login = extractLogin(path);
			if (login != null) {
				String password = null;
				// POST /user/{login} - sign in
		        try (JsonReader reader = Json.createReader(req.getReader())) {
			        JsonObject registerData = reader.readObject();
			        password = registerData.getString("password");
		        }
		        
		        if (password != null) {
		        	token = AccountManager.getInstance().signIn(login, password);
		        }
			}
	    }
				
		sendToken(token, resp);
	}
}
