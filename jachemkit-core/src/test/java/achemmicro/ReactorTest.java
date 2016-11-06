package achemmicro;


import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ReactorTest {

	private final Logger log = Logger.getLogger(this.getClass());
	
	@Test
	public void simpleTest() {
		Molecule<String> molA = new MoleculeBuilder<String>().fromElement(0,0, "A").build();
		Molecule<String> molB = new MoleculeBuilder<String>().fromElement(0,0, "B").build();
		ImmutableMultiset<Molecule<String>> reactants = ImmutableMultiset.of(molA, molB);
		AsciiRenderer renderer = new AsciiRenderer();
		Reactor<String> reactor = new Reactor<>(new LocalGraphBondTester<String>());
		Multiset<Reaction<String>> reactions = reactor.getReactions(molA, molB);
		for (Reaction<String> reaction : reactions) {
			log.info("new reaction");
			log.info(renderer.toAscii(molA)+"\n"+renderer.toAscii(molB));
			log.info(renderer.toAscii(reaction.getIntermediate()));
			StringBuilder sb = new StringBuilder();
			for (Molecule<String> product : reaction.getProducts()) {
				sb.append(renderer.toAscii(product));
			}
			log.info(sb.toString());
			
			Assert.assertEquals("Reactants must be preserved", reactants, reaction.getReactants());
		}
		//TODO finish test
	}
	
	@Test
	public void moderateTest() {
		Molecule<String> molA = new MoleculeBuilder<String>()
				.fromElement(0,0, "A")
				.fromElement(0,1, "A")
				.fromBond(0,0, 0,1)
				.fromElement(1,1, "A")
				.fromBond(0,1, 1,1)
				.build();
		Molecule<String> molB = new MoleculeBuilder<String>()
				.fromElement(0,0, "B")
				.fromElement(1,0, "B")
				.fromBond(0,0, 1,0)
				.fromElement(2,0, "B")
				.fromBond(1,0, 2,0)
				.build();

		// make sure they are the right way around
		if (molA.compareTo(molB) > 0) {
			Molecule<String> temp = molA;
			molA = molB;
			molB = temp;
		}
		AsciiRenderer renderer = new AsciiRenderer();
		
		Reactor<String> reactor = new Reactor<>(new LocalGraphBondTester<String>());
		Multiset<Reaction<String>> reactions = reactor.getReactions(molA, molB);
		for (Reaction<String> reaction : reactions) {
			log.info("new reaction");
			log.info(renderer.toAscii(molA)+"\n"+renderer.toAscii(molB));
			log.info(renderer.toAscii(reaction.getIntermediate()));
			for (Molecule<String> product : reaction.getProducts()) {
				log.info(renderer.toAscii(product));
			}
		}
	}
}
