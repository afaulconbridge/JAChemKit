package achemmicro;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.Collections2;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMolecule {
	
	@Test
	public void simpleTest() {

		Map<Coordinate,Element<String>> elements = new HashMap<>();
		elements.put(Coordinate.from(0,0), new Element<>("A"));
		Molecule<String> mol = Molecule.build(elements, new HashSet<Set<Coordinate>>());
		
		assertEquals(1, mol.getHeight());
		assertEquals(1, mol.getWidth());
		assertEquals("A", mol.getElement(Coordinate.from(0,0)));
		assertEquals("A", mol.getElement(0,0));
		
		List<String> target = new ArrayList<>();
		target.add("A");
		assertEquals(target, mol.getGraphFrom(Coordinate.from(0, 0)));
	}
	
	@Test
	public void builderTest() {
		Molecule<String> mol = new MoleculeBuilder<String>().fromElement(Coordinate.from(0,0), "A").build();	
		
		assertEquals(1, mol.getHeight());
		assertEquals(1, mol.getWidth());
	}
	
	@Test
	public void graphTest() {

		Molecule mol = new MoleculeBuilder<String>()
				.fromElement(0,1, "A")
				.fromElement(1,1, "D")
				.fromElement(2,1, "E")
				.fromElement(0,0, "B")
				.fromElement(1,2, "C")
				.fromBond(0,1, 1,1)
				.fromBond(1,1, 2,1)
				.fromBond(0,1, 0,0)
				.fromBond(1,2, 1,1)
				.build();

		List<String> target = new ArrayList<>();
		target.add("C");
		target.add("D");
		target.add("A");
		target.add("E");
		target.add("B");
		assertEquals(target, mol.getGraphFrom(1,2));
	}

	
	//TODO that bonds make a difference
	//TODO that elements make a difference 
}
