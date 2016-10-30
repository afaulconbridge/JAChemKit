package achemmicro;

import static org.junit.Assert.*;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestAsciiRenderer {

	private final Logger log = Logger.getLogger(this.getClass());
	
	@Test
	public void simpleTest() {
		Molecule mol = new MoleculeBuilder().fromElement(Coordinate.from(0,0), "A").build();
		
		AsciiRenderer renderer = new AsciiRenderer();
		log.info("\n"+renderer.toAscii(mol));
		assertEquals("+-+\n"
				   + "|A|\n"
				   + "+-+", renderer.toAscii(mol));

		mol = new MoleculeBuilder()
				.fromElement(Coordinate.from(0,1), "A")
				.fromElement(Coordinate.from(1,1), "A")
				.fromElement(Coordinate.from(2,1), "A")
				.fromElement(Coordinate.from(0,0), "B")
				.fromElement(Coordinate.from(2,2), "B")
				.build();
		log.info("\n"+renderer.toAscii(mol));
		assertEquals("+---+\n"
				   + "|B  |\n"
				   + "|AAA|\n"
				   + "|  B|\n"
				   + "+---+", renderer.toAscii(mol));
	}
}
