package achemmicro;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.google.common.collect.ImmutableSortedMap;
import com.google.common.collect.ImmutableSortedSet;

import achemmicro.util.SortedSetComparator;

public class Molecule<T extends Comparable<T>> implements Comparable<Molecule<T>> {
	/**
	 * Total height of molecule, starting form zero
	 **/
	protected final int height;
	/**
	 * Total width of molecule, starting form zero
	 **/
	protected final int width;
		
	protected final ImmutableSortedMap<Coordinate, Element<T>> elements;
	//need to track bonding explicitly, to allow flexibily and fragmentation
	//do this by coordinates to easily order
	protected final ImmutableSortedSet<ImmutableSortedSet<Coordinate>> bonds;
		
	protected Molecule(ImmutableSortedMap<Coordinate,Element<T>> elements, ImmutableSortedSet<ImmutableSortedSet<Coordinate>> bonds) {
		//calculate the height and width
		this.height = elements.keySet().stream().map(c -> c.y).max((a,b) -> Integer.compare(a,b)).get()+1;
		this.width = elements.keySet().stream().map(c -> c.x).max((a,b) -> Integer.compare(a,b)).get()+1;

		this.elements = elements;
		this.bonds = bonds;
	}
	
	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}
	
	public boolean has(int x, int y) {
		return has(Coordinate.from(x, y));
	}

	public boolean has(Coordinate coord) {
		return elements.containsKey(coord);
	}
	
	public ImmutableSortedSet<Coordinate> getCoordinates() {
		return ImmutableSortedSet.copyOf(elements.keySet());
	}
	
	public ImmutableSortedMap<Coordinate, Element<T>> getElements() {
		return elements;
	}

	public ImmutableSortedSet<ImmutableSortedSet<Coordinate>> getBonds() {
		return bonds;
	}

	public T getElement(int x, int y) {
		return getElement(Coordinate.from(x,y));
	}
	
	public T getElement(Coordinate coord) {
		if (!elements.containsKey(coord)) {
			throw new IllegalArgumentException("Coordinate not in molecule "+coord);					
		}
		return elements.get(coord).value;
	}
	
	
	public ImmutableSortedSet<Coordinate> getBondedFrom(Coordinate start) {
		SortedSet<Coordinate> toReturn = new TreeSet<>();
		for (SortedSet<Coordinate> bond : bonds) {
			if (bond.contains(start)) {
				toReturn.addAll(bond);
			}
		}
		//ensure we don't return the start
		toReturn.remove(start);
		return ImmutableSortedSet.copyOf(toReturn);
	}
	
	public List<T> getGraphFrom(int x, int y) {
		return getGraphFrom(Coordinate.from(x,y));
	}
	public  List<T> getGraphFrom(Coordinate start) {
		Queue<Coordinate> locations = new ArrayDeque<>();
		Set<Coordinate> visited = new HashSet<>();
		List<T> output = new ArrayList<>();
		locations.add(start);
		while (locations.peek() != null) {			
			//visit a location
			Coordinate loc = locations.poll();
			visited.add(loc);
			output.add(getElement(loc));
			//get any neighbours of that location
			for (Coordinate other : getBondedFrom(loc)) {
				if (!visited.contains(other) && !locations.contains(other)) {
					locations.add(other);
				}				
			}
		}
		return output;
	}


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Molecule)) {
            return false;
        }
        Molecule<?> other = (Molecule<?>) o;
        return Objects.equals(this.elements, other.elements) 
        		&& Objects.equals(this.bonds, other.bonds);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.elements);
    }

	@Override
	public int compareTo(Molecule<T> other) {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than 
        // other and 0 if they are supposed to be equal
		
		TreeSet<Coordinate> thisCoords = new TreeSet<>(this.elements.keySet());
		TreeSet<Coordinate> otherCoords = new TreeSet<>(other.elements.keySet());
		
		//go by size
		if (thisCoords.size() < otherCoords.size()) {
			return -1;
		} else if (thisCoords.size() > otherCoords.size()) {
			return 1;
		}
		
		//iterate over each set of coordinates
		//since they are sorted set this will be in consistent order
		Iterator<Coordinate> thisCoordIter = thisCoords.iterator();
		Iterator<Coordinate> otherCoordIter = otherCoords.iterator();
		while (thisCoordIter.hasNext() && otherCoordIter.hasNext()) {
			//compare each matched coordinate
			Coordinate thisCoord = thisCoordIter.next();
			Coordinate otherCoord = otherCoordIter.next();
			int cmp = thisCoord.compareTo(otherCoord) ;
			if (cmp != 0) {
				return cmp;
			}
			//compare the element at that coordinate
			T thisElement = this.getElement(thisCoord);
			T otherElement = other.getElement(otherCoord);
			cmp = thisElement.compareTo(otherElement);
			if (cmp != 0) {
				return 0;
			}
		}
		//has the same coordinates at the same order
		//go by bonding
		//TODO finish
		return 0;
	}
        
	public static <T extends Comparable<T>> Molecule<T> build(Map<Coordinate,Element<T>> elements, Set<? extends Set<Coordinate>> bonds) {
		//work out the min and max coordinates
		int minX = elements.keySet().stream().map(c -> c.x).min((a,b) -> Integer.compare(a,b)).get();
		int minY = elements.keySet().stream().map(c -> c.y).min((a,b) -> Integer.compare(a,b)).get();
		
		// use the min and the max to create a zero-relative coordinate system
		SortedMap<Coordinate,Element<T>> elementsNew = new TreeMap<>();
		for (Coordinate c : elements.keySet()) {
			Coordinate cNew = Coordinate.from(c.x-minX, c.y-minY);
			elementsNew.put(cNew, elements.get(c));
		}
		
		//now create a deep-copy of bonds
		SortedSetComparator<Coordinate> comparator = new SortedSetComparator<>();
		SortedSet<ImmutableSortedSet<Coordinate>> bondsNew = new TreeSet<>(comparator);
		for (Set<Coordinate> bond : bonds) {
			//check there are only two coordinates in thebond
			if (bond.size() != 2) {
				throw new IllegalArgumentException("Bond does not have two coordinates : "+bond);
			}
			SortedSet<Coordinate> newBond = new TreeSet<>();
			for (Coordinate c : bond) {
				// use the min and the max to create a zero-relative coordinate system
				Coordinate cNew = Coordinate.from(c.x-minX, c.y-minY);
				//check that the coordinate refers to an element
				if (!elementsNew.containsKey(cNew)) {
					throw new IllegalArgumentException("Bond referes to coordinate "+c+" not in elements");
				}
				//TODO check that the coordinates are adjacent
				newBond.add(cNew);
			}
			bondsNew.add(ImmutableSortedSet.copyOf(newBond));
		}
		
		//TODO check that bonds form a single connected graph
		return new Molecule<T>(ImmutableSortedMap.copyOf(elementsNew), ImmutableSortedSet.copyOfSorted(bondsNew));
		
	}
}
