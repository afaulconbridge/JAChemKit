package jachemkit.hashchem.model;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "HAS_REACTANT")
public class NeoReactant {

	@GraphId
	private Long relationshipId;
	
	@Property
	private Integer count;
	
	@StartNode
	private NeoReaction reaction;
	
	@EndNode
	private NeoMolecule reactant;

	//Required dummy constructor for ogm
	@SuppressWarnings("unused")
	private NeoReactant() {};	
	
	public NeoReactant(NeoReaction reaction, NeoMolecule reactant, int count) {
		this.reaction = reaction;
		this.reactant = reactant;
		this.count = count;
	}

	public Integer getCount() {
		return count;
	}

	public NeoReaction getReaction() {
		return reaction;
	}

	public NeoMolecule getReactant() {
		return reactant;
	}

}
