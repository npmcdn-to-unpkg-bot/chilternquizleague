package org.chilternquizleague.domain;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class Text {

	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	


}
