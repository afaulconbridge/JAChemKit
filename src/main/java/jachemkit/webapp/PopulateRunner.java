package jachemkit.webapp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import jachemkit.hashchem.model.NeoAtom;
import jachemkit.hashchem.model.NeoMolecule;
import jachemkit.hashchem.repo.MoleculeRepository;

@Component
public class PopulateRunner implements ApplicationRunner {

	@Autowired
	private MoleculeRepository moleculeRepository;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {

		//ensure there is some basic molecules in the repos
		
		//create what we want to be there
		Set<NeoMolecule> wantedMolecules = new HashSet<>();
		for (int i =0; i < 100; i++) {
			Random rng = new Random(i*42);
			List<Integer> element = new ArrayList<>();
			for (int j=0; j < 4; j++) {
				element.add(rng.nextInt(8));
			}
			NeoMolecule mol = new NeoMolecule(new NeoAtom(element));
			wantedMolecules.add(mol);
		}
		
		//for each thing that is there, remove its partner from the wanted
		for (NeoMolecule mol : moleculeRepository.findAtoms()) {
			Iterator<NeoMolecule> wantedIterator = wantedMolecules.iterator();
			while (wantedIterator.hasNext()) {
				NeoMolecule wanted = wantedIterator.next();
				if (wanted.equals(mol)) {
					wantedIterator.remove();
					break;
				}
			}
		}
		
		//add what is left
		Iterator<NeoMolecule> wantedIterator = wantedMolecules.iterator();
		while (wantedIterator.hasNext()) {
			NeoMolecule wanted = wantedIterator.next();
			moleculeRepository.save(wanted);
		}
	}

}
