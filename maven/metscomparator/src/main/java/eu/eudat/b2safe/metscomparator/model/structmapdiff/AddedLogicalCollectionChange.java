package eu.eudat.b2safe.metscomparator.model.structmapdiff;

public class AddedLogicalCollectionChange extends StructMapChange {

	public AddedLogicalCollectionChange() {

	}
	
	public AddedLogicalCollectionChange(String id) {
		super(id);
	}

	@Override
	public void accept(StructMapChangeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "AddedLogicalCollection:"+getId();
	}
}
