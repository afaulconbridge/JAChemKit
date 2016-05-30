package jachemkit.core;

import java.util.Random;

import org.springframework.stereotype.Component;

@Component
public interface ArtificialChemistry<M> {

	public default M createRandomMolecule(){
		return createRandomMolecule(new Random());
	}
	
	public M createRandomMolecule(Random rng);
}
