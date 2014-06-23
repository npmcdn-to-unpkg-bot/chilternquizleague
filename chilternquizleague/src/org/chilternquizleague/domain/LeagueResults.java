package org.chilternquizleague.domain;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Parent;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@Cache
@Entity
public class LeagueResults extends BaseEntity {
	
	private @Parent Ref<LeagueCompetition> competition;
	
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
