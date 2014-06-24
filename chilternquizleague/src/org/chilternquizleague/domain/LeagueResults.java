package org.chilternquizleague.domain;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class LeagueResults{
	
	/**
	 * Key is string representation of the UTC date as yyyyMMdd
	 */
	private Map<String, LeagueResultRow> results = new HashMap<>();

	public Map<String, LeagueResultRow> getResults() {
		return results;
	}

	public void setResults(Map<String, LeagueResultRow> results) {
		this.results = results;
	}

}
