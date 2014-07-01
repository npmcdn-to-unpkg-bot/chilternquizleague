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
public class LeagueCompetition extends TeamCompetition{
	

	public LeagueCompetition()
	{
		super(CompetitionType.LEAGUE);
	}
	
	private List<LeagueTable> leagueTables = new ArrayList<>();

	public List<LeagueTable> getLeagueTables() {
		return leagueTables;
	}

	public void setLeagueTables(List<LeagueTable> leagueTables) {
		this.leagueTables = leagueTables;
	}
	


}
