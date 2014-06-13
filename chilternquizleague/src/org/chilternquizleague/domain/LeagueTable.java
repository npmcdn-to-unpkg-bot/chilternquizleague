package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;


@Entity
public class LeagueTable {
	

	@Id
	private long id;
	

	private String season;
	
	@Index
	private int startYear;
	
	@Embedded
	private List<LeagueTableRow> rows = new ArrayList<>();
	
	
	public String getSeason() {
		return season;
	}
	public void setSeason(String season) {
		this.season = season;
	}
	public List<LeagueTableRow> getRows() {
		return rows;
	}
	public void setRows(List<LeagueTableRow> rows) {
		this.rows = rows;
	}

}
