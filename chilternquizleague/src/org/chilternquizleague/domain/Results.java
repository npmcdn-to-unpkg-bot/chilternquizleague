package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@Entity
@Cache
public class Results extends BaseEntity{
	
	private Date date;
	private String description;
	

	public Results(){}
	
	public Results(Results template){
		
		this.date = template.date;
		this.description = template.description;
	}
	
	private List<Result> results = new ArrayList<>();

	public List<Result> getResults() {
		return results;
	}

	public void setResults(List<Result> results) {
		this.results = results;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	public Result findRow(Fixture fixture){
		
		for(Result row : results){
			
			if(row.getFixture().isSame(fixture)){
				return row;
			}
		}
		
		return null;
	}
	
	public boolean addResult(Result incoming){
		
		final Result row = findRow(incoming.getFixture());
		
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