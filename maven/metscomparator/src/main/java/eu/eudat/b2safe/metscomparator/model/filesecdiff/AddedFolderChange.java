package eu.eudat.b2safe.metscomparator.model.filesecdiff;

public class AddedFolderChange extends FileSecChange {
	public AddedFolderChange() {

	}
	
	public AddedFolderChange(String id) {
		super(id);
	}
	
	@Override
	public void accept(FileSecChangeVisitor visitor) {
		visitor.visit(this);
	}

	@Override
	public String toString() {
		return "AddedFolder:"+getId();
	}

}
