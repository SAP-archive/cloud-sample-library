package com.sap.hana.cloud.samples.user.service;

import java.io.IOException;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.sap.hana.cloud.samples.adapters.PersistenceAdapter;
import com.sap.hana.cloud.samples.persistence.entities.LibraryUser;

/**
 * This class extracts and returns all the {@link com.sap.hana.cloud.samples.persistence.entities.LibraryUser LibraryUser} entries.
 *
 * This servlet can be called by admin users.
 * */

@WebServlet(name="ExtractAllUsersServlet",
urlPatterns={"/restricted/admin/ExtractAllUsersServlet"})
public class ExtractAllUsersServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("application/json");

		EntityManager em = PersistenceAdapter.getEntityManager();

		@SuppressWarnings("unchecked")
		List<LibraryUser> allUsers = em.createNamedQuery("getAllUsers").getResultList();
		

		response.getWriter().print(new Gson().toJson(allUsers));

	}
}
