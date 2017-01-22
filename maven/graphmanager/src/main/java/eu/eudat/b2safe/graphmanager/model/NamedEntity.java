package eu.eudat.b2safe.graphmanager.model;

import org.neo4j.ogm.annotation.Property;

public abstract class NamedEntity extends GraphEntity {
	@Property(name = "name")
	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
