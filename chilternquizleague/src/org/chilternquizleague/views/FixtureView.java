/**
 * 
 */
package org.chilternquizleague.views;

import java.util.Date;

import org.chilternquizleague.domain.Competition;
import org.chilternquizleague.domain.Fixture;
import org.chilternquizleague.domain.Team;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

/**
 * @author gb106507
 *
 */
@JsonAutoDetect(fieldVisibility=Visibility.ANY)
public class FixtureView {

	public FixtureView(Fixture fixture, Competition competition){
		
		
		date = fixture.getDate();
		home = fixture.getHome();
		away = fixture.getAway();
		
		this.competition = competition.getDescription();
		
	}
	
	
	protected Date date;
	
	protected Team home;
	protected Team away;
	
	protected String competition;
}
