package org.example.listeners;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.example.utils.MailUtils;
import org.example.utils.QueryUtils;
import org.hippoecm.repository.HippoStdNodeType;
import org.hippoecm.repository.util.JcrUtils;
import org.hippoecm.repository.util.NodeIterable;
import org.onehippo.cms7.services.HippoServiceRegistry;
import org.onehippo.cms7.services.eventbus.HippoEventBus;
import org.onehippo.cms7.services.eventbus.Subscribe;
import org.onehippo.repository.events.HippoWorkflowEvent;
import org.onehippo.repository.modules.DaemonModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BadWordsListener implements DaemonModule {

	private static final Logger log = LoggerFactory.getLogger(BadWordsListener.class);

	public static final String PUBLICATION_INTERACTION = "default:handle:publish";
	public static final String TITLE_PROPERTY = "gogreen:title";

	private Session session;

	@Override
	public void initialize(Session session) throws RepositoryException {
		this.session = session;
		HippoServiceRegistry.registerService(this, HippoEventBus.class);

	}

	@Override
	public void shutdown() {
		HippoServiceRegistry.unregisterService(this, HippoEventBus.class);

	}

	@Subscribe
	public void handleEvent(final HippoWorkflowEvent event) {
		if (event.success() && PUBLICATION_INTERACTION.equals(event.interaction())) {
			postPublish(event);
		}
	}

	private void postPublish(final HippoWorkflowEvent workflowEvent) {

		try {
			// parent node
			Node node = session.getNodeByIdentifier(workflowEvent.subjectId());
			// child node (the one that has been published)
			Node published = getPublishedVariant(node);
			// content of the publication
			String content = null;
			
			// instance of QueryUtils class
			QueryUtils qu = new QueryUtils(session);			
			
			// gets the badwords array using the object
			String[] badWords = qu.getBadWords();

			NodeIterator ni = published.getNodes();

			while (ni.hasNext()) {
				Node childNode = ni.nextNode();
				if (childNode.getName().contains("content")) {
					// gets the content published
					content = childNode.getProperty("hippostd:content").getString();
					log.debug("Content: " + content);
				}
			}

			// iteration over badWords array 			
			for (String badWord : badWords) {
				if (content.toLowerCase().contains(badWord)) {
					log.debug("Found bad word " + badWord + " in news document " + node.getName());
					String recipient = MailUtils.getRecipient(session);
					MailUtils.sendEmail(badWord, recipient);
				}
			}

		} catch (ItemNotFoundException e) {
			e.printStackTrace();
		} catch (RepositoryException e) {
			e.printStackTrace();
		}

	}

	private static Node getPublishedVariant(Node handle) throws RepositoryException {
		for (Node variant : new NodeIterable(handle.getNodes(handle.getName()))) {
			final String state = JcrUtils.getStringProperty(variant, HippoStdNodeType.HIPPOSTD_STATE, null);
			if (HippoStdNodeType.PUBLISHED.equals(state)) {
				return variant;
			}
		}
		return null;
	}

}
