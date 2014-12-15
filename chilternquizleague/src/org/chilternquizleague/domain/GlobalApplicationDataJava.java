package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

@Entity
@Cache
public class GlobalApplicationDataJava extends BaseEntity{
	
	private String frontPageText;
	private String leagueName = "Chiltern Quiz League";
	private Ref<Season> currentSeason;
	private Ref<GlobalText> globalText;
	private List<EmailAlias> emailAliases = new ArrayList<>();
	
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
	
	public List<EmailAlias> getEmailAliases() {
		return emailAliases;
	}
	
	public void setEmailAliases(List<EmailAlias> emailAliases) {
		this.emailAliases = emailAliases;
	}
	
	@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
	public static class EmailAlias{
		
		public EmailAlias() {

		}
		private String alias;
		private Ref<User> user;
		
		public String getAlias() {
			return alias;
		}
		public void setAlias(String alias) {
			this.alias = alias;
		}
		public User getUser() {
			return user.get();
		}
		public void setUser(User user) {
			this.user = Ref.create(user);
		}
	}
	

}
