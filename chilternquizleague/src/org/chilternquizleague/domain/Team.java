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
	
	private transient Ref<Venue> venueRef;
	
	private transient List<Ref<User>> userRefs = new ArrayList<>();
	
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
		
		List<User> users = new ArrayList<>(userRefs.size());
		
		for(Ref<User> user : userRefs)
		{
			users.add(user.get());
		}
		
		return users;
	}

	public void setUsers(List<User> users) {
		
		final List<Ref<User>> userRefs = new ArrayList<>(users.size());
		
		for(User user : users){
			userRefs.add(Ref.create(user));
		}
	}


}
