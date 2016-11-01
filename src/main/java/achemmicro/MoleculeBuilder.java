package achemmicro;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MoleculeBuilder<T extends Comparable<T>> {

	protected Map<Coordinate,Element<T>> elements = new HashMap<>();	
	protected Set<Set<Coordinate>> bonds = new HashSet<>();
	
	public MoleculeBuilder(){
		
	}
	
	public MoleculeBuilder<T> fromElement(int x, int y, T element) {
		return fromElement(Coordinate.from(x, y), element);
	}
	
	public MoleculeBuilder<T> fromElement(Coordinate c, T element) {
		elements.put(c, Element.build(element));
		return this;
	}

	public MoleculeBuilder<T> fromBond(int x1, int y1, int x2, int y2) {
		return fromBond(Coordinate.from(x1, y1), Coordinate.from(x2, y2));
		
	}
	public MoleculeBuilder<T> fromBond(Coordinate a, Coordinate b) {
		//only allow bonds where elements exist
		if (!elements.containsKey(a)) {
			throw new IllegalArgumentException("Coordinate "+a+" not in elements");
		}
		if (!elements.containsKey(b)) {
			throw new IllegalArgumentException("Coordinate "+b+" not in elements");
		}
		Set<Coordinate> bond = new HashSet<>();
		bond.add(a);
		bond.add(b);
		bonds.add(bond);
		return this;
	}
	
	public Molecule<T> build() {
		return Molecule.build(elements, bonds);
	}
}
