package com.sap.hana.cloud.samples.my.books.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.sap.hana.cloud.samples.util.CalendarUtils;
import com.google.gson.Gson;
import com.sap.hana.cloud.samples.adapters.IdentityAdapter;
import com.sap.hana.cloud.samples.adapters.PersistenceAdapter;
import com.sap.hana.cloud.samples.persistence.entities.BookLending;
import com.sap.hana.cloud.samples.persistence.entities.LibraryUser;


/**
 * This servlet extracts, from the database, all the books that the currently logged-on user
 * has taken from the library. The result is displayed in the 'My Books' tab in the UI.
 * 
 * This servlet can be called by all users.
 * */
@WebServlet(name="ExtractMyBooksServlet",
urlPatterns={"/restricted/everyone/ExtractMyBooksServlet"})
public class ExtractMyBooksServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("application/json");

		EntityManager em = PersistenceAdapter.getEntityManager();

		String currentUserId = IdentityAdapter.getLoggedUserId(request);

		LibraryUser userFromDatabase = (LibraryUser) em.createNamedQuery("getUserById").setParameter("userId", currentUserId).getSingleResult();

		@SuppressWarnings("unchecked")
		List<BookLending> myBooks =  (List<BookLending>) em.createNamedQuery("lendingsByUser").setParameter("user", userFromDatabase).getResultList();

		updateRemainingDays(myBooks);

		response.getWriter().print(new Gson().toJson(myBooks));
	}


	private void updateRemainingDays(List<BookLending> myBooks) {

		EntityManager em = PersistenceAdapter.getEntityManager();
		
		for (BookLending lending : myBooks) {

			int remainingDays = CalendarUtils.getDaysBetween(new Date(), lending.getLendedBook().getReservedUntil());

			if (lending.getRemainingDays() != remainingDays) {
				lending.setRemainingDays(remainingDays);
				em.getTransaction().begin();
				em.merge(lending);
				em.getTransaction().commit();
			}

		}
	}
}
