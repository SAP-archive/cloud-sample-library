package com.sap.hana.cloud.samples.user.service;

import java.io.BufferedReader;
import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.sap.hana.cloud.samples.adapters.PersistenceAdapter;
import com.sap.hana.cloud.samples.persistence.entities.LibraryUser;

/**
 * This servlet is called when the user changes a field in the 'My Profile' tab. It updates
 * the corresponding record in the database.
 * 
 * This servlet can be called by all users.
 * */
@WebServlet(name="UpdateUserServlet",
urlPatterns={"/restricted/everyone/UpdateUserServlet"})
public class UpdateUserServlet extends HttpServlet {
	private static final long serialVersionUID = 1;

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		Gson gson = new Gson();

		String line;
		StringBuilder builder = new StringBuilder();
		BufferedReader reader = request.getReader();
        while ((line = reader.readLine()) != null) {
               builder.append(line);
        }

        LibraryUser updatedUser = gson.fromJson(builder.toString(), LibraryUser.class);

		EntityManager em = PersistenceAdapter.getEntityManager();

		em.getTransaction().begin();
		em.merge(updatedUser);
		em.getTransaction().commit();

	}

}
