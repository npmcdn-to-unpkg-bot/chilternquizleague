package org.chilternquizleague.domain;

import static org.chilternquizleague.domain.Utils.persist;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.googlecode.objectify.Ref;

public abstract class TeamCompetition extends Competition {

	private List<Ref<Fixtures>> fixtures = new ArrayList<>();
	private List<Ref<Results>> results = new ArrayList<>();
	
	protected TeamCompetition(final CompetitionType type) {
		this(type, false);
	}
	
	protected TeamCompetition(final CompetitionType type, boolean subsidiary) {
		
		super(type, subsidiary);
		
		setStartTime("20:30");
		setEndTime("22:00");
	}

	public List<Fixtures> getFixtures() {
		return Utils.refsToEntities(fixtures);
	}

	public void setFixtures(List<Fixtures> fixtures) {
		this.fixtures = Utils.entitiesToRefs(fixtures,this);
		persist(this);
	}

	public List<Results> getResults() {
		return Utils.refsToEntities(results);
		
	}

	public void setResults(List<Results> results) {
		this.results = Utils.entitiesToRefs(results, this);
		persist(this);
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
}
