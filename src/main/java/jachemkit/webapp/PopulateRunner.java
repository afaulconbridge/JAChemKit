package jachemkit.webapp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jachemkit.hashchem.model.NeoAtom;
import jachemkit.hashchem.model.NeoMolecule;
import jachemkit.hashchem.repo.MoleculeRepository;
import jachemkit.hashchem.service.NeoMoleculeComparator;

@Component
public class PopulateRunner implements ApplicationRunner {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private MoleculeRepository moleculeRepository;
	
	@Autowired
	private NeoMoleculeComparator moleculeComparator;
	
	@Autowired 
	private ObjectMapper objectMapper;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {

		//ensure there is some basic molecules in the repos
		
		//create what we want to be there
		Set<NeoMolecule> wantedMolecules = new HashSet<>();
		for (int i = 0; i < 10; i++) {
			Random rng = new Random(i);
			//warm up the randomness a bit
			for (int j=0; j < rng.nextInt(10); j++) {
				rng.nextInt();
			}
			
			List<Integer> element = new ArrayList<>();
			for (int j=0; j < 4; j++) {
				element.add(rng.nextInt(8));
			}
			//convert to json
			String value = objectMapper.writeValueAsString(element);
			//create the molecule
			NeoMolecule mol = new NeoMolecule(new NeoAtom(value));
			wantedMolecules.add(mol);
		}
		
		//for each thing that is there, remove its partner from the wanted
		for (NeoMolecule mol : moleculeRepository.findAtoms()) {
			//make sure we get it in sufficient depth
			mol = moleculeRepository.findOne(mol.getNeoId(), 3);
			
			Iterator<NeoMolecule> wantedIterator = wantedMolecules.iterator();
			while (wantedIterator.hasNext()) {
				NeoMolecule wanted = wantedIterator.next();				
				if (moleculeComparator.compare(wanted, mol)==0) {
					wantedIterator.remove();
					break;
				}
			}
		}
		
		//add what is left
		Iterator<NeoMolecule> wantedIterator = wantedMolecules.iterator();
		while (wantedIterator.hasNext()) {
			log.info("Adding new atomic molecule");
			NeoMolecule wanted = wantedIterator.next();
			moleculeRepository.save(wanted);
		}
	}

}
