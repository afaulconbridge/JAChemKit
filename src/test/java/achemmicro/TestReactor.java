package achemmicro;

import java.util.Set;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestReactor {

	private final Logger log = Logger.getLogger(this.getClass());
	
	@Test
	public void simpleTest() {
		Molecule molA = new MoleculeBuilder().fromElement(Coordinate.from(0,0), "A").build();
		Molecule molB = new MoleculeBuilder().fromElement(Coordinate.from(0,0), "B").build();

		// make sure they are the right way around
		if (molA.compareTo(molB) > 0) {
			Molecule temp = molA;
			molA = molB;
			molB = temp;
		}
		AsciiRenderer renderer = new AsciiRenderer();
		
		Reactor reactor = new Reactor();
		Set<Reaction> reactions = reactor.getReactions(molA, molB);
		for (Reaction reaction : reactions) {
			log.info("new reaction");
			log.info(renderer.toAscii(molA));
			log.info(renderer.toAscii(molB));
			for (Molecule product : reaction.getProducts()) {
				log.info(renderer.toAscii(product));
			}
		}
	}
	@Test
	public void moderateTest() {
		Molecule molA = new MoleculeBuilder()
				.fromElement(Coordinate.from(0,0), "A")
				.fromElement(Coordinate.from(0,1), "A")
				.fromElement(Coordinate.from(1,1), "A")
				.build();
		Molecule molB = new MoleculeBuilder()
				.fromElement(Coordinate.from(0,0), "B")
				.fromElement(Coordinate.from(0,1), "B")
				.fromElement(Coordinate.from(0,2), "B")
				.build();

		// make sure they are the right way around
		if (molA.compareTo(molB) > 0) {
			Molecule temp = molA;
			molA = molB;
			molB = temp;
		}
		AsciiRenderer renderer = new AsciiRenderer();
		
		Reactor reactor = new Reactor();
		Set<Reaction> reactions = reactor.getReactions(molA, molB);
		for (Reaction reaction : reactions) {
			log.info("new reaction");
			log.info(renderer.toAscii(molA));
			log.info(renderer.toAscii(molB));
			for (Molecule product : reaction.getProducts()) {
				log.info(renderer.toAscii(product));
			}
		}
	}
}
