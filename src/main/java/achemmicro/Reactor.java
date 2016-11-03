package achemmicro;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

public class Reactor<T extends Comparable<T>> {

	private final Logger log = Logger.getLogger(this.getClass());

	public Multiset<Reaction<T>> getReactions(Molecule<T> molA, Molecule<T> molB) {
		// make sure they are the right way around
		if (molA.compareTo(molB) > 0) {
			Molecule<T> temp = molA;
			molA = molB;
			molB = temp;
		}
		SortedMultiset<Molecule<T>> reactants = TreeMultiset.create();
		reactants.add(molA);
		reactants.add(molB);
		
		Multiset<Reaction<T>> reactions = HashMultiset.create();
		// now slide the second one over the first in all possible coordinate
		// sets
		for (int y = 0 - molA.height; y <= molA.height; y++) {
			for (int x = 0 - molA.width; x <= molA.width; x++) {
				log.info(""+x+","+y);
				try {
					// create a new molecule with both sets of atoms on them
					Map<Coordinate, Element<T>> newElements = new HashMap<>();
					newElements.putAll(molB.getElements());
					for (Coordinate c : molA.getElements().keySet()) {
						Coordinate cNew = Coordinate.from(c.x + x, c.y + y);
						if (newElements.containsKey(cNew)) {
							// newMolecule already contains it
							throw new DuplicateCoordinateException();
						} else {
							newElements.put(cNew, Element.build(molA.getElement(c)));
						}
					}
					Set<ImmutableSet<Coordinate>> newBonds = new HashSet<>();
					newBonds.addAll(molB.getBonds());
					for (Set<Coordinate> bond : molA.getBonds()) {
						Set<Coordinate> newBond = new HashSet<>();
						for (Coordinate c : bond) {
							Coordinate cNew = Coordinate.from(c.x + x, c.y + y);
							//check coordinate has an element
							newBond.add(cNew);
						}
						newBonds.add(ImmutableSet.copyOf(newBond));
					}
					
					//now we have a set of coordinates, but they may not join into a single continuous molecule
					//the molecule builder will handle that for us
					
					//TODO check bonding is possible
					//TOOD validate all existing bonds
					
					
					//product as a single, possibly non-continuous, molecule
					Molecule<T> intermediate = Molecule.build(newElements, newBonds);
					//AsciiRenderer renderer = new AsciiRenderer();
					//log.info(renderer.toAscii(product));


					MoleculeBuilder<T> builder = new MoleculeBuilder<>();
					for (Coordinate c : newElements.keySet()) {
						builder.fromElement(c, newElements.get(c));
					}
					for (Coordinate c : newElements.keySet()) {
						for (ImmutableSet<Coordinate> bond : newBonds) {
							if (bond.contains(c)) {
								builder.fromBond(bond);
							}
						}
					}			
					reactions.add(Reaction.<T>build(reactants, builder.buildAll()));
					
				} catch (DuplicateCoordinateException e) {
					// duplicate coordinate, allow to fail gracefully
					log.trace("Duplicated coordinates at offset "+x+","+y);
				}
			}
		}
		return reactions;
	}

	protected static class DuplicateCoordinateException extends Exception {
		private static final long serialVersionUID = -8075674045910462900L;

		public DuplicateCoordinateException() {
		}

		public DuplicateCoordinateException(String message) {
			super(message);
		}
	}
}
