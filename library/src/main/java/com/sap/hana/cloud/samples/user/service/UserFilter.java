package com.sap.hana.cloud.samples.user.service;

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
import javax.servlet.http.HttpServletRequest;

import com.sap.hana.cloud.samples.adapters.IdentityAdapter;
import com.sap.hana.cloud.samples.adapters.PersistenceAdapter;
import com.sap.hana.cloud.samples.persistence.entities.LibraryUser;


/**
 * This filter is called when a user requests the index.jsp page. It extracts, from the database, the user's data via the identity service (if possible),
 * and creates/updates the corresponding record in the database.
 * */
@WebFilter(filterName="UserFilter", urlPatterns = {"/index.jsp"})
public class UserFilter implements Filter {

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		LibraryUser userFromIdentityService = IdentityAdapter.getLoggedUser((HttpServletRequest) request);

		String currentUserId = userFromIdentityService.getUserId();

		try{
			EntityManager em = PersistenceAdapter.getEntityManager();
			LibraryUser userFromDatabase = (LibraryUser) em.createNamedQuery("getUserById").setParameter("userId", currentUserId).getSingleResult();
			// this user exists in the Database

			updateUser(userFromDatabase, userFromIdentityService);
		}
		catch (NoResultException exc) {
			// this user does not yet exist in the Database
			persistNewUser(userFromIdentityService);
		}

		chain.doFilter(request, response);
	}


	private void persistNewUser(LibraryUser userFromIdentityService) {
		LibraryUser currentUser = userFromIdentityService;
		currentUser.setImgSrc("res/img/default_user_image.jpg");
		
		EntityManager em = PersistenceAdapter.getEntityManager();
		
		em.getTransaction().begin();
		em.persist(currentUser);
		em.getTransaction().commit();
	}


	private void updateUser(LibraryUser currentUser, LibraryUser userFromIdentityService) {
		// check if there are some updates from the Identity service (e.g. mail is changes, or another role is added)
		// and merge them into the user from the Database
		currentUser.mergeWith(userFromIdentityService);
		
		EntityManager em = PersistenceAdapter.getEntityManager();
		
		em.getTransaction().begin();
		em.merge(currentUser);
		em.getTransaction().commit();
	}


	public void init(FilterConfig fConfig) throws ServletException {

	}

	public void destroy() {

	}
}
