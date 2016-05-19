package jachemkit.hashchem.neo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.google.common.collect.ImmutableSet;

@NodeEntity
public class NeoMolecule {

	@GraphId
	private Long id;

	@Relationship(type = "BUILT_FROM", direction = Relationship.OUTGOING)
	private Set<NeoAtom> atoms;
		
	private transient UnmodifiableUndirectedGraph<NeoAtom, DefaultEdge> structure = null;

	//Required dummy constructor for ogm
	@SuppressWarnings("unused")
	private NeoMolecule() {};
	
	public NeoMolecule(NeoAtom atom) {
		atoms = new HashSet<>();
		atom.setMolecule(this);
		atoms.add(atom);
	}
	
	public Long getNeoId() {
		return id;
	}
	
	public ImmutableSet<NeoAtom> getAtoms() {
		return ImmutableSet.copyOf(atoms);
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
	
	public UnmodifiableUndirectedGraph<NeoAtom, DefaultEdge> getStructure() {
		if (structure == null) {
			//create a new simple graph
			SimpleGraph<NeoAtom,DefaultEdge> newStructure = new SimpleGraph<>(DefaultEdge.class);
			for (NeoAtom atom : atoms) {
				newStructure.addVertex(atom);
			}
			for (NeoAtom atom : atoms) {
				for (NeoAtom otherAtom : atom.getBondedTo()) {
					if (!newStructure.containsEdge(atom, otherAtom)) {
						newStructure.addEdge(atom, otherAtom);
					}
				}
			}
			//now bake it into an unmodifiable one
			structure = new UnmodifiableUndirectedGraph<>(newStructure);
		} 
		return structure;
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

	
	private class DefaultEdgeComparator implements Comparator<DefaultEdge> {
		@Override
		public int compare(DefaultEdge e1, DefaultEdge e2) {
			return 0;
		}			
	}
	private class NeoAtomComparator implements Comparator<NeoAtom> {
		@Override
		public int compare(NeoAtom a1, NeoAtom a2) {
			if (a1.getValue().size() < a2.getValue().size()) {
				return -1;
			} else if (a1.getValue().size() > a2.getValue().size()) {
				return 1;
			} else  {
				for (int i=0; i < a1.getValue().size(); i++) {
					Integer v1 = a1.getValue().get(i);
					Integer v2 = a2.getValue().get(i);
					if (v1.compareTo(v2) != 0) {
						return v1.compareTo(v2);
					}
				}
				return 0;
			}
		}			
	}
	
	public boolean equals(NeoMolecule other) {
		if (other == null) {
			return false;
		}
		if (other == this) {
			return true;
		}
		if (this.getNeoId() != null && other.getNeoId() != null 
				&& this.getNeoId().equals(other.getNeoId())) {
			return true;
		}
		//have to do graph isomorphism, might be slow!
		VF2GraphIsomorphismInspector<NeoAtom, DefaultEdge> inspector = 
				new VF2GraphIsomorphismInspector<>(this.getStructure(), other.getStructure(),
						new NeoAtomComparator(), new DefaultEdgeComparator());
		return inspector.isomorphismExists();
	}
}
