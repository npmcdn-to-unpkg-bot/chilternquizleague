package org.chilternquizleague.views;

import org.chilternquizleague.domain.Team;
import org.chilternquizleague.domain.Venue;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class TeamView {
	
	public TeamView(Team team){
		id = team.getId();
		shortName = team.getShortName();
		name = team.getName();
		venue = team.getVenue();
	}
	
	protected Long id;
	
	protected String shortName;
	protected String name;
	protected Venue venue;

}
