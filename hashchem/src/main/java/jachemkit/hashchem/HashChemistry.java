package jachemkit.hashchem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.generate.RandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jachemkit.core.ArtificialChemistry;
import jachemkit.core.Atom;
import jachemkit.core.Molecule;

public class HashChemistry implements ArtificialChemistry {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	
	private List<Byte> getRandomByteList(Random rng) {
		byte[] hash = new byte[8];
		rng.nextBytes(hash);
		List<Byte> listHash = new ArrayList<>(hash.length);
		for (int i = 0; i < hash.length; i++) {
			listHash.add(new Byte(hash[i]));
		}
		return Collections.unmodifiableList(listHash);
	}
	
	@Override
	public Molecule<List<Byte>> createRandomMolecule(Random rng) {
		Molecule<List<Byte>> mol = null;
		while (mol == null) {
			log.info("Generating molecule...");
			mol = new Molecule<>();
			GraphGenerator<Atom<List<Byte>>,DefaultEdge,?> gen = new RandomGraphGenerator<>(16,24, rng.nextLong());
			try {
				gen.generateGraph(mol, ()-> new Atom<>(getRandomByteList(rng)), null);
				if (!(new ConnectivityInspector<Atom<List<Byte>>,DefaultEdge>(mol)).isGraphConnected()) {
					throw new IllegalArgumentException("Must be a single connected component");
				}
			} catch (IllegalArgumentException e) {
				//thrown either if try to randomly create self-edges (loops) or multiple components
				mol = null;
			}
		}
		return mol;
	}

}
