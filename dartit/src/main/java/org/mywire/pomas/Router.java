package org.mywire.pomas;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("")
public class Router extends HttpServlet {
	private static final long serialVersionUID = 2L;
       
    public Router() {
        super();
    }

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		AccountManager.User user = AccountManager.getInstance().getUser(req);
		
		resp.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
		if (user != null) {
			if (user.isManager())
				resp.setHeader("Location", "/manager");
			else
				resp.setHeader("Location", "/shop");
		} else {
			resp.setHeader("Location", "/unauthorized");
		}
	}
}
