package jachemkit.hashchem.neo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

@NodeEntity
public class NeoAtom {

	@GraphId
	private Long id;

	@Relationship(type = "BUILT_FROM", direction = Relationship.INCOMING)
	private NeoMolecule molecule;

	@Relationship(type = "BONDED_TO", direction = Relationship.UNDIRECTED)
	private Set<NeoAtom> bondedTo = new HashSet<>();
	
	//this has to be mutable so can be created from dummy constructor
	//but practically it is immutable
	@Property
	private List<Integer> value;
	
	//Required dummy constructor for ogm
	@SuppressWarnings("unused")
	private NeoAtom() {};
	
	public NeoAtom(ImmutableList<Integer> value) {
		this.value = value;
	};
	
	public NeoMolecule getMolecule() {
		return molecule;
	}

	public void setMolecule(NeoMolecule molecule) {
		this.molecule = molecule;
	}

	public ImmutableList<Integer> getValue() {
		return ImmutableList.copyOf(value);
	}
	
	public ImmutableSet<NeoAtom> getBondedTo() {
		return ImmutableSet.copyOf(bondedTo);
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
