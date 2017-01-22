package eu.eudat.b2safe.metscomparator.model.filesecdiff;

public class AddedFileChange extends FileSecChange {

	public AddedFileChange() {

	}
	
	public AddedFileChange(String id) {
		super(id);
	}
	
	@Override
	public void accept(FileSecChangeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "AddedFile:"+getId();
	}
	
}
