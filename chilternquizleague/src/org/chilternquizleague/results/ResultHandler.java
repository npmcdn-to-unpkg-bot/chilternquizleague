package org.chilternquizleague.results;

import static com.googlecode.objectify.ObjectifyService.ofy;

import org.chilternquizleague.domain.CompetitionType;
import org.chilternquizleague.domain.Result;
import org.chilternquizleague.domain.Season;
import org.chilternquizleague.domain.TeamCompetition;
import org.chilternquizleague.domain.User;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.VoidWork;

public class ResultHandler {
	
	private final Result result;
	private final Long seasonId;
	private final CompetitionType competitionType;
	private String email;
	
	
	public ResultHandler(Result result, String email, Long seasonId, CompetitionType competitionType){
		this.result = result;
		this.seasonId = seasonId;
		this.competitionType = competitionType;
		this.email = email;
		
	}
	
	public void commit(){
		

		//set the first submitter (users are not serialisable, by design)
		for(User user : ofy().load().type(User.class).filter("email",email).list()){
			
			result.setFirstSubmitter(user);
		}
		
		ofy().transact(new VoidWork() {
			
			@Override
			public void vrun() {
				final Season season = ofy().load().key(Key.create(Season.class,seasonId)).now();
				final TeamCompetition competition = season.getCompetition(competitionType);
				
				if(competition != null){
					competition.addResult(result);
				}
				
				ofy().save().entity(season);
				
				System.out.println("Result " + result + " committed");
				
			}
		
		});
	}
	
	

}
