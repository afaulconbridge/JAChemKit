package jachemkit;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import jachemkit.hashchem.model.NeoAtom;
import jachemkit.hashchem.model.NeoMolecule;
import jachemkit.hashchem.service.NeoMoleculeEqualityTester;

@RunWith(SpringRunner.class)
public class NeoMoleculeTest {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private NeoMoleculeEqualityTester moleculeEqualityTester;
	
	@Autowired 
	private ObjectMapper objectMapper;

	@Test
	public void testPersistance() {
		

		//create a molecule
		NeoAtom a1 = new NeoAtom("[1]");
		NeoMolecule mola = new NeoMolecule(a1);
		mola.addAtom(new NeoAtom ("[2]"), a1);

		NeoAtom b1 = new NeoAtom("[1]");
		NeoMolecule molb = new NeoMolecule(b1);
		molb.addAtom(new NeoAtom ("[2]"), b1);

		assertTrue(moleculeEqualityTester.areEqual(mola, molb));
		
	}
	
}
