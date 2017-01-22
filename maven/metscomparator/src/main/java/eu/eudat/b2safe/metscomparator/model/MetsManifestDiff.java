package eu.eudat.b2safe.metscomparator.model;

public class MetsManifestDiff {
	private FileSecDiff fileSecDiff;
	private StructMapDiff structMapDiff;
	
	public MetsManifestDiff() {
	}
	
	public MetsManifestDiff(FileSecDiff fileSecDiff, StructMapDiff structMapDiff) {
		super();
		this.setFileSecDiff(fileSecDiff);
		this.setStructMapDiff(structMapDiff);
	}

	public StructMapDiff getStructMapDiff() {
		return structMapDiff;
	}

	public void setStructMapDiff(StructMapDiff structMapDiff) {
		this.structMapDiff = structMapDiff;
	}

	public FileSecDiff getFileSecDiff() {
		return fileSecDiff;
	}

	public void setFileSecDiff(FileSecDiff fileSecDiff) {
		this.fileSecDiff = fileSecDiff;
	}
}
