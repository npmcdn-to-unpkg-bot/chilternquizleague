package org.chilternquizleague.domain;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class LeagueResults {
	

	@Id
	private Long id;
	
	/**
	 * Key is string representation of the UTC date in millis
	 */
	private Map<String, LeagueResultRow> results = new HashMap<>();

	public Map<String, LeagueResultRow> getResults() {
		return results;
	}

	public void setResults(Map<String, LeagueResultRow> results) {
		this.results = results;
	}

}
