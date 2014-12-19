package org.chilternquizleague.domain;

public enum CompetitionType {

	LEAGUE("League") {
		@Override
		public LeagueCompetition castTo(Competition competition) {

			return LeagueCompetition.class.cast(competition);
		}
	},
	BEER("Beer Leg") {
		@Override
		public BeerCompetition castTo(Competition competition) {

			return BeerCompetition.class.cast(competition);
		}
	},
	INDIVIDUAL("Individual") {
		@Override
		public IndividualCompetition castTo(Competition competition) {

			return IndividualCompetition.class.cast(competition);
		}
	}, 
	CUP("Knockout Cup") {
		@Override
		public KnockoutCompetition castTo(Competition competition) {

			return KnockoutCompetition.class.cast(competition);
		}
	},
	PLATE("Plate") {
		@Override
		public KnockoutCompetition castTo(Competition competition) {

			return KnockoutCompetition.class.cast(competition);
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
