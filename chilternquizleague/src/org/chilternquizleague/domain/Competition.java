package org.chilternquizleague.domain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class Competition {
	

	@Id
	private Long id;
	
	private CompetitionType type;
	
	/**
	 * Key is date in yyyyMMdd format
	 */
	private Map<String,List<Fixture>> fixtures = new HashMap<>();
	
	/**
	 * Key is date in yyyyMMdd format
	 */
	private Map<String,LeagueResults> results = new HashMap<>();

	public CompetitionType getType() {
		return type;
	}

	public void setType(CompetitionType type) {
		this.type = type;
	}

	public Map<String, List<Fixture>> getFixtures() {
		return fixtures;
	}

	public void setFixtures(Map<String, List<Fixture>> fixtures) {
		this.fixtures = fixtures;
	}

	public Map<String, LeagueResults> getResults() {
		return results;
	}

	public void setResults(Map<String, LeagueResults> results) {
		this.results = results;
	}

}
