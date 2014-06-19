package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Subclass;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@Cache
@Subclass
public class LeagueCompetition extends Competition{
	

	/**
	 * Key is date in yyyyMMdd format
	 */
	private Map<String,List<Ref<Fixture>>> fixtures = new HashMap<>();
	
	private List<Ref<LeagueResults>> results = new ArrayList<>();

	private List<LeagueTable> leagueTables = new ArrayList<>();

	public Map<String, List<Fixture>> getFixtures() {
		return Utils.refsToEntities(fixtures);
	}

	public void setFixtures(Map<String, List<Fixture>> fixtures) {
		this.fixtures = Utils.entitiesToRefs(fixtures);
	}

	public List<LeagueResults> getResults() {
		return Utils.refsToEntities(results);
	}

	public void setResults(List<LeagueResults> results) {
		this.results = Utils.entitiesToRefs(results);
	}

	public List<LeagueTable> getLeagueTables() {
		return leagueTables;
	}

	public void setLeagueTables(List<LeagueTable> leagueTables) {
		this.leagueTables = leagueTables;
	}
	


}
