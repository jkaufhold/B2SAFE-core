package eu.eudat.b2safe.metscomparator.model.filesecdiff;

public class RemovedFileChange extends FileSecChange {

	public RemovedFileChange() {

	}
	
	public RemovedFileChange(String id) {
		super(id);
	}
	
	@Override
	public void accept(FileSecChangeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "RemovedFile:"+getId();
	}

}
