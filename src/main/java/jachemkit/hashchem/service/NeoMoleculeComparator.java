package jachemkit.hashchem.service;

import java.util.Comparator;

import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.UnmodifiableUndirectedGraph;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jachemkit.hashchem.model.NeoAtom;
import jachemkit.hashchem.model.NeoMolecule;

@Service
public class NeoMoleculeComparator implements Comparator<NeoMolecule> {

	@Autowired
	private StructureService structureService;
	
	private static class DefaultEdgeComparator implements Comparator<DefaultEdge> {
		@Override
		public int compare(DefaultEdge e1, DefaultEdge e2) {
			return 0;
		}			
	}
	private static class NeoAtomComparator implements Comparator<NeoAtom> {
		@Override
		public int compare(NeoAtom a1, NeoAtom a2) {
			return a1.getValue().compareTo(a2.getValue());
			
/*
 * legacy from list of integers value
			if (a1.getValue().size() < a2.getValue().size()) {
				return -1;
			} else if (a1.getValue().size() > a2.getValue().size()) {
				return 1;
			} else  {
				for (int i=0; i < a1.getValue().size(); i++) {
					Integer v1 = a1.getValue().get(i);
					Integer v2 = a2.getValue().get(i);
					if (v1.compareTo(v2) != 0) {
						return v1.compareTo(v2);
					}
				}
				return 0;
			}								
 */
		}			
	}

	@Override
	public int compare(NeoMolecule m1, NeoMolecule m2) {
		if (m1 == null) {
			throw new IllegalArgumentException("Cannot comapre to null");
		}
		if (m2 == null) {
			throw new IllegalArgumentException("Cannot comapre to null");
		}
		if (m2 == m1) {
			return 0;
		}
		if (m1.getNeoId() != null && m2.getNeoId() != null 
				&& m1.getNeoId().equals(m2.getNeoId())) {
			return 0;
		}
		UnmodifiableUndirectedGraph<NeoAtom, DefaultEdge> s1 = structureService.getStructure(m1);
		UnmodifiableUndirectedGraph<NeoAtom, DefaultEdge> s2 = structureService.getStructure(m2);
		//have to do graph isomorphism, might be slow!
		VF2GraphIsomorphismInspector<NeoAtom, DefaultEdge> inspector = 
				new VF2GraphIsomorphismInspector<>(s1, s2,
						new NeoAtomComparator(), new DefaultEdgeComparator());
		if (inspector.isomorphismExists()) {
			return 0;
		} else {
			//they are different, need to order them somehow?
			return 1;
		}
	}
}
