package eu.eudat.b2safe.metscomparator;

import edu.harvard.hul.ois.mets.FileSec;
import edu.harvard.hul.ois.mets.StructMap;

public class MetsMainComponents {
	private FileSec fileSec;
	private StructMap structMap;

	public MetsMainComponents() {

	}
	
	public MetsMainComponents(FileSec fileSec, StructMap structMap) {
		super();
		this.fileSec = fileSec;
		this.structMap = structMap;
	}
	public FileSec getFileSec() {
		return fileSec;
	}
	public void setFileSec(FileSec fileSec) {
		this.fileSec = fileSec;
	}
	public StructMap getStructMap() {
		return structMap;
	}
	public void setStructMap(StructMap structMap) {
		this.structMap = structMap;
	}
	
	
}
