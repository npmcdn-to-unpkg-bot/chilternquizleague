package org.chilternquizleague;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chilternquizleague.domain.CompetitionType;
import org.chilternquizleague.domain.Season;
import org.chilternquizleague.domain.Team;
import org.chilternquizleague.domain.User;
import org.chilternquizleague.domain.Venue;
import org.chilternquizleague.views.LeagueTableView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.Key;

/**
 * @author gb106507
 * 
 */
@SuppressWarnings("serial")
public class RESTServices extends HttpServlet {

	private ObjectMapper objectMapper;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getPathInfo().endsWith("leaguetable/current")) {

			currentLeagueTable(resp);
		}

		else if (req.getPathInfo().endsWith("venue-list")) {

			makeEntityList(resp, Venue.class);
		}

		else if (req.getPathInfo().contains("venue")) {
			entityByKey(req, resp, Venue.class);
		}

		else if (req.getPathInfo().endsWith("team-list")) {

			makeEntityList(resp, Team.class);
		} else if (req.getPathInfo().contains("team")) {
			entityByKey(req, resp, Team.class);
		}

		else if (req.getPathInfo().endsWith("season-list")) {

			makeEntityList(resp, Season.class);
		} else if (req.getPathInfo().contains("season")) {
			entityByKey(req, resp, Season.class);
		}

		else if (req.getPathInfo().endsWith("user-list")) {

			makeEntityList(resp, User.class);
		} else if (req.getPathInfo().contains("user")) {
			entityByKey(req, resp, User.class);
		}
		
		else if (req.getPathInfo().endsWith("competition-type-list")) {

			objectMapper.writeValue(resp.getWriter(), CompetitionType.values());
		}
		


	}

	private <T> void entityByKey(HttpServletRequest req,
			HttpServletResponse resp, Class<T> clazz) throws IOException {

		try {
			final long id = Long.parseLong(getLastPathPart(req));
			T entity = ofy().load().now(Key.create(clazz, id));

			objectMapper.writeValue(resp.getWriter(), entity);
		} catch (NumberFormatException e) {

		}
	}

	private String getLastPathPart(HttpServletRequest req) {
		final String[] split = req.getPathInfo().split("\\/");

		return split[split.length - 1];
	}

	private <T> void makeEntityList(HttpServletResponse resp, Class<T> clazz)
			throws IOException {
		final List<T> venues = ofy().load().type(clazz).list();

		objectMapper.writeValue(resp.getWriter(), venues);

	}

	private void currentLeagueTable(HttpServletResponse resp)
			throws IOException {
		final List<Season> seasons = ofy()
				.load()
				.type(Season.class)
				.filter("endYear >=", Calendar.getInstance().get(Calendar.YEAR))
				.list();

		if (!seasons.isEmpty()) {
			objectMapper.writeValue(resp.getWriter(), new LeagueTableView(
					seasons.iterator().next()));

		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getPathInfo().endsWith("venue")) {

			saveUpdate(req, Venue.class);

		} else if (req.getPathInfo().endsWith("team")) {

			saveUpdate(req, Team.class);

		} else if (req.getPathInfo().endsWith("season")) {

			saveUpdate(req, Season.class);

		}

		else if (req.getPathInfo().endsWith("user")) {

			saveUpdate(req, User.class);

		}
		

	}

	private <T> void saveUpdate(HttpServletRequest req, Class<T> clazz)
			throws IOException {

		T entity = objectMapper.readValue(req.getReader(), clazz);

		ofy().save().entity(entity).now();
	}

	@Override
	public void init() throws ServletException {
		super.init();

		objectMapper = new ObjectMapper();
	}

	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		super.doDelete(req, resp);
	}

	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		super.doPut(req, resp);
	}

}
