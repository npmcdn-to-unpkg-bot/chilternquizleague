package org.chilternquizleague.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Parent;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class Fixture{
	
	private @Parent Ref<Fixtures> fixtures;
	
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

	public boolean isSame(Fixture fixture){
		
		return Utils.isSameDay(date, fixture.getDate()) && home.getKey().equivalent(fixture.home.getKey());
	}
}
