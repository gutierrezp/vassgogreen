package org.example.utils;

import java.util.Properties;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class MailUtils {

	
	/**
	 * gets the recipient through a query
	 * @param s instance of javax.jcr.Session
	 * @return string that represents the recipient email
	 */
	public static String getRecipient(Session s) {
		String recipient = null;
		try {
			Query q = s.getWorkspace().getQueryManager().createQuery(
					"SELECT * FROM [gogreen:Property] WHERE [gogreen:name] LIKE 'admin' AND [hippo:availability] LIKE 'live'",
					Query.JCR_SQL2);
			QueryResult r = q.execute();
			for (NodeIterator i = r.getNodes(); i.hasNext();) {
				recipient = i.nextNode().getProperty("gogreen:value").getString();
			}

			return recipient;
		} catch (InvalidQueryException e) {
			e.printStackTrace();
			return recipient;
		} catch (RepositoryException e) {
			e.printStackTrace();
			return recipient;
		}
	}

	/**
	 * send an email to the recipient
	 * @param word: the bad word typed by the user
	 * @param recipient the recipient of the email
	 * @see getRecipient(Session s)
	 */
	public static void sendEmail(String word, String recipient) {
		// who sends the message
		String transmitter = "cmshippo5@gmail.com";
		String pass = "hippo123cms";

		Properties props = System.getProperties();
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.user", transmitter);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.port", "587");

		javax.mail.Session session = javax.mail.Session.getDefaultInstance(props);
		MimeMessage message = new MimeMessage(session);

		try {
			message.setFrom(new InternetAddress(transmitter));
			message.addRecipients(Message.RecipientType.TO, recipient);
			message.setSubject("Hippo CMS bad language warning");
			message.setText("Someone has tried to write a news document using the bad word " + word);
			Transport transport = session.getTransport("smtp");
			transport.connect("smtp.gmail.com", transmitter, "hippo123cms");
			transport.sendMessage(message, message.getAllRecipients());
			transport.close();
		} catch (MessagingException me) {
			me.printStackTrace(); // Si se produce un error
		}
	}

}
