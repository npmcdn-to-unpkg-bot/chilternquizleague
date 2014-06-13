/**
 * 
 */
package org.chilternquizleague;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chilternquizleague.domain.LeagueTable;
import org.chilternquizleague.domain.LeagueTableRow;

import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.gson.Gson;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;

/**
 * @author gb106507
 *
 */
@SuppressWarnings("serial")
public class Services extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if(req.getPathInfo().contains("leaguetable/current")){
			
			Objectify ofy = ObjectifyService.ofy();

			final List<LeagueTable> tables = ofy.load().type(LeagueTable.class)/*.filter("endYear >=", Calendar.getInstance().get(Calendar.YEAR))*/.list();
			
			if(!tables.isEmpty()){			
				LeagueTable table = tables.iterator().next();
				
				Gson gson = new Gson();
				String json = gson.toJson(table);
			
				resp.getWriter().write(json);
			}
		}
		
		if(req.getPathInfo().contains("maketable")){
			Objectify ofy = ObjectifyService.ofy();

			LeagueTable table = new LeagueTable();
			table.setStartYear(2000);
			table.setEndYear(2001);
			
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
			
			ofy.save().entity(table);
			
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doGet(req, resp);
	}

	@Override
	public void init() throws ServletException {
		// TODO Auto-generated method stub
		super.init();
	}

}
