package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class Fixtures extends BaseEntity{
	
	private Date start;
	private Date end;
	private CompetitionType competitionType;
	


	private String description;
	
	private List<Fixture> fixtures = new ArrayList<>();

	public Fixtures(){}
	
	public Fixtures(Fixtures template){
		this.start = template.start;
		this.end = template.end;
		this.description = template.description;
	}
	
	
	public List<Fixture> getFixtures() {
		return fixtures;
	}

	public void setFixtures(List<Fixture> fixtures) {
		this.fixtures = fixtures;
	}

	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date startTime) {
		this.start = startTime;
	}

	public Date getEnd() {
		return end;
	}

	public void setEnd(Date endTime) {
		this.end = endTime;
	}

	public CompetitionType getCompetitionType() {
		return competitionType;
	}

	public void setCompetitionType(CompetitionType competitionType) {
		this.competitionType = competitionType;
	}

}
