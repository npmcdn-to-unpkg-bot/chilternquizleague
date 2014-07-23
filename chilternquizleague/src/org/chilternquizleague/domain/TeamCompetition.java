package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class TeamCompetition extends Competition {

	private List<Fixtures> fixtures = new ArrayList<>();
	private List<LeagueResults> results = new ArrayList<>();

	protected TeamCompetition(final CompetitionType type) {
		this(type, false);
	}
	
	protected TeamCompetition(final CompetitionType type, boolean subsidiary) {
		
		super(type, subsidiary);
		
		setStartTime("20:30");
		setEndTime("22:00");
	}

	public List<Fixtures> getFixtures() {
		return fixtures;
	}

	public void setFixtures(List<Fixtures> fixtures) {
		this.fixtures = fixtures;
	}

	public List<LeagueResults> getResults() {
		return results;
	}

	public void setResults(List<LeagueResults> results) {
		this.results = results;
	}

	protected final LeagueResults getResultsForDate(Date date) {

		for (LeagueResults resultSet : results) {

			if (Utils.isSameDay(date, resultSet.getDate())) {

				return resultSet;
			}
		}

		final LeagueResults newResults = new LeagueResults();
		final Fixtures fixtures = getFixturesForDate(date);

		newResults.setDescription(fixtures != null ? fixtures.getDescription()
				: getDescription());

		newResults.setDate(date);
		results.add(newResults);

		return newResults;
	}

	private Fixtures getFixturesForDate(Date date) {
		for (Fixtures fixtureSet : fixtures) {

			if (Utils.isSameDay(date, fixtureSet.getStart())) {

				return fixtureSet;
			}
		}

		return null;
	}

	public abstract void addResult(LeagueResultRow result);

}
