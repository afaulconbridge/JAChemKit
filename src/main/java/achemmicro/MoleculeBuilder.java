package achemmicro;

import java.util.HashMap;
import java.util.Map;

public class MoleculeBuilder {

	private Map<Coordinate,String> elements = new HashMap<>();	
	
	public MoleculeBuilder fromElement(Coordinate c, String element) {
		elements.put(c, element);
		return this;
	}
	
	public Molecule build() {
		return Molecule.build(elements);
	}
	
	public static MoleculeBuilder start() {
		return new MoleculeBuilder();
	}
}
