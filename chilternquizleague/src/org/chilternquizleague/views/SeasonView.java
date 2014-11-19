package org.chilternquizleague.views;

import org.chilternquizleague.domain.Season;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility = Visibility.ANY)
public class SeasonView {

	protected Long id;
	protected String description;

	public SeasonView(Season season) {

		id = season.getId();
		description = season.getDescription();
	}
}
