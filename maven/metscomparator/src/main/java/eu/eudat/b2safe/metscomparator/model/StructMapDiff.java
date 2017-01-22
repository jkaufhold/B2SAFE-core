package eu.eudat.b2safe.metscomparator.model;

import java.util.List;

import eu.eudat.b2safe.metscomparator.model.structmapdiff.StructMapChange;

public class StructMapDiff {

	private List<StructMapChange> changes;

	public StructMapDiff(List<StructMapChange> changes) {
		this.changes = changes;
	}

	public List<StructMapChange> getChanges() {
		return changes;
	}
	
	public void setChanges(List<StructMapChange> changes) {
		this.changes = changes;
	}
}
