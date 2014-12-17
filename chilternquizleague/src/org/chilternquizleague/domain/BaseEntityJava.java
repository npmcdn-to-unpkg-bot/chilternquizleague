package org.chilternquizleague.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;

@JsonIgnoreProperties(ignoreUnknown=true)
public abstract class BaseEntityJava {
	
	@Id
	protected Long id;
	
	@Ignore
	@JsonProperty
	protected String refClass = this.getClass().getSimpleName();
	
	//Only needed for serialisation of nested entities
	@Ignore
	private String key;
	
	
	private boolean retired;
	
	public Long getId()
	{
		return id;
	}
	
	public boolean isRetired() {
		return retired;
	}

	public void setRetired(boolean retired) {
		this.retired = retired;
	}
	
	public void setParent(BaseEntity parent){
		//noop
	}
	
	public BaseEntity getParent(){
		return null;
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BaseEntity other = (BaseEntity) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	public void prePersist(){
		
	}

	public String getKey(){
		
		return key = key == null && id != null ? Key.create(this).getString() : key; 
	}
	
	public void setKey(String key){
		//noop
	}
	
	protected void internalSetKey(String key){
		this.key = key; 
	}
}
