package org.example.validators;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import javax.jcr.Node;
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

import org.apache.wicket.model.IModel;
import org.hippoecm.frontend.editor.validator.plugins.AbstractCmsValidator;
import org.hippoecm.frontend.model.JcrNodeModel;
import org.hippoecm.frontend.plugin.IPluginContext;
import org.hippoecm.frontend.plugin.config.IPluginConfig;
import org.hippoecm.frontend.validation.IFieldValidator;
import org.hippoecm.frontend.validation.ValidationException;
import org.hippoecm.frontend.validation.Violation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class BadWordsValidator extends AbstractCmsValidator {

	private static Logger log = LoggerFactory.getLogger(BadWordsValidator.class);

	public BadWordsValidator(IPluginContext context, IPluginConfig config) {
		super(context, config);
	}

	@Override
	public void preValidation(IFieldValidator type) throws ValidationException {
		if (!"hippostd:html".equals(type.getFieldType().getType())) {
			throw new ValidationException(
					"Invalid validation exception; cannot validate non-string field for emptyness");
		}
	}

	@Override
	public Set<Violation> validate(IFieldValidator fieldValidator, JcrNodeModel node, IModel childModel)
			throws ValidationException {
		try {
			// colletions
			Set<Violation> violations = new HashSet<Violation>();
			String[] badWords = null;

			// get the node and the value introduced by the writer
			Node nd = (Node) childModel.getObject();
			String value = nd.getProperty("hippostd:content").getString();

			// get session using the node
			Session session = nd.getSession();

			// query to get the bad words
			Query q = session.getWorkspace().getQueryManager().createQuery(
					"SELECT * FROM [gogreen:Property] WHERE [hippo:availability] LIKE 'live'", Query.JCR_SQL2);

			QueryResult r = q.execute();

			for (NodeIterator i = r.getNodes(); i.hasNext();) {
				String str = i.nextNode().getProperty("gogreen:value").getString();
				str = str.replaceAll("\\s+", "");
				badWords = str.split(",");
			}

			for (String s : badWords) {
				if (value.toLowerCase().contains(s)) {

					String recipient = getRecipient(session);
					sendEmail(s, recipient);

					// sendEmail(s, getRecipient(session));
					violations.add(fieldValidator.newValueViolation(childModel, getTranslation()));
				}

			}

			return violations;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	private String getRecipient(Session s) {
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

	private static void sendEmail(String word, String recipient) {
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
