package org.chilternquizleague.domain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@Cache
@Entity
public class Season {
	
	@Id
	private Long id;
	
	@Index
	private int startYear;
	
	@Index
	private int endYear;

	private Ref<LeagueTable> leagueTable;
	
	private Ref<LeagueTable> beerLegTable;

	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public Ref<LeagueTable> getLeagueTable() {
		return leagueTable;
	}

	public void setLeagueTable(Ref<LeagueTable> leagueTable) {
		this.leagueTable = leagueTable;
	}

	public Ref<LeagueTable> getBeerLegTable() {
		return beerLegTable;
	}

	public void setBeerLegTable(Ref<LeagueTable> beerLegTable) {
		this.beerLegTable = beerLegTable;
	}

}
