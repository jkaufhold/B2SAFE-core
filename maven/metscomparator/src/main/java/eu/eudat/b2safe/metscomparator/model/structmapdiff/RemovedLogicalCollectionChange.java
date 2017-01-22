package eu.eudat.b2safe.metscomparator.model.structmapdiff;

public class RemovedLogicalCollectionChange extends StructMapChange {

	public RemovedLogicalCollectionChange() {
		super();
	}

	public RemovedLogicalCollectionChange(String id) {
		super(id);
	}

	@Override
	public void accept(StructMapChangeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "RemovedLogicalCollection:"+getId();
	}
}
