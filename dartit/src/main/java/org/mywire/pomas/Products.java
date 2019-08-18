package org.mywire.pomas;

import java.io.IOException;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/products/*")
public class Products extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Products() {
        super();
    }
    
    private Integer extractProductId(String path) {    	
    	if (path == null || !path.startsWith("/")) 
    		return null;
    	
    	try {
    		Integer id = Integer.parseInt(path.substring(1));
    		if (id < 1) {
    			return null;
    		}
    		
    		return id;
    	} catch (NumberFormatException e) {
    		return null;
    	}
    }
        
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		
		// redirect
		if (path == null) {
			resp.sendRedirect(req.getRequestURI() + "/");
			return;
		}
		
		JsonObjectBuilder productsResult = Json.createObjectBuilder();
		
		if (path.equals("/")) {
			// GET /products/ - get list of all products
			List<ProductManager.Product> products = ProductManager.getInstance().getProducts();
			
	    	if (products != null) {				
		        resp.setStatus(HttpServletResponse.SC_OK);
		        productsResult.add("result", "success");
		        
		        JsonArrayBuilder productsJson = Json.createArrayBuilder();
		        
		        for (ProductManager.Product product : products) {
		        	
		        	JsonObjectBuilder productJson = Json.createObjectBuilder();
		        	productJson.add("id", product.getId());
		        	productJson.add("name", product.getName());
		        	productJson.add("amount", product.getAmount());
		        	productJson.add("price", product.getPrice());
		        	
		        	productsJson.add(productJson);
		        }
		        
		        productsResult.add("products", productsJson);
			} else {
		        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		        productsResult.add("result", "fail");
			}
		} else {
			Integer id = extractProductId(path);
			if (id != null) {
				// GET /products/{id} - get existing product
				ProductManager.Product product = ProductManager.getInstance().getProduct(id);
				
		    	if (product != null) {
			        resp.setStatus(HttpServletResponse.SC_OK);
			        productsResult.add("result", "success");
			        
		        	JsonObjectBuilder productJson = Json.createObjectBuilder();
		        	productJson.add("id", product.getId());
		        	productJson.add("name", product.getName());
		        	productJson.add("amount", product.getAmount());
		        	productJson.add("price", product.getPrice());
			        
			        productsResult.add("product", productJson);
				} else {
			        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			        productsResult.add("result", "fail");
				}
			}
		}
		
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        try (JsonWriter writer = Json.createWriter(resp.getWriter())) {
        	writer.writeObject(productsResult.build());
        }
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		
		// redirect
		if (path == null) {
			resp.sendRedirect(req.getRequestURI() + "/");
			return;
		} else if (path.equals("/")) {
			// POST /products/ - create new product
		} else {
			Integer id = extractProductId(path);
			if (id != null) {
				// POST /products/{id} - modify existing product
			}
		}
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String path = req.getPathInfo();
		
		// redirect
		if (path == null) {
			resp.sendRedirect(req.getRequestURI() + "/");
			return;
		} else if (path.equals("/")) {
			// DELETE /products/ - delete all products
		} else {
			Integer id = extractProductId(path);
			if (id != null) {
				// DELETE /products/{id} - delete existing product
			}
		}
	}
}
