package com.sap.hana.cloud.samples.user.image.service;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import com.sap.hana.cloud.samples.util.IOUtils;

import com.sap.hana.cloud.samples.adapters.DocumentAdapter;
import com.sap.hana.cloud.samples.adapters.IdentityAdapter;
import com.sap.hana.cloud.samples.adapters.PersistenceAdapter;
import com.sap.hana.cloud.samples.persistence.entities.LibraryUser;


/**
 * This servlet is called if the user does not want to use the default image but has uploaded a new one instead, and then the picture is visualized. 
 * The servlet retrieves the image from the document service repository,
 * and as a response returns the image itself.
 * 
 * This servlet can be called by all users.
 * */
@WebServlet(name="VisualizeImageServlet",
urlPatterns={"/restricted/everyone/VisualizeImageServlet"})
public class VisualizeImageServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		response.setContentType("image/png");


		String filename = request.getParameter("filename");

		 try {
	            byte[] pictureAsBytes = DocumentAdapter.getDocumentAsByteArray(filename);
	            OutputStream servletOut = response.getOutputStream();

	            IOUtils.writeToOutputStream(new ByteArrayInputStream(pictureAsBytes), servletOut);
	        } catch (CmisObjectNotFoundException exc) {

	        	fallbackToDefaultImage(request);
	        }

	}

	private void fallbackToDefaultImage(HttpServletRequest request) {
		EntityManager em = PersistenceAdapter.getEntityManager();
		String currentUserId = IdentityAdapter.getLoggedUserId(request);
		LibraryUser userFromDatabase = (LibraryUser) em.createNamedQuery("getUserById").setParameter("userId", currentUserId).getSingleResult();
		userFromDatabase.setImgSrc("res/img/default_user_image.jpg");

		em.getTransaction().begin();
		em.merge(userFromDatabase);
		em.getTransaction().commit();
	}
}
