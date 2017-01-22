package eu.eudat.b2safe.graphmanager.model.node;

import java.util.ArrayList;
import java.util.List;

import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import eu.eudat.b2safe.graphmanager.model.relation.Contains;

@NodeEntity
public class Aggregation extends DigitalEntity {
	
	@Relationship(type = "CONTAINS", direction = "OUTGOING")
	private List<Contains> contains = new ArrayList<>();

	public List<Contains> getContains() {
		return contains;
	}

	public void setContains(List<Contains> contains) {
		this.contains = contains;
	}
}
