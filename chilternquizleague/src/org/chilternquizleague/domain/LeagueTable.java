package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@Cache
@Entity
public class LeagueTable {
	

	@Id
	protected Long id;
	
	private String description;

	private List<LeagueTableRow> rows = new ArrayList<>();
	

	public List<LeagueTableRow> getRows() {
		return rows;
	}
	public void setRows(List<LeagueTableRow> rows) {
		this.rows = rows;
	}
	protected String getDescription() {
		return description;
	}
	protected void setDescription(String description) {
		this.description = description;
	}

}
