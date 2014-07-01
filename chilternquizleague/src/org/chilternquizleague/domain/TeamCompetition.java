package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

public abstract class TeamCompetition extends Competition {

	/**
	 * Key is date in yyyyMMdd format
	 */
	private List<Fixtures> fixtures = new ArrayList<>();
	private List<LeagueResults> results = new ArrayList<>();

	protected TeamCompetition(final CompetitionType type) {
		super(type);

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

}
