package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class LeagueTable{
	
	private String description;

	private List<LeagueTableRow> rows = new ArrayList<>();
	

	public List<LeagueTableRow> getRows() {
		return rows;
	}
	public void setRows(List<LeagueTableRow> rows) {
		this.rows = rows;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

}
