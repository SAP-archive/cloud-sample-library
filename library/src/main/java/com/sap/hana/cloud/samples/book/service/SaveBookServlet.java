package com.sap.hana.cloud.samples.book.service;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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
 * This class adds a book to the library.
 * 
 * If such a book already exists, it returns Bad Request (400).
 * 
 * This servlet can be only called by users with role 'admin'. 
 * */
@WebServlet(name="SaveBookServlet",
urlPatterns={"/restricted/admin/SaveBookServlet"})
public class SaveBookServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String data = IOUtils.extractDataFromRequest(request);

        JsonObject bookData = (new JsonParser().parse(data)).getAsJsonObject();
        String author = bookData.get("authorName").getAsString().trim();
        String title = bookData.get("bookName").getAsString().trim();
       
        try{
    		EntityManager em = PersistenceAdapter.getEntityManager();
    		em.createNamedQuery("bookByTitleAndAuthor").setParameter("bookName", title).setParameter("authorName", author).getSingleResult();
    		JsonObject jsonResponse = new JsonObject();
    		jsonResponse.addProperty("alreadyExists", true);
    		response.getWriter().print(jsonResponse);
    		response.setStatus(400);
    		return;
    		
    	} catch (NoResultException exc) {
    		// this is a new book
    		persistNewBook(bookData);
    	}

	}

	private Book persistNewBook(JsonObject bookData) {
		
		EntityManager em = PersistenceAdapter.getEntityManager();
		
		String author = bookData.get("authorName").getAsString().trim();
	    String title = bookData.get("bookName").getAsString().trim();
		
		Book currentBook = new Book();
		currentBook.setAuthorName(author);
		currentBook.setBookName(title);
		
		// isbn is optional
		if (bookData.get("isbn") != null) {
			currentBook.setIsbn(bookData.get("isbn").getAsString());
		}
		
		em.getTransaction().begin();
		em.persist(currentBook);
		em.getTransaction().commit();
		
		return currentBook;
	}
}
