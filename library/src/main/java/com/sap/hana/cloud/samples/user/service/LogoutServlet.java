package com.sap.hana.cloud.samples.user.service;

import java.io.IOException;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.security.auth.login.LoginContextFactory;

/**
 * This servlet is called when the user clicks on the "Log out" button in the UI.
 * It logs out the user from the application. If the operation is successful, the user is
 * redirected to the logout.jsp page.
 * 
 * This servlet can be called by all users.
 * */
@WebServlet(name="LogoutServlet",
urlPatterns={"/restricted/everyone/LogoutServlet"})
public class LogoutServlet extends HttpServlet {
	private static final Logger LOGGER = LoggerFactory.getLogger(LogoutServlet.class);
	private static final long serialVersionUID = 1;

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	  if (request.getRemoteUser() != null) {
		    try {
		      LoginContext loginContext = LoginContextFactory.createLoginContext();
		      loginContext.logout();
		    } catch (LoginException exc) {
		    	LOGGER.error("Logout failed.", exc);
		    }
	   }
	}
}