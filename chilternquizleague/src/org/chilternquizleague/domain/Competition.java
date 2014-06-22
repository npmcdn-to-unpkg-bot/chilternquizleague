package org.chilternquizleague.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
//@Cache
//@Entity
public abstract class Competition {

//	@Id
//	protected Long id;
	private final CompetitionType type;
	private String description;

	protected Competition(CompetitionType type)
	{
		this.type = type;
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

}
