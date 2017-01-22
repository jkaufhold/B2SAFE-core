package eu.eudat.b2safe.metscomparator.model.structmapdiff;

public class RemovedDivisionChange extends StructMapChange {
	public RemovedDivisionChange() {

	}
	
	public RemovedDivisionChange(String id) {
		super(id);
	}
	
	@Override
	public void accept(StructMapChangeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "RemovedDivision:"+getId();
	}
}
