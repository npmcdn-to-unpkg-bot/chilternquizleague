package org.chilternquizleague.domain;

import com.googlecode.objectify.annotation.Id;

abstract class BaseEntity {
	
	@Id
	protected Long id;
	
	public Long getId()
	{
		return id;
	}

}
