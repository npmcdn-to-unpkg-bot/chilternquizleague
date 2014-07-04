package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.Ref;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class LeagueResultRow {

	private int homeScore;
	private int awayScore;
	
	private Ref<Fixture> fixture;
	private List<String> reports = new ArrayList<>();
	
	public int getHomeScore() {
		return homeScore;
	}
	public void setHomeScore(int homeScore) {
		this.homeScore = homeScore;
	}
	public int getAwayScore() {
		return awayScore;
	}
	public void setAwayScore(int awayScore) {
		this.awayScore = awayScore;
	}
	public Fixture getFixture() {
		return fixture == null ? null : fixture.get();
	}
	public void setFixture(Fixture fixture) {
		this.fixture = fixture == null ? null : Ref.create(fixture);
	}
	public List<String> getReports() {
		return reports;
	}
	public void setReports(List<String> reports) {
		this.reports = reports;
	}
}