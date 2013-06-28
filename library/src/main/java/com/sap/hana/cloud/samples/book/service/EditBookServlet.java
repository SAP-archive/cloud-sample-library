package com.sap.hana.cloud.samples.book.service;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sap.hana.cloud.samples.adapters.PersistenceAdapter;
import com.sap.hana.cloud.samples.persistence.entities.Book;
import com.sap.hana.cloud.samples.util.IOUtils;

/**
 * This class applies changes for a particular book.
 *
 * This servlet can be only called by users with role 'admin'.
 * */
@WebServlet(name="EditBookServlet",
urlPatterns={"/restricted/admin/EditBookServlet"})
public class EditBookServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String data = IOUtils.extractDataFromRequest(request);

        JsonObject bookData = (new JsonParser().parse(data)).getAsJsonObject();

        String previousAuthor = bookData.get("previousAuthorName").getAsString().trim();
        String previousTitle = bookData.get("previousBookName").getAsString().trim();

        EntityManager em = PersistenceAdapter.getEntityManager();
		Book bookToUpdate = (Book) em.createNamedQuery("bookByTitleAndAuthor").setParameter("bookName", previousTitle).setParameter("authorName", previousAuthor).getSingleResult();

		updateBook(bookToUpdate, bookData);

	}

	private Book updateBook(Book bookToUpdate, JsonObject bookData) {

		String author = bookData.get("authorName").getAsString().trim();
	    String title = bookData.get("bookName").getAsString().trim();

	    bookToUpdate.setAuthorName(author);
		bookToUpdate.setBookName(title);

		// isbn is optional
		if (bookData.get("isbn") != null) {
			bookToUpdate.setIsbn(bookData.get("isbn").getAsString());
		}

		EntityManager em = PersistenceAdapter.getEntityManager();

		em.getTransaction().begin();
		em.merge(bookToUpdate);
		em.getTransaction().commit();

		return bookToUpdate;
	}
}
