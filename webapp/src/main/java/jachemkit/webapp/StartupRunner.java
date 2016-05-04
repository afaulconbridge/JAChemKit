package jachemkit.webapp;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

import jachemkit.hashchem.model.HashChemistry;
import jachemkit.hashchem.model.HashMolecule;
import jachemkit.hashchem.neo.MoleculeRepository;
import jachemkit.hashchem.neo.NeoAtom;
import jachemkit.hashchem.neo.NeoMolecule;

@Component
public class StartupRunner implements CommandLineRunner  {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private HashChemistry hashChemistry;
	
	@Autowired
	private MoleculeRepository moleculeRepository;
	
	@Override
	public void run(String... args) throws Exception {
		//create a molecule
		NeoAtom a1 = new NeoAtom();
		NeoMolecule mol = new NeoMolecule(a1);
		
		mol.addAtom(new NeoAtom(), a1);
		mol.addAtom(new NeoAtom(), a1);

		NeoAtom a4 = new NeoAtom();
		mol.addAtom(a4, a1);
		mol.addAtom(new NeoAtom(), a4);
		
		//persist it
		
		mol = moleculeRepository.save(mol);
		
		//restore it
		
		NeoMolecule mol2 = moleculeRepository.findAll().iterator().next();
		
		//compare it
	
		if (mol2.getAtoms().size() != mol.getAtoms().size()) {
			throw new RuntimeException("Size difference! "+mol.getAtoms().size()+" vs "+mol2.getAtoms().size());
		}
		
		Multiset<Integer> molMultiset = HashMultiset.create();
		mol.getAtoms().stream().mapToInt((a)->a.getBondedTo().size()).boxed().forEach((i)->molMultiset.add(i));
		Multiset<Integer> mol2Multiset = HashMultiset.create();
		mol2.getAtoms().stream().mapToInt((a)->a.getBondedTo().size()).boxed().forEach((i)->mol2Multiset.add(i));
		
		if (!molMultiset.equals(mol2Multiset)) {

			throw new RuntimeException("Connectivity difference!");
			
		}
		
		//TODO finish
		
	}

}
