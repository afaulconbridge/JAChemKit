package jachemkit.hashchem.neo;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
	
	@SuppressWarnings("unused")
	private NeoMolecule() {};
	
	public NeoMolecule(NeoAtom atom) {
		atoms = new HashSet<>();
		atom.setMolecule(this);
		atoms.add(atom);
	}
	
	public ImmutableSet<NeoAtom> getAtoms() {
		return ImmutableSet.copyOf(atoms);
	}
	
	public void addAtom(NeoAtom atom, NeoAtom ...bonded) {
		//check if it already contains the added attom
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
}
