package eu.eudat.b2safe.metscomparator.model.structmapdiff;

import java.util.ArrayList;
import java.util.List;

public class LogicalCollectionChange extends StructMapChange {

	private List<StructMapChange> changes;
	
	public LogicalCollectionChange() {
		super();
		changes = new ArrayList<>();
	}

	public LogicalCollectionChange(String id) {
		super(id);
		changes = new ArrayList<>();
	}

	public LogicalCollectionChange(String id, List<StructMapChange> changes) {
		super(id);
		this.changes = changes;
	}

	public List<StructMapChange> getChanges() {
		return changes;
	}
	
	public void setChanges(List<StructMapChange> changes) {
		this.changes = changes;
	}
	
	@Override
	public void accept(StructMapChangeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		StringBuilder result = new StringBuilder("LogicalCollectionChange:")
			.append(getId()).append("(");
		
		for(StructMapChange change : changes) {
			result.append(change.toString()).append(",");
		}
		
		return result.append(")").toString();
	}
}
