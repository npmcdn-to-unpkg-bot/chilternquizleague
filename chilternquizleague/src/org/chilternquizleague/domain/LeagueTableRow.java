package org.chilternquizleague.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.Ref;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class LeagueTableRow {
	
	private Ref<Team> team;
	private String position;
	private int played;
	private int won;
	private int lost;
	private int drawn;
	private int leaguePoints;
	private int matchPointsFor;
	private int matchPointsAgainst;
	
	public Team getTeam() {
		return team == null ? null : team.get();
	}
	public void setTeam(Team team) {
		this.team = team == null ? null : Ref.create(team);
	}
	public int getPlayed() {
		return played;
	}
	public void setPlayed(int played) {
		this.played = played;
	}
	public int getWon() {
		return won;
	}
	public void setWon(int won) {
		this.won = won;
	}
	public int getLost() {
		return lost;
	}
	public void setLost(int lost) {
		this.lost = lost;
	}
	public int getDrawn() {
		return drawn;
	}
	public void setDrawn(int drawn) {
		this.drawn = drawn;
	}
	public int getLeaguePoints() {
		return leaguePoints;
	}
	public void setLeaguePoints(int matchPoints) {
		this.leaguePoints = matchPoints;
	}
	public int getMatchPointsFor() {
		return matchPointsFor;
	}
	public void setMatchPointsFor(int leaguePointsFor) {
		this.matchPointsFor = leaguePointsFor;
	}
	public int getMatchPointsAgainst() {
		return matchPointsAgainst;
	}
	public void setMatchPointsAgainst(int leaguePointsAgainst) {
		this.matchPointsAgainst = leaguePointsAgainst;
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position;
	}

}
