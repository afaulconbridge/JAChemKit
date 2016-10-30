package achemmicro;

import java.util.Objects;

import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;

public class Reaction {
	
	private final ImmutableSortedMultiset<Molecule> reactants;
	private final ImmutableSortedMultiset<Molecule> products;

	protected Reaction(Multiset<Molecule> reactants, Multiset<Molecule> products) {
		this.reactants = ImmutableSortedMultiset.copyOf(reactants);
		this.products = ImmutableSortedMultiset.copyOf(products);
	}
	
	public ImmutableSortedMultiset<Molecule> getReactants() {
		return reactants;
	}

	public ImmutableSortedMultiset<Molecule> getProducts() {
		return products;
	}

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Reaction)) {
            return false;
        }
        Reaction other = (Reaction) o;
        return Objects.equals(this.reactants, other.reactants) && Objects.equals(this.products, other.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.reactants, this.products);
    }
    
	public static Reaction build(Multiset<Molecule> reactants, Multiset<Molecule> products) {
		return new Reaction(reactants, products);
	}
}
