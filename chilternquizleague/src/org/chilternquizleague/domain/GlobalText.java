package org.chilternquizleague.domain;

import java.util.HashMap;
import java.util.Map;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

@Entity
@Cache
public class GlobalText extends BaseEntity {
	
	private final Map<String,String> text = new HashMap<>();
	
	public String getText(final String key){
		
		return text.get(key);
	}
	
	public void setText(final String key, final String text){
		this.text.put(key, text);
	}
	
}
