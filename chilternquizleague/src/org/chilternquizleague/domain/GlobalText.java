package org.chilternquizleague.domain;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

@Entity
@Cache
public class GlobalText extends BaseEntity {
	
	
	private String name;
	private Map<String,TextEntry> text = new HashMap<>();
	
	public String getText(final String key){
		
		return text.containsKey(key) ? text.get(key).getText() : "No text found for '" + key +"'" ;
	}
	
	public void setText(final String key, final String text){
		this.text.put(key, new TextEntry(key, text));

	}
	
	public List<TextEntry> getEntries(){
		
		return new ArrayList<>(text.values());
	}
	
	public void setEntries(List<TextEntry> entries){
		
		for(TextEntry entry : entries){
			text.put(entry.getName(), entry);
		}
	}
	
	public static class TextEntry{
		

		private String name;
		private String text;
		
		public TextEntry(){}
		
		public TextEntry(String name, String text) {

			this.name = name;
			this.text = text;
		}
		
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
