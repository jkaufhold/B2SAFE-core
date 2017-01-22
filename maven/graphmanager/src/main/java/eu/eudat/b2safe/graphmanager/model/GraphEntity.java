package eu.eudat.b2safe.graphmanager.model;

import org.neo4j.ogm.annotation.GraphId;

public abstract class GraphEntity {
	@GraphId
	private Long id;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}
	
}
