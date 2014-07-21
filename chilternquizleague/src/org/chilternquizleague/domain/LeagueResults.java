package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class LeagueResults{
	
	private Date date;
	private String description;
	

	public LeagueResults(){}
	
	public LeagueResults(LeagueResults template){
		
		this.date = template.date;
		this.description = template.description;
	}
	
	private List<LeagueResultRow> results = new ArrayList<>();

	public List<LeagueResultRow> getResults() {
		return results;
	}

	public void setResults(List<LeagueResultRow> results) {
		this.results = results;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public LeagueResultRow findRow(Fixture fixture){
		
		for(LeagueResultRow row : results){
			
			if(row.getFixture().isSame(fixture)){
				return row;
			}
		}
		
		return null;
	}
	
	public boolean addResult(LeagueResultRow incoming){
		
		final LeagueResultRow row = findRow(incoming.getFixture());
		
		if(row == null){
			return results.add(incoming);
		}else{
			row.getReports().addAll(incoming.getReports());
			return false;
		}
		
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
