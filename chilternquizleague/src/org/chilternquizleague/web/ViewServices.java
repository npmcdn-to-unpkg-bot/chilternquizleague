package org.chilternquizleague.web;

import static com.googlecode.objectify.ObjectifyService.ofy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.chilternquizleague.domain.CompetitionType;
import org.chilternquizleague.domain.Fixture;
import org.chilternquizleague.domain.Fixtures;
import org.chilternquizleague.domain.GlobalApplicationData;
import org.chilternquizleague.domain.LeagueResultRow;
import org.chilternquizleague.domain.LeagueResults;
import org.chilternquizleague.domain.Season;
import org.chilternquizleague.domain.Team;
import org.chilternquizleague.domain.TeamCompetition;
import org.chilternquizleague.domain.Text;
import org.chilternquizleague.domain.User;
import org.chilternquizleague.domain.Venue;
import org.chilternquizleague.results.ResultHandler;
import org.chilternquizleague.views.FixtureView;
import org.chilternquizleague.views.GlobalApplicationDataView;
import org.chilternquizleague.views.LeagueTableView;
import org.chilternquizleague.views.ResultSubmission;
import org.chilternquizleague.views.ResultView;
import org.chilternquizleague.views.TeamExtras;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
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

		final SimpleModule module = new SimpleModule();

		module.addSerializer(User.class, new UserSerializer());
		module.addSerializer(Text.class, new TextSerializer());

		objectMapper.registerModule(module);
	}

	private static class UserSerializer extends JsonSerializer<User> {

		@Override
		public void serialize(User team, JsonGenerator gen,
				SerializerProvider prov) throws IOException,
				JsonProcessingException {

		}

	}

	private static class TextSerializer extends JsonSerializer<Text> {

		@Override
		public void serialize(Text text, JsonGenerator gen,
				SerializerProvider prov) throws IOException,
				JsonProcessingException {
			gen.writeStartObject();
			gen.writeNullField("text");
			gen.writeEndObject();
		}

	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getPathInfo().contains("globaldata")) {

			globalData(response);
		}

		else if (request.getPathInfo().contains("leaguetable")) {

			currentLeagueTable(request, response);
		}

		else if (request.getPathInfo().contains("teams")) {
			makeEntityList(response, Team.class);
		}

		else if (request.getPathInfo().contains("team-extras")) {
			teamExtras(request, response);
		}

		else if (request.getPathInfo().contains("team")) {
			entityByKey(request, response, Team.class);
		}

		else if (request.getPathInfo().contains("venue")) {
			entityByKey(request, response, Venue.class);
		}

		else if (request.getPathInfo().endsWith("fixtures-for-email")) {
			fixturesForEmail(request, response);
		}

	}

	private void fixturesForEmail(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		final String email = ("" + request.getParameter("email")).trim();
		final Season season = ofy()
				.load()
				.key(Key.create(Season.class,
						Long.parseLong(request.getParameter("seasonId"))))
				.now();

		final List<Team> teams = ofy().load().type(Team.class).list();

		for (Team team : teams) {

			for (User user : team.getUsers()) {
				if (email.equalsIgnoreCase(user.getEmail())) {
					objectMapper.writeValue(response.getWriter(),
							getTeamFixtures(team.getId(), season));
				}
			}
		}
	}

	@Override
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		if (request.getPathInfo().endsWith("submit-results")) {
			submitResults(request);
		}
	}

	private <T> void entityByKey(HttpServletRequest req,
			HttpServletResponse resp, Class<T> clazz) throws IOException {

		final long id = Long.parseLong(getLastPathPart(req));
		T entity = ofy().load().now(Key.create(clazz, id));
		objectMapper.writeValue(System.out, entity);
		objectMapper.writeValue(resp.getWriter(), entity);

	}

	private <T> void makeEntityList(HttpServletResponse resp, Class<T> clazz)
			throws IOException {
		final List<T> venues = ofy().load().type(clazz).list();

		objectMapper.writeValue(resp.getWriter(), venues);

	}

	private String getLastPathPart(HttpServletRequest req) {
		final String[] split = req.getPathInfo().split("\\/");

		return split[split.length - 1];
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

	private void teamExtras(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		final Long seasonId = Long.parseLong(req.getParameter("seasonId"));
		final Long teamId = Long.parseLong(req.getParameter("teamId"));

		final Season season = ofy().load()
				.key(Key.create(Season.class, seasonId)).now();
		final Team team = ofy().load().key(Key.create(Team.class, teamId))
				.now();

		final List<FixtureView> fixtures = getTeamFixtures(teamId, season);

		TeamExtras extras = new TeamExtras(team, fixtures, getTeamResults(teamId, season));

		objectMapper.writeValue(System.out, extras);

		objectMapper.writeValue(resp.getWriter(), extras);

	}

	private List<FixtureView> getTeamFixtures(final Long teamId,
			final Season season) {
		final List<TeamCompetition> competitions = Arrays
				.<TeamCompetition> asList((TeamCompetition) season
						.getCompetition(CompetitionType.LEAGUE),
						(TeamCompetition) season
								.getCompetition(CompetitionType.CUP),
						(TeamCompetition) season
								.getCompetition(CompetitionType.PLATE));
		final List<FixtureView> fixtures = new ArrayList<>();

		for (TeamCompetition competition : competitions) {

			if (competition != null) {

				for (Fixtures fixtureSet : competition.getFixtures()) {
					for (Fixture fixture : fixtureSet.getFixtures()) {

						if (fixture.getHome().getId().equals(teamId)
								|| fixture.getAway().getId().equals(teamId)) {
							fixtures.add(new FixtureView(fixture, competition));
						}

					}
				}
			}

		}
		return fixtures;
	}

	private List<ResultView> getTeamResults(final Long teamId,
			final Season season) {
		final List<TeamCompetition> competitions = Arrays
				.<TeamCompetition> asList((TeamCompetition) season
						.getCompetition(CompetitionType.LEAGUE),
						(TeamCompetition) season
								.getCompetition(CompetitionType.CUP),
						(TeamCompetition) season
								.getCompetition(CompetitionType.PLATE));
		final List<ResultView> results = new ArrayList<>();

		for (TeamCompetition competition : competitions) {

			if (competition != null) {

				for (LeagueResults resultSet : competition.getResults()) {
					for (LeagueResultRow result : resultSet.getResults()) {

						final Fixture fixture = result.getFixture();

						if (fixture.getHome().getId().equals(teamId)
								|| fixture.getAway().getId().equals(teamId)) {
							results.add(new ResultView(result, competition));
						}

					}
				}
			}

		}
		return results;
	}

	private void submitResults(HttpServletRequest request) throws IOException {

		final ResultSubmission[] submissions = objectMapper.readValue(
				request.getReader(), ResultSubmission[].class);

		for (final ResultSubmission submission : submissions) {
			new ResultHandler(submission.getResult(), submission.getSeasonId(),
					submission.getCompetitionType()).commit();

		}

	}

}
