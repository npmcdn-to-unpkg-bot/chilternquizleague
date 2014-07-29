package org.chilternquizleague.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.googlecode.objectify.annotation.Container;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class Competition {


	private final CompetitionType type;
	private String description;
	private String startTime;
	private String endTime;
	private boolean subsidiary = false;
	
	
	@JsonIgnore
	@Container
	private Season season;

	protected Competition(CompetitionType type)
	{
		this.type = type;
	}
	
	protected Competition(CompetitionType type, final boolean subsidiary)
	{
		this(type);
		this.subsidiary = subsidiary;
		
	}
	
	
	public CompetitionType getType() {
		return type;
	}

	public void setType(CompetitionType type) {
		//noop
	}

	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getStartTime() {
		return startTime;
	}


	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}


	public String getEndTime() {
		return endTime;
	}


	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}


	public boolean isSubsidiary() {
		return subsidiary;
	}


	public void setSubsidiary(boolean subsidiary) {
		//noop
	}

	public Season getSeason() {
		return season;
	}



}
