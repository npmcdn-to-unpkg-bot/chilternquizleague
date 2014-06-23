package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Subclass;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@Cache
@Subclass
public class LeagueCompetition extends Competition{
	

	public LeagueCompetition()
	{
		super(CompetitionType.LEAGUE);
	}
	
	/**
	 * Key is date in yyyyMMdd format
	 */
	private List<Fixtures> fixtures = new ArrayList<>();
	
	private List<LeagueResults> results = new ArrayList<>();

	private List<LeagueTable> leagueTables = new ArrayList<>();

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

	public List<LeagueTable> getLeagueTables() {
		return leagueTables;
	}

	public void setLeagueTables(List<LeagueTable> leagueTables) {
		this.leagueTables = leagueTables;
	}
	


}
