package org.chilternquizleague.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@Entity
@Cache
public abstract class KnockoutCompetition extends TeamCompetition {


	
	public KnockoutCompetition(CompetitionType type) {
		super(type);
		
	}

	@Override
	public Results addResult(Result result) {
		
		final Results results = getResultsForDate(result.getFixture().getStart());
		results.addResult(result);
		return results;
		
	}

}
