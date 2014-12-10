package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import scala.org.chilternquizleague.domain.util.CompetitionTypeStringifier;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Stringify;

@JsonAutoDetect(fieldVisibility = Visibility.PROTECTED_AND_PUBLIC)
@JsonIgnoreProperties("teamCompetitions")
@Cache
@Entity
public class Season extends BaseEntity {


	@Index
	private int startYear;

	@Index
	private int endYear;
	
	@Stringify(CompetitionTypeStringifier.class)
	private Map<CompetitionType, Ref<Competition>> competitions = new EnumMap<>(
			CompetitionType.class);
	
	@Ignore
	private Map<CompetitionType, Competition> competitionEnts = new EnumMap<>(CompetitionType.class);
	
	public Season()
	{
		startYear = Calendar.getInstance().get(Calendar.YEAR);
		endYear = startYear + 1;
	}



	public int getStartYear() {
		return startYear;
	}

	public void setStartYear(int startYear) {
		this.startYear = startYear;
	}

	public int getEndYear() {
		return endYear;
	}

	public void setEndYear(int endYear) {
		this.endYear = endYear;
	}

	public Map<CompetitionType, Competition> getCompetitions() {
		return competitionEnts = competitionEnts.isEmpty() ? Utils.refToEntityMap(competitions) : competitionEnts; 
	}

	public void setCompetitions(Map<CompetitionType, Competition> competitions) {
		this.competitionEnts = competitions;
	}
	
	public String getDescription(){
		
		return "" + startYear + "/" + endYear;
	}
	
	public void setDescription(String desc){
		//noop
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Competition> T getCompetition(CompetitionType type){
		
		return (T)type.castTo(getCompetitions().get(type));
	}
	
	

	public List<TeamCompetition> getTeamCompetitions(){
		
		final Set<CompetitionType> types = new HashSet<>(Arrays.asList(CompetitionType.LEAGUE,CompetitionType.BEER, CompetitionType.CUP, CompetitionType.PLATE));
		
		final List<TeamCompetition> competitions = new ArrayList<>();
		
		for(Competition competition : getCompetitions().values()){
			
			if(types.contains(competition.getType())){
				
				competitions.add((TeamCompetition)competition);
			}
		}
		
		return competitions;
	}



	@Override
	public void prePersist() {
		
		Utils.persist(this);
		
		for(Competition competition : competitionEnts.values()){
			competition.setParent(this);
			competition.prePersist();
		}
		
		competitions = Utils.entityToRefMap(competitionEnts, this);
	}


}
