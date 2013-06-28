package com.sap.hana.cloud.samples.book.service;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.hana.cloud.samples.adapters.PersistenceAdapter;


/**
* This class deletes a book.
* 
* This servlet can be only called by users with role 'admin'.
*/
@WebServlet(name="RemoveBookServlet",
urlPatterns={"/restricted/admin/RemoveBookServlet"})
public class RemoveBookServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String title = request.getParameter("bookName").trim();
	    String author = request.getParameter("authorName").trim();

		EntityManager em = PersistenceAdapter.getEntityManager();

		em.getTransaction().begin();
		em.createNamedQuery("removeBookByTitleAndAuthor").setParameter("bookName", title).setParameter("authorName", author).executeUpdate();
		em.getTransaction().commit();
	}

}
