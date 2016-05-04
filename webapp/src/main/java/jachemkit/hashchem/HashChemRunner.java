package jachemkit.hashchem;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.builder.UndirectedGraphBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jachemkit.hashchem.model.HashChemistry;
import jachemkit.hashchem.model.HashMolecule;

@Component
public class HashChemRunner implements CommandLineRunner {

	private Logger log = LoggerFactory.getLogger(this.getClass());
	

	@Autowired
	private HashChemistry hashChemistry;
	
	@Override
	public void run(String... args) throws Exception {

		Random rng = new Random(42);	
		
		//create a random molecule		
		for (int i=0; i < 128; i++) {
			HashMolecule mol = hashChemistry.createRandomMolecule(rng);
			//test if it is stable
			//getBreakingEdges(mol);
			hashChemistry.getDecompositionProducts(mol);
			log.info("Finished molecule");
		}
	}
	
}
