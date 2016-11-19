package achemmicro.bonding;

import java.nio.charset.Charset;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import achemmicro.Coordinate;
import achemmicro.Molecule;
/**
 * Tests bonds by looking at the local bonding graph
 * and randomly assigneing particular graphs sucess or failure.
 * 
 *  Note that this does not take the coordinates of the molecules into account in any way, only the 
 *  bonding graphs
 * 
 * @author Adam
 *
 * @param <T>
 */
public class LocalGraphBondTester<T extends Comparable<T>> implements BondTester<T> {

	private final Logger log = Logger.getLogger(this.getClass());
	
	private final double bondProb;
	private final int bondLocalGraphLength;
	
	
	//this needs to be concurrent map so that multiple threads can test against it at once
	private final ConcurrentMap<ImmutableSet<ImmutableList<T>>, Boolean> historic = new ConcurrentHashMap<>();
	
	public LocalGraphBondTester() {
		bondProb = 0.1;
		bondLocalGraphLength = 5;
	}

	@Override
	public boolean testBond(Molecule<T> molecule, Set<Coordinate> bond) {
		if (molecule == null)  throw new IllegalArgumentException("molecule must not be null");
		if (bond == null)  throw new IllegalArgumentException("bond must not be null");
		if (bond.size() != 2) throw new IllegalArgumentException("bond must be two coordinates");
		
		Set<ImmutableList<T>> localGraphs = new HashSet<>();
		for (Coordinate end : bond) {
			if (!molecule.has(end)) throw new IllegalArgumentException("bond must be in molecule");
			List<T> localGraph = new ArrayList<>();
			for (Coordinate c : getGraphFrom(molecule, end, bond)) {
				localGraph.add(molecule.getElement(c));
				if (localGraph.size() >= bondLocalGraphLength) break;
			}
			localGraphs.add(ImmutableList.copyOf(localGraph));
		}
		ImmutableSet<ImmutableList<T>> localGraphFixed = ImmutableSet.copyOf(localGraphs);
		if (!historic.containsKey(localGraphFixed)) {
			//create a new random number generated based on the hash of the local graphs
			//need an evenly distributed hash, suitable for seeding an RNG
			HashFunction hashFunction = Hashing.murmur3_128();
			Hasher hasher = hashFunction.newHasher();
			for (List<T> l : localGraphFixed) {
				for (T t : l) {
					hasher.putInt(t.hashCode());
				}
			}			
			Random rng = new Random(hasher.hash().padToLong());
			//now use that random number generator to determine if the bond should exist
			//use putIfAbsent to ensure it only created by one thread ever
			double next = rng.nextDouble();
			log.info(next);
			//TODO partway through chanign this from list<T> to coordinates - because we need the shape to give diffeeren tmolecular results
			historic.putIfAbsent(localGraphFixed, next < bondProb);
		}
		return historic.get(localGraphFixed);	
	}

	protected List<Coordinate> getGraphFrom(Molecule<T> molecule, int x, int y) {
		return getGraphFrom(molecule, x, y, ImmutableSet.of());
	}
	protected  List<Coordinate> getGraphFrom(Molecule<T> molecule, Coordinate start) {
		return getGraphFrom(molecule, start, ImmutableSet.of());		
	}
	protected List<Coordinate> getGraphFrom(Molecule<T> molecule, int x, int y, Set<Coordinate> bondExclude) {
		return getGraphFrom(molecule, Coordinate.from(x,y), bondExclude);
	}
	protected  List<Coordinate> getGraphFrom(Molecule<T> molecule, Coordinate start, Set<Coordinate> bondExclude) {
		Queue<Coordinate> locations = new ArrayDeque<>();
		Set<Coordinate> visited = new HashSet<>();
		List<Coordinate> output = new ArrayList<>();
		locations.add(start);
		while (locations.peek() != null) {			
			//visit a location
			Coordinate loc = locations.poll();
			visited.add(loc);
			output.add(loc);
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
