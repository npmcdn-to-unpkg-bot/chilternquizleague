package org.chilternquizleague.views;

import java.util.List;

import org.chilternquizleague.domain.Fixtures;
import org.chilternquizleague.domain.Results;
import org.chilternquizleague.domain.Team;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class PreSubmissionView {

	protected Team team;
	protected List<Fixtures> fixtures;
	protected List<Results> results;
	
	
	public PreSubmissionView(Team team, List<Fixtures> fixtures, List<Results> results) {
		this.team = team;
		this.fixtures = fixtures;
		this.results = results;
	}
	
	


}
