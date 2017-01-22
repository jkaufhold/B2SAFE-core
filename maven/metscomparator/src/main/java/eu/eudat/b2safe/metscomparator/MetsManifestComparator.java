package eu.eudat.b2safe.metscomparator;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;

import edu.harvard.hul.ois.mets.Div;
import edu.harvard.hul.ois.mets.File;
import edu.harvard.hul.ois.mets.FileGrp;
import edu.harvard.hul.ois.mets.FileSec;
import edu.harvard.hul.ois.mets.Fptr;
import edu.harvard.hul.ois.mets.Mets;
import edu.harvard.hul.ois.mets.StructMap;
import edu.harvard.hul.ois.mets.helper.MetsReader;
import eu.eudat.b2safe.metscomparator.model.FileSecDiff;
import eu.eudat.b2safe.metscomparator.model.MetsManifestDiff;
import eu.eudat.b2safe.metscomparator.model.StructMapDiff;
import eu.eudat.b2safe.metscomparator.model.filesecdiff.AddedFileChange;
import eu.eudat.b2safe.metscomparator.model.filesecdiff.AddedFolderChange;
import eu.eudat.b2safe.metscomparator.model.filesecdiff.FileSecChange;
import eu.eudat.b2safe.metscomparator.model.filesecdiff.RemovedFileChange;
import eu.eudat.b2safe.metscomparator.model.filesecdiff.RemovedFolderChange;
import eu.eudat.b2safe.metscomparator.model.structmapdiff.AddedDivisionChange;
import eu.eudat.b2safe.metscomparator.model.structmapdiff.AddedLogicalCollectionChange;
import eu.eudat.b2safe.metscomparator.model.structmapdiff.LogicalCollectionChange;
import eu.eudat.b2safe.metscomparator.model.structmapdiff.RemovedDivisionChange;
import eu.eudat.b2safe.metscomparator.model.structmapdiff.RemovedLogicalCollectionChange;
import eu.eudat.b2safe.metscomparator.model.structmapdiff.StructMapChange;

public class MetsManifestComparator {

	Logger log = LoggerFactory.getLogger(getClass());
	private Mets oldManifest;
	private Mets newManifest;
	private MetsMainComponents oldMainComponents;
	private MetsMainComponents newMainComponents;

	private StructMapDiff compareStructMapInfo() {
		// change in logical structure, file deleted from one log aggregation
		// and added to other

		// - change in isRelatedTo relation: remove old relation add a new one
		// <ns1:div LABEL="rel_9561c557-d494-4f51-96cc-f19f327d95a2"
		// TYPE="entityRelation"> safe the Content i na Map for comparison
		// compare relation isRelatedTo

		// - change in node properties 'type' update the properties of the node
		// save the divs from the Collection and Entities in a map to compare
		// type and format

		// main div -> TYPE == DigitalCollection
		// nicht in collection angeordnete Knoten:
		// TYPE == DescriptiveMetaData -> one metadata file
		// TYPE not DescriptiveMetaData -> Data

		// TYPE entityRelation -> logical collection mit subdivs = Knoten,
		// mit TYPE DescriptiveMetaData and not DescriptiveMetaData
		// Knoten is: LABEL, TYPE, FileID = file path -> vergleichen
		
		StructMap oldStructMap = oldMainComponents.getStructMap();
		StructMap newStructMap = newMainComponents.getStructMap();

		HashMap<String, Div> oldlLogicalCollections = new HashMap<String, Div>();
		HashMap<String, Div> oldDefaultDivs = new HashMap<String, Div>();
		
		contentMapsFromStructMap(oldStructMap, oldlLogicalCollections, oldDefaultDivs);

		HashMap<String, Div> newLogicalCollections = new HashMap<String, Div>();
		HashMap<String, Div> newDefaultDivs = new HashMap<String, Div>();
		
		contentMapsFromStructMap(newStructMap, newLogicalCollections, newDefaultDivs);

		List<StructMapChange> changes = new ArrayList<>();
		
		createChangesFromLogicalCollections(oldlLogicalCollections, newLogicalCollections, changes);
		createChangesFromDefaultDivs(oldDefaultDivs, newDefaultDivs, changes);
		
		return new StructMapDiff(changes);
	}

	private void createChangesFromDefaultDivs(HashMap<String, Div> oldDefaultDivs, HashMap<String, Div> newDefaultDivs,
			List<StructMapChange> changes) {
		Set<String> deletedDivs = new HashSet<>(oldDefaultDivs.keySet());
		
		for(String key : newDefaultDivs.keySet()) {
			if(!oldDefaultDivs.containsKey(key)) {
				changes.add(new AddedDivisionChange(key));
			} else {
				deletedDivs.remove(key);
			}
		}
		
		for(String key : deletedDivs) {
			changes.add(new RemovedDivisionChange(key));
		}
	}


	private void createChangesFromLogicalCollections(HashMap<String, Div> oldlLogicalCollections,
			HashMap<String, Div> newLogicalCollections, List<StructMapChange> changes) {
		Set<String> deletedDivs = new HashSet<>(oldlLogicalCollections.keySet());
		
		for(String key : newLogicalCollections.keySet()) {
			if(!oldlLogicalCollections.containsKey(key)) {
				changes.add(new AddedLogicalCollectionChange(key));
			} else {
				deletedDivs.remove(key);
				
				createChangesForCollection(key,oldlLogicalCollections.get(key),newLogicalCollections.get(key),changes);
			}
		}
		
		for(String key : deletedDivs) {
			changes.add(new RemovedLogicalCollectionChange(key));
		}
	}

	private void createChangesForCollection(String parent, Div oldDiv, Div newDiv, List<StructMapChange> changes) {
		Map<String, Div> oldMap = createMapFromDiv(oldDiv);
		Map<String, Div> newMap = createMapFromDiv(newDiv);
		
		Set<String> deletedDivs = new HashSet<>(oldMap.keySet());
		
		List<StructMapChange> innerChanges = new ArrayList<>();

		for(String key : newMap.keySet()) {
			if(!oldMap.containsKey(key)) {
				innerChanges.add(new AddedDivisionChange(key));
			} else {
				deletedDivs.remove(key);
			}
		}
		
		for(String key : deletedDivs) {
			innerChanges.add(new RemovedDivisionChange(key));
		}
		
		changes.add(new LogicalCollectionChange(parent, innerChanges));
	}

	private Map<String, Div> createMapFromDiv(Div div) {
		Map<String, Div> map = new HashMap<>();
		for(Object entry : div.getContent()) {
			if(entry instanceof Div) {
				Div d = (Div) entry;
				map.put(createIdFrom(d), d);
			} else {
				throw new RuntimeException("Invalid format");
			}
		}
		return map;
	}


	private String createIdFrom(Div d) {
		Object object = d.getContent().get(0);
		if(object instanceof Fptr) {
			// TODO Refactor
			return ((Fptr)object).getFILEID().keySet().iterator().next().toString();
		} else {
			throw new RuntimeException("Could not extract div id");
		}
	}

	private void contentMapsFromStructMap(StructMap oldStructMap, HashMap<String, Div> oldlLogicalCollections,
			HashMap<String, Div> oldDefaultDivs) {
		Div mainDiv = (Div) oldStructMap.getContent().get(0);
		for (Object entry: mainDiv.getContent()) {
			Div innerDiv = (Div) entry;
			if (innerDiv.getTYPE().equals("entityRelation")) {
				oldlLogicalCollections.put(innerDiv.getLABEL(), innerDiv);
			} else {
				oldDefaultDivs.put(createIdFrom(innerDiv), innerDiv);
			}
		}
	}

	private FileSecDiff compareFileSecInfo() {
		// - add file or directory: find subgraph and add new nodes and
		// relations
		// - remove file or directory: remove the node and relations from graph

		List<Object> oldFileGroups = getFileGroups(oldMainComponents);
		List<Object> newFileGroups = getFileGroups(newMainComponents);

		return compareFileGroups(oldFileGroups, newFileGroups);
	}

	@SuppressWarnings("unchecked")
	private List<Object> getFileGroups(MetsMainComponents mainComponents) {
		return (List<Object>) mainComponents.getFileSec().getContent();
	}

	private FileSecDiff compareFileGroups(List<Object> oldFileGroup, List<Object> newFileGroup) {
		if (oldFileGroup.size() != 1 || newFileGroup.size() != 1) {
			throw new RuntimeException("Invalid file structure.");
		}

		ArrayList<HashMap> newFilesAndDirectories = getFilesAndDirectories(((FileGrp) newFileGroup.get(0)));
		ArrayList<HashMap> oldFilesAndDirectories = getFilesAndDirectories(((FileGrp) oldFileGroup.get(0)));

		List<FileSecChange> changes = new LinkedList<>();

		compareFiles(oldFilesAndDirectories, newFilesAndDirectories, changes);
		compareFolders(oldFilesAndDirectories, newFilesAndDirectories, changes);

		return new FileSecDiff(changes);
	}

	private void compareFolders(ArrayList<HashMap> oldFilesAndDirectories, ArrayList<HashMap> newFilesAndDirectories,
			List<FileSecChange> changes) {
		HashMap<String, FileGrp> newDirectories = newFilesAndDirectories.get(1);
		HashMap<String, FileGrp> oldDirectories = oldFilesAndDirectories.get(1);
		Set<String> deletedDirectories = new HashSet<String>(oldDirectories.keySet());

		for (String newDirectoryID : newDirectories.keySet()) {
			if (!oldDirectories.keySet().contains(newDirectoryID)) {
				changes.add(new AddedFolderChange(newDirectoryID));
			} else {
				deletedDirectories.remove(newDirectoryID);
			}
		}

		for (String removedDirectoryId : deletedDirectories) {
			changes.add(new RemovedFolderChange(removedDirectoryId));
		}
	}

	private void compareFiles(ArrayList<HashMap> oldFilesAndDirectories, ArrayList<HashMap> newFilesAndDirectories,
			List<FileSecChange> changes) {
		HashMap<String, File> newFiles = newFilesAndDirectories.get(0);
		HashMap<String, File> oldFiles = oldFilesAndDirectories.get(0);

		Set<String> deletedFilesSet = new HashSet<>(oldFiles.keySet());

		for (String newFileID : newFiles.keySet()) {
			if (!oldFiles.keySet().contains(newFileID)) {
				changes.add(new AddedFileChange(newFileID));
			} else {
				deletedFilesSet.remove(newFileID);
			}
		}

		for (String deletedFile : deletedFilesSet) {
			changes.add(new RemovedFileChange(deletedFile));
		}
	}

	private ArrayList<HashMap> getFilesAndDirectories(FileGrp fileGroup) {
		if (fileGroup.getID() == null) {
			List content = fileGroup.getContent();
			if (content.size() != 1) {
				throw new RuntimeException("Invalid Input file.");
			}
			fileGroup = (FileGrp) content.get(0);
		}

		ArrayList<HashMap> result = new ArrayList<HashMap>();
		String id = fileGroup.getID();

		HashMap<String, FileGrp> directories = new HashMap<>();
		HashMap<String, File> files = new HashMap<>();
		if (id != null && !fileGroup.getContent().isEmpty()) {
			recursiveGetFilesAndFolders(fileGroup, directories, files);
		}
		result.add(files);
		result.add(directories);
		return result;
	}

	private void recursiveGetFilesAndFolders(Object object, HashMap<String, FileGrp> directories,
			HashMap<String, File> files) {
		if (object instanceof File) {
			File file = (File) object;
			files.put(file.getID(), file);
		} else if (object instanceof FileGrp) {
			FileGrp fileGroup = (FileGrp) object;
			directories.put(fileGroup.getID(), fileGroup);

			for (Object entry : fileGroup.getContent()) {
				recursiveGetFilesAndFolders(entry, directories, files);
			}
		} else {
			throw new RuntimeException("unknown mets element found");
		}
	}

	private ArrayList<HashMap> getContentToCompare(String pathToManifest) {
		ArrayList<HashMap> result = new ArrayList<>();

		HashMap<String, FileGrp> mainDirectories = new HashMap<String, FileGrp>();
		HashMap<String, Div> mainCollectionDivs = new HashMap<String, Div>();
		try (InputStream in = getClass().getResourceAsStream(pathToManifest)) {
			Mets mets = Mets.reader(new MetsReader(in));

			for (Object element : mets.getContent().toArray()) {
				if (element instanceof StructMap) {
					mainCollectionDivs = getCollectionDivs((StructMap) element);
				}
				if (element instanceof FileSec) {
					mainDirectories = getMainFileGroups((FileSec) element);
				}
			}
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		result.add(mainDirectories);
		result.add(mainCollectionDivs);
		return result;
	}

	private HashMap<String, Div> getCollectionDivs(StructMap structMap) {
		HashMap<String, Div> collectionDivs = new HashMap<>();
		if (structMap != null) {
			Object[] structMapArr = structMap.getContent().toArray();
			for (int i = 0; i < structMapArr.length; i++) {
				Object structMapElement = structMapArr[i];
				if (structMapElement instanceof Div) {
					Div structMapDiv = (Div) structMapElement;
					String type = structMapDiv.getTYPE();
					if (type.equals("digitalCollection")) {
						collectionDivs.put(structMapDiv.getLABEL(), structMapDiv);
					}
				}
			}
		}
		return collectionDivs;
	}

	private HashMap<String, FileGrp> getMainFileGroups(FileSec fileSec) {
		HashMap<String, FileGrp> directories = new HashMap<>();
		if (fileSec != null && fileSec != null) {
			Object[] fileSecArr = fileSec.getContent().toArray();
			for (int i = 0; i < fileSecArr.length; i++) {
				Object fileSecElement = fileSecArr[i];
				if (fileSecElement instanceof FileGrp) {
					FileGrp fileGroup = (FileGrp) fileSecElement;
					String id = fileGroup.getID();
					if (id == null && !fileGroup.getContent().isEmpty()) {
						// main not empty FileGrp
						Object[] fileGroupArr = fileGroup.getContent().toArray();
						for (int j = 0; j < fileGroupArr.length; j++) {
							if (fileGroupArr[j] instanceof FileGrp) {
								FileGrp element = (FileGrp) fileGroupArr[j];
								directories.put(element.getID(), element);
							} else {
								System.out.println("manifest.xml not correct");
								// should not happen
							}
						}
					}
				} else {
					System.out.println("manifest.xml not correct");
					// should not happen
				}
			}
		}
		return directories;
	}

	private MetsMainComponents extractMainMetsComponents(Mets mets) {
		FileSec fileSec = null;
		StructMap map = null;

		for (Object element : mets.getContent().toArray()) {
			if (element instanceof StructMap) {
				map = (StructMap) element;
			}
			if (element instanceof FileSec) {
				fileSec = (FileSec) element;
			}
		}

		if (fileSec == null) {
			throw new RuntimeException("Could not find FileSec");
		}
		if (map == null) {
			throw new RuntimeException("Could not find StructMap");
		}

		return new MetsMainComponents(fileSec, map);
	}

	public MetsManifestDiff compareMetsManifests(String pathToOldManifest, String pathToNewManifest)
			throws JsonProcessingException {
		this.oldManifest = readMetsFileFrom(pathToOldManifest);
		this.newManifest = readMetsFileFrom(pathToNewManifest);

		this.oldMainComponents = extractMainMetsComponents(oldManifest);
		this.newMainComponents = extractMainMetsComponents(newManifest);

		FileSecDiff changesInFileSec = compareFileSecInfo();
		StructMapDiff changesInLogicCollections = compareStructMapInfo();

		return new MetsManifestDiff(changesInFileSec, changesInLogicCollections);
	}

	private Mets readMetsFileFrom(String pathToManifest) {
		try (InputStream in = getClass().getResourceAsStream(pathToManifest)) {
			return Mets.reader(new MetsReader(in));
		} catch (Exception e) {
			throw new RuntimeException("could not read manifest " + pathToManifest, e);
		}
	}
}
