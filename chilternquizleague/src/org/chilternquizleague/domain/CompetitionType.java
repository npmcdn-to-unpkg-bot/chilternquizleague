package org.chilternquizleague.domain;

public enum CompetitionType {

	LEAGUE("League") {
		@Override
		public LeagueCompetition castTo(Competition competition) {

			return LeagueCompetition.class.cast(competition);
		}
	},
	BEER_LEG("Beer Leg") {
		@Override
		public LeagueCompetition castTo(Competition competition) {

			return LeagueCompetition.class.cast(competition);
		}
	},
	INDIVIDUAL("Individual"), 
	CUP("Knockout Cup") {
		@Override
		public CupCompetition castTo(Competition competition) {

			return CupCompetition.class.cast(competition);
		}
	},
	PLATE("Plate") {
		@Override
		public CupCompetition castTo(Competition competition) {

			return CupCompetition.class.cast(competition);
		}
	},
	BUZZER_QUIZ("Buzzer Quiz");

	private final String description;

	private CompetitionType(String description) {
		this.description = description;
	}

	public Competition castTo(Competition competition) {
		return competition;
	}

	public String getDescription() {
		return description;
	}

}