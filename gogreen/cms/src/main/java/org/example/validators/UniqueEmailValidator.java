package org.example.validators;

import java.util.HashSet;
import java.util.Set;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.apache.wicket.model.IModel;
import org.example.utils.QueryUtils;
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

			Set<Violation> violations = new HashSet<Violation>();

			// get the node and the value introduced by the writer
			String value = childModel.getObject().toString();
			log.debug("value: " + value);

			// get session using the node
			Node n = node.getNode();
			Session session = n.getSession();

			// query to get the emails using QueryUtils class
			// it only gets the ones with the property availability in live
			QueryUtils qu = new QueryUtils(session);

			NodeIterator it = qu.getEmail(value);
			
			// in case of not empty, the email typed is not valid
			if (it.getSize() != 0) {
				violations.add(fieldValidator.newValueViolation(childModel, getTranslation()));	
			}			

			return violations;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

}
