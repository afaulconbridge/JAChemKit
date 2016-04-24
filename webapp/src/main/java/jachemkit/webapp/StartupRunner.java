package jachemkit.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jachemkit.hashchem.HashChemistry;
import jachemkit.hashchem.HashMolecule;

@Component
public class StartupRunner implements CommandLineRunner  {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private HashChemistry hashChemistry;
	
	@Override
	public void run(String... args) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		HashMolecule mol = hashChemistry.createRandomMolecule();
		log.info(mapper.writeValueAsString(mol));
	}

}
