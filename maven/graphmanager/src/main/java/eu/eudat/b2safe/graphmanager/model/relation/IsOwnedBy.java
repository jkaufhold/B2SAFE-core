package eu.eudat.b2safe.graphmanager.model.relation;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import eu.eudat.b2safe.graphmanager.model.GraphEntity;
import eu.eudat.b2safe.graphmanager.model.node.FileEntity;
import eu.eudat.b2safe.graphmanager.model.node.User;

@RelationshipEntity(type = "IS_OWNED_BY")
public class IsOwnedBy extends GraphEntity {
    @StartNode
    private FileEntity digitalEntity;

    @EndNode
    private User user;
}
