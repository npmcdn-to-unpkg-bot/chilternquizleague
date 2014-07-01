package org.chilternquizleague.web;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chilternquizleague.domain.Competition;
import org.chilternquizleague.domain.CompetitionType;
import org.chilternquizleague.domain.Fixture;
import org.chilternquizleague.domain.Fixtures;
import org.chilternquizleague.domain.GlobalApplicationData;
import org.chilternquizleague.domain.LeagueCompetition;
import org.chilternquizleague.domain.Season;
import org.chilternquizleague.domain.Team;
import org.chilternquizleague.domain.TeamCompetition;
import org.chilternquizleague.views.FixtureView;
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
		
		else if(request.getPathInfo().contains("team_fixtures")){
			allFixturesForTeam(request, response);
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
	
	private void allFixturesForTeam(HttpServletRequest req,
			HttpServletResponse resp) throws IOException{
		final Long seasonId = Long.parseLong(req.getParameter("seasonId"));
		final Long teamId = Long.parseLong(req.getParameter("teamId"));
		
		final Season season = ofy().load().key(Key.create(Season.class, seasonId)).now();
		final Team team = ofy().load().key(Key.create(Team.class, teamId)).now();
		
		final List<TeamCompetition> competitions = Arrays.<TeamCompetition>asList((TeamCompetition)season.getCompetition(CompetitionType.LEAGUE), (TeamCompetition)season.getCompetition(CompetitionType.CUP), (TeamCompetition)season.getCompetition(CompetitionType.PLATE));
		final List<FixtureView> fixtures = new ArrayList<>();
		
		for(TeamCompetition competition : competitions){
			
			for(Fixtures fixtureSet: competition.getFixtures()){
				for(Fixture fixture : fixtureSet.getFixtures()){
					
					fixtures.add(new FixtureView(fixture, competition));
				}
			}
			
			
		}
		
		objectMapper.writeValue(resp.getWriter(), fixtures);
		
	}

}
