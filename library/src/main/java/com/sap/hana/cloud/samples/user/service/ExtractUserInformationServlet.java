package com.sap.hana.cloud.samples.user.service;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.google.gson.Gson;
import com.sap.hana.cloud.samples.adapters.IdentityAdapter;
import com.sap.hana.cloud.samples.adapters.PersistenceAdapter;
import com.sap.hana.cloud.samples.persistence.entities.LibraryUser;

/**
 * This servlet is relevant to the 'My Profile' tab in the UI. It extracts, from the database, details about the currently logged-on
 * user and generates a JSON file with it, which the UI displays.
 * 
 * This servlet can be called by all users.
 * */
@WebServlet(name="ExtractUserInformationServlet",
urlPatterns={"/restricted/everyone/ExtractUserInformationServlet"})
public class ExtractUserInformationServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("application/json");

		EntityManager em = PersistenceAdapter.getEntityManager();

		String currentUserId = IdentityAdapter.getLoggedUserId(request);

		LibraryUser userFromDatabase = (LibraryUser) em.createNamedQuery("getUserById").setParameter("userId", currentUserId).getSingleResult();

		response.getWriter().print(new Gson().toJson(userFromDatabase));

	}
}
