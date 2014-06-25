package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Parent;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class Fixtures extends BaseEntity{
	
	private @Parent Ref<Season> parent;
	
	private Date date;
	
	private List<Fixture> fixtures = new ArrayList<>();

	public List<Fixture> getFixtures() {
		return fixtures;
	}

	public void setFixtures(List<Fixture> fixtures) {
		this.fixtures = fixtures;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	

}
