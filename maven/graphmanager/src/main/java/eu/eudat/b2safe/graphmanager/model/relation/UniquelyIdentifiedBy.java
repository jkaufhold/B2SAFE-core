package eu.eudat.b2safe.graphmanager.model.relation;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import eu.eudat.b2safe.graphmanager.model.GraphEntity;
import eu.eudat.b2safe.graphmanager.model.node.FileEntity;
import eu.eudat.b2safe.graphmanager.model.node.PersistentIdentifier;

@RelationshipEntity(type = "UNIQUELY_IDENTIFIED_BY")
public class UniquelyIdentifiedBy extends GraphEntity {
	@StartNode
    private FileEntity digitalEntity;

    @EndNode
    private PersistentIdentifier persistentIdentifier;
}
