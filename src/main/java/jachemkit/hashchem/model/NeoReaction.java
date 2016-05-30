package jachemkit.hashchem.model;

import java.util.HashSet;
import java.util.Set;

import org.neo4j.ogm.annotation.GraphId;
import org.neo4j.ogm.annotation.Relationship;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableMultiset;
import com.google.common.collect.Multiset;

import jachemkit.core.Reaction;

public class NeoReaction implements Reaction<NeoMolecule> {

	@GraphId
	private Long id;

	//because neo only allows one edge between nodes, we have to store them explicitly and track count
	@Relationship(type = "HAS_REACTANT")
	private Set<NeoReactant> reactantEdges;
	
	private transient ImmutableMultiset<NeoMolecule> reactants;

	//because neo only allows one edge between nodes, we have to store them explicitly and track count
	@Relationship(type = "HAS_PRODUCT")
	private Set<NeoProduct> productEdges;
	
	private transient ImmutableMultiset<NeoMolecule> products;

	//Required dummy constructor for ogm
	@SuppressWarnings("unused")
	private NeoReaction() {};
	
	public NeoReaction(Multiset<NeoMolecule> reactants, Multiset<NeoMolecule> products) {
		
		this.reactantEdges = new HashSet<>();
		for (NeoMolecule reactant : reactants.elementSet()) {
			NeoReactant reactantEdge = new NeoReactant(this, reactant, reactants.count(reactant));
			reactantEdges.add(reactantEdge);
		}
		
		this.productEdges = new HashSet<>();
		for (NeoMolecule product : products.elementSet()) {
			NeoProduct productEdge = new NeoProduct(this, product, products.count(product));
			productEdges.add(productEdge);
		}
	}
	
	public Long getNeoId() {
		return id;
	}
	
	@Override
	public ImmutableMultiset<NeoMolecule> getReactants() {
		if (reactants == null) {
			Multiset<NeoMolecule> tmpReactants = HashMultiset.create();
			for (NeoReactant reactantEdge : reactantEdges) {
				tmpReactants.add(reactantEdge.getReactant(), reactantEdge.getCount());
			}
			reactants = ImmutableMultiset.copyOf(tmpReactants);
		}
		return reactants;
	}

	@Override
	public ImmutableMultiset<NeoMolecule> getProducts() {
		if (products == null) {
			Multiset<NeoMolecule> tmpProducts = HashMultiset.create();
			for (NeoProduct reactantEdge : productEdges) {
				tmpProducts.add(reactantEdge.getProduct(), reactantEdge.getCount());
			}
			products = ImmutableMultiset.copyOf(tmpProducts);
		}
		return products;
	}

}
