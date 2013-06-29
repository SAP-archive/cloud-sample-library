package com.sap.hana.cloud.samples.my.books.service;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.hana.cloud.samples.adapters.IdentityAdapter;
import com.sap.hana.cloud.samples.adapters.PersistenceAdapter;
import com.sap.hana.cloud.samples.persistence.entities.Book;
import com.sap.hana.cloud.samples.persistence.entities.BookLending;
import com.sap.hana.cloud.samples.persistence.entities.LibraryUser;


/**
 * The user can rate the books he/she has taken. A change in the personal rating calls this servlet, which
 * calculates the overall rating of the book.
 * 
 * This servlet can be called by all users.
 * */
@WebServlet(name="ChangeRatingOfBookServlet",
urlPatterns={"/restricted/everyone/ChangeRatingOfBookServlet"})
public class ChangeRatingOfBookServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String author = request.getParameter("author").trim();
		String title = request.getParameter("title").trim();
		int newRating = Integer.parseInt(request.getParameter("newRating").trim());

		EntityManager em = PersistenceAdapter.getEntityManager();

		String currentUserId = IdentityAdapter.getLoggedUserId(request);
		LibraryUser userFromDatabase = (LibraryUser) em.createNamedQuery("getUserById").setParameter("userId", currentUserId).getSingleResult();
		Book currentBook = (Book) em.createNamedQuery("bookByTitleAndAuthor").setParameter("bookName", title).setParameter("authorName", author).getSingleResult();

		int numberOfRatings = currentBook.getNumberOfRatings() + 1;
		currentBook.setNumberOfRatings(numberOfRatings);

		long sumOfRatings = currentBook.getSumOfRatings() + newRating;
		currentBook.setSumOfRatings(sumOfRatings);

		float calculatedRating = (float) ((double)sumOfRatings/numberOfRatings);
		currentBook.setBookRating(calculatedRating);

		BookLending currentLending = (BookLending) em.createNamedQuery("lendingsByUserAndBook").setParameter("user", userFromDatabase).setParameter("lendedBook", currentBook).getSingleResult();
		currentLending.setRating(newRating);

		em.getTransaction().begin();
		em.merge(currentBook);
		em.merge(currentLending);
		em.getTransaction().commit();
	}

}
