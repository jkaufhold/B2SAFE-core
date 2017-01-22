package eu.eudat.b2safe.graphmanager.model.relation;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import eu.eudat.b2safe.graphmanager.model.GraphEntity;
import eu.eudat.b2safe.graphmanager.model.node.FileEntity;

@RelationshipEntity(type = "IS_MASTER_OF")
public class IsMasterOf extends GraphEntity {
    @StartNode
    private FileEntity original;

    @EndNode
    private FileEntity replica;
}
