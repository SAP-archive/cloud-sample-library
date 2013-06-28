package com.sap.hana.cloud.samples.book.service;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.sap.hana.cloud.samples.adapters.PersistenceAdapter;
import com.sap.hana.cloud.samples.persistence.entities.Book;

/**
 * This filter checks if a book has been reserved. It is possible, for example, to have a book reserved, and another user to click
 * on the 'Reserve' button. Since the book has been already reserved, the second user should be notified and data reloading is required.
 * */
@WebFilter(filterName="ActionsFilter", urlPatterns = {"/restricted/everyone/ReserveBookServlet",
		"/restricted/admin/EditBookServlet", "/restricted/admin/RemoveBookServlet", "/restricted/admin/ReturnBookServlet"})
public class ActionsFilter implements Filter {

	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		// if a book has been reserved then it cannot be reserved again, cannot be edited and cannot be removed
		// if a book has been returned then it cannot be returned again

		boolean canProceed = false;

		boolean tryingToReturnBook = (request.getParameter("returningBook") != null);
		
		EntityManager em = PersistenceAdapter.getEntityManager();

		String title = request.getParameter("bookName").trim();
		String author = request.getParameter("authorName").trim();

		Book book = (Book) em.createNamedQuery("bookByTitleAndAuthor").setParameter("bookName", title).setParameter("authorName", author).getSingleResult();

		try {
			em.createNamedQuery("lendingByBook").setParameter("lendedBook", book).getSingleResult();

			// if the user is trying to return a book and the book has been borrowed then we can proceed
			if (tryingToReturnBook) {
				canProceed = true;
			} else {
				canProceed = false;
			}

		} catch (NoResultException exc) {

			// if the book is has not been borrowed and the user is trying to return it, then we cannot proceed
			if (tryingToReturnBook) {
				canProceed = false;
			} else {
				canProceed = true;
			}
		}

		if (canProceed) {
			chain.doFilter(request, response);
		} else{
			JsonObject object = new JsonObject();
			object.addProperty("canProceed", false);
			response.getWriter().print(new Gson().toJson(object));
			((HttpServletResponse)response).setStatus(400);
			return;
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {

	}

	@Override
	public void destroy() {
		
	}

}
