package jachemkit.hashchem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.generate.GraphGenerator;
import org.jgrapht.generate.RandomGraphGenerator;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.builder.UndirectedGraphBuilder;
import org.jgrapht.graph.builder.UndirectedGraphBuilderBase;
import org.jgrapht.traverse.DepthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jachemkit.core.Atom;
import jachemkit.core.Molecule;

@Component
public class HashChemRunner implements CommandLineRunner {

	private static final int BREAKDIFF = 64;
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Override
	public void run(String... args) throws Exception {

		Random rng = new Random(42);
		
		HashChemistry hashChemistry = new HashChemistry();
		
		//create a random molecule
		
		for (int i=0; i < 128; i++) {
			Molecule<List<Byte>> mol = hashChemistry.createRandomMolecule(rng);
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
	
	private Multiset<Molecule<List<Byte>>> testStability(Molecule<List<Byte>> mol) {
		log.info("Starting stability test...");
		log.info("Old molecule has "+mol.edgeSet().size()+" edges");
		log.info("Old molecule has "+mol.vertexSet().size()+" atoms");
		Multiset<Molecule<List<Byte>>> toReturn = HashMultiset.create();
		Set<DefaultEdge> brokenEdges = getBreakingEdges(mol);
		log.info(""+brokenEdges.size()+" edges broke");
		if (brokenEdges.size() == 0) {
			//nothing broke
			log.info("Molecule is stable");
			toReturn.add(mol);
		} else {
			//something broke
			mol.removeAllEdges(brokenEdges);
			
			ConnectivityInspector<Atom<List<Byte>>,DefaultEdge> connectivityInspector = new ConnectivityInspector<>(mol);
			List<Set<Atom<List<Byte>>>> atomSets = connectivityInspector.connectedSets();
			log.info("Molecule broke into "+atomSets.size());
			for (Set<Atom<List<Byte>>> atomSet : atomSets ) {
				log.info("Fragment has "+atomSet.size()+" atoms in it");
				//create a new molecule from this set
				UndirectedGraphBuilder<Atom<List<Byte>>,DefaultEdge,Molecule<List<Byte>>> builder = 
						new UndirectedGraphBuilder<>(new Molecule<List<Byte>>());
				
				Set<DefaultEdge> edgeSet = new HashSet<>();
				for (Atom<List<Byte>> atom : atomSet) {
					builder.addVertex(atom);
					edgeSet.addAll(mol.edgesOf(atom));
				}
				
				log.info("Fragment has "+edgeSet.size()+" edges in it");
				for (DefaultEdge edge : edgeSet) {
					builder.addEdge(mol.getEdgeSource(edge), mol.getEdgeTarget(edge));
				}
				
				Molecule<List<Byte>> newMol = builder.build();
				log.info("New molecule has "+newMol.edgeSet().size()+" edges");
				log.info("New molecule has "+newMol.vertexSet().size()+" atoms");
				
				if (newMol.equals(mol)) {
					throw new IllegalArgumentException("no difference after broken");
				}
				
				//recursively test stability of broken fragments
				Multiset<Molecule<List<Byte>>> fragments = testStability(newMol);
				toReturn.addAll(fragments);			
			}
		}
		log.info("Finished stability test - "+toReturn.size()+" fragments");
		return toReturn;		
	}
	
	private Set<DefaultEdge> getBreakingEdges(Molecule<List<Byte>> mol) {
		if (!(new ConnectivityInspector(mol)).isGraphConnected()) {
			throw new IllegalArgumentException("Must be a single connected component");
		}
		
		Set<DefaultEdge> breakingEdges = new HashSet<>();
		for (DefaultEdge e : new HashSet<>(mol.edgeSet())) {
			Atom<List<Byte>> source = mol.getEdgeSource(e);
			Atom<List<Byte>> target = mol.getEdgeTarget(e);
			//test if this edge can break
			//remove it
			mol.removeEdge(e);
			//evaluate
			int diff = getHashDiff(getHash(mol, source), getHash(mol, target));
			//log.debug("diff = "+diff);
			if (diff < BREAKDIFF) {
				breakingEdges.add(e);
			}
			//restore edge
			mol.addEdge(source, target, e);
		}
		return breakingEdges;
	}
	
	private Integer getHashDiff(List<Byte> hashA, List<Byte> hashB) {
		//TODO check lengths
		Integer total = 0;
		for (Byte b : hashA) {
			total += b;
		}
		for (Byte b : hashB) {
			total -= b;
		}
		return Math.abs(total);
	}
	
	private List<Byte> getHash(Graph<Atom<List<Byte>>,DefaultEdge> graph, Atom<List<Byte>> start) {
		//if the hashing is order sensitive (md5) need this to iterate in a reliable and consistent manner 
		//  this doesnt because it uses set and its hard to order arbitrary graph nodes
		//so use a order independent (commutative) hashing algorithm (e.g. addition with overflow)
		//  then we dont care about the ordering of nodes
		//    but then the structure of the graph wont matter, only content
		List<Byte> hash = new ArrayList<>();
		hash.addAll(start.body);
		Iterator<Atom<List<Byte>>> it = new DepthFirstIterator<Atom<List<Byte>>,DefaultEdge>(graph,start);
		while(it.hasNext()) {
			Atom<List<Byte>> next = it.next();
			for (int i = 0; i < hash.size(); i++) {
				hash.set(i, (byte) (hash.get(i)+next.body.get(i)));
			}
		}
		return Collections.unmodifiableList(hash);
	}
}
