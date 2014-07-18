package org.chilternquizleague.results;

import static com.googlecode.objectify.ObjectifyService.ofy;

import org.chilternquizleague.domain.CompetitionType;
import org.chilternquizleague.domain.LeagueResultRow;
import org.chilternquizleague.domain.LeagueResults;
import org.chilternquizleague.domain.Season;
import org.chilternquizleague.domain.TeamCompetition;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;

public class ResultHandler {
	
	private final LeagueResultRow result;
	private final Long seasonId;
	private final CompetitionType competitionType;
	
	
	public ResultHandler(LeagueResultRow result, Long seasonId, CompetitionType competitionType){
		this.result = result;
		this.seasonId = seasonId;
		this.competitionType = competitionType;
		
	}
	
	public void commit(){
		
		ofy().transact(new VoidWork() {
			
			@Override
			public void vrun() {
				final Season season = ofy().load().key(Key.create(Season.class,seasonId)).now();
				final TeamCompetition competition = season.getCompetition(competitionType);
				
				if(competition != null){
					competition.addResult(result);
				}
				
				ofy().save().entity(season);
				
			}
		
		});
	}

}
