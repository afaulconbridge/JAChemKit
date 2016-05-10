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
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.builder.UndirectedGraphBuilder;
import org.jgrapht.traverse.DepthFirstIterator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multiset;

import jachemkit.core.ArtificialChemistry;
import jachemkit.hashchem.neo.NeoAtom;
import jachemkit.hashchem.neo.NeoMolecule;

@Component
public class HashChemistry implements ArtificialChemistry<NeoMolecule> {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	private static final int BREAKDIFF = 64;

	
	private ImmutableList<Integer> getRandomByteList(Random rng) {
		byte[] hash = new byte[8];
		rng.nextBytes(hash);
		List<Integer> listHash = new ArrayList<>(hash.length);
		for (int i = 0; i < hash.length; i++) {
			listHash.add(new Integer(hash[i]));
		}
		return ImmutableList.copyOf(listHash);
	}
	
	@Override
	public NeoMolecule createRandomMolecule(Random rng) {
		int noAtoms = 16;
		int noEdges = noAtoms + (noAtoms/2);
		NeoMolecule mol = null;
		while (mol == null) {
			log.info("Generating molecule...");
			SimpleGraph<NeoAtom,DefaultEdge> structure = new SimpleGraph<NeoAtom,DefaultEdge>(DefaultEdge.class);
					
			GraphGenerator<NeoAtom,DefaultEdge,?> gen = new RandomGraphGenerator<>(noAtoms,noEdges, rng.nextLong());
			try {
				gen.generateGraph(structure, ()-> new NeoAtom(getRandomByteList(rng)), null);
				if (!(new ConnectivityInspector<NeoAtom,DefaultEdge>(structure)).isGraphConnected()) {
					throw new IllegalArgumentException("Must be a single connected component");
				}
				mol = NeoMolecule.createFrom(structure);
			} catch (IllegalArgumentException e) {
				//thrown either if try to randomly create self-edges (loops) or multiple components
				mol = null;
			}
		}
		return mol;
	}

	
	private Integer getHashDiff(List<Integer> hashA, List<Integer> hashB) {
		//TODO check lengths
		Integer total = 0;
		for (Integer b : hashA) {
			total += b;
		}
		for (Integer b : hashB) {
			total -= b;
		}
		return Math.abs(total);
	}
	
	private List<Integer> getHash(Graph<NeoAtom,DefaultEdge> graph, NeoAtom start) {
		//if the hashing is order sensitive (md5) need this to iterate in a reliable and consistent manner 
		//  this doesnt because it uses set and its hard to order arbitrary graph nodes
		//so use a order independent (commutative) hashing algorithm (e.g. addition with overflow)
		//  then we dont care about the ordering of nodes
		//    but then the structure of the graph wont matter, only content
		List<Integer> hash = new ArrayList<>();
		hash.addAll(start.getValue());//this might double-count first node?
		Iterator<NeoAtom> it = new DepthFirstIterator<NeoAtom,DefaultEdge>(graph,start);
		while(it.hasNext()) {
			NeoAtom next = it.next();
			for (int i = 0; i < hash.size(); i++) {
				//TODO some sort of rollover?
				hash.set(i, hash.get(i)+next.getValue().get(i));
			}
		}
		return Collections.unmodifiableList(hash);
	}

	public Set<DefaultEdge> getBreakingEdges(NeoMolecule mol) {
		if (!(new ConnectivityInspector<NeoAtom, DefaultEdge>(mol.getStructure())).isGraphConnected()) {
			throw new IllegalArgumentException("Must be a single connected component");
		}
		//create a simple graph so we can modify it
		SimpleGraph<NeoAtom,DefaultEdge> testGraph = new SimpleGraph<NeoAtom,DefaultEdge>(DefaultEdge.class);
		for (NeoAtom atom : mol.getStructure().vertexSet()) {
			testGraph.addVertex(atom);
		}
		for (DefaultEdge e : mol.getStructure().edgeSet()) {
			NeoAtom sourceAtom = mol.getStructure().getEdgeSource(e);
			NeoAtom targetAtom = mol.getStructure().getEdgeTarget(e);
			testGraph.addEdge(sourceAtom, targetAtom, e);
		}
		
		Set<DefaultEdge> breakingEdges = new HashSet<>();
		for (DefaultEdge e : new HashSet<>(testGraph.edgeSet())) {
			NeoAtom source = testGraph.getEdgeSource(e);
			NeoAtom target = testGraph.getEdgeTarget(e);
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
	
	public Multiset<NeoMolecule> getDecompositionProducts(NeoMolecule mol) {
		log.info("Starting stability test...");
		Multiset<NeoMolecule> toReturn = HashMultiset.create();
		Set<DefaultEdge> brokenEdges = getBreakingEdges(mol);
		log.info(""+brokenEdges.size()+" edges broke");
		if (brokenEdges.size() == 0) {
			//nothing broke
			log.info("Molecule is stable");
			toReturn.add(mol);
		} else {
			//something broke
			//create a simple graph so we can modify it
			SimpleGraph<NeoAtom,DefaultEdge> testGraph = new SimpleGraph<NeoAtom,DefaultEdge>(DefaultEdge.class);
			for (NeoAtom atom : mol.getStructure().vertexSet()) {
				testGraph.addVertex(atom);
			}
			for (DefaultEdge e : mol.getStructure().edgeSet()) {
				NeoAtom sourceAtom = mol.getStructure().getEdgeSource(e);
				NeoAtom targetAtom = mol.getStructure().getEdgeTarget(e);
				testGraph.addEdge(sourceAtom, targetAtom, e);
			}
			
			testGraph.removeAllEdges(brokenEdges);
			
			ConnectivityInspector<NeoAtom,DefaultEdge> connectivityInspector = new ConnectivityInspector<NeoAtom,DefaultEdge>(testGraph);
			List<Set<NeoAtom>> atomSets = connectivityInspector.connectedSets();
			log.info("Molecule broke into "+atomSets.size());
			for (Set<NeoAtom> atomSet : atomSets) {
				log.info("Fragment has "+atomSet.size()+" atoms in it");
				//create a new molecule from this set
				UndirectedGraphBuilder<NeoAtom,DefaultEdge,SimpleGraph<NeoAtom,DefaultEdge>> builder = 
						new UndirectedGraphBuilder<>(new SimpleGraph<NeoAtom,DefaultEdge>(DefaultEdge.class));
				
				Set<DefaultEdge> edgeSet = new HashSet<>();
				for (NeoAtom atom : atomSet) {
					builder.addVertex(atom);
					edgeSet.addAll(testGraph.edgesOf(atom));
				}
				
				log.info("Fragment has "+edgeSet.size()+" edges in it");
				for (DefaultEdge edge : edgeSet) {
					builder.addEdge(testGraph.getEdgeSource(edge), testGraph.getEdgeTarget(edge));
				}
				
				NeoMolecule newMol = NeoMolecule.createFrom(builder.build());
				
				if (newMol.equals(mol)) {
					throw new IllegalArgumentException("no difference after broken");
				}
				
				//recursively test stability of broken fragments
				Multiset<NeoMolecule> fragments = getDecompositionProducts(newMol);
				toReturn.addAll(fragments);			
			}
		}
		log.info("Finished stability test - "+toReturn.size()+" fragments");
		return toReturn;		
	}

}
