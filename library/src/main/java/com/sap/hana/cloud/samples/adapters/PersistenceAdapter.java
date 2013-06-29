package com.sap.hana.cloud.samples.adapters;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.sql.DataSource;

import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class perform lazy initialization of the persistence service and caches the entity manager instance for reuse.
 * 
 * @see <a href="https://help.hana.ondemand.com/help/frameset.htm?e7b3c275bb571014a910b3fb4329cf09.html">SAP HANA Cloud Persistence Service</a>
 * */
public class PersistenceAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(PersistenceAdapter.class);

	private static EntityManagerFactory emf;
	private static EntityManager em;

	/**
	 * This method returns an {@link javax.persistence.EntityManager EntityManager} instance.
	 *
	 * @return {@link javax.persistence.EntityManager EntityManager} instance
	 * */
	public static EntityManager getEntityManager() {
		if (emf == null) {
			init();
		}

		if (em == null) {
			em = emf.createEntityManager();
		}

		return em;
	}


	private static void init() {

		Connection connection = null;
		try {
			InitialContext ctx = new InitialContext();
			DataSource ds = (DataSource) ctx.lookup("java:comp/env/jdbc/DefaultDB");
			connection = ds.getConnection();
			Map<String, Object> properties = new HashMap<String, Object>();
			properties.put(PersistenceUnitProperties.NON_JTA_DATASOURCE, ds);
			String databaseProductName = connection.getMetaData().getDatabaseProductName();

			// it is possible to use HANA DB
			// if you would like to use HANA DB, please refer to the official documentation where
			// the prerequisites are described (https://help.hana.ondemand.com/help/frameset.htm?e7a837f4bb5710149251a99bcf22430b.html)
			if (databaseProductName.equals("HDB")) {
				properties.put("eclipselink.target-database",
						"com.sap.persistence.platform.database.HDBPlatform");
			}
			emf = Persistence.createEntityManagerFactory("library",	properties);
		} catch (NamingException exc) {
			LOGGER.error("A NamingException has occured while initializing persistence service.", exc);
			throw new RuntimeException("Persistence service cannot be initialized.", exc);
		} catch (SQLException exc) {
			LOGGER.error("An SQLException has occured while initializing persistence service.", exc);
			throw new RuntimeException("Persistency service cannot be initialized.", exc);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException exc) {
					LOGGER.error("An SQLException has occured while closing connection.", exc);
				}
			}
		}
	}
}
