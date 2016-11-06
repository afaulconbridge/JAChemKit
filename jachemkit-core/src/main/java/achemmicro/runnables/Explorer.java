package achemmicro.runnables;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.UnmodifiableIterator;

import achemmicro.AsciiRenderer;
import achemmicro.Molecule;
import achemmicro.MoleculeBuilder;
import achemmicro.Reaction;
import achemmicro.Reactor;
import achemmicro.bonding.LocalGraphBondTester;

public class Explorer {

	private static final Logger log = Logger.getLogger(Explorer.class);
	
	private static AsciiRenderer asciiRenderer = new AsciiRenderer();

	public static void main(String[] args) {

		// create a reactor
		Reactor<String> reactor = new Reactor<>(new LocalGraphBondTester<String>());
		// create some initial molecules
		Set<Molecule<String>> molecularSpecies = new HashSet<>();
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "A").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "B").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "C").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "D").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "E").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "F").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "G").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "H").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "I").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "J").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "K").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "L").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "M").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "N").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "O").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "P").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "Q").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "R").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "S").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "T").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "U").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "V").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "W").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "X").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "Y").build());
		molecularSpecies.add(new MoleculeBuilder<String>().fromElement(0, 0, "Z").build());
		// create a queue of reactants to test
		// TODO multithread safe and finite size here
		Queue<ImmutableMultiset<Molecule<String>>> reactantsQueue = new LinkedList<>();
		for (Molecule<String> molA : molecularSpecies) {
			for (Molecule<String> molB : molecularSpecies) {
				ImmutableMultiset<Molecule<String>> reactants = ImmutableMultiset.of(molA, molB);
				// this might be a slow check...
				if (!reactantsQueue.contains(reactants)) {
					reactantsQueue.add(reactants);
				}
			}
		}
		// main processing loop here
		while (!reactantsQueue.isEmpty()) {
			ImmutableMultiset<Molecule<String>> reactants = reactantsQueue.poll();
			UnmodifiableIterator<Molecule<String>> iter = reactants.iterator();
			Molecule<String> molA = iter.next();
			Molecule<String> molB = iter.next();
			for (Reaction<String> reaction : reactor.getReactions(molA, molB)) {
				for (Molecule<String> product : reaction.getProducts()) {
					//check if its not been seen before
					if (!molecularSpecies.contains(product)) {
						molecularSpecies.add(product);
						//print it
						log.info(asciiRenderer.toAscii(product));
						//add all possible reactions involving it
						for (Molecule<String> molC : molecularSpecies) {
							reactantsQueue.add(ImmutableMultiset.of(product, molC));							
						}
						reactantsQueue.add(ImmutableMultiset.of(product, product));
					}
				}
			}
		}
	}

}
