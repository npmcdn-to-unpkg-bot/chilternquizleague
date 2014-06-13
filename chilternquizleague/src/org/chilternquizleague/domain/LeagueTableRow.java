package org.chilternquizleague.domain;

public class LeagueTableRow {
	
	private String team;
	private String position;
	private int played;
	private int won;
	private int lost;
	private int drawn;
	private int leaguePoints;
	private int matchPointsFor;
	private int matchPointsAgainst;
	
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team;
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
