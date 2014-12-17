package org.chilternquizleague.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Subclass;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
@Cache
@Subclass
public class CupCompetitionJava extends KnockoutCompetitionJava {

	public CupCompetitionJava() {
		super(CompetitionType.CUP);

	}

}
