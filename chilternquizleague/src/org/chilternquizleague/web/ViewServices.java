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

import org.chilternquizleague.domain.BaseEntity;
import org.chilternquizleague.domain.Competition;
import org.chilternquizleague.domain.CompetitionType;
import org.chilternquizleague.domain.Fixture;
import org.chilternquizleague.domain.Fixtures;
import org.chilternquizleague.domain.GlobalApplicationData;
import org.chilternquizleague.domain.GlobalText;
import org.chilternquizleague.domain.Result;
import org.chilternquizleague.domain.Results;
import org.chilternquizleague.domain.Season;
import org.chilternquizleague.domain.Team;
import org.chilternquizleague.domain.TeamCompetition;
import org.chilternquizleague.domain.Text;
import org.chilternquizleague.domain.User;
import org.chilternquizleague.domain.Venue;
import org.chilternquizleague.results.ResultHandler;
import org.chilternquizleague.views.CompetitionView;
import org.chilternquizleague.views.GlobalApplicationDataView;
import org.chilternquizleague.views.LeagueTableView;
import org.chilternquizleague.views.PreSubmissionView;
import org.chilternquizleague.views.ResultSubmission;
import org.chilternquizleague.views.SeasonView;
import org.chilternquizleague.views.TeamExtras;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
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

		else if (request.getPathInfo().contains("venues")) {
			makeEntityList(response, Venue.class);
		}

		else if (request.getPathInfo().contains("seasons")) {
			makeEntityList(response, Season.class);
		}

		else if (request.getPathInfo().contains("season-views")) {
			makeSeasonViews(response);
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

		else if (request.getPathInfo().contains("all-results")) {
			allResults(request, response);
		}

		else if (request.getPathInfo().endsWith("fixtures-for-email")) {
			fixturesForEmail(request, response);
		}

		else if (request.getPathInfo().contains("competitions-view")) {
			competitionsForSeason(request, response);
		} else if (request.getPathInfo().contains("text")) {
			textForName(request, response);
		}

	}

	private void textForName(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		final Long id = Long.parseLong(request.getParameter("id"));
		final String name = request.getParameter("name");

		final GlobalText text = ofy().load()
				.key(Key.create(GlobalText.class, id)).now();

		objectMapper.writeValue(response.getWriter(), text.getText(name));

	}

	private void competitionsForSeason(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		final Long seasonId = Long.parseLong(request.getParameter("id"));

		final Season season = ofy().load()
				.key(Key.create(Season.class, seasonId)).now();

		List<CompetitionView> views = new ArrayList<>();

		for (Competition competition : season.getCompetitions().values()) {

			views.add(new CompetitionView(competition));
		}

		objectMapper.writeValue(response.getWriter(), views);

	}

	private void allResults(HttpServletRequest request,
			HttpServletResponse response) throws IOException {
		final Long seasonId = Long.parseLong(request.getParameter("id"));

		final Season season = ofy().load()
				.key(Key.create(Season.class, seasonId)).now();

		final List<Results> results = new ArrayList<>();

		for (TeamCompetition competition : season.getTeamCompetitions()) {

			results.addAll(competition.getResults());
		}

		objectMapper.writeValue(response.getWriter(), results);
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
					objectMapper.writeValue(
							response.getWriter(),
							new PreSubmissionView(team, getTeamFixtures(
									team.getId(), season)));
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

	private <T extends BaseEntity> void makeEntityList(
			HttpServletResponse resp, Class<T> clazz) throws IOException {
		final List<T> items = new ArrayList<>();

		for (T item : ofy().load().type(clazz).list()) {

			if (!item.isRetired()) {
				items.add(item);
			}
		}

		objectMapper.writeValue(resp.getWriter(), items);

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

		final Season season = ofy().load().now(
				Key.create(Season.class, seasonId));

		final Team team = ofy().load().now(Key.create(Team.class, teamId));

		TeamExtras extras = new TeamExtras(team,
				getTeamFixtures(teamId, season), getTeamResults(teamId, season));

		objectMapper.writeValue(System.out, extras);

		objectMapper.writeValue(resp.getWriter(), extras);

	}

	private List<Fixtures> getTeamFixtures(final Long teamId,
			final Season season) {
		final List<TeamCompetition> competitions = season.getTeamCompetitions();
		final List<Fixtures> fixtures = new ArrayList<>();

		for (TeamCompetition competition : competitions) {

			if (competition != null
					&& competition.getType() != CompetitionType.BEER_LEG) {

				for (Fixtures fixtureSet : competition.getFixtures()) {

					final Fixtures newFixSet = new Fixtures(fixtureSet);

					for (Fixture fixture : fixtureSet.getFixtures()) {

						if (fixture.getHome().getId().equals(teamId)
								|| fixture.getAway().getId().equals(teamId)) {
							newFixSet.getFixtures().add(fixture);
						}

					}

					if (!newFixSet.getFixtures().isEmpty()) {
						fixtures.add(newFixSet);
					}
				}
			}

		}
		return fixtures;
	}

	private void makeSeasonViews(HttpServletResponse resp) throws IOException {

		final List<SeasonView> seasons = new ArrayList<>();

		for (Season season : ofy().load().type(Season.class)) {

			seasons.add(new SeasonView(season));
		}

		objectMapper.writeValue(resp.getWriter(), seasons);
	}

	private List<Results> getTeamResults(final Long teamId, final Season season) {
		final List<TeamCompetition> competitions = Arrays
				.<TeamCompetition> asList((TeamCompetition) season
						.getCompetition(CompetitionType.LEAGUE),
						(TeamCompetition) season
								.getCompetition(CompetitionType.CUP),
						(TeamCompetition) season
								.getCompetition(CompetitionType.PLATE));
		final List<Results> results = new ArrayList<>();

		for (TeamCompetition competition : competitions) {

			if (competition != null) {

				for (Results resultSet : competition.getResults()) {

					final Results newResultSet = new Results(resultSet);

					for (Result result : resultSet.getResults()) {

						final Fixture fixture = result.getFixture();

						if (fixture.getHome().getId().equals(teamId)
								|| fixture.getAway().getId().equals(teamId)) {
							newResultSet.getResults().add(result);
						}

					}

					if (!newResultSet.getResults().isEmpty()) {
						results.add(newResultSet);
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
			new ResultHandler(submission.getResult(),submission.getEmail(), submission.getSeasonId(),
					submission.getCompetitionType()).commit();

		}

	}

}
