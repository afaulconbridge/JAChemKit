package achemmicro;

import java.util.Objects;

public class Element<T extends Comparable<T>> {

	public final T value;
	
	protected Element(T value) { 
		this.value = value;
	}
	
    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Element)) {
            return false;
        }
        Element<?> other = (Element<?>) o;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.value);
    }

	public int compareTo(Element<T> other) {
		return this.value.compareTo(other.value);
	}
	
	public static <T extends Comparable<T>> Element<T> build(T value) {
		//TODO cache 
		return new Element<T>(value);
	}
}
