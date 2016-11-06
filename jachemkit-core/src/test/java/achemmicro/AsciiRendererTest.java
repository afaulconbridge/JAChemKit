package achemmicro;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class AsciiRendererTest {

	private final Logger log = Logger.getLogger(this.getClass());
	
	@Test
	public void simpleTest() {
		Molecule<String> mol = new MoleculeBuilder<String>().fromElement(Coordinate.from(0,0), "A").build();
		
		AsciiRenderer renderer = new AsciiRenderer();
		log.info("\n"+renderer.toAscii(mol));
		assertEquals("+-+\n"
				   + "|A|\n"
				   + "+-+", renderer.toAscii(mol));

		mol = new MoleculeBuilder<String>()
				.fromElement(Coordinate.from(0,1), "A")
				.fromElement(Coordinate.from(1,1), "A")
				.fromElement(Coordinate.from(2,1), "A")
				.fromElement(Coordinate.from(0,0), "B")
				.fromElement(Coordinate.from(2,2), "B")
				.fromBond(0,0, 0,1)
				.fromBond(0,1, 1,1)
				.fromBond(1,1, 2,1)
				.fromBond(2,1, 2,2)
				.build();
		assertEquals("B", mol.getElement(0, 0));		
		log.info("\n"+renderer.toAscii(mol));
		assertEquals("+---+\n"
				   + "|B  |\n"
				   + "|AAA|\n"
				   + "|  B|\n"
				   + "+---+", renderer.toAscii(mol));
	}
}
