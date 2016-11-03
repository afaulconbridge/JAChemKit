package achemmicro;

import java.util.Collection;
import java.util.Objects;

import com.google.common.collect.ImmutableSortedMultiset;
import com.google.common.collect.Multiset;

public class Reaction<T extends Comparable<T>> {
	
	private final ImmutableSortedMultiset<Molecule<T>> reactants;
	private final ImmutableSortedMultiset<Molecule<T>> products;

	protected Reaction(ImmutableSortedMultiset<Molecule<T>> reactants, ImmutableSortedMultiset<Molecule<T>> products) {
		this.reactants = reactants;
		this.products = products;
	}
	
	public ImmutableSortedMultiset<Molecule<T>> getReactants() {
		return reactants;
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
        return Objects.equals(this.reactants, other.reactants) && Objects.equals(this.products, other.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.reactants, this.products);
    }
    
	public static <T extends Comparable<T>> Reaction<T> build(Collection<Molecule<T>> reactants, Collection<Molecule<T>> products) {
		return new Reaction<T>(ImmutableSortedMultiset.copyOf(reactants), ImmutableSortedMultiset.copyOf(products));
	}
}
