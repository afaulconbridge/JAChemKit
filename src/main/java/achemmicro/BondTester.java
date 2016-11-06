package achemmicro;

import java.util.Set;

public interface BondTester<T extends Comparable<T>> {

	public boolean testBond(Molecule<T> molecule, Set<Coordinate> bond);
}
