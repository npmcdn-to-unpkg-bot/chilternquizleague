package org.chilternquizleague.domain;

import java.util.Date;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class Fixture {
	

	@Id
	private Long id;
	
	private Date date;
	private Ref<Team> home;
	private Ref<Team> away;
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Ref<Team> getHome() {
		return home;
	}
	public void setHome(Ref<Team> home) {
		this.home = home;
	}
	public Ref<Team> getAway() {
		return away;
	}
	public void setAway(Ref<Team> away) {
		this.away = away;
	}

}
