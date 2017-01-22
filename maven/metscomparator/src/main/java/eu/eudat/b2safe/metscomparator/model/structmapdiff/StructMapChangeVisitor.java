package eu.eudat.b2safe.metscomparator.model.structmapdiff;

public interface StructMapChangeVisitor {
	void visit(AddedDivisionChange addedDivisionChange);
	void visit(RemovedDivisionChange removedDivisionChange);
	void visit(AddedLogicalCollectionChange addedLogicalCollectionChange);
	void visit(RemovedLogicalCollectionChange removedLogicalCollectionChange);
	void visit(LogicalCollectionChange logicalCollectionChange);
}
