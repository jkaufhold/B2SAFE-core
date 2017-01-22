/**
 * 
 */
package eu.eudat.b2safe.graphmanager;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import org.jglue.cdiunit.AdditionalClasses;
import org.jglue.cdiunit.CdiRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.neo4j.ogm.session.Session;
import org.neo4j.ogm.session.SessionFactory;
import org.neo4j.ogm.transaction.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.harvard.hul.ois.mets.File;
import edu.harvard.hul.ois.mets.FileGrp;
import edu.harvard.hul.ois.mets.FileSec;
import edu.harvard.hul.ois.mets.Mets;
import edu.harvard.hul.ois.mets.helper.MetsReader;
import eu.eudat.b2safe.graphmanager.internal.producer.SessionFactoryProducer;
import eu.eudat.b2safe.graphmanager.model.node.Aggregation;
import eu.eudat.b2safe.graphmanager.model.node.FileEntity;
import eu.eudat.b2safe.graphmanager.model.relation.Contains;

/**
 * @author Julia Kaufhold
 *
 */
@RunWith(CdiRunner.class)
@AdditionalClasses({
	SessionFactoryProducer.class
})
public class GraphManagerDemo {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Inject
	private SessionFactory factory;
	
	@Inject
	private Session session;

	@Test
	public void importTest() {

		// create instance of GraphManager
		// initialize GraphManager with infos from config file about:
		// logging
		// connection to iRODS
		
		clearGraph();
		
		final Transaction transaction = session.beginTransaction();
		
		// create Nodes: User, Zone, Resource, from iRODS
		createInitialGraph();
		
		try (InputStream in = getClass().getResourceAsStream("/eudat_mets_example_2.xml")) {
			Mets mets = Mets.reader(new MetsReader(in));

			for (Object element : mets.getContent().toArray()) {
				if (element instanceof FileSec) {
					FileSec fSecElement = (FileSec) element;
					parseMetsElement(fSecElement);
				}
				
//				if (element instanceof StructMap) {
//					StructMap s = (StructMap) element;
//					for (Object sMapElement : mets.getContent().toArray()) {
//						if (sMapElement instanceof Div) {
//							((Div) sMapElement).getLABEL();
//						}
//					}
//
//				}
			}
			transaction.commit();
		} catch (Exception e) {
			log.error("Exception during graph creation.",e);
		}
		factory.close();
	}

	private void clearGraph() {
		try (Transaction t = session.beginTransaction()) {
			//delete all entities in the graph
			session.purgeDatabase();
			t.commit();
		}
	}

	private void parseMetsElement(FileSec current) {
		for (Object element : current.getContent().toArray()) {
			if(element instanceof FileGrp) {
				parseMetsElement(null, (FileGrp)element);
			} else if(element instanceof File) {
				parseMetsFile(null, (File)element);
			}
		}
	}
	
	private void parseMetsElement(Aggregation parent, FileGrp current) {
		Aggregation aggregation = new Aggregation();
		Aggregation newparent = null;
		if(current.getID() != null) {
			aggregation.setName(current.getID());
			
			if(parent != null) {
				Contains.connect(parent, aggregation);
			}
			
			session.save(aggregation);
			
			Map<String,Object> fields = new HashMap<>();
			fields.put("name", current.getID());
			Iterable<Aggregation> query = session.query(Aggregation.class,"MATCH (n:Aggregation {name: {name}}) RETURN n", fields);
			
			newparent = query.iterator().next();	
		}
		
		for (Object element : current.getContent().toArray()) {
			if(element instanceof FileGrp) {
				parseMetsElement(newparent, (FileGrp)element);
			} else if(element instanceof File) {
				parseMetsFile(newparent, (File)element);
			}
		}
	}

	private void parseMetsFile(Aggregation parent, File element) {	
		FileEntity file = new FileEntity();
		file.setName(element.getID());
		
		if(parent != null) {
			Contains.connect(parent, file);
		}
		
		session.save(file);
	}

	private void createInitialGraph() {
		// create Node "Zone" with name, endpoint from config file
		// Node("Zone", name=self.conf.irods_zone_name, endpoint=self.conf.irods_zone_ep)

		// create Nodes "Resource" with name, path from config file and Relationship IS_AVAILABLE_IN
		// Node("Resource", name=res_name, path=res_path)
		// Relationship(self.resNode, "IS_AVAILABLE_IN", self.zone)
		
		// create Node "User" for all users from irods with name, id? irods id?
	}
}
