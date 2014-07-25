package org.chilternquizleague.views;

import org.chilternquizleague.domain.GlobalApplicationData;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.ANY)
public class GlobalApplicationDataView {
	
	protected String frontPageText;
	
	protected String leagueName;
	
	protected Long currentSeasonId;
	
	protected Long textId;

	
	
	
	public GlobalApplicationDataView(GlobalApplicationData data) {
		frontPageText = data.getFrontPageText();
		leagueName = data.getLeagueName();
		currentSeasonId = data.getCurrentSeason() == null ? null : data.getCurrentSeason().getId();
		textId = data.getGlobalText() == null ? null :data.getGlobalText().getId();
	}




}
