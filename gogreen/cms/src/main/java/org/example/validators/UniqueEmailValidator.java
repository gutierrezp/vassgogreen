package org.example.validators;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PropertyIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

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

public class UniqueEmailValidator extends AbstractCmsValidator {

	private static Logger log = LoggerFactory.getLogger(UniqueEmailValidator.class);

	public UniqueEmailValidator(IPluginContext context, IPluginConfig config) {
		super(context, config);
	}

	@Override
	public void preValidation(IFieldValidator type) throws ValidationException {

		if (!"String".equals(type.getFieldType().getType())) {
			throw new ValidationException(
					"Invalid validation exception; cannot validate non-string field for emptyness");
		}
	}

	@Override
	public Set<Violation> validate(IFieldValidator fieldValidator, JcrNodeModel node, IModel childModel)
			throws ValidationException {

		try {
			// stores all the emails of the documents
			ArrayList<String> emails = new ArrayList<>();

			Set<Violation> violations = new HashSet<Violation>();

			// gets the value introduced by the admin in the cms
			String value = (String) childModel.getObject();

			Node n = node.getNode();

			Session session = n.getSession();

			Query q = session.getWorkspace().getQueryManager().createQuery("SELECT * FROM [gogreen:Author] ", Query.JCR_SQL2);

			QueryResult r = q.execute();			
			
			int cont = 0;

			for (NodeIterator i = r.getNodes(); i.hasNext();) {
				String email = i.nextNode().getProperty("gogreen:email").getString();
				if (email.equals(value)) {
					cont++;
				}
			}
			
			if (cont >= 2) {
				violations.add(fieldValidator.newValueViolation(childModel, getTranslation()));
			}

			return violations;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
