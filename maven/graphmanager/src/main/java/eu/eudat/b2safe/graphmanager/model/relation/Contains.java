package eu.eudat.b2safe.graphmanager.model.relation;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import eu.eudat.b2safe.graphmanager.model.GraphEntity;
import eu.eudat.b2safe.graphmanager.model.node.Aggregation;
import eu.eudat.b2safe.graphmanager.model.node.DigitalEntity;

@RelationshipEntity(type = "CONTAINS")
public class Contains extends GraphEntity {
    @StartNode
    private Aggregation aggregation;

    @EndNode
    private DigitalEntity digitalEntity;

    public Contains() {

    }
    
	private Contains(Aggregation parent, DigitalEntity child) {
		this.setAggregation(parent);
		this.setDigitalEntity(child);
		child.getParents().add(this);
		parent.getContains().add(this);
	}
	
	public static Contains connect(Aggregation parent, DigitalEntity child) {
		return new Contains(parent, child);
	}

	public Aggregation getAggregation() {
		return aggregation;
	}

	public void setAggregation(Aggregation aggregation) {
		this.aggregation = aggregation;
		this.aggregation.getContains().add(this);
	}

	public DigitalEntity getDigitalEntity() {
		return digitalEntity;
	}

	public void setDigitalEntity(DigitalEntity digitalEntity) {
		this.digitalEntity = digitalEntity;
	}
    
    
}
