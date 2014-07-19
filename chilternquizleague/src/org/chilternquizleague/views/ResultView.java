package org.chilternquizleague.views;

import org.chilternquizleague.domain.Competition;
import org.chilternquizleague.domain.LeagueResultRow;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
public class ResultView {
	
	protected FixtureView fixture;
	protected int homeScore;
	protected int awayScore;
	protected String description;
	
	public ResultView(LeagueResultRow result, Competition competition, String description) {
		this.fixture = new FixtureView(result.getFixture(), competition);
		this.homeScore = result.getHomeScore();
		this.awayScore = result.getAwayScore();
		this.description = description;
	}
	

}
