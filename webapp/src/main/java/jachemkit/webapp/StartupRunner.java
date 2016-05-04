package jachemkit.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.guava.GuavaModule;

import jachemkit.hashchem.model.HashChemistry;
import jachemkit.hashchem.model.HashMolecule;

@Component
public class StartupRunner implements CommandLineRunner  {
	
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Autowired
	private HashChemistry hashChemistry;
	
	@Override
	public void run(String... args) throws Exception {
		
	}

}
