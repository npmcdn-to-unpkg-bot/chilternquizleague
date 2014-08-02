package org.chilternquizleague.domain;



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
