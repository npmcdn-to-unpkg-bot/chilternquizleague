package org.chilternquizleague.domain;

public enum CompetitionType {

	LEAGUE("League") {
		@Override
		public LeagueCompetitionJava castTo(Competition competition) {

			return LeagueCompetitionJava.class.cast(competition);
		}
	},
	BEER("Beer Leg") {
		@Override
		public LeagueCompetitionJava castTo(Competition competition) {

			return LeagueCompetitionJava.class.cast(competition);
		}
	},
	INDIVIDUAL("Individual"), 
	CUP("Knockout Cup") {
		@Override
		public KnockoutCompetitionJava castTo(Competition competition) {

			return KnockoutCompetitionJava.class.cast(competition);
		}
	},
	PLATE("Plate") {
		@Override
		public KnockoutCompetitionJava castTo(Competition competition) {

			return KnockoutCompetitionJava.class.cast(competition);
		}
	},
	BUZZER("Buzzer Quiz"){
		@Override
		public BuzzerCompetition castTo(Competition competition) {

			return BuzzerCompetition.class.cast(competition);
		}
	};

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
