package eu.eudat.b2safe.graphmanager.model.relation;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import eu.eudat.b2safe.graphmanager.model.GraphEntity;
import eu.eudat.b2safe.graphmanager.model.node.Resource;
import eu.eudat.b2safe.graphmanager.model.node.Zone;

@RelationshipEntity(type = "IS_AVAILABLE_IN")
public class IsAvailableIn extends GraphEntity {
    @StartNode
    private Resource irodsResource;

    @EndNode
    private Zone irodsZone;
}
