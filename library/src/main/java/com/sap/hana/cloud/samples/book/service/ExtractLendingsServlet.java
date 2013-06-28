package com.sap.hana.cloud.samples.book.service;

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
import com.sap.hana.cloud.samples.persistence.entities.BookLending;

/**
 * This class extracts and returns all books that have been taken from the library, together 
 * with the relevant users currently reading them.
 * 
 * This servlet can be called only by users with role 'admin'. 
 * */
@WebServlet(name="ExtractLendingsServlet",
urlPatterns={"/restricted/admin/ExtractLendingsServlet"})
public class ExtractLendingsServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		EntityManager em = PersistenceAdapter.getEntityManager();

		@SuppressWarnings("unchecked")
		List<BookLending> allLendings = (List<BookLending>) em.createNamedQuery("allLendings").getResultList();

		response.getWriter().print(new Gson().toJson(allLendings));

	}

}
