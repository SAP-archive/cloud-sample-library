package com.sap.hana.cloud.samples.book.service;

import java.io.IOException;

import javax.mail.MessagingException;
import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.sap.hana.cloud.samples.adapters.IdentityAdapter;
import com.sap.hana.cloud.samples.adapters.PersistenceAdapter;
import com.sap.hana.cloud.samples.persistence.entities.Book;
import com.sap.hana.cloud.samples.persistence.entities.LibraryUser;
import com.sap.hana.cloud.samples.util.AlertsUtil;
import com.sap.hana.cloud.samples.util.MailSenderUtil;

/**
 * This class sends an e-mail to a user. 
 * 
 * Two cases for sending e-mails are relevant: 
 * 1) When a book is reserved
 * 2) When a book is returned to the library.
 * 
 * This servlet can be called by all users.
 * */
@WebServlet(name="SendMailServlet",
urlPatterns={"/restricted/everyone/SendMailServlet"})
public class SendMailServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String status = request.getParameter("status").trim();
		String title = request.getParameter("bookName").trim();
	    String author = request.getParameter("authorName").trim();
	    
		
		EntityManager em = PersistenceAdapter.getEntityManager();
		
		String currentUserId = IdentityAdapter.getLoggedUserId(request);		
		LibraryUser currentUser = (LibraryUser) em.createNamedQuery("getUserById").setParameter("userId", currentUserId).getSingleResult();
				    
		Book bookToReserve = (Book) em.createNamedQuery("bookByTitleAndAuthor").setParameter("bookName", title).setParameter("authorName", author).getSingleResult();
		
		MailSenderUtil util = new MailSenderUtil(currentUser, bookToReserve);
		try {
			
			if (status.equals("reserved")) {
				util.sendForReservedBook();	
				return;
			}
			
			if (status.equals("returned")) {
				util.sendForReturningBook();
				return;
			}
			
		} catch (MessagingException e) {
			AlertsUtil.alert(request.getSession(), response, "Could not send email to user " + currentUser.getUserId() + " due to an error.");
		}
		
	}
}
