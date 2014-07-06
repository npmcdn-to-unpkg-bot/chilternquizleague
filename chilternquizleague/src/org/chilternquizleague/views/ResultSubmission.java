package org.chilternquizleague.views;

import org.chilternquizleague.domain.CompetitionType;
import org.chilternquizleague.domain.LeagueResultRow;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class ResultSubmission {
	
	protected LeagueResultRow result;
	protected Long seasonId;
	protected CompetitionType competitionType;
	
	public LeagueResultRow getResult() {
		return result;
	}
	public Long getSeasonId() {
		return seasonId;
	}
	public CompetitionType getCompetitionType() {
		return competitionType;
	}

}
