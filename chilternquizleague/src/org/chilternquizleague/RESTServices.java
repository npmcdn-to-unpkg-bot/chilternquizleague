package org.chilternquizleague;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chilternquizleague.domain.Fixture;
import org.chilternquizleague.domain.Fixtures;
import org.chilternquizleague.domain.GlobalApplicationData;
import org.chilternquizleague.domain.LeagueCompetition;
import org.chilternquizleague.domain.Season;
import org.chilternquizleague.domain.Team;
import org.chilternquizleague.domain.User;
import org.chilternquizleague.domain.Venue;
import org.chilternquizleague.views.CompetitionTypeView;
import org.chilternquizleague.views.GlobalApplicationDataView;
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
		if (req.getPathInfo().endsWith("globaldata")) {

			globalData(resp);
		}

		if (req.getPathInfo().contains("leaguetable")) {

			currentLeagueTable(req, resp);
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
		} else if (req.getPathInfo().contains("leagueCompetition")) {
			entityByKey(req, resp, LeagueCompetition.class);
		} else if (req.getPathInfo().contains("global")) {
			globalDetails(resp);
		}

		else if (req.getPathInfo().endsWith("competitionType-list")) {

			objectMapper.writeValue(resp.getWriter(),
					CompetitionTypeView.getList());
		}

	}
	
	private void globalDetails(HttpServletResponse resp) throws IOException {
		final GlobalApplicationData data = ofy().load().key(Key.create(GlobalApplicationData.class, AppStartListener.globalApplicationDataId)).now();
		
		objectMapper.writeValue(resp.getWriter(), data);
	}

	private void globalData(HttpServletResponse resp) throws IOException {
		final GlobalApplicationData data = ofy().load().now(
				Key.create(GlobalApplicationData.class,
						AppStartListener.globalApplicationDataId));

		if (data != null) {
			objectMapper.writeValue(resp.getWriter(),
					new GlobalApplicationDataView(data));
		}
	}

	private <T> void entityByKey(HttpServletRequest req,
			HttpServletResponse resp, Class<T> clazz) throws IOException {

		try {
			final long id = Long.parseLong(getLastPathPart(req));
			T entity = ofy().load().now(Key.create(clazz, id));
			objectMapper.writeValue(System.out, entity);
			objectMapper.writeValue(resp.getWriter(), entity);
		} catch (NumberFormatException e) {
			try {

				T entity = clazz.newInstance();
				objectMapper.writerWithDefaultPrettyPrinter().writeValue(resp.getWriter(), entity);
			} catch (Exception ex) {
				ex.printStackTrace();
			}
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

	private void currentLeagueTable(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {

		final Long seasonId = Long.parseLong(getLastPathPart(req));

		final Season season = ofy().load().now(
				Key.create(Season.class, seasonId));

		if (season != null) {
			objectMapper.writeValue(resp.getWriter(), new LeagueTableView(
					season));

		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getPathInfo().endsWith("venue")) {

			saveUpdate(req, resp, Venue.class);

		} else if (req.getPathInfo().endsWith("team")) {

			saveUpdate(req, resp, Team.class);

		} else if (req.getPathInfo().endsWith("season")) {

			saveUpdate(req, resp, Season.class);

		}

		else if (req.getPathInfo().endsWith("user")) {

			saveUpdate(req, resp, User.class);

		}

		else if (req.getPathInfo().endsWith("leagueCompetition")) {

			saveUpdate(req, resp, LeagueCompetition.class);

		}

		else if (req.getPathInfo().endsWith("fixture")) {

			saveUpdate(req, resp, Fixture.class);

		}

		else if (req.getPathInfo().endsWith("fixtures")) {

			saveUpdate(req, resp, Fixtures.class);

		}
		
		else if(req.getPathInfo().endsWith("global")){
			saveUpdate(req, resp, GlobalApplicationData.class);
		}

	}

	private <T> void saveUpdate(HttpServletRequest req,
			HttpServletResponse resp, Class<T> clazz) throws IOException {

		T entity = objectMapper.readValue(req.getReader(), clazz);

		Key<T> key = ofy().save().entity(entity).now();

		T reloaded = ofy().load().key(key).now();
		System.out.println("out:" + objectMapper.writeValueAsString(reloaded));

		objectMapper.writeValue(resp.getWriter(), reloaded);

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
