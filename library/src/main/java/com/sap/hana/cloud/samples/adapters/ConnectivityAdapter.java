package com.sap.hana.cloud.samples.adapters;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.http.client.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sap.core.connectivity.api.DestinationException;
import com.sap.core.connectivity.api.http.HttpDestination;

/**
 * This class perform initialization of the SAP HANA Cloud connectivity service.
 *
 * @see <a href="https://help.hana.ondemand.com/help/frameset.htm?e54cc8fbbb571014beb5caaf6aa31280.html">SAP HANA Cloud Connectivity Service</a>
 * */
public class ConnectivityAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(ConnectivityAdapter.class);

	private static  HttpClient httpClient;

	/**
	 * This method provides an {@link org.apache.http.client.HttpClient
	 * HttpClient} object. This instance is later used to retrieve details for a particular book from the OpenLibrary's REST API.
	 *
	 * @return an {@link org.apache.http.client.HttpClient HttpClient} instance,
	 *         configured for the OpenLibrary destination.
	 * */
	public static HttpClient getHttpClient() {

	    if (httpClient == null) {

			try {
		           Context ctx = new InitialContext();
		           HttpDestination destination = (HttpDestination) ctx.lookup("java:comp/env/OpenLibrary");
		           httpClient = destination.createHttpClient();
		       } catch (NamingException exc) {
		           LOGGER.error("A NamingException has occurred while trying to retrieve destination OpenLibrary");
		       } catch (DestinationException exc) {
		    	   LOGGER.error("A DestinationException has occurred while trying to create HttpClient instance", exc);
		       }

	    }

			return httpClient;

	    }

}
