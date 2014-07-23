package org.chilternquizleague.views;

import org.chilternquizleague.domain.Competition;

public class CompetitionView {
	
	protected String description;
	protected CompetitionTypeView type;
	protected boolean subsidiary;
	protected String startTime;
	
	public CompetitionView(Competition competition){
		
		description = competition.getDescription();
		type = new CompetitionTypeView(competition.getType());
		subsidiary = competition.isSubsidiary();
		startTime = competition.getStartTime();
	}

}
