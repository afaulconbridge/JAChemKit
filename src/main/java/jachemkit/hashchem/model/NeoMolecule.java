package jachemkit.hashchem.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.fasterxml.jackson.annotation.JsonIgnore;

@NodeEntity
public class NeoMolecule {

	@GraphId
	private Long id;

	@Relationship(type = "BUILT_FROM", direction = Relationship.OUTGOING)
	private Set<NeoAtom> atoms;

	@JsonIgnore
	private transient UnmodifiableUndirectedGraph<NeoAtom, DefaultEdge> structure = null;

	//Required dummy constructor for ogm
	private NeoMolecule() {};
	
	public NeoMolecule(NeoAtom atom) {
		atoms = new HashSet<>();
		atom.setMolecule(this);
		atoms.add(atom);
	}
	
	public Long getNeoId() {
		return id;
	}
	
	public Set<NeoAtom> getAtoms() {
		return atoms;
	}
	
	public void addAtom(NeoAtom atom, NeoAtom ...bonded) {
		//check if it already contains the added atom
		if (atoms.contains(atom)) {
			throw new IllegalArgumentException("Can only add novel atoms");
		}
		//check each atom bonded is in the molecule
		if (Arrays.stream(bonded).anyMatch((a) -> !atoms.contains(a))) {
			throw new IllegalArgumentException("Can only bond to atoms in same molecule");
		}
		
		atom.setMolecule(this);
		atoms.add(atom);
		
		Arrays.stream(bonded).forEach((a) -> atom.addBondTo(a));
	}
	

	public static NeoMolecule createFrom(SimpleGraph<NeoAtom, DefaultEdge> structure) {
		//create a new graph
		SimpleGraph<NeoAtom,DefaultEdge> newStructure = new SimpleGraph<>(DefaultEdge.class);
		
		//copy each atom
		Map<NeoAtom,NeoAtom> atomInstance = new HashMap<>();
		for (NeoAtom oldAtom : structure.vertexSet()) {
			NeoAtom newAtom = new NeoAtom(oldAtom.getValue());
			atomInstance.put(oldAtom, newAtom);
		}
		//set the bonds of each atom
		for (NeoAtom oldAtom : structure.vertexSet()) {
			NeoAtom newAtom = atomInstance.get(oldAtom);
			for (DefaultEdge e : structure.edgesOf(newAtom)) {
				NeoAtom source = structure.getEdgeSource(e);
				NeoAtom target = structure.getEdgeTarget(e);
				//compare by id
				if (source == oldAtom) {
					newAtom.addBondTo(atomInstance.get(target));
				}
				if (target == oldAtom) {
					newAtom.addBondTo(atomInstance.get(source));
				}
			}
		}
		
		
		//create a molecule from those atoms
		NeoMolecule mol = new NeoMolecule();
		mol.atoms = new HashSet<>(atomInstance.values());
		
		return mol;
	}

	
}
