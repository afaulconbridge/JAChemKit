package achemmicro;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;

public class MoleculeBuilder<T extends Comparable<T>> {

	protected Map<Coordinate,Element<T>> elements = new HashMap<>();	
	protected Set<ImmutableSet<Coordinate>> bonds = new HashSet<>();
	
	public MoleculeBuilder(){
		
	}
	
	public MoleculeBuilder<T> fromElement(int x, int y, T element) {
		return fromElement(Coordinate.from(x, y), element);
	}	
	public MoleculeBuilder<T> fromElement(Coordinate c, T element) {
		return fromElement(c, Element.build(element));
	}	
	public MoleculeBuilder<T> fromElement(int x, int y, Element<T> element) {
		return fromElement(Coordinate.from(x, y), element);
	}	
	public MoleculeBuilder<T> fromElement(Coordinate c, Element<T> element) {
		elements.put(c, element);
		return this;
	}
	
	public MoleculeBuilder<T> fromBond(int x1, int y1, int x2, int y2) {
		return fromBond(Coordinate.from(x1, y1), Coordinate.from(x2, y2));
		
	}
	public MoleculeBuilder<T> fromBond(Collection<Coordinate> b) {
		if (b.size() != 2) {
			throw new IllegalArgumentException("Bond must be of size 2 "+b);
		}
		Iterator<Coordinate> iter = b.iterator();
		return fromBond(iter.next(), iter.next());
	}
	public MoleculeBuilder<T> fromBond(Coordinate a, Coordinate b) {
		//only allow bonds where elements exist
		if (!elements.containsKey(a)) {
			throw new IllegalArgumentException("Coordinate "+a+" not in elements");
		}
		if (!elements.containsKey(b)) {
			throw new IllegalArgumentException("Coordinate "+b+" not in elements");
		}
		//don't worry about duplicating bonds
		bonds.add(ImmutableSet.of(a,b));
		return this;
	}
	
	public Multiset<Molecule<T>> buildAll() {
		//split the elements and bonds into their connected components
		Multiset<Molecule<T>> molecules = HashMultiset.create();
		Set<ImmutableSet<Coordinate>> components = findComponents();
		for (ImmutableSet<Coordinate> component : components) {
			//use a new builder for each component
			MoleculeBuilder<T> builder = new MoleculeBuilder<>();
			//feed the elements to the builder
			for (Coordinate c : component) {
				builder.fromElement(c, elements.get(c));
			}
			//now feed the bonds to make sure we can't refer to an element we haven't added yet
			for (Coordinate c : component) {
				for (ImmutableSet<Coordinate> bond : bonds) {
					if (bond.contains(c)) {
						builder.fromBond(bond);
					}
				}
			}
			molecules.add(builder.build());
		}
		return molecules;
	}
	
	public ImmutableSet<ImmutableSet<Coordinate>> findComponents() {
		//create a distinct component for each element
		Set<ImmutableSet<Coordinate>> components = new HashSet<>();
		for (Coordinate c : elements.keySet()) {
			Set<Coordinate> component = new HashSet<>();
			component.add(c);
			components.add(ImmutableSet.copyOf(component));
		}
		//merge components joined by bonds
		for (Set<Coordinate> bond : bonds) {
			Set<ImmutableSet<Coordinate>> bondComponents = new HashSet<>();
			//find which components contain the bond
			for(ImmutableSet<Coordinate> component : components)
				if (intersectionExists(component, bond)) {
					bondComponents.add(component);
			}
			//now merge those components
			Set<Coordinate> newComponent = new HashSet<>();
			for (ImmutableSet<Coordinate> component : bondComponents) {
				components.remove(component);
				newComponent.addAll(component);
			}
			components.add(ImmutableSet.copyOf(newComponent));
		}
		return ImmutableSet.copyOf(components);
	}
	
	private boolean intersectionExists(Set<?> setA, Set<?> setB) {
		for (Object o : setA) {
			if (setB.contains(o)) {
				return true;
			}
		}
		return false;
	}
	
	public Molecule<T> build() {
		if (findComponents().size() != 1) {
			throw new IllegalArgumentException("Cannot build a single moulecule from multiple components");
		}
		return Molecule.build(elements, bonds);
	}
}
