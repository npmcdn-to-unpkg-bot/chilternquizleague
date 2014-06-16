package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

@Cache
@Entity
public class Team {
	

	@Id
	private Long id;
	
	private String name;
	
	private Ref<Venue> venue;
	
	private List<String> emails = new ArrayList<>();

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Ref<Venue> getVenue() {
		return venue;
	}

	public void setVenue(Ref<Venue> venue) {
		this.venue = venue;
	}

	public List<String> getEmails() {
		return emails;
	}

	public void setEmails(List<String> emails) {
		this.emails = emails;
	}

}
