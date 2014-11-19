package org.chilternquizleague.domain.utils;

import com.googlecode.objectify.stringifier.Stringifier;

abstract class EnumStringifier<T extends Enum<T> > implements Stringifier<T> {

	private final Class<T> enumClass;
	
	
	protected EnumStringifier(Class<T> enumClass){
		this.enumClass = enumClass; 
	}
	
	@Override
	public T fromString(String name) {
		return Enum.valueOf(enumClass, name);
	}

	@Override
	public String toString(T member) {
		return member.name();
	}

	
		
	

}
