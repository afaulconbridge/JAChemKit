package achemmicro;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Multiset;
import com.google.common.collect.SortedMultiset;
import com.google.common.collect.TreeMultiset;

import achemmicro.bonding.BondTester;

public class Reactor<T extends Comparable<T>> {

	private final Logger log = Logger.getLogger(this.getClass());
	private final BondTester<T> bondTester;
	
	public Reactor(BondTester<T> bondTester) {
		this.bondTester = bondTester;
	}

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
							newBond.add(cNew);
						}
						newBonds.add(ImmutableSet.copyOf(newBond));
					}
					
					//now we have a set of coordinates, but they may not join into a single continuous molecule
					//the molecule builder will handle that for us
					
					//TODO check bonding is possible
					//for each used coordinate
					for (Coordinate cNew : newElements.keySet()) {
						//check if we can bond to the one right or down
						Coordinate cNewRight = Coordinate.from(cNew.x+1, cNew.y);
						if (newElements.containsKey(cNewRight)) {
							newBonds.add(ImmutableSet.of(cNew, cNewRight));
						}
						Coordinate cNewDown = Coordinate.from(cNew.x, cNew.y+1);
						if (newElements.containsKey(cNewDown)) {
							newBonds.add(ImmutableSet.of(cNew, cNewDown));
						}						
					}
					
					//intermediate as a single, possibly non-continuous, molecule
					Molecule<T> intermediate = Molecule.build(newElements, newBonds);
										
					//AsciiRenderer renderer = new AsciiRenderer();
					//log.info(renderer.toAscii(product));

					MoleculeBuilder<T> builder = new MoleculeBuilder<>();
					for (Coordinate c : intermediate.getElements().keySet()) {
						builder.fromElement(c, intermediate.getElements().get(c));
					}
					for (ImmutableSet<Coordinate> bond : intermediate.getBonds()) {
						//validate all existing bonds
						if (bondTester.testBond(intermediate, bond)) {
							builder.fromBond(bond);
						}
					}	
					Multiset<Molecule<T>> products = builder.buildAll();
					Reaction<T> reaction = Reaction.<T>build(reactants, intermediate, products); 
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
