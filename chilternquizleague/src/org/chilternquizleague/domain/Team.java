package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@Cache
@Entity
public class Team {
	

	@Id
	protected Long id;
	
	private String name;
	
	private String shortName;
	
	private Ref<Venue> venueRef;
	
	private List<Ref<User>> userRefs = new ArrayList<>();
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Venue getVenue() {
		return venueRef == null ? null :venueRef.get();
	}

	public void setVenue(Venue venue) {
		this.venueRef = Ref.create(venue);
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public List<User> getUsers() {
		
		return Utils.refsToEntities(userRefs);
	}

	public void setUsers(List<User> users) {
		
		userRefs = Utils.entitiesToRefs(users);
	}


}
