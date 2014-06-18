package org.chilternquizleague.domain;

import java.util.EnumMap;
import java.util.Map;

import org.chilternquizleague.domain.utils.CompetitionTypeStringifier;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Stringify;

@JsonAutoDetect(fieldVisibility = Visibility.PROTECTED_AND_PUBLIC)
@Cache
@Entity
public class Season {

	@Id
	protected Long id;

	@Index
	private int startYear;

	@Index
	private int endYear;

	@Stringify(value = CompetitionTypeStringifier.class)
	private Map<CompetitionType, Ref<Competition>> competitions = new EnumMap<>(
			CompetitionType.class);

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
		return Utils.refToEntity(competitions);
	}

	public void setCompetitions(Map<CompetitionType, Competition> competitions) {
		this.competitions = Utils.entityToRef(competitions);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends Competition> T getCompetition(CompetitionType type){
		
		return (T)type.castTo(getCompetitions().get(type));
	}

}
