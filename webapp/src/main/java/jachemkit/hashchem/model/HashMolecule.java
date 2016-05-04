package jachemkit.hashchem.model;

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
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import jachemkit.hashchem.HashMoleculeStructureDeserializer;
import jachemkit.hashchem.UnmodifiableUndirectedGraphSerializer;

@NodeEntity
public class HashMolecule {

	@GraphId
	private Long id;
	
	@JsonSerialize(using = UnmodifiableUndirectedGraphSerializer.class)
	@JsonDeserialize(using = HashMoleculeStructureDeserializer.class)
	private UnmodifiableUndirectedGraph<HashAtom, DefaultEdge> structure;
	
	
	private HashMolecule(){
		
	}
	
	public UnmodifiableUndirectedGraph<HashAtom, DefaultEdge> getStructure() {
		return structure;
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
