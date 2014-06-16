package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Ref;

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
	public Ref<Fixture> getFixture() {
		return fixture;
	}
	public void setFixture(Ref<Fixture> fixture) {
		this.fixture = fixture;
	}
	public List<String> getReports() {
		return reports;
	}
	public void setReports(List<String> reports) {
		this.reports = reports;
	}
}
