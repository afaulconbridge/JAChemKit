package jachemkit.hashchem.model;

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
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.builder.UndirectedGraphBuilder;
import org.jgrapht.traverse.DepthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jachemkit.core.ArtificialChemistry;

public class HashChemistry implements ArtificialChemistry<HashMolecule> {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final int BREAKDIFF = 64;

	
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
	
	private List<Byte> getHash(Graph<HashAtom,DefaultEdge> graph, HashAtom start) {
		//if the hashing is order sensitive (md5) need this to iterate in a reliable and consistent manner 
		//  this doesnt because it uses set and its hard to order arbitrary graph nodes
		//so use a order independent (commutative) hashing algorithm (e.g. addition with overflow)
		//  then we dont care about the ordering of nodes
		//    but then the structure of the graph wont matter, only content
		List<Byte> hash = new ArrayList<>();
		hash.addAll(start.value);//this might double-count first node?
		Iterator<HashAtom> it = new DepthFirstIterator<HashAtom,DefaultEdge>(graph,start);
		while(it.hasNext()) {
			HashAtom next = it.next();
			for (int i = 0; i < hash.size(); i++) {
				hash.set(i, (byte) (hash.get(i)+next.value.get(i)));
			}
		}
		return Collections.unmodifiableList(hash);
	}

	public Set<DefaultEdge> getBreakingEdges(HashMolecule mol) {
		if (!(new ConnectivityInspector<HashAtom, DefaultEdge>(mol.getStructure())).isGraphConnected()) {
			throw new IllegalArgumentException("Must be a single connected component");
		}
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
		
		Set<DefaultEdge> breakingEdges = new HashSet<>();
		for (DefaultEdge e : new HashSet<>(testGraph.edgeSet())) {
			HashAtom source = testGraph.getEdgeSource(e);
			HashAtom target = testGraph.getEdgeTarget(e);
			//test if this edge can break
			//remove it
			testGraph.removeEdge(e);
			//evaluate
			int diff = getHashDiff(getHash(testGraph, source), getHash(testGraph, target));
			//log.debug("diff = "+diff);
			if (diff > BREAKDIFF) {
				breakingEdges.add(e);
			}
			//restore edge
			testGraph.addEdge(source, target, e);
		}
		return breakingEdges;
	}
	
	public Multiset<HashMolecule> getDecompositionProducts(HashMolecule mol) {
		log.info("Starting stability test...");
		Multiset<HashMolecule> toReturn = HashMultiset.create();
		Set<DefaultEdge> brokenEdges = getBreakingEdges(mol);
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
				Multiset<HashMolecule> fragments = getDecompositionProducts(newMol);
				toReturn.addAll(fragments);			
			}
		}
		log.info("Finished stability test - "+toReturn.size()+" fragments");
		return toReturn;		
	}

}
