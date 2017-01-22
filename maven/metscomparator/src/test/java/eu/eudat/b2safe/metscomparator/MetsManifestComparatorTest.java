package eu.eudat.b2safe.metscomparator;

import java.io.IOException;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import eu.eudat.b2safe.metscomparator.model.MetsManifestDiff;

public class MetsManifestComparatorTest {

	@Test
	public void testCompare() throws JsonProcessingException {
		String pathToOldManifest = "/manifest.xml";
		String pathToNewManifest = "/manifest_changed.xml";
		
		MetsManifestComparator comparator = new MetsManifestComparator();
		
		MetsManifestDiff diff = comparator.compareMetsManifests(pathToOldManifest, pathToNewManifest);

		try {
			new ObjectMapper().writer(new DefaultPrettyPrinter()).writeValue(System.out, diff);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//curl http://localhost:8080/graph[?limit=50]
	}
}
