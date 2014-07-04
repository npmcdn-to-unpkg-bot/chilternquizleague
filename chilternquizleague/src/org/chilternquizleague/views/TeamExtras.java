package org.chilternquizleague.views;

import java.util.ArrayList;
import java.util.List;

import org.chilternquizleague.domain.Team;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
public class TeamExtras {

	protected Long id;
	
	protected String text;
	
	protected List<FixtureView> fixtures = new ArrayList<>();
	
	public TeamExtras(Team team, List<FixtureView> fixtures) {
		text = team.getRubric().getText();
		id = team.getId();
		this.fixtures = fixtures; 
	}

}