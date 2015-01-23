package org.chilternquizleague.domain;

public enum CompetitionType {

	LEAGUE("League", LeagueCompetition.class) ,
	BEER("Beer Leg", BeerCompetition.class) ,
	INDIVIDUAL("Individual", IndividualCompetition.class) , 
	CUP("Knockout Cup", CupCompetition.class) ,
	PLATE("Plate", PlateCompetition.class) ,
	BUZZER("Buzzer Quiz", BuzzerCompetition.class);

	private final String description;
	private final Class<? extends Competition> compClass;

	private CompetitionType(String description, Class<? extends Competition> compClass) {
		this.description = description;
		this.compClass = compClass;
	}

	public final Competition castTo(Competition competition) {
		return compClass.cast(competition);
	}
	
	public final Class<? extends Competition> compClass(){
		return compClass;
	}

	public String description() {
		return description;
	}

}
