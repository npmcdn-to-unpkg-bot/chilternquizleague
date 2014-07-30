package org.chilternquizleague.web;

import static com.googlecode.objectify.ObjectifyService.ofy;
import static java.lang.String.format;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chilternquizleague.domain.BaseEntity;
import org.chilternquizleague.domain.individuals.IndividualQuiz;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.Key;

@SuppressWarnings("serial")
abstract class BaseRESTService extends HttpServlet {

	private static final Logger LOG = Logger.getLogger(BaseRESTService.class
			.getName());
	protected final Map<String, String> aliases = new HashMap<>();
	protected final List<Package> domainPackages = new ArrayList<>();
	protected ObjectMapper objectMapper;

	public BaseRESTService() {
		super();
	}

	protected void handleEntities(HttpServletResponse resp,
			final String[] parts, final String head) throws IOException {
		final String entityName = getEntityName(head);

		if (head.contains("-list")) {

			makeEntityList(resp, entityName);
		} else {
			entityByKey(resp, entityName, parts[1]);
		}
	}

	protected String[] getParts(HttpServletRequest req) {
		final String[] parts = req.getPathInfo().split("\\/");
		return Arrays.copyOfRange(parts, 1, parts.length);
	}

	protected String getEntityName(final String head) {
		final String stripped = head.replace("-list", "");

		return aliases.containsKey(stripped) ? aliases.get(stripped) : stripped;

	}

	@SuppressWarnings("unchecked")
	protected <T> Class<T> getClassFromPart(String part) {

		final String className = part.substring(0, 1).toUpperCase()
				+ part.substring(1);

		for (Package packge : domainPackages) {

			try {
				return (Class<T>) Class.forName(packge.getName() + "."
						+ className);
			} catch (ClassNotFoundException e) {
				// consume
			}
		}

		final String errorMsg = format("No class found for %1$s", className);
		LOG.severe(errorMsg);
		throw new RuntimeException(errorMsg);

	}

	private <T> void entityByKey(HttpServletResponse resp,
			String entityName, String idPart) throws IOException {

		final Class<T> clazz = getClassFromPart(entityName);
		
		try {
			final long id = Long.parseLong(idPart);
			final T entity = ofy().load().now(Key.create(clazz, id));

			if (LOG.isLoggable(Level.FINE)) {
				LOG.fine(objectMapper.writeValueAsString(entity));
			}

			objectMapper.writeValue(resp.getWriter(), entity);

		} catch (NumberFormatException e) {
			try {

				final T entity = clazz.newInstance();
				objectMapper.writerWithDefaultPrettyPrinter().writeValue(
						resp.getWriter(), entity);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private <T extends BaseEntity> void makeEntityList(
			HttpServletResponse resp, String entityName) throws IOException {
		
		final Class<T> clazz = getClassFromPart(entityName);
		
		final List<T> items = filterEntityList(ofy().load().type(clazz).list());

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine(objectMapper.writeValueAsString(items));
		}

		objectMapper.writeValue(resp.getWriter(), items);

	}

	protected <T extends BaseEntity> List<T> filterEntityList(List<T> entities) {
		return entities;
	}

	protected <T extends BaseEntity> void saveUpdate(final HttpServletRequest req,
			final HttpServletResponse resp, final String entityName)
			throws IOException {

		final Class<T> clazz =  getClassFromPart(entityName);
		
		final T entity = objectMapper.readValue(req.getReader(),clazz);

		entity.prePersist();
		
		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("in:" + objectMapper.writeValueAsString(entity));
		}

		final Key<T> key = ofy().save().entity(entity).now();

		final T reloaded = ofy().load().key(key).now();

		if (LOG.isLoggable(Level.FINE)) {
			LOG.fine("out:" + objectMapper.writeValueAsString(reloaded));
		}

		objectMapper.writeValue(resp.getWriter(), reloaded);

	}

	@Override
	public void init() throws ServletException {
		super.init();
		objectMapper = new ObjectMapper();

		domainPackages.add(BaseEntity.class.getPackage());
		domainPackages.add(IndividualQuiz.class.getPackage());
	}

}