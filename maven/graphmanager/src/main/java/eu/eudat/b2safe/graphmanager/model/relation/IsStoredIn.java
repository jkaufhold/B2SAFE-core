package eu.eudat.b2safe.graphmanager.model.relation;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

import eu.eudat.b2safe.graphmanager.model.GraphEntity;
import eu.eudat.b2safe.graphmanager.model.node.FileEntity;
import eu.eudat.b2safe.graphmanager.model.node.Resource;

@RelationshipEntity(type = "IS_STORED_IN")
public class IsStoredIn extends GraphEntity {
    @StartNode
    private FileEntity digitalEntity;

    @EndNode
    private Resource resource;
}
