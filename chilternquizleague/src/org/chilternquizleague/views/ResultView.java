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
	
	public ResultView(LeagueResultRow result, Competition competition) {
		this.fixture = new FixtureView(result.getFixture(), competition);
		this.homeScore = result.getHomeScore();
		this.awayScore = result.getAwayScore();
	}
	

}
