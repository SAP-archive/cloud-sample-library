package com.sap.hana.cloud.samples.adapters;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class presents how the SAP HANA Cloud mail configurations can be used in applications.
 * 
 * @see <a href="https://help.hana.ondemand.com/help/frameset.htm?e6ff28a3bb571014b89ad0fa2f410a1f.html">Sending and Fetching E-Mail</a>
 * */
public class MailAdapter {
	private static final Logger LOGGER = LoggerFactory.getLogger(MailAdapter.class);

	// MAIL_FROM will be replaces by the mail address from the Mail destination (named 'Session') in the Cloud scenario
	// and will be displayed as sender in the local server scenario
	private static final String MAIL_FROM="<sap.library@sap.com>";

	private static Session session;

	/**
	 * This method sends an e-mail to a single user. In this application sending e-mails is optional.
	 *
	 * @param mailTo - the e-mail address of the person who receives the letter
	 * @param subject - the e-mail subject
	 * @param mailContent - the letter content
	 * */
	public static void send(String mailTo, String subject, String mailContent) throws MessagingException  {
        Session session = getSession();
        
        if (session == null) {
        	return;
        }
        
        Transport transport = session.getTransport();
        transport.connect();

        Message msg = createMessage(mailTo, subject, mailContent, session);

        transport.sendMessage(msg, msg.getAllRecipients());
        transport.close();
	}

	private static Message createMessage(String mailTo, String subject, String mailContent, Session session) throws AddressException,
			MessagingException {
		InternetAddress addressFrom = new InternetAddress(MAIL_FROM);
		InternetAddress addressTo = new InternetAddress(mailTo);
		Message message = new MimeMessage(session);
		message.setFrom(addressFrom);
		message.setRecipient(Message.RecipientType.TO, addressTo);
		message.setSubject(subject);
		message.setContent(mailContent, "text/plain");

		return message;
	}

	private static Session getSession() {
		if (session == null) {
			 InitialContext ctx;
			try {
				ctx = new InitialContext();
				session =  (Session) ctx.lookup("java:comp/env/mail/Session");
			} catch (NamingException exc) {
				LOGGER.debug("NamingException has occurred while trying to lookup Session!", exc);
			}
		}

		return session;
	}

}
