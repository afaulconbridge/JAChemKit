package jachemkit;

import java.util.Comparator;

import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultEdge;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit4.SpringRunner;

import com.google.common.collect.ImmutableList;

import jachemkit.hashchem.Config;
import jachemkit.hashchem.neo.MoleculeRepository;
import jachemkit.hashchem.neo.NeoAtom;
import jachemkit.hashchem.neo.NeoMolecule;

@RunWith(SpringRunner.class)
@SpringBootTest(classes=Config.class)
public class NeoMoleculeTest {
	
	@Autowired
	private MoleculeRepository moleculeRepository;

	@Test
	public void testPersistance() {

		//create a molecule
		NeoAtom a1 = new NeoAtom(ImmutableList.of(1,2,3,4,5,6,7,8));
		
		NeoMolecule mol = new NeoMolecule(a1);
		
		mol.addAtom(new NeoAtom (ImmutableList.of(2,2,3,4,5,6,7,8)), a1);
		mol.addAtom(new NeoAtom (ImmutableList.of(2,2,3,4,5,6,7,8)), a1);

		NeoAtom  a4 = new NeoAtom(ImmutableList.of(3,2,3,4,5,6,7,8));
		mol.addAtom(a4, a1);
		mol.addAtom(new NeoAtom (ImmutableList.of(5,2,3,4,5,6,7,8)), a4);
		
		//persist it		
		mol = moleculeRepository.save(mol);
		
		//restore it		
		NeoMolecule mol2 = moleculeRepository.findAll().iterator().next();
		
		//compare it		
		Comparator<DefaultEdge> edgeComparator = new Comparator<DefaultEdge>(){
			@Override
			public int compare(DefaultEdge e1, DefaultEdge e2) {
				return 0;
			}			
		};
		Comparator<NeoAtom> vertexComparator = new Comparator<NeoAtom>(){
			@Override
			public int compare(NeoAtom a1, NeoAtom a2) {
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
			}			
		};
		VF2GraphIsomorphismInspector<NeoAtom, DefaultEdge> inspector = 
				new VF2GraphIsomorphismInspector<>(mol.getStructure(), mol2.getStructure(),
						vertexComparator, edgeComparator);
			
		if (!inspector.isomorphismExists()) {
			throw new RuntimeException("Unable to find isomorphism");
		}
	}
	
}
