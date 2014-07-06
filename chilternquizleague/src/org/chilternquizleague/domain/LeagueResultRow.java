package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class LeagueResultRow {

	private int homeScore;
	private int awayScore;
	
	private Fixture fixture;
	private List<Text> reports = new ArrayList<>();
	
	public LeagueResultRow(){}
	
	public LeagueResultRow(Fixture fixture, int home, int away, String report){
		
		this.fixture = fixture;
		this.homeScore = home;
		this.awayScore = away;
		this.reports.add(new Text(report));
	}
	
	
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
		return fixture;
	}
	public void setFixture(Fixture fixture) {
		this.fixture = fixture;
	}
	public List<Text> getReports() {
		return reports;
	}
	public void setReports(List<Text> reports) {
		this.reports = reports;
	}
}
