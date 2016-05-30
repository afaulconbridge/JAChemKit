package jachemkit.hashchem.model;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.Relationship;
import org.neo4j.ogm.annotation.typeconversion.Convert;

@NodeEntity
public class NeoAtom {

	@GraphId
	private Long id;

	@Relationship(type = "BUILT_FROM", direction = Relationship.INCOMING)
	private NeoMolecule molecule;

	@Relationship(type = "BONDED_TO", direction = Relationship.UNDIRECTED)
	private Set<NeoAtom> bondedTo = new HashSet<>();
	
	//this is a string, usually JSON. avoids relying on neo4j
	//conversion of objects to strings and back, which can be problematic
	//e.g. lists that get turned into sets...
	@Property
	private String value;
	
	//Required dummy constructor for ogm
	@SuppressWarnings("unused")
	private NeoAtom() {};
	
	public NeoAtom(String  value) {
		this.value = value;
	};
	
	public NeoMolecule getMolecule() {
		return molecule;
	}

	public void setMolecule(NeoMolecule molecule) {
		this.molecule = molecule;
	}

	public String getValue() {
		return value;
	}
	
	public Set<NeoAtom> getBondedTo() {
		return bondedTo;
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
