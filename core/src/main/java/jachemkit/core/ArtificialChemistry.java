package jachemkit.core;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public interface ArtificialChemistry {

	public default Molecule createRandomMolecule(){
		return createRandomMolecule(new Random());
	}
	
	public Molecule createRandomMolecule(Random rng);
}
