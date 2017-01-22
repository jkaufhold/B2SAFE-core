package eu.eudat.b2safe.metscomparator.model.structmapdiff;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use=JsonTypeInfo.Id.NAME, include=JsonTypeInfo.As.PROPERTY, property="@class")
public abstract class StructMapChange {
	private String id;
	
	public StructMapChange() {

	}
	
	public StructMapChange(String id) {
		super();
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public abstract void accept(StructMapChangeVisitor visitor);
}
