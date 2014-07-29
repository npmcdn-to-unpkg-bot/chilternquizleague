package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.googlecode.objectify.Ref;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@JsonIgnoreProperties({"firstSubmitter"})
public class Result {

	private int homeScore;
	private int awayScore;
	
	private Fixture fixture;
	private List<Report> reports = new ArrayList<>();

	private Ref<User> firstSubmitter;
	
	public Result(){}
	
	public Result(Fixture fixture, int home, int away, String report, User firstSubmitter, Team team){
		
		this.fixture = fixture;
		this.homeScore = home;
		this.awayScore = away;
		setFirstSubmitter(firstSubmitter);
		this.reports.add(new Report(report,team));
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
	public List<Report> getReports() {
		return reports;
	}
	public void setReports(List<Report> reports) {
		this.reports = reports;
	}
	
	
	public User getFirstSubmitter(){
		
		return firstSubmitter == null ? null : firstSubmitter.get();
	}
	

	public void setFirstSubmitter(User user){
		
		firstSubmitter  = user == null ? null : Ref.create(user);
	}

	@Override
	public String toString() {
		return "Result [homeScore=" + homeScore + ", awayScore=" + awayScore
				+ ", fixture=" + fixture + ", reports=" + reports
				+ ", firstSubmitter=" + firstSubmitter + "]";
	}
	

}
