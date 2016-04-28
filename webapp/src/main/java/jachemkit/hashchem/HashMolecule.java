package jachemkit.hashchem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.ConnectivityInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.jgrapht.traverse.DepthFirstIterator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class HashMolecule {
	
	private static final int BREAKDIFF = 64;
	
	@JsonSerialize(using = UnmodifiableUndirectedGraphSerializer.class)
	@JsonDeserialize(using = HashMoleculeStructureDeserializer.class)
	private UnmodifiableUndirectedGraph<HashAtom, DefaultEdge> structure;
	
	private HashMolecule(){
		
	}
	
	public UnmodifiableUndirectedGraph<HashAtom, DefaultEdge> getStructure() {
		return structure;
	}
	
	@JsonIgnore
	public Set<DefaultEdge> getBreakingEdges() {
		if (!(new ConnectivityInspector<HashAtom, DefaultEdge>(this.structure)).isGraphConnected()) {
			throw new IllegalArgumentException("Must be a single connected component");
		}
		//create a simple graph so we can modify it
		SimpleGraph<HashAtom,DefaultEdge> testGraph = new SimpleGraph<HashAtom,DefaultEdge>(DefaultEdge.class);
		for (HashAtom atom : structure.vertexSet()) {
			testGraph.addVertex(atom);
		}
		for (DefaultEdge e : structure.edgeSet()) {
			HashAtom sourceAtom = structure.getEdgeSource(e);
			HashAtom targetAtom = structure.getEdgeTarget(e);
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
			if (diff < BREAKDIFF) {
				breakingEdges.add(e);
			}
			//restore edge
			testGraph.addEdge(source, target, e);
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
	
	public static HashMolecule createFrom(SimpleGraph<HashAtom,DefaultEdge> structure) {
		//create a new simple graph
		SimpleGraph<HashAtom,DefaultEdge> newStructure = new SimpleGraph<HashAtom,DefaultEdge>(DefaultEdge.class);
		for (HashAtom atom : structure.vertexSet()) {
			newStructure.addVertex(atom);
		}
		for (DefaultEdge e : structure.edgeSet()) {
			HashAtom sourceAtom = structure.getEdgeSource(e);
			HashAtom targetAtom = structure.getEdgeTarget(e);
			newStructure.addEdge(sourceAtom, targetAtom);
		}
		//now bake it into an unmodifiable one
		HashMolecule mol = new HashMolecule();
		mol.structure = new UnmodifiableUndirectedGraph<HashAtom, DefaultEdge>(newStructure);
		return mol;
	}
}
