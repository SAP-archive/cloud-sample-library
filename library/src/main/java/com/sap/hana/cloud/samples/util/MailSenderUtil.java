package com.sap.hana.cloud.samples.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;

import javax.mail.MessagingException;

import com.sap.hana.cloud.samples.adapters.MailAdapter;
import com.sap.hana.cloud.samples.persistence.entities.Book;
import com.sap.hana.cloud.samples.persistence.entities.LibraryUser;


/**
 * This utility class is used for processing the templates for e-mails.
 * */
public class MailSenderUtil {
	
	private static final String SUBJECT = "SAP Library Sample";

	private LibraryUser user;
	private Book book;
	
	public MailSenderUtil(LibraryUser user, Book book) {
		this.user = user;
		this.book = book;
	}
	
	/**
	 * This method sends e-mail to the user who has reserved a book.
	 * */
	public void sendForReservedBook() throws IOException, MessagingException {
	
		String template = commonTemplateProcessing(getTemplate(Mailing.BOOK_RESERVATION));
		
		SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd");
		String message = template.replace("${deadline}", dt.format(book.getReservedUntil()));

		// send mail to the user that the book is now reserved
		MailAdapter.send(user.getEmail(), SUBJECT, message);
	}
	
	/**
	 * This method sends e-mail to the user who has just returned a book.
	 * */
	public void sendForReturningBook() throws IOException, MessagingException {
			
		String message = commonTemplateProcessing(getTemplate(Mailing.BOOK_RETURN));

		// send mail to the user that the book has been reserved
		MailAdapter.send(user.getEmail(), SUBJECT, message);
	}
	
	private String getTemplate(Mailing mailing) throws IOException {
		
		InputStream is;
		String template = null;
		
		switch(mailing) {
		case BOOK_RESERVATION: 
			is = getClass().getResourceAsStream("/reserve_book_mail_template.txt");
			template = new String(IOUtils.toByteArray(is), "UTF-8");
			break;
			
		case BOOK_RETURN:
			is = getClass().getResourceAsStream("/return_book_mail_template.txt");
			template = new String(IOUtils.toByteArray(is), "UTF-8");
			break;
		}
		
		return template;
	}
	
	/**
	 * This method proceeds some replacements in the template 
	 * that are needed in both the cases: when reserving a book and when returning a book.
	 * 
	 * @param template - the template over which the replacing operations are going to be held
	 * @return the template after the replacements 
	 * */
	private String commonTemplateProcessing(String template) {
		
		template = template.replace("${displayname}", user.getDisplayName());
		template = template.replace("${title}", book.getBookName());
		template = template.replace("${author}", book.getAuthorName());
		
		return template;
	}
	
}


enum Mailing{
	BOOK_RESERVATION, 
	BOOK_RETURN
};
