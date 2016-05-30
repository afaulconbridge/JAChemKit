package jachemkit.hashchem.service;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jachemkit.hashchem.model.NeoAtom;
import jachemkit.hashchem.model.NeoMolecule;

@Service
public class StructureService {

	public UnmodifiableUndirectedGraph<NeoAtom, DefaultEdge> getStructure(NeoMolecule mol) {
		//create a new simple graph
		SimpleGraph<NeoAtom,DefaultEdge> newStructure = new SimpleGraph<>(DefaultEdge.class);
		for (NeoAtom atom : mol.getAtoms()) {
			newStructure.addVertex(atom);
		}
		for (NeoAtom atom : mol.getAtoms()) {
			for (NeoAtom otherAtom : atom.getBondedTo()) {
				if (!newStructure.containsEdge(atom, otherAtom)) {
					newStructure.addEdge(atom, otherAtom);
				}
			}
		}
		//now bake it into an unmodifiable one
		UnmodifiableUndirectedGraph<NeoAtom, DefaultEdge> structure = new UnmodifiableUndirectedGraph<>(newStructure);
		return structure;
	}
}
