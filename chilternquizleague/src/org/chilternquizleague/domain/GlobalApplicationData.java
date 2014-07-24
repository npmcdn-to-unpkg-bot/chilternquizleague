package org.chilternquizleague.domain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

@Entity
@Cache
public class GlobalApplicationData extends BaseEntity{
	
	private String frontPageText;
	private String leagueName = "Chiltern Quiz League";
	private Ref<Season> currentSeason;
	private Ref<GlobalText> globalText;
	
	public String getFrontPageText() {
		return frontPageText;
	}
	public void setFrontPageText(String frontPageText) {
		this.frontPageText = frontPageText;
	}
	public String getLeagueName() {
		return leagueName;
	}
	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	} 
	
	public Season getCurrentSeason(){
		return currentSeason == null ? null : currentSeason.get();
	}
	
	public void setCurrentSeason(Season season){
		
		currentSeason = season == null ? null : Ref.create(season);
	}
	
	public GlobalText getGlobalText(){
		return globalText == null ? null : globalText.get();
	}
	
	public void setGlobalText(GlobalText globalText){
		this.globalText = globalText == null ? null : Ref.create(globalText);
	}
	


}
