package achemmicro;

import org.junit.Assert;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.ImmutableSet;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MoleculeTest {
	
	@Test
	public void simpleTest() {

		Map<Coordinate,Element<String>> elements = new HashMap<>();
		elements.put(Coordinate.from(0,0), new Element<>("A"));
		Molecule<String> molA = Molecule.build(elements, new HashSet<ImmutableSet<Coordinate>>());
		
		Assert.assertEquals(1, molA.getHeight());
		Assert.assertEquals(1, molA.getWidth());
		Assert.assertEquals("A", molA.getElement(Coordinate.from(0,0)));
		Assert.assertEquals("A", molA.getElement(0,0));

		elements = new HashMap<>();
		elements.put(Coordinate.from(0,0), new Element<>("A"));
		Molecule<String> molA2 = Molecule.build(elements, new HashSet<ImmutableSet<Coordinate>>());
		
		elements = new HashMap<>();
		elements.put(Coordinate.from(0,0), new Element<>("B"));
		Molecule<String> molB = Molecule.build(elements, new HashSet<ImmutableSet<Coordinate>>());

		Assert.assertTrue("molA2 must equal molA", molA2.equals(molA));
		Assert.assertFalse("A must not equal B", molA.equals(molB));
		Assert.assertTrue("Hashcodes should be consistent", molA.hashCode() == molA2.hashCode());
		Assert.assertFalse("Hashcodes should be different", molA.hashCode() == molB.hashCode());
		Assert.assertEquals("Comparison should match equals", 0, molA.compareTo(molA2));
		Assert.assertNotEquals("Comparison should match equals", 0, molA.compareTo(molB));
		Assert.assertTrue("Comparisons should be consistent", molA.compareTo(molA2) == molA2.compareTo(molA));
		Assert.assertEquals("Comparisons should be reciprocal", molA.compareTo(molB), -(molB.compareTo(molA)));
	}
	
	@Test
	public void builderTest() {
		Molecule<String> mol = new MoleculeBuilder<String>().fromElement(Coordinate.from(0,0), "A").build();	
		
		Assert.assertEquals(1, mol.getHeight());
		Assert.assertEquals(1, mol.getWidth());
	}
		
	//TODO that bonds make a difference
	//TODO that elements make a difference 
}
