package org.chilternquizleague.domain;

import com.googlecode.objectify.Ref;

public class Report{
	
	private Text text;
	private Ref<Team> team;
	
	public Report(){}
	
	public Report(String text, Team team){
		
		this.text = new Text(text);
		setTeam(team);
	}
	
	public Text getText() {
		return text;
	}
	public void setText(Text text) {
		this.text = text;
	}
	public Team getTeam() {
		return team == null ? null : team.get();
	}
	public void setTeam(Team team) {
		this.team = team == null ? null : Ref.create(team);
	}
}