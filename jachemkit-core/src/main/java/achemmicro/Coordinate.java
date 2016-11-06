package achemmicro;

import java.util.Objects;

public class Coordinate implements Comparable<Coordinate> {
	public final int x;
	public final int y;
	
	//TODO use a factory method with instance caching to save GC
	private Coordinate(int x, int y) {
		this.x = x;
		this.y = y;
	}

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Coordinate)) {
            return false;
        }
        Coordinate other = (Coordinate) o;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }

	@Override
	public int compareTo(Coordinate other) {
		if (this.x == other.x) {
			return Integer.compare(this.y, other.y);		
		} else {
			return Integer.compare(this.x, other.x);
		}
	}	
	
	@Override
	public String toString() {
		return "Coordinate("+x+","+y+")";
	}
	
	
	public static Coordinate from(Integer x, Integer y) {
		return new Coordinate(x,y);
	}
}
