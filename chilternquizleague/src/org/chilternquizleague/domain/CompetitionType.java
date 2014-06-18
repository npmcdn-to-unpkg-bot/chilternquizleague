package org.chilternquizleague.domain;

public enum CompetitionType {
	
	LEAGUE {
		@Override
		public LeagueCompetition castTo(Competition competition) {
			
			return LeagueCompetition.class.cast(competition);
		}
	},
	BEER_LEG {
		@Override
		public LeagueCompetition castTo(Competition competition) {
			
			return LeagueCompetition.class.cast(competition);
		}
	},
	INDIVIDUAL,
	CUP,
	PLATE,
	BUZZER_QUIZ;
	

	
	public Competition castTo(Competition competition)
	{
		return competition;
	}

}
