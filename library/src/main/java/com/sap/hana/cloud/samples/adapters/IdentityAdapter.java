package com.sap.hana.cloud.samples.adapters;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import com.sap.hana.cloud.samples.persistence.entities.LibraryUser;
import com.sap.security.um.service.UserManagementAccessor;
import com.sap.security.um.user.PersistenceException;
import com.sap.security.um.user.UnsupportedUserAttributeException;
import com.sap.security.um.user.UserProvider;
import com.sap.security.um.user.User;

/**
 * This class makes use of the identity service provided by SAP HANA Cloud Platform.
 * It aims to retrieve details about a specific SAP HANA Cloud user, maintained in the Platform.
 *
 * @see <a href="https://help.hana.ondemand.com/help/frameset.htm?e6b196abbb5710148c8ec6a698441b1e.html">SAP HANA Cloud Identity Service</a>
 * */
public class IdentityAdapter {

	/**
	 * This method returns the currently logged-on user as a {@link com.sap.hana.cloud.samples.persistence.entities.LibraryUser LibraryUser} object.
	 *
	 * @param request from which the method extracts the required information
	 * @return an instance of the LibraryUser entity
	 * */
	public static LibraryUser getLoggedUser (HttpServletRequest request) {

        if (request == null) {
            throw new IllegalArgumentException("Request must not be null");
        }

        LibraryUser libUser = null;

        try{
	        User idmUser = null;
	        Principal principal = request.getUserPrincipal();
	        if (principal != null) {
	            // Read the currently logged in user from the user storage
	        	UserProvider provider = UserManagementAccessor.getUserProvider();
	            idmUser = provider.getUser(principal.getName());
	        }

	        if (idmUser == null) {
	        	throw new RuntimeException("Could not get identity management user obejct");
	        }

	        String userName = idmUser.getName();
	        libUser = new LibraryUser(userName.toLowerCase());

	        String firstName = idmUser.getAttribute("firstname");
	        libUser.setFirstName(firstName);

	        String lastName = idmUser.getAttribute("lastname");
	        libUser.setLastName(lastName);

	        String displayName = firstName + " " + lastName;
	        libUser.setDisplayName(displayName);

	        String email = idmUser.getAttribute("email");
	        libUser.setEmail(email);

	        libUser.setRoles(idmUser.getRoles());
        }
        catch (PersistenceException exc) {
        	throw new RuntimeException("Could not retrieve currently logged in user due to a PersistenceException", exc);
        }
        catch (UnsupportedUserAttributeException exc) {
        	throw new RuntimeException("Could not retrieve currently logged in user due to an UnsupportedUserAttributeException", exc);
		}

		return libUser;
	}

	/**
	 * This method returns only the unique user ID that belongs to the currently logged-on user.
	 *
	 * @param request from which the user ID is extracted
	 * @return the unique user ID
	 * */
	public static String getLoggedUserId (HttpServletRequest request) {

		if (request == null) {
			throw new IllegalArgumentException("Request must not be null.");
		}

		Principal userPrincipal = request.getUserPrincipal();
		String userName = userPrincipal.getName().toLowerCase();

		return userName;
	}

}
