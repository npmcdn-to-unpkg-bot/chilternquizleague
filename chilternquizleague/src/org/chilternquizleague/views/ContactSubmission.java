package org.chilternquizleague.views;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;

@JsonAutoDetect(fieldVisibility=Visibility.PROTECTED_AND_PUBLIC)
public class ContactSubmission {

	protected String recipient;
	protected String sender;
	protected String text;
	
	public String getRecipient() {
		return recipient;
	}
	public String getSender() {
		return sender;
	}
	public String getText() {
		return text;
	}
	
	

}
