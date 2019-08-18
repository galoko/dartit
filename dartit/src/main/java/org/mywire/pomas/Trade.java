package org.mywire.pomas;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.json.Json;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/trade")
public class Trade extends HttpServlet {
	private static final long serialVersionUID = 6L;
       
    public Trade() {
        super();
    }
    
    @Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	List<TradeManager.Product> products = new ArrayList<>();
    	
        try (JsonReader reader = Json.createReader(req.getReader())) {
	        JsonObject tradeData = reader.readObject();
	        JsonObject productsJson = tradeData.getJsonObject("products");
	        
	        for (Entry<String, JsonValue> entry : productsJson.entrySet()) {
	        	int id = Integer.parseInt(entry.getKey());
	        	int amount = ((JsonNumber)entry.getValue()).intValue();
	        	
	        	TradeManager.Product product = new TradeManager.Product(id, amount);
	        	
	        	products.add(product);
	        }
        }
    	
    	JsonObjectBuilder tradeResult = Json.createObjectBuilder();
   
    	AccountManager.User user = AccountManager.getInstance().getUser(req);
    	
    	boolean success = TradeManager.getInstance().trade(user, products);
    	if (success) {
	        resp.setStatus(HttpServletResponse.SC_OK);
	        tradeResult.add("result", "success");
		} else {
	        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	        tradeResult.add("result", "fail");
		}
    	
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try (JsonWriter writer = Json.createWriter(resp.getWriter())) {
        	writer.writeObject(tradeResult.build());
        }
    }

}
