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
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

@Component
public class HashChemRunner implements CommandLineRunner {

	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void run(String... args) throws Exception {

		Random rng = new Random(42);
		
		HashChemistry hashChemistry = new HashChemistry();
		
		//create a random molecule
		
		for (int i=0; i < 128; i++) {
			HashMolecule mol = hashChemistry.createRandomMolecule(rng);
			//test if it is stable
			//getBreakingEdges(mol);
			testStability(mol);
			log.info("Finished molecule");
		}
		
		/*
		Molecule<List<Byte>> mol = new Molecule<>();
		Atom<List<Byte>> a = new Atom<List<Byte>>(getRandomByteList(rng));
		Atom<List<Byte>> b = new Atom<List<Byte>>(getRandomByteList(rng));
		mol.addVertex(a);
		mol.addVertex(b);
		mol.addEdge(a,b);
		*/
		
		/*
		SimpleGraph<Atom<String>, DefaultEdge> molA = new SimpleGraph<>(DefaultEdge.class);
		SimpleGraph<Atom<String>, DefaultEdge> molB = new SimpleGraph<>(DefaultEdge.class);
		
		molA.addVertex(new Atom<String>("A"));
		molB.addVertex(new Atom<String>("B"));
		
		//now do an actual reaction
		for (Atom<String> atomA: molA.vertexSet()) {
			List<Byte> hashA = getHash(molA, atomA);
			for (Atom<String> atomB: molB.vertexSet()) {
				//should probably do this outside of the inner loop
				List<Byte> hashB = getHash(molB, atomB);
				Integer score = getHashDiff(hashA, hashB);
				log.info("score = "+score);
			}
		}
		*/
	}
	
	private Multiset<HashMolecule> testStability(HashMolecule mol) {
		log.info("Starting stability test...");
		Multiset<HashMolecule> toReturn = HashMultiset.create();
		Set<DefaultEdge> brokenEdges = mol.getBreakingEdges();
		log.info(""+brokenEdges.size()+" edges broke");
		if (brokenEdges.size() == 0) {
			//nothing broke
			log.info("Molecule is stable");
			toReturn.add(mol);
		} else {
			//something broke
			//create a simple graph so we can modify it
			SimpleGraph<HashAtom,DefaultEdge> testGraph = new SimpleGraph<HashAtom,DefaultEdge>(DefaultEdge.class);
			for (HashAtom atom : mol.getStructure().vertexSet()) {
				testGraph.addVertex(atom);
			}
			for (DefaultEdge e : mol.getStructure().edgeSet()) {
				HashAtom sourceAtom = mol.getStructure().getEdgeSource(e);
				HashAtom targetAtom = mol.getStructure().getEdgeTarget(e);
				testGraph.addEdge(sourceAtom, targetAtom, e);
			}
			
			testGraph.removeAllEdges(brokenEdges);
			
			ConnectivityInspector<HashAtom,DefaultEdge> connectivityInspector = new ConnectivityInspector<HashAtom,DefaultEdge>(testGraph);
			List<Set<HashAtom>> atomSets = connectivityInspector.connectedSets();
			log.info("Molecule broke into "+atomSets.size());
			for (Set<HashAtom> atomSet : atomSets) {
				log.info("Fragment has "+atomSet.size()+" atoms in it");
				//create a new molecule from this set
				UndirectedGraphBuilder<HashAtom,DefaultEdge,SimpleGraph<HashAtom,DefaultEdge>> builder = 
						new UndirectedGraphBuilder<>(new SimpleGraph<HashAtom,DefaultEdge>(DefaultEdge.class));
				
				Set<DefaultEdge> edgeSet = new HashSet<>();
				for (HashAtom atom : atomSet) {
					builder.addVertex(atom);
					edgeSet.addAll(testGraph.edgesOf(atom));
				}
				
				log.info("Fragment has "+edgeSet.size()+" edges in it");
				for (DefaultEdge edge : edgeSet) {
					builder.addEdge(testGraph.getEdgeSource(edge), testGraph.getEdgeTarget(edge));
				}
				
				HashMolecule newMol = HashMolecule.createFrom(builder.build());
				
				if (newMol.equals(mol)) {
					throw new IllegalArgumentException("no difference after broken");
				}
				
				//recursively test stability of broken fragments
				Multiset<HashMolecule> fragments = testStability(newMol);
				toReturn.addAll(fragments);			
			}
		}
		log.info("Finished stability test - "+toReturn.size()+" fragments");
		return toReturn;		
	}
}
