package org.chilternquizleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.googlecode.objectify.Ref;
import com.googlecode.objectify.annotation.Parent;

public abstract class CompetitionChild extends BaseEntity {

	@Parent
	private Ref<BaseEntity> parent;
	
	
	@Override
	@JsonIgnore
	public void setParent(BaseEntity parent) {
		this.parent = Utils.entityToRef(parent);
	}


	@Override
	public void prePersist() {
		Utils.persist(this);
	}


	

}
