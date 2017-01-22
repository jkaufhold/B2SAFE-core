package eu.eudat.b2safe.metscomparator.model.structmapdiff;

import com.fasterxml.jackson.annotation.JsonTypeName;

public class AddedDivisionChange extends StructMapChange {
	public AddedDivisionChange() {

	}
	
	public AddedDivisionChange(String id) {
		super(id);
	}
	
	@Override
	public void accept(StructMapChangeVisitor visitor) {
		visitor.visit(this);
	}
	
	@Override
	public String toString() {
		return "AddedDivision:"+getId();
	}

}
