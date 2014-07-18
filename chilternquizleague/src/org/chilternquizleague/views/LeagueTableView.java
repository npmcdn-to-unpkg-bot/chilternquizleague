package org.chilternquizleague.views;

import java.util.List;

import org.chilternquizleague.domain.CompetitionType;
import org.chilternquizleague.domain.LeagueCompetition;
import org.chilternquizleague.domain.LeagueTable;
import org.chilternquizleague.domain.Season;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class LeagueTableView {
	
	protected List<LeagueTable> tables;
	
	protected String description;
	
	public LeagueTableView(Season season){
		
		final LeagueCompetition competition =  season.getCompetition(CompetitionType.LEAGUE);
		
		tables = competition.getLeagueTables();
		description = season.getDescription();
		
		
	}

}
