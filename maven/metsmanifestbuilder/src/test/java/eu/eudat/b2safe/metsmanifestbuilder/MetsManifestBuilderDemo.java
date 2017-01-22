package eu.eudat.b2safe.metsmanifestbuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.Test;

import edu.harvard.hul.ois.mets.Div;
import edu.harvard.hul.ois.mets.FLocat;
import edu.harvard.hul.ois.mets.FileGrp;
import edu.harvard.hul.ois.mets.FileSec;
import edu.harvard.hul.ois.mets.Fptr;
import edu.harvard.hul.ois.mets.Loctype;
import edu.harvard.hul.ois.mets.Mets;
import edu.harvard.hul.ois.mets.StructMap;
import edu.harvard.hul.ois.mets.helper.MetsValidator;
import edu.harvard.hul.ois.mets.helper.MetsWriter;

public class MetsManifestBuilderDemo {
	// Unterscheiden zwsch Filesystem und iRODS um den Filesystem zu durchlaufen
	private List<String> filePathList = new ArrayList<String>();

	@Test
	public void builManifestTest() {
		//test with filesystem
		String path = "collection_A";

		Mets mets = createMetsObjectFromFileSystemStructure(path);

		//create xml from MetsManifest object
		//	    dom = xml.dom.minidom.parseString(manifest.toxml("utf-8"))
		//	    manifestXML = dom.toprettyxml(indent="  ", encoding="utf-8")
		//
		//	    if dryrun 
		//	        print to console manifestXML
		//	    else:
		//	        Writing the manifest to a file
		//	        if filesystem

		File file = new File("manifest.xml");

		try (OutputStream os = new FileOutputStream(file)) {

			//TODO: activate and resolve exception
			//mets.validate(new MetsValidator());
			mets.write(new MetsWriter(os));

			os.flush();
			os.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//	            target = args.filesystem[0] + os.sep + 'manifest.xml'
		//	        else: 
		//	            in the irods namespace: {}'.format(target)
		//	                irodsu.putFile(temp.name, target, configuration.irods_resource)		
	}

	//	Building the METS manifest object
	private Mets createMetsObjectFromFileSystemStructure(String path) {
		Mets mets = new Mets();

		// Top-level stuff
		mets.setOBJID("ID:_EUDATMETS_d2190295-7dee-4bb0-87d7-0baedc4c11ca");
		mets.setLABEL("EUDAT METS document");
		mets.setSchema("mods", "http://www.loc.gov/METS/", "http://www.loc.gov/standards/mods/v3/mods-3-0.xsd");

		// MetsHdr
		//		MetsHdr metsHdr = new MetsHdr();
		//		metsHdr.setCREATEDATE(new Date());
		//		mets.getContent().add(metsHdr);

		File rootFile = new File(path);

		// Create a FileSec from path in mets object
		FileSec fileSec = createFileSecFrom(rootFile);
		mets.getContent().add(fileSec);

		// create structMap from metadata.json and path
		String pathToMetadataJSON = "/metadata.json";
		StructMap structMap = createStructMap(pathToMetadataJSON, rootFile);
		mets.getContent().add(structMap);

		return mets;
	}

	private StructMap createStructMap(String pathToMetadataJSON, File rootFile) {
		StructMap structMap = new StructMap();
		structMap.setTYPE("Relational");

		Div mainDiv = new Div();
		mainDiv.setTYPE("digitalCollection");
		mainDiv.setLABEL(rootFile.getName()+"_"+UUID.randomUUID().toString());

		//iteriere über entity in structure teil des metadata.json files
		JSONParser parser = new JSONParser();

		try {     
			Object obj = parser.parse(new FileReader("metadata.json"));

			JSONObject jsonObject =  (JSONObject) obj;

			// loop array
			JSONArray structure = (JSONArray) jsonObject.get("Structure");
			Iterator<Object> iterator = structure.iterator();
			
			Map<String, Div> innerDivs = new HashMap<String, Div>();
			Map<String, Div> relationsDivs = new HashMap<String, Div>();
			
			while (iterator.hasNext()) {
				JSONObject entity = (JSONObject) iterator.next();
				String type = (String) entity.get("@type");
				if(type.equals("Entity")) {
					String pathPattern = ((String) entity.get("path")).substring(2);
					pathPattern = pathPattern.replaceAll("\\*", ".*");
					pathPattern = pathPattern.replaceAll("\\$\\{.*\\}", ".*");
					
					// path attribute ist ein pattern für den Path zum file, 
					// finde alle dateipfade unter path die den Pattern entsprechen
					for(String filePath: filePathList) {					
						String pathPat = FilenameUtils.separatorsToSystem(pathPattern);
						filePath = FilenameUtils.separatorsToSystem(filePath);
						if(Pattern.matches(pathPat, filePath)) {
							Div innerDiv = new Div();
							innerDiv.setTYPE((String) entity.get("type"));
							innerDiv.setLABEL((String) entity.get("format"));
							Fptr fprt = new Fptr();
							fprt.setFILEID("_"+FilenameUtils.getName(filePath)+"_"+UUID.randomUUID().toString());
							innerDiv.getContent().add(fprt);
							
							JSONArray relations = (JSONArray) entity.get("isRelatedTo");
							if(relations != null) {
								Div relationsDiv = new Div();
								relationsDiv.setLABEL("rel_"+UUID.randomUUID().toString());
								relationsDiv.setTYPE("entityRelation");
								
								
							}
							
							innerDivs.put(filePath, innerDiv);
						}
					}

					
					// trenne die ab, die nicht zu einem Pattern passen aber unter path sind (processedPaths für die restlichen default werte) 
					System.out.println(pathPattern);
				}
			}
			
			for(Div innerDiv: innerDivs.values()) {
				mainDiv.getContent().add(innerDiv);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}



		//		pathSubSet = self.patternMatch(normPath, self.fileMap.keys())
		//	    processedPaths += pathSubSet.keys()
		//	    for path in pathSubSet.keys():
		//	        self.entityRelMgmt(path, entity, pathSubSet[path], temp_div, temp_rel, future_rel)
		//
		//	        divMainList = []
		//	        # for each entity 
		//	        for p in temp_div:
		//	            # check if it is involved in relations
		//	            if p in future_rel:
		//	                # and with which other entities
		//	                for relPath in future_rel[p]:
		//	                    # find the related mets div and avoid to add it multiple times.
		//	                    if (relPath in temp_rel 
		//	                        and temp_rel[relPath] not in divMainList):
		//	                        divMain.append(temp_rel[relPath])
		//	                        divMainList.append(temp_rel[relPath])
		//	            # otherwise just add a single entity
		//	            elif p not in temp_rel.keys():
		//	                divMain.append(temp_div[p])
		//
		//	        # apply defaults to remaining entities, not explicitly considered in the
		//	        # metadata mapping
		//	        pp = set(processedPaths)
		//	        leftPaths = [x for x in self.fileMap.keys() if x not in pp]
		//	        for path in leftPaths:
		//	            divMain.append(self.divBuilder(self.conf.format_default, 
		//	                                           self.conf.type_default, path))

		structMap.getContent().add(mainDiv);

		return structMap;
	}

	//entityRelMgmt(self, normPath, entity, templateDict, divDict, relDict, placeHolderDict):
	//
	//# for each path a mets div is created and stored in a temp list
	//div = self.divBuilder(entity['format'], entity['type'], normPath)
	//divDict[normPath] = div
	//if 'isRelatedTo' in entity.keys():
	//  # analyze the relations of this entity with others
	//  label = 'rel_' + str(uuid.uuid4())
	//  divRel = divType(LABEL=label, TYPE="entityRelation")
	//  divRel.append(div)
	//  for relation in entity['isRelatedTo']:
	//      normPathRel = relation['@id'][2:]
	//      if len(templateDict) > 0:
	//          for tkey in templateDict.keys():
	//              normPathRel = normPathRel.replace('${'+ tkey +'}',
	//                                                templateDict[tkey])
	//      print 'normPathRel: ' + normPathRel
	//      pathSubSet = fnmatch.filter(self.fileMap.keys(), normPathRel)
	//      for path in pathSubSet:
	//          if path in divDict.keys():
	//          # if the entity is associated to an already 
	//          # defined mets div, then put the div inside 
	//          # the same div container
	//              divRel.append(divDict[path])
	//          # anyway store the relation for later checks
	//          if path in placeHolderDict:
	//              placeHolderDict[path].append(normPath)
	//          else:
	//              placeHolderDict[path] = [normPath]
	//  relDict[normPath] = divRel
	//# if this entity does not provide its own relations, check if 
	//# is related to previously defined entities.
	//if normPath in placeHolderDict.keys():
	//  for relatedPath in placeHolderDict[normPath]:
	//      relDict[relatedPath].append(div)

	private FileSec createFileSecFrom(File rootFile) {
		// Walking through the path: ' + path 
		// Using the absolute path as key or Using just the name as key
		FileSec fileSec = new FileSec();

		filePathList.add(rootFile.getAbsolutePath());

		//<ns1:fileGrp>
		FileGrp fileGrp = new FileGrp();
		if(rootFile.isDirectory()) {
			traversePath(rootFile, fileGrp);
			fileSec.getContent().add(fileGrp);
		} else {
			//<ns1:file ID="_test1.txt_714b3749-9a0b-4425-8d09-75eb2f5977a3">
			edu.harvard.hul.ois.mets.File file = new edu.harvard.hul.ois.mets.File();
			file.setID("_"+rootFile.getName()+"_"+UUID.randomUUID().toString());

			//<ns1:FLocat LOCTYPE="URL" ns2:href="file://collection_A/collection_A1/test1.txt" ns2:type="simple" />
			FLocat flocat = new FLocat();
			flocat.setLOCTYPE(Loctype.URL);
			filePathList.add(rootFile.getAbsolutePath());
			flocat.setXlinkHref(rootFile.getAbsolutePath().toString());
			flocat.setXlinkRole("simple");

			file.getContent().add(flocat);
			fileGrp.getContent().add(file);
			fileSec.getContent().add(fileGrp);
		}

		return fileSec;
	}

	private void traversePath(File fileSystemFile, FileGrp fileGrp) {
		File[] listOfFiles = fileSystemFile.listFiles();

		boolean containsFiles = false;
		FileGrp filesFileGrp = new FileGrp();
		filesFileGrp.setID(fileSystemFile.getName()+"__files__"); // <ns1:fileGrp ID="A_0__files__">

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				filePathList.add(listOfFiles[i].getAbsolutePath().toString());

				containsFiles = true;

				//<ns1:file ID="_test1.txt_714b3749-9a0b-4425-8d09-75eb2f5977a3">
				edu.harvard.hul.ois.mets.File file = new edu.harvard.hul.ois.mets.File();
				file.setID("_"+listOfFiles[i].getName()+"_"+UUID.randomUUID().toString());

				//<ns1:FLocat LOCTYPE="URL" ns2:href="file://collection_A/collection_A1/test1.txt" ns2:type="simple" />
				FLocat flocat = new FLocat();
				flocat.setLOCTYPE(Loctype.URL);
				flocat.setXlinkHref(listOfFiles[i].getAbsolutePath().toString());
				flocat.setXlinkRole("simple");

				file.getContent().add(flocat);

				filesFileGrp.getContent().add(file);

			} else if (listOfFiles[i].isDirectory()) {
				filePathList.add(listOfFiles[i].getAbsolutePath().toString());

				FileGrp internalFileGrp = new FileGrp();
				internalFileGrp.setID("_"+listOfFiles[i].getName());

				traversePath(listOfFiles[i], internalFileGrp);

				fileGrp.getContent().add(internalFileGrp);
			}
		}

		if(containsFiles) {
			fileGrp.getContent().add(filesFileGrp);
		}
	}
	
	//		JSONParser parser = new JSONParser();
	//
	//		try {     
	//			Object obj = parser.parse(new FileReader("metadata.json"));
	//
	//			JSONObject jsonObject =  (JSONObject) obj;
	//
	//			Map context = new HashMap();
	//			JsonLdOptions options = new JsonLdOptions();
	//			Map<String, Object> compact = JsonLdProcessor.compact(jsonObject, context, options);
	//
	//			String test = JsonUtils.toPrettyString(compact);
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
}
