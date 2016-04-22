package jachemkit.core;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.SimpleGraph;

public class Molecule<T> extends SimpleGraph<Atom<T>, DefaultEdge> {

	private static final long serialVersionUID = 2060061214517061000L;

	public Molecule() {
		super(DefaultEdge.class);
	}

}
