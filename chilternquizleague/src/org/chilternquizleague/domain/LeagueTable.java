package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

public class LeagueTable {
	
	private String season;
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
