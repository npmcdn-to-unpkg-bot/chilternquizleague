/**
 * 
 */
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
import org.chilternquizleague.domain.Venue;

import com.google.gson.Gson;
import com.googlecode.objectify.Key;

/**
 * @author gb106507
 * 
 */
@SuppressWarnings("serial")
public class Services extends HttpServlet {

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

			venueList(resp);
		} else if (req.getPathInfo().contains("venue")) {
			venueByKey(req, resp);
		}

	}

	private void venueByKey(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		try {
			final long id = Long.parseLong(getLastPathPart(req));
			Venue venue = ofy().load().now(Key.create(Venue.class, id));

			Gson gson = new Gson();

			resp.getWriter().write(gson.toJson(venue));
		} catch (NumberFormatException e) {

		}
	}

	private String getLastPathPart(HttpServletRequest req) {
		final String[] split = req.getPathInfo().split("\\/");

		return split[split.length - 1];
	}

	private void venueList(HttpServletResponse resp) throws IOException {

		final List<Venue> venues = ofy().load().type(Venue.class).list();

		Gson gson = new Gson();

		String json = gson.toJson(venues);
		resp.getWriter().write(json);

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

			Gson gson = new Gson();
			String json = gson.toJson(table);

			resp.getWriter().write(json);
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getPathInfo().endsWith("venue")) {
			
			Gson gson = new Gson();
			Venue venue = gson.fromJson(req.getReader(), Venue.class);

			ofy().save().entity(venue);
			

		}
	}

	@Override
	public void init() throws ServletException {
		super.init();
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
