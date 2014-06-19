package org.chilternquizleague.views;

import java.util.ArrayList;
import java.util.List;

import org.chilternquizleague.domain.CompetitionType;

public class CompetitionTypeView {
	
	private String name;
	private String description;
	
	private CompetitionTypeView(CompetitionType type){
		
		name= type.name();
		description = type.getDescription();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public static List<CompetitionTypeView> getList(){
		
		final List<CompetitionTypeView> list = new ArrayList<>();
		
		for(CompetitionType type : CompetitionType.values()){
			list.add(new CompetitionTypeView(type));
		}
		
		return list;
	}

}
