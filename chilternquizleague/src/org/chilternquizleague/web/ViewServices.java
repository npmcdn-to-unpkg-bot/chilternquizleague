package org.chilternquizleague.web;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chilternquizleague.domain.GlobalApplicationData;
import org.chilternquizleague.domain.Season;
import org.chilternquizleague.domain.Team;
import org.chilternquizleague.views.GlobalApplicationDataView;
import org.chilternquizleague.views.LeagueTableView;
import org.chilternquizleague.views.TeamView;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.objectify.Key;

/**
 * Servlet implementation class ViewServices
 */
public class ViewServices extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private ObjectMapper objectMapper;
	


	/**
	 * @see Servlet#init(ServletConfig)
	 */
	public void init(ServletConfig config) throws ServletException {
		objectMapper = new ObjectMapper();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		if (request.getPathInfo().contains("globaldata")) {

			globalData(response);
		}
		
		else if (request.getPathInfo().contains("leaguetable")) {

			currentLeagueTable(request, response);
		}
		
		else if (request.getPathInfo().contains("teams")){
			allTeams(response);
		}
		
		else if (request.getPathInfo().contains("team")){
			team(request,response);
		}
	}
	
	private void team(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		final Long id = Long.parseLong(request.getParameter("id"));
		
		final TeamView team = new TeamView(ofy().load().key(Key.create(Team.class, id)).now());
		objectMapper.writeValue(response.getWriter(),team);
		
	}

	private void allTeams(HttpServletResponse response) throws IOException {
		final List<TeamView> views = new ArrayList<>();
		
		for(Team team :ofy().load().type(Team.class).list()){
			
			views.add(new TeamView(team));
			
		}
		
		objectMapper.writeValue(response.getWriter(), views);
		
		
		
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
	
	private void currentLeagueTable(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {

		final Long seasonId = Long.parseLong(req.getParameter("id"));

		final Season season = ofy().load().now(
				Key.create(Season.class, seasonId));

		if (season != null) {
			objectMapper.writeValue(resp.getWriter(), new LeagueTableView(
					season));

		}
	}

}
