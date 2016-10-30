package achemmicro;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;

public class Molecule implements Comparable<Molecule> {
	protected final int height;
	protected final int width;
	protected final Map<Coordinate,String> elements = new HashMap<>();
	//no need to explicit bonds, if things are adjacent, they bond
		
	protected Molecule(Map<Coordinate,String> elements) {
		this.elements.putAll(elements);
		this.height = elements.keySet().stream().map(c -> c.y).max((a,b) -> Integer.compare(a,b)).get()+1;
		this.width = elements.keySet().stream().map(c -> c.x).max((a,b) -> Integer.compare(a,b)).get()+1;
	}
	
	public int getHeight() {
		return height;
	}
	public int getWidth() {
		return width;
	}
	
	public Optional<String> getElement(int x, int y) {
		return getElement(Coordinate.from(x,y));
	}
	
	public Optional<String> getElement(Coordinate coord) {
		if (elements.containsKey(coord)) {
			return Optional.of(elements.get(coord));
		} else {
			return Optional.empty();					
		}
	}
	
	public Map<Coordinate, String> getElements() {
		return Collections.unmodifiableMap(elements);
	}

	public String getGraphFrom(int x, int y) {
		return getGraphFrom(Coordinate.from(x,y));
	}
	public String getGraphFrom(Coordinate start) {
		Queue<Coordinate> locations = new ArrayDeque<>();
		Set<Coordinate> visited = new HashSet<>();
		StringBuilder stringBuilder = new StringBuilder();
		locations.add(start);
		while (locations.peek() != null) {			
			//visit a location
			Coordinate loc = locations.poll();
			visited.add(loc);
			stringBuilder.append(getElement(loc).get());
			//get any neighbours of that location that have not already been seen
			Coordinate left = Coordinate.from(loc.x-1, loc.y);
			if (getElement(left).isPresent() && !visited.contains(left) && !locations.contains(left)) {
				locations.add(left);
			}
			Coordinate up = Coordinate.from(loc.x, loc.y-1);
			if (getElement(up).isPresent() && !visited.contains(up) && !locations.contains(up)) {
				locations.add(up);
			}
			Coordinate right = Coordinate.from(loc.x+1, loc.y);
			if (getElement(right).isPresent() && !visited.contains(right) && !locations.contains(right)) {
				locations.add(right);
			}
			Coordinate down = Coordinate.from(loc.x, loc.y+1);
			if (getElement(down).isPresent() && !visited.contains(down) && !locations.contains(down)) {
				locations.add(down);
			}
		}
		return stringBuilder.toString();
	}


    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Molecule)) {
            return false;
        }
        Molecule other = (Molecule) o;
        return Objects.equals(this.elements, other.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.elements);
    }

	@Override
	public int compareTo(Molecule other) {
        // compareTo should return < 0 if this is supposed to be
        // less than other, > 0 if this is supposed to be greater than 
        // other and 0 if they are supposed to be equal
		List<Coordinate> thisCoords = new ArrayList<>(this.elements.keySet());
		Collections.sort(thisCoords);
		List<Coordinate> otherCoords = new ArrayList<>(other.elements.keySet());
		Collections.sort(otherCoords);
		
		int i= 0;
		while (i < thisCoords.size() && i < otherCoords.size()) {
			if (thisCoords.get(i).compareTo(otherCoords.get(i)) < 0) {
				return -1;
			} else if (thisCoords.get(i).compareTo(otherCoords.get(i)) > 0) {
				return 1;
			}
			//compare the content at each coordinate
			if (this.getElement(thisCoords.get(i)).get().compareTo(this.getElement(thisCoords.get(i)).get()) < 0) {
				return -1;
			} else if (this.getElement(thisCoords.get(i)).get().compareTo(this.getElement(thisCoords.get(i)).get()) > 0) {
				return 1;
			}
			i++;
		}
		//go by size
		if (thisCoords.size() < otherCoords.size()) {
			return -1;
		} else if (thisCoords.size() > otherCoords.size()) {
			return 1;
		}
		//must be the same
		return 0;
	}
        
	public static Molecule build(Map<Coordinate,String> elements) {
		//work out the min and max coordinates
		int minX = elements.keySet().stream().map(c -> c.x).min((a,b) -> Integer.compare(a,b)).get();
		int minY = elements.keySet().stream().map(c -> c.y).min((a,b) -> Integer.compare(a,b)).get();
		
		Map<Coordinate,String> elementsNew = new HashMap<>();
		for (Coordinate c : elements.keySet()) {
			Coordinate cNew = Coordinate.from(c.x-minX, c.y-minY);
			elementsNew.put(cNew, elements.get(c));
		}
		return new Molecule(elementsNew);
	}
}
