package jachemkit.hashchem.neo;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Relationship;

import com.google.common.collect.ImmutableSet;

@NodeEntity
public class NeoAtom {

	@GraphId
	private Long id;

	@Relationship(type = "BUILT_FROM", direction = Relationship.INCOMING)
	private NeoMolecule molecule;

	@Relationship(type = "BONDED_TO", direction = Relationship.UNDIRECTED)
	private Set<NeoAtom> bondedTo = new HashSet<>();
	
	public NeoAtom() {};
	
	public ImmutableSet<NeoAtom> getBondedTo() {
		return ImmutableSet.copyOf(bondedTo);
	}
	
	public NeoMolecule getMolecule() {
		return molecule;
	}

	public void setMolecule(NeoMolecule molecule) {
		this.molecule = molecule;
	}

	public void addBondTo(NeoAtom other) {
		if (other == null) {
			throw new IllegalArgumentException("Other must not be null");
		}
		
		//comparison by reference
		if (other == this) {
			throw new IllegalArgumentException("Self bonds not permitted");
		}
		//comparison by reference
		if (other.molecule != this.molecule) {
			throw new IllegalArgumentException("Cannot bond to another molecule");
		}
		
		if (bondedTo.contains(other) || other.bondedTo.contains(this)) {
			throw new IllegalArgumentException("Double bonds not permitted");
		}
		
		bondedTo.add(other);
		other.bondedTo.add(this);
	}
}
