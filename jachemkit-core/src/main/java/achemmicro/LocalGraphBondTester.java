package achemmicro;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.google.common.collect.ImmutableSet;

public class LocalGraphBondTester<T extends Comparable<T>> implements BondTester<T> {
	
	private final double bondProb;
	//this needs to be concurrent map so that multiple threads can test against it at once
	private final ConcurrentMap<ImmutableSet<List<T>>, Boolean> historic = new ConcurrentHashMap<>();
	
	public LocalGraphBondTester() {
		bondProb = 0.1;
	}

	@Override
	public boolean testBond(Molecule<T> molecule, Set<Coordinate> bond) {
		if (molecule == null)  throw new IllegalArgumentException("molecule must not be null");
		if (bond == null)  throw new IllegalArgumentException("bond must not be null");
		if (bond.size() != 2) throw new IllegalArgumentException("bond must be two coordinates");
		
		Set<List<T>> localGraphs = new HashSet<>();
		for (Coordinate end : bond) {
			localGraphs.add(getGraphFrom(molecule, end, bond));
		}
		ImmutableSet<List<T>> localGraphFixed = ImmutableSet.copyOf(localGraphs);
		if (!historic.containsKey(localGraphFixed)) {
			//create a new random number generated based on the hash of the local graphs
			Random rng = new Random(localGraphFixed.hashCode());
			//now use that random number generator to determine if the bond should exist
			//use putIfAbsent to ensure it only created by one thread ever
			historic.putIfAbsent(localGraphFixed, rng.nextDouble() < bondProb);
		}
		return historic.get(localGraphFixed);	
	}

	protected List<T> getGraphFrom(Molecule<T> molecule, int x, int y) {
		return getGraphFrom(molecule, x, y, ImmutableSet.of());
	}
	protected  List<T> getGraphFrom(Molecule<T> molecule, Coordinate start) {
		return getGraphFrom(molecule, start, ImmutableSet.of());		
	}
	protected List<T> getGraphFrom(Molecule<T> molecule, int x, int y, Set<Coordinate> bondExclude) {
		return getGraphFrom(molecule, Coordinate.from(x,y), bondExclude);
	}
	protected  List<T> getGraphFrom(Molecule<T> molecule, Coordinate start, Set<Coordinate> bondExclude) {
		Queue<Coordinate> locations = new ArrayDeque<>();
		Set<Coordinate> visited = new HashSet<>();
		List<T> output = new ArrayList<>();
		locations.add(start);
		while (locations.peek() != null) {			
			//visit a location
			Coordinate loc = locations.poll();
			visited.add(loc);
			output.add(molecule.getElement(loc));
			//get any neighbours of that location
			for (Coordinate other : molecule.getBondedFrom(loc)) {
				if (!visited.contains(other) 
						&& !locations.contains(other)
						&& (!bondExclude.contains(loc) && !bondExclude.contains(other))) {
					locations.add(other);
				}				
			}
		}
		return output;
	}
	
}
