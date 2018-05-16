package org.example.utils;

import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.InvalidQueryException;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

public class QueryUtils {
	
	private Session session;
	
	public QueryUtils(Session session) {
		this.session = session;
	}
	
	/**
	 * method that searchs an email
	 * @param email -> Email to be searched in the query
	 * @return	a NodeIterator with the nodes of the QueryResult
	 */
	public NodeIterator getEmail(String email) {
		NodeIterator ni = null;
		try {
			Query q = session.getWorkspace().getQueryManager().createQuery("SELECT * FROM [gogreen:Author] WHERE [hippo:availability] LIKE 'live' AND [gogreen:email] LIKE '" + email + "'", Query.JCR_SQL2);
			QueryResult r = q.execute();
			ni = r.getNodes();
			return ni;
		} catch (InvalidQueryException e) {			
			e.printStackTrace();
			return ni;
		} catch (RepositoryException e) {
			e.printStackTrace();
			return ni;
		}
	}
	
	/**
	 * method that gets the badword node and create an array with it
	 * @return a String array that contains the bad words
	 */
	public String[] getBadWords() {
		String[] badWords = null;
		try {
			Query q = this.session.getWorkspace().getQueryManager().createQuery(
					"SELECT * FROM [gogreen:Property] WHERE [hippo:availability] LIKE 'live'", Query.JCR_SQL2);
			QueryResult r = q.execute();

			for (NodeIterator i = r.getNodes(); i.hasNext();) {
				String str = i.nextNode().getProperty("gogreen:value").getString();
				str = str.replaceAll("\\s+", "");
				badWords = str.split(",");
			}
			return badWords;
		} catch (RepositoryException e) {
			e.printStackTrace();
			return badWords;
		}
	}

}
