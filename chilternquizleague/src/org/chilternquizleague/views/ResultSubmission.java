package org.chilternquizleague.views;

import org.chilternquizleague.domain.CompetitionType;
import org.chilternquizleague.domain.Result;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class ResultSubmission {
	
	protected String email;
	protected Result result;
	protected Long seasonId;
	protected CompetitionType competitionType;
	
	public Result getResult() {
		return result;
	}
	public Long getSeasonId() {
		return seasonId;
	}
	public CompetitionType getCompetitionType() {
		return competitionType;
	}
	public String getEmail() {
		return email;
	}

}
