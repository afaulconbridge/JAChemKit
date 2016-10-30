package achemmicro;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestMolecule {
	
	@Test
	public void simpleTest() {

		Map<Coordinate,String> elements = new HashMap<>();
		elements.put(Coordinate.from(0,0), "A");
		Molecule mol = Molecule.build(elements);
		
		assertEquals(1, mol.getHeight());
		assertEquals(1, mol.getWidth());
		assertEquals("A", mol.getElement(Coordinate.from(0,0)).get());
		assertEquals("A", mol.getElement(0,0).get());
		
		assertEquals("A", mol.getGraphFrom(Coordinate.from(0, 0)));
	}
	
	@Test
	public void builderTest() {
		Molecule mol = new MoleculeBuilder().fromElement(Coordinate.from(0,0), "A").build();	
		
		assertEquals(1, mol.getHeight());
		assertEquals(1, mol.getWidth());
	}
	
	@Test
	public void graphTest() {

		Molecule mol = new MoleculeBuilder()
				.fromElement(Coordinate.from(0,1), "A")
				.fromElement(Coordinate.from(1,1), "D")
				.fromElement(Coordinate.from(2,1), "E")
				.fromElement(Coordinate.from(0,0), "B")
				.fromElement(Coordinate.from(1,2), "C")
				.build();
		
		assertEquals("CDAEB", mol.getGraphFrom(1,2));
	}

}
