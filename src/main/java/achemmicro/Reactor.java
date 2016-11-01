package achemmicro;

import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

public class Reactor<T extends Comparable<T>> {

	private final Logger log = Logger.getLogger(this.getClass());

	public Set<Reaction<T>> getReactions(Molecule<T> molA, Molecule<T> molB) {
		// make sure they are the right way around
		if (molA.compareTo(molB) > 0) {
			Molecule<T> temp = molA;
			molA = molB;
			molB = temp;
		}
		SortedMultiset<Molecule<T>> reactants = TreeMultiset.create();
		reactants.add(molA);
		reactants.add(molB);
		
		Set<Reaction<T>> reactions = new HashSet<>();
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
					Set<Set<Coordinate>> newBonds = new HashSet<>();
					newBonds.addAll(molB.getBonds());
					for (Set<Coordinate> bond : molA.getBonds()) {
						Set<Coordinate> newBond = new HashSet<>();
						for (Coordinate c : bond) {
							Coordinate cNew = Coordinate.from(c.x + x, c.y + y);
							//check coordinate has an element
							newBond.add(cNew);
						}
						newBonds.add(newBond);
					}
					
					//now we have a set of coordinates, but they may not join into a single continuous molecule
					//the molecule builder will handle that for us
					
					//we can create a product molecule now
					//TODO check bonding is possible
					//TOOD validate all existing bonds
					Molecule<T> product = Molecule.build(newElements, newBonds);

					//AsciiRenderer renderer = new AsciiRenderer();
					//log.info(renderer.toAscii(product));
					
					SortedMultiset<Molecule<T>> products = TreeMultiset.create();
					products.add(product);
					
					Reaction<T> reaction = Reaction.<T>build(reactants, products);
					reactions.add(reaction);
					
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
