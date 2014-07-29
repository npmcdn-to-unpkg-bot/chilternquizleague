package org.chilternquizleague.domain;

import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Parent;

public abstract class SeasonChild extends BaseEntity {

	@Parent
	private Ref<Object> parent;
	
	
	@Override
	void setParent(Object parent) {
		this.parent = Ref.create(parent);
	}


	

}
