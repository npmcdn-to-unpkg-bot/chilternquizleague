package org.chilternquizleague;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chilternquizleague.domain.LeagueTable;
import org.chilternquizleague.domain.LeagueTableRow;
import org.chilternquizleague.domain.Team;
import org.chilternquizleague.domain.Venue;

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

		if (req.getPathInfo().endsWith("maketable")) {

			makeNewLeagueTableDummy();

		} else if (req.getPathInfo().endsWith("addrow")) {

			addLeagueTableRowDummy();

		}

		else if (req.getPathInfo().endsWith("venue-list")) {

			makeEntityList(resp, Venue.class);
		} else if (req.getPathInfo().contains("venue")) {
			entityByKey(req, resp, Venue.class);
		}
		
		else if (req.getPathInfo().endsWith("team-list")) {

			makeEntityList(resp, Team.class);
		} else if (req.getPathInfo().contains("team")) {
			entityByKey(req, resp, Team.class);
		}


	}

	private <T>void entityByKey(HttpServletRequest req, HttpServletResponse resp, Class<T> clazz)
			throws IOException {

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



	private <T> void makeEntityList(HttpServletResponse resp, Class<T> clazz) throws IOException {
		final List<T> venues = ofy().load().type(clazz).list();

		objectMapper.writeValue(resp.getWriter(), venues);

	}

	private void addLeagueTableRowDummy() {
		final List<LeagueTable> tables = ofy()
				.load()
				.type(LeagueTable.class)
				.filter("endYear >=", Calendar.getInstance().get(Calendar.YEAR))
				.list();

		if (!tables.isEmpty()) {
			LeagueTable table = tables.iterator().next();

			LeagueTableRow row1 = new LeagueTableRow();
			row1.setPosition("2");
			row1.setTeam("Squirrel");
			row1.setPlayed(20);
			row1.setWon(18);
			row1.setLost(1);
			row1.setDrawn(1);
			row1.setMatchPointsFor(1600);
			row1.setMatchPointsAgainst(1500);
			row1.setLeaguePoints(37);

			table.getRows().add(row1);

			ofy().save().entity(table);
		}
	}

	private void makeNewLeagueTableDummy() {
		LeagueTable table = new LeagueTable();
		table.setStartYear(2013);
		table.setEndYear(2014);

		LeagueTableRow row1 = new LeagueTableRow();
		row1.setPosition("1");
		row1.setTeam("Squirrel");
		row1.setPlayed(20);
		row1.setWon(18);
		row1.setLost(1);
		row1.setDrawn(1);
		row1.setMatchPointsFor(1600);
		row1.setMatchPointsAgainst(1500);
		row1.setLeaguePoints(37);

		table.getRows().add(row1);

		ofy().save().entity(table);
	}

	private void currentLeagueTable(HttpServletResponse resp)
			throws IOException {
		final List<LeagueTable> tables = ofy()
				.load()
				.type(LeagueTable.class)
				.filter("endYear >=", Calendar.getInstance().get(Calendar.YEAR))
				.list();

		if (!tables.isEmpty()) {
			LeagueTable table = tables.iterator().next();

			
			objectMapper.writeValue(resp.getWriter(),table);

		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getPathInfo().endsWith("venue")) {
			
			saveUpdate(req, Venue.class);

		}
		else if (req.getPathInfo().endsWith("team")) {
			
			saveUpdate(req, Team.class);

		}
	}

	private <T>  void saveUpdate(HttpServletRequest req, Class<T> clazz) throws IOException {
		
		
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
