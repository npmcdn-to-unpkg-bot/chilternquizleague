package org.chilternquizleague.domain;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.Ref;

@JsonAutoDetect(fieldVisibility = Visibility.PROTECTED_AND_PUBLIC)
public class FixtureJava {

	private Date start;

	private Date end;
	private Ref<Team> home;
	private Ref<Team> away;

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

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date end) {
		this.end = end;
	}

	public boolean isSame(Fixture fixture) {

		return Utils.isSameDay(start, fixture.getStart())
				&& home.getKey().equivalent(fixture.home.getKey());
	}

	@Override
	public String toString() {
		return "Fixture [start=" + start + ", end=" + end + ", home=" + home
				+ ", away=" + away + "]";
	}

}
