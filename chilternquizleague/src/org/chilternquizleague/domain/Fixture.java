package org.chilternquizleague.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Parent;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@Cache
@Entity
public class Fixture {
	
	
	@Id
	protected Long id;
	
	private @Parent Ref<LeagueCompetition> competition;
	
	private Date date;
	private Ref<Team> home;
	private Ref<Team> away;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Team getHome() {
		return home == null ? null : home.get();
	}
	public void setHome(Team home) {
		this.home = home == null ? null : Ref.create(home);
	}
	public Team getAway() {
		return away == null ? null : away.get();
	}
	public void setAway(Team away) {
		this.away = away == null ? null : Ref.create(away);
	}

}
