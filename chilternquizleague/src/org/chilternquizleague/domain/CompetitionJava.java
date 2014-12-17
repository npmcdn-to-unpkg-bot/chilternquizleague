package org.chilternquizleague.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Parent;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@JsonTypeInfo(use=JsonTypeInfo.Id.MINIMAL_CLASS, include=JsonTypeInfo.As.PROPERTY, property="@class")
@Entity
@Cache
public abstract class CompetitionJava extends BaseEntity {


	private final CompetitionType type;
	private String description;
	private String startTime;
	private String endTime;
	private boolean subsidiary = false;
	
	@Parent
	private Ref<BaseEntity> parent;
	


	protected CompetitionJava(CompetitionType type)
	{
		this.type = type;
	}
	
	protected CompetitionJava(CompetitionType type, final boolean subsidiary)
	{
		this(type);
		this.subsidiary = subsidiary;
		this.description = type.getDescription();
		
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

	@Override
	@JsonIgnore
	public void setParent(final BaseEntity parent) {
		this.parent = Utils.entityToRef(parent);
		
	}

}
