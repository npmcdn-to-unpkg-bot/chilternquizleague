package org.chilternquizleague.web;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chilternquizleague.domain.GlobalApplicationData;
import org.chilternquizleague.views.CompetitionTypeView;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;

@SuppressWarnings("serial")
public class EntityServices extends BaseRESTService {

	private final static Logger LOG = Logger.getLogger(EntityServices.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		final String[] parts = getParts(req);

		final String head = parts[0];

		// first deal with any non-standard bits
		if (head.equals("global")) {
			globalDetails(resp);
		}

		else if (head.equals("competitionType-list")) {

			objectMapper.writeValue(resp.getWriter(),
					CompetitionTypeView.getList());
		} else {

			handleEntities(resp, parts, head);
		}

	}

	private void globalDetails(HttpServletResponse resp) throws IOException {
		final GlobalApplicationData data = ofy()
				.load()
				.key(Key.create(GlobalApplicationData.class,
						AppStartListener.globalApplicationDataId)).now();

		objectMapper.writeValue(resp.getWriter(), data);
	}

	@Override
	protected void doPost(final HttpServletRequest req, final HttpServletResponse resp)
			throws ServletException, IOException {

		final String[] parts = getParts(req);

		final String entityName = getEntityName(parts[0]);

	
		saveUpdate(req, resp,entityName);
	
	}

	@Override
	public void init() throws ServletException {
		super.init();

		aliases.put("text", "globalText");
		aliases.put("global", "GlobalApplicationData");

	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doDelete(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		doPost(req, resp);
	}

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		

	}

}
