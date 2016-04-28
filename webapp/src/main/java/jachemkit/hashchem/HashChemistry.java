package jachemkit.hashchem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.generate.RandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jachemkit.core.ArtificialChemistry;

public class HashChemistry implements ArtificialChemistry<HashMolecule> {
	
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
	public HashMolecule createRandomMolecule(Random rng) {
		int noAtoms = 16;
		int noEdges = noAtoms + (noAtoms/2);
		HashMolecule mol = null;
		while (mol == null) {
			log.info("Generating molecule...");
			SimpleGraph<HashAtom,DefaultEdge> structure = new SimpleGraph<HashAtom,DefaultEdge>(DefaultEdge.class);
					
			GraphGenerator<HashAtom,DefaultEdge,?> gen = new RandomGraphGenerator<>(noAtoms,noEdges, rng.nextLong());
			try {
				gen.generateGraph(structure, ()-> new HashAtom(getRandomByteList(rng)), null);
				if (!(new ConnectivityInspector<HashAtom,DefaultEdge>(structure)).isGraphConnected()) {
					throw new IllegalArgumentException("Must be a single connected component");
				}
				mol = HashMolecule.createFrom(structure);
			} catch (IllegalArgumentException e) {
				//thrown either if try to randomly create self-edges (loops) or multiple components
				mol = null;
			}
		}
		return mol;
	}

}
