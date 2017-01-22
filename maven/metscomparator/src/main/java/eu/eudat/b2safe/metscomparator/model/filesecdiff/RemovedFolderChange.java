package eu.eudat.b2safe.metscomparator.model.filesecdiff;

public class RemovedFolderChange extends FileSecChange {
	public RemovedFolderChange() {

	}
	
	public RemovedFolderChange(String id) {
		super(id);
	}
	
	@Override
	public void accept(FileSecChangeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "RemovedFolder:"+getId();
	}

}
