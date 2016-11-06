package achemmicro;

import java.util.Collection;
import java.util.Objects;

import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;

public class Reaction<T extends Comparable<T>> {
	
	private final ImmutableSortedMultiset<Molecule<T>> reactants;
	private final ImmutableSortedMultiset<Molecule<T>> products;
	private final Molecule<T> intermediate;

	protected Reaction(ImmutableSortedMultiset<Molecule<T>> reactants,  Molecule<T> intermediate, ImmutableSortedMultiset<Molecule<T>> products) {
		this.reactants = reactants;
		this.intermediate = intermediate;
		this.products = products;
	}
	
	public ImmutableSortedMultiset<Molecule<T>> getReactants() {
		return reactants;
	}

	public Molecule<T> getIntermediate() {
		return intermediate;
	}

	public ImmutableSortedMultiset<Molecule<T>> getProducts() {
		return products;
	}

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Reaction)) {
            return false;
        }
        Reaction<?> other = (Reaction<?>) o;
        return Objects.equals(this.reactants, other.reactants)
        		&& Objects.equals(this.intermediate, other.intermediate)
        		&& Objects.equals(this.products, other.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.reactants, intermediate, this.products);
    }
    
	public static <T extends Comparable<T>> Reaction<T> build(Multiset<Molecule<T>> reactants, Molecule<T> intermediate, Multiset<Molecule<T>> products) {
		return new Reaction<T>(ImmutableSortedMultiset.copyOf(reactants), intermediate, ImmutableSortedMultiset.copyOf(products));
	}
}
