package jachemkit.hashchem.neo;

import org.neo4j.ogm.annotation.EndNode;
import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Property;
import org.neo4j.ogm.annotation.RelationshipEntity;
import org.neo4j.ogm.annotation.StartNode;

@RelationshipEntity(type = "HAS_Product")
public class NeoProduct {

	@GraphId
	private Long relationshipId;
	
	@Property
	private Integer count;

	@EndNode
	private NeoReaction reaction;

	@StartNode
	private NeoMolecule product;

	//Required dummy constructor for ogm
	@SuppressWarnings("unused")
	private NeoProduct() {};	
	
	public NeoProduct(NeoReaction reaction, NeoMolecule product, int count) {
		this.reaction = reaction;
		this.product = product;
		this.count = count;
	}

	public Integer getCount() {
		return count;
	}

	public NeoReaction getReaction() {
		return reaction;
	}

	public NeoMolecule getProduct() {
		return product;
	}

}
