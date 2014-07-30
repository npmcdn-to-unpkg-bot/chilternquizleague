package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Ignore;

public abstract class TeamCompetition extends Competition {

	private List<Ref<Fixtures>> fixtures = new ArrayList<>();
	private List<Ref<Results>> results = new ArrayList<>();
	
	@Ignore
	private List<Fixtures> fixturesEnts = new ArrayList<>();
	@Ignore
	private List<Results> resultsEnts = new ArrayList<>();
	
	protected TeamCompetition(final CompetitionType type) {
		this(type, false);
	}
	
	protected TeamCompetition(final CompetitionType type, boolean subsidiary) {
		
		super(type, subsidiary);
		
		setStartTime("20:30");
		setEndTime("22:00");
	}

	public List<Fixtures> getFixtures() {
		return fixturesEnts = fixturesEnts.isEmpty() ? Utils.refsToEntities(fixtures) : fixturesEnts;
	}

	public void setFixtures(List<Fixtures> fixtures) {
		fixturesEnts = fixtures;
	}

	public List<Results> getResults() {
		return resultsEnts = resultsEnts.isEmpty()?Utils.refsToEntities(results): resultsEnts;
		
	}

	public void setResults(List<Results> results) {
		this.resultsEnts = results;

	}

	protected final Results getResultsForDate(Date date) {

		for (Results resultSet : getResults()) {

			if (Utils.isSameDay(date, resultSet.getDate())) {

				return resultSet;
			}
		}

		final Results newResults = new Results();
		final Fixtures fixtures = getFixturesForDate(date);

		newResults.setDescription(fixtures != null ? fixtures.getDescription()
				: getDescription());

		newResults.setDate(date);
		
		results.add(Utils.entityToRef(newResults, this));

		return newResults;
	}

	private Fixtures getFixturesForDate(Date date) {
		for (Fixtures fixtureSet : getFixtures()) {

			if (Utils.isSameDay(date, fixtureSet.getStart())) {

				return fixtureSet;
			}
		}

		return null;
	}

	public abstract Results addResult(Result result);

	public void addResults(Results results){
		
		this.results.add(Ref.create(results));
	}

	@Override
	public void prePersist() {
		
		fixtures = Utils.entitiesToRefs(fixturesEnts, this);
		results = Utils.entitiesToRefs(resultsEnts, this);
		
		Utils.persist(this);
	}
}
