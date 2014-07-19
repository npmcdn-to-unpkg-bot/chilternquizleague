/**
 * 
 */
package org.chilternquizleague.views;

import java.util.Date;

import org.chilternquizleague.domain.Competition;
import org.chilternquizleague.domain.Fixture;
import org.chilternquizleague.domain.Fixtures;
import org.chilternquizleague.domain.Team;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * @author gb106507
 *
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY)
public class FixtureView {

	public FixtureView(Fixture fixture, Competition competition, Fixtures fixtures){
		this(fixture,competition, fixtures.getDescription(), fixtures.getStartTime(), fixtures.getEndTime());

	}
	
	
	public FixtureView(Fixture fixture, Competition competition){
		
		this(fixture,competition, competition.getDescription(), competition.getStartTime(), competition.getEndTime());
		
		
	}
	
	private FixtureView(Fixture fixture, Competition competition, String description, String startTime, String endTime){
		
		date = fixture.getDate();
		
		home = fixture.getHome();
		away = fixture.getAway();
		
		this.description = description;
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	
	protected Date date;
	
	protected Team home;
	protected Team away; 
	
	protected String startTime;
	protected String endTime;
	
	protected String description;
}
