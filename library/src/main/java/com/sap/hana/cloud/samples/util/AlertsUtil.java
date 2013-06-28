package com.sap.hana.cloud.samples.util;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * This utility class is used for providing an alerting message to the user if a specific situation
 * has occurred during code execution in a servlet (e.g. validation of uploaded image has failed).
 * */
public class AlertsUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(AlertsUtil.class);
	/**
	 * This method redirects the user to a page that displays the alert message
	 * in front of the view the user is currently seeing.
	 *
	 * @param alertText - the text to be displayed in the alert message
	 * @param session - the session in which the method stores the alert message
	 * @param response which executes the redirect
	 * */
	public static void alert(HttpSession session, HttpServletResponse response, String alertText) {
        session.setAttribute("message", alertText);
        try {
            response.sendRedirect("/library/showAlert.jsp");
        } catch (IOException exc) {
        	LOGGER.error("Alert message cannot be displayed! (text: " + alertText + ")", exc);
        }
    }

}
