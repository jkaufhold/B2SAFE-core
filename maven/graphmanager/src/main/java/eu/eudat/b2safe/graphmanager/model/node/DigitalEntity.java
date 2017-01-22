package eu.eudat.b2safe.graphmanager.model.node;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.Relationship;

import eu.eudat.b2safe.graphmanager.model.NamedEntity;
import eu.eudat.b2safe.graphmanager.model.relation.Contains;

public abstract class DigitalEntity extends NamedEntity {
	@Relationship(type = "CONTAINS", direction = "INCOMING")
	private List<Contains> parents = new ArrayList<>();

	public List<Contains> getParents() {
		return parents;
	}

	public void setParents(List<Contains> parents) {
		this.parents = parents;
	}
}
