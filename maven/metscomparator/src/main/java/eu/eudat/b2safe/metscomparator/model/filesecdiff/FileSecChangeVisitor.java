package eu.eudat.b2safe.metscomparator.model.filesecdiff;

public interface FileSecChangeVisitor {
	void visit(AddedFileChange addFileChange);
	void visit(RemovedFileChange removedFileChange);
	
	void visit(AddedFolderChange addedFolderChange);
	void visit(RemovedFolderChange removedFolderChange);
}
