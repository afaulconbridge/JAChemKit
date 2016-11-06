package achemmicro.bonding;

import java.util.Set;

import achemmicro.Coordinate;
import achemmicro.Molecule;

public interface BondTester<T extends Comparable<T>> {

	public boolean testBond(Molecule<T> molecule, Set<Coordinate> bond);
}
