package com.sap.hana.cloud.samples.book.service;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.hana.cloud.samples.adapters.PersistenceAdapter;
import com.sap.hana.cloud.samples.persistence.entities.Book;
import com.sap.hana.cloud.samples.persistence.entities.LibraryUser;


/**
 * This class marks a book as returned.
 * 
 * A user can return his/her book.
 * This updates the status of the {@link com.sap.hana.cloud.samples.persistence.entities.Book book}
 * and the {@link com.sap.hana.cloud.samples.persistence.entities.LibraryUser user}.
 * 
 * This servlet can be called by users with role 'Everyone'. 
 * */
@WebServlet(name="ReturnBookServlet",
urlPatterns={"/restricted/everyone/ReturnBookServlet"})
public class ReturnBookServlet extends HttpServlet {

	private static final long serialVersionUID = 1;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String userId = request.getParameter("userId").trim();
		String title = request.getParameter("bookName").trim();
	    String author = request.getParameter("authorName").trim();

        EntityManager em = PersistenceAdapter.getEntityManager();

        LibraryUser userFromDatabase = (LibraryUser) em.createNamedQuery("getUserById").setParameter("userId", userId).getSingleResult();
        Book lendedBook = (Book) em.createNamedQuery("bookByTitleAndAuthor").setParameter("bookName", title).setParameter("authorName", author).getSingleResult();

        lendedBook.setReserved(false);
        lendedBook.setReservedBy(null);
        lendedBook.setReservedUntil(null);

        em.getTransaction().begin();
        em.createNamedQuery("deleteLendingByUserAndBook").setParameter("user", userFromDatabase).setParameter("lendedBook", lendedBook).executeUpdate();
		em.merge(lendedBook);
		em.getTransaction().commit();
		
	}
}

