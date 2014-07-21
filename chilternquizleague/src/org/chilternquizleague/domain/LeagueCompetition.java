package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Subclass;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@Cache
@Subclass
public class LeagueCompetition extends TeamCompetition{
	
	private int win = 2;
	private int loss = 0;
	private int draw = 1;

	public LeagueCompetition()
	{
		super(CompetitionType.LEAGUE);
		setDescription("League");
	}
	
	private List<LeagueTable> leagueTables = new ArrayList<>();

	public List<LeagueTable> getLeagueTables() {
		return leagueTables;
	}

	public void setLeagueTables(List<LeagueTable> leagueTables) {
		this.leagueTables = leagueTables;
	}

	@Override
	public void addResult(LeagueResultRow result) {
		final LeagueResults results = getResultsForDate(result.getFixture().getStart());
		
		if(results.addResult(result)){
			
			for(LeagueTable table : leagueTables){
				
				for(LeagueTableRow row : table.getRows()){
					
					if(row.getTeam().equals(result.getFixture().getHome())){
						
						updateRow(row, result.getHomeScore(), result.getAwayScore());
					}
					else if(row.getTeam().equals(result.getFixture().getAway())){
						updateRow(row, result.getAwayScore(), result.getHomeScore());
					}
				}
			}
		}

	}
	
	private void updateRow(LeagueTableRow row, int score, int oppoScore){
		
		final int points = score > oppoScore ? win : score == oppoScore ? draw : loss;
		
		row.setLeaguePoints(row.getLeaguePoints() + points);
		row.setMatchPointsFor(row.getMatchPointsFor() + score);
		row.setMatchPointsAgainst(row.getMatchPointsAgainst() + oppoScore);
		row.setDrawn(row.getDrawn() + (points == 1 ? 1 :0));
		row.setWon(row.getWon() + (points == 2 ? 1 :0));
		row.setLost(row.getLost() + (points == 0 ? 1 :0));
		row.setPlayed(row.getPlayed() + 1);
		
		resortTable();

	}

	private void resortTable() {
		// TODO Auto-generated method stub
		
	}

	public int getWin() {
		return win;
	}

	public void setWin(int win) {
		this.win = win;
	}

	public int getLoss() {
		return loss;
	}

	public void setLoss(int loss) {
		this.loss = loss;
	}

	public int getDraw() {
		return draw;
	}

	public void setDraw(int draw) {
		this.draw = draw;
	}
	


}
