package eu.eudat.b2safe.metscomparator.model;

import java.util.List;

import eu.eudat.b2safe.metscomparator.model.filesecdiff.FileSecChange;

public class FileSecDiff {
	private List<FileSecChange> fileChanges;

	public FileSecDiff(List<FileSecChange> fileChanges) {
		super();
		this.fileChanges = fileChanges;
	}

	public List<FileSecChange> getFileChanges() {
		return fileChanges;
	}

	public void setFileChanges(List<FileSecChange> fileChanges) {
		this.fileChanges = fileChanges;
	}
}
