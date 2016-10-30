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

public class Reactor {

	private final Logger log = Logger.getLogger(this.getClass());

	public Set<Reaction> getReactions(Molecule molA, Molecule molB) {
		// make sure they are the right way around
		if (molA.compareTo(molB) > 0) {
			Molecule temp = molA;
			molA = molB;
			molB = temp;
		}
		SortedMultiset<Molecule> reactants = TreeMultiset.create();
		reactants.add(molA);
		reactants.add(molB);
		
		Set<Reaction> reactions = new HashSet<>();
		// now slide the second one over the first in all possible coordinate
		// sets
		for (int y = 0 - molA.height; y <= molA.height; y++) {
			for (int x = 0 - molA.width; x <= molA.width; x++) {
				log.info(""+x+","+y);
				try {
					// create a new molecule with both sets of atoms on them
					Map<Coordinate, String> newMol = new HashMap<>();
					newMol.putAll(molB.getElements());
					for (Coordinate c : molA.getElements().keySet()) {
						Coordinate cNew = Coordinate.from(c.x + x, c.y + y);
						if (newMol.containsKey(cNew)) {
							// newMolecule already contains it
							throw new DuplicateCoordinateException();
						} else {
							newMol.put(cNew, molA.getElement(c).get());
						}
					}
					
					//now we have a set of coordinates, but they may not join into a single continuous molecule
					//to do that, we pick a start and see if we can follow all of it
					Set<Coordinate> visited = new HashSet<>();
					Queue<Coordinate> locations = new ArrayDeque<>();
					locations.add(newMol.keySet().iterator().next());
					while(locations.peek() != null) {
						Coordinate loc = locations.poll();
						visited.add(loc);

						Coordinate left = Coordinate.from(loc.x-1, loc.y);
						if (newMol.containsKey(left) && !visited.contains(left) && !locations.contains(left)) {
							locations.add(left);
						}
						Coordinate up = Coordinate.from(loc.x, loc.y-1);
						if (newMol.containsKey(up) && !visited.contains(up) && !locations.contains(up)) {
							locations.add(up);
						}
						Coordinate right = Coordinate.from(loc.x+1, loc.y);
						if (newMol.containsKey(right) && !visited.contains(right) && !locations.contains(right)) {
							locations.add(right);
						}
						Coordinate down = Coordinate.from(loc.x, loc.y+1);
						if (newMol.containsKey(down) && !visited.contains(down) && !locations.contains(down)) {
							locations.add(down);
						}
					}
					//now check that our visited set is the same as our new key set
					if (visited.size() != newMol.keySet().size()) {
						throw new IncompleteCoordinateException();
					}
					
					//we can create a product molecule now
					//TODO check bonding is possible
					//TOOD validate all existing bonds
					Molecule product = Molecule.build(newMol);

					//AsciiRenderer renderer = new AsciiRenderer();
					//log.info(renderer.toAscii(product));
					
					SortedMultiset<Molecule> products = TreeMultiset.create();
					products.add(product);
					
					Reaction reaction = Reaction.build(reactants, products);
					reactions.add(reaction);
					
				} catch (DuplicateCoordinateException e) {
					// duplicate coordinate, allow to fail gracefully
					log.trace("Duplicated coordinates at offset "+x+","+y);
				}catch (IncompleteCoordinateException e) {
					// did not form continuous molecule, allow to fail gracefully
					log.trace("Non-continuous coordinates at offset "+x+","+y);
				}

			}
		}
		return reactions;
	}

	class DuplicateCoordinateException extends Exception {
		private static final long serialVersionUID = -8075674045910462900L;

		public DuplicateCoordinateException() {
		}

		public DuplicateCoordinateException(String message) {
			super(message);
		}
	}

	class IncompleteCoordinateException extends Exception {
		private static final long serialVersionUID = 4098343249681434560L;

		public IncompleteCoordinateException() {
		}

		public IncompleteCoordinateException(String message) {
			super(message);
		}
	}
}
