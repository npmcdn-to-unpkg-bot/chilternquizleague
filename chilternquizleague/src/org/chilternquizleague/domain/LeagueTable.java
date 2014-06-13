package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.annotate.JsonAutoDetect;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

@JsonAutoDetect
@Cache
@Entity
public class LeagueTable {
	

	@Id
	private Long id;


	@Index
	private int startYear;
	
	@Index
	private int endYear;
	

	private List<LeagueTableRow> rows = new ArrayList<>();
	
	
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
	public List<LeagueTableRow> getRows() {
		return rows;
	}
	public void setRows(List<LeagueTableRow> rows) {
		this.rows = rows;
	}

}
